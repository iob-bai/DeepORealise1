package com.deepO.Release_2.resource;

import static com.deepO.Release_2.utility.constant.FileConstant.*;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.deepO.Release_2.models.Deposit;
import com.deepO.Release_2.models.Item;
import com.deepO.Release_2.repository.ItemRepository;
import com.deepO.Release_2.service.inter.DepositService;
import com.deepO.Release_2.service.inter.ItemService;
import com.deepO.Release_2.utility.domain.HttpResponse;
import com.deepO.Release_2.utility.exception.ExceptionHandling;
import com.deepO.Release_2.utility.exception.domain.deposit.DepositAdressAlreadyExistsException;
import com.deepO.Release_2.utility.exception.domain.deposit.DepositNameAlreadyExistsException;
import com.deepO.Release_2.utility.exception.domain.deposit.DepositNameNotFoundException;
import com.deepO.Release_2.utility.exception.domain.item.ItemByTitleNotFoundException;
import com.deepO.Release_2.utility.exception.domain.item.ItemTitleAlreadyExistsException;
import com.deepO.Release_2.utility.exception.domain.users.NotAnImageFileException;

@RestController
@RequestMapping(path = { "/", "/item" })
public class ItemResource extends ExceptionHandling {

	public static final String ITEM_DELETED_SUCESSFULLY = "The Item was been Deleted sucessfully";

	@Autowired
	private ItemService itemService;

	
	
	
	
	
	
	
	
	
	
	


	// --------------------------------------------------
	@PostMapping("/add")
	public ResponseEntity<Item> addNewItem(	@RequestParam("itemTitle") String itemTitle,
											@RequestParam("itemCategory") String itemCategory,
											@RequestParam("itemDescription") String itemDescription,
											@RequestParam("itemPrice") String itemPrice,
											@RequestParam("quantity") String quantity,
											@RequestParam(value = "profileImage", required = false/* optional */) MultipartFile itemImage,
											@RequestParam("depositName") String depositName)
			throws NumberFormatException, ItemByTitleNotFoundException, ItemTitleAlreadyExistsException, IOException,
			NotAnImageFileException, DepositNameNotFoundException {
		Item newItem = itemService.addNewItem( itemTitle, itemCategory, itemDescription,Double.parseDouble(itemPrice),Integer.parseInt(quantity),itemImage,depositName);
		return new ResponseEntity<Item>(newItem, OK);
	}

	// --------------------------------------------------
	@PutMapping("/update")
	public ResponseEntity<Item> updateNewItem(@RequestParam("CurrentitemTitle") String CurrentitemTitle,
											@RequestParam("itemTitle") String itemTitle,
											@RequestParam("itemCategory") String itemCategory,
											@RequestParam("itemDescription") String itemDescription,
											@RequestParam("itemPrice") String itemPrice,
											@RequestParam("quantity") String quantity,
											@RequestParam(value = "profileImage", required = false/* optional */) MultipartFile itemImage,
											@RequestParam("depositName") String depositName)
			throws NumberFormatException, ItemByTitleNotFoundException, ItemTitleAlreadyExistsException, IOException,
			NotAnImageFileException,DepositNameNotFoundException {
		Item newItem = itemService.updateItem(CurrentitemTitle, itemTitle, itemCategory, itemDescription,Double.parseDouble(itemPrice),Integer.parseInt(quantity), itemImage,depositName);
		return new ResponseEntity<Item>(newItem, OK);
	}

	// --------------------------------------------------
	@GetMapping("/find/{itemTitle}")
	public ResponseEntity<Item> getItem(@PathVariable("itemTitle") String itemTitle) {
		Item item = itemService.findItemtByTitle(itemTitle);
		return new ResponseEntity<Item>(item, OK);
	}

	// --------------------------------------------------
	@GetMapping("/list")
	public ResponseEntity<List<Item>> getAllItem() {
		List<Item> item = itemService.getAllItem();
		return new ResponseEntity<>(item, OK);
	}

	// -------------------------------------------------
	// --------------------------------------------------
	@DeleteMapping("/delete/{itemTitle}")
	@PreAuthorize("hasAnyAuthority('user:delete')")
	public ResponseEntity<HttpResponse> deleteItem(@PathVariable("itemTitle") String itemTitle)
			throws IOException, ItemByTitleNotFoundException,ItemTitleAlreadyExistsException {
		 
		itemService.deleteItem(itemTitle);
		return response(OK, ITEM_DELETED_SUCESSFULLY);
	}

	// --------------------------------------------------
	@PutMapping("/updateItemImage")
	public ResponseEntity<Item> updateProfileImage(@RequestParam("itemTitle") String itemTitle,@RequestParam("profileImage") MultipartFile itemImage)
			throws IOException, NotAnImageFileException, DepositAdressAlreadyExistsException,
			DepositNameAlreadyExistsException, DepositNameNotFoundException, ItemByTitleNotFoundException,
			com.deepO.Release_2.utility.exception.domain.item.ItemTitleAlreadyExistsException {
		Item item = itemService.UpdateItemImage(itemTitle, itemImage);
		return new ResponseEntity<Item>(item, OK);
	}

	// --------------------------------------------------
	@GetMapping(path = "/image/{itemTitle}/{fileName}", produces = IMAGE_JPEG_VALUE)
	public byte[] getProfileImage(@PathVariable("itemTitle") String itemTitle,
			@PathVariable("fileName") String fileName) throws IOException {
		return Files.readAllBytes(Paths.get(ITEM_FOLDER + itemTitle + FORWARD_SLASH + fileName));
	}

	// --------------------------------------------------
	@GetMapping(path = "/image/profile/{itemTitle}", produces = IMAGE_JPEG_VALUE)
	public byte[] getTempProfileImage(@PathVariable("itemTitle") String itemTitle) throws IOException {
		URL url = new URL(TEMP_PROFILE_IMAGE_BASE_URL + itemTitle);
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try (InputStream inputStream = url.openStream()) {
			int bytesRead;
			byte[] chunk = new byte[1024];
			while ((bytesRead = inputStream.read(chunk)) > 0) {
				byteArrayOutputStream.write(chunk, 0, bytesRead);
			}
		}
		return byteArrayOutputStream.toByteArray();
	}

	// ================================================================================================================================
	private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {

		return new ResponseEntity<>(
				new HttpResponse(httpStatus.value(), httpStatus, httpStatus.getReasonPhrase(), message), OK);
	}

}
