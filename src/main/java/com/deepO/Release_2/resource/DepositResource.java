package com.deepO.Release_2.resource;

import static com.deepO.Release_2.utility.constant.FileConstant.FORWARD_SLASH;
import static com.deepO.Release_2.utility.constant.FileConstant.TEMP_PROFILE_IMAGE_BASE_URL;
import static com.deepO.Release_2.utility.constant.FileConstant.DEPOSIT_FOLDER;
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
import com.deepO.Release_2.service.inter.DepositService;
import com.deepO.Release_2.utility.domain.HttpResponse;
import com.deepO.Release_2.utility.exception.ExceptionHandling;
import com.deepO.Release_2.utility.exception.domain.deposit.DepositAdressAlreadyExistsException;
import com.deepO.Release_2.utility.exception.domain.deposit.DepositNameAlreadyExistsException;
import com.deepO.Release_2.utility.exception.domain.deposit.DepositNameNotFoundException;
import com.deepO.Release_2.utility.exception.domain.item.ItemByTitleNotFoundException;
import com.deepO.Release_2.utility.exception.domain.item.ItemTitleAlreadyExistsException;
import com.deepO.Release_2.utility.exception.domain.users.NotAnImageFileException;
import com.deepO.Release_2.utility.exception.domain.users.UserNotFoundException;

@RestController
@RequestMapping(path = { "/", "/deposit" })
public class DepositResource extends ExceptionHandling{
	
	
	public static final String DEPOSIT_DELETED_SUCESSFULLY ="The DEPOSIT was been Deleted sucessfully";
	
	
	
	@Autowired
	private DepositService depositService;
	
	
	
	
	
	
	
	
	
	
	//-------------------------------------------------------------------------
	@PostMapping("/addDepositToUser")
	public ResponseEntity<Deposit> addDepositToUser(@RequestParam("depositName") String depositName,
											@RequestParam("userName") String userName) throws UserNotFoundException, DepositAdressAlreadyExistsException, DepositNameAlreadyExistsException, DepositNameNotFoundException
	{
		Deposit newdeposit = depositService.addDepositToUser(depositName, userName);
		return new ResponseEntity<Deposit>(newdeposit, OK);
	}
	//--------------------------------------------------
	@PostMapping("/add")
	public ResponseEntity<Deposit> addNewDeposit(@RequestParam("DepositName") String DepositName,
											@RequestParam("DepositAdress") String DepositAdress,
											@RequestParam("description") String description,
											@RequestParam("category") String category,
											@RequestParam("price") String price,
											@RequestParam("isRent") String isRent,
											@RequestParam("islocked") String islocked,
											@RequestParam(value = "profileImage",required = false/*optional*/) MultipartFile profileImage) throws NumberFormatException, DepositAdressAlreadyExistsException, DepositNameAlreadyExistsException, DepositNameNotFoundException, IOException, NotAnImageFileException 
	{
		Deposit newUser = depositService.addNewDeposit(DepositName, DepositAdress, description, category,Double.parseDouble(price) , Boolean.parseBoolean(isRent), Boolean.parseBoolean(islocked), profileImage);
		return new ResponseEntity<Deposit>(newUser,OK);
	}
	//--------------------------------------------------
	@PutMapping("/update")
	public ResponseEntity<Deposit> updateNewDeposit(@RequestParam("CurrentDepositName") String CurrentDepositName,
													@RequestParam("DepositName") String DepositName,
													@RequestParam("DepositAdress") String DepositAdress,
													@RequestParam("description") String description,
													@RequestParam("category") String category,
													@RequestParam("price") String price,
													@RequestParam("isRent") String isRent,
													@RequestParam("islocked") String islocked,
													@RequestParam(value = "profileImage",required = false/*optional*/) MultipartFile profileImage) throws NumberFormatException, DepositAdressAlreadyExistsException, DepositNameAlreadyExistsException, DepositNameNotFoundException, IOException, NotAnImageFileException 
	{
	Deposit newUser = depositService.updateDeposit(CurrentDepositName,DepositName, DepositAdress, description, category,Double.parseDouble(price) , Boolean.parseBoolean(isRent), Boolean.parseBoolean(islocked), profileImage);
	return new ResponseEntity<Deposit>(newUser,OK);
	}
	//--------------------------------------------------
	@GetMapping("/find/{depositName}")
	public ResponseEntity<Deposit> getUser(@PathVariable("depositName")String depositName)
	{
		Deposit deposit = depositService.findDepositByName(depositName);
		return new ResponseEntity<Deposit>(deposit,OK);
	}
	//--------------------------------------------------
	@GetMapping("/list")
	public ResponseEntity<List<Deposit>> getAllUser()
	{
		List<Deposit> deposit = depositService.getAllDeposit();
		return new ResponseEntity<>(deposit,OK);
	}
	//-------------------------------------------------
	//--------------------------------------------------
	@DeleteMapping("/delete/{depositName}")
	@PreAuthorize("hasAnyAuthority('user:delete')")
	public ResponseEntity<HttpResponse> deleteUser(@PathVariable("depositName") String depositName) throws IOException 
	{
		depositService.deleteDeposit(depositName);
		return response(OK,DEPOSIT_DELETED_SUCESSFULLY);
	}
	//--------------------------------------------------
	@PutMapping("/updateDepositImage")
	public ResponseEntity<Deposit> updateProfileImage(@RequestParam("depositName") String depositName,@RequestParam("profileImage") MultipartFile profileImage)
			throws  IOException, NotAnImageFileException, DepositAdressAlreadyExistsException, DepositNameAlreadyExistsException, DepositNameNotFoundException
	{
		Deposit deposit = depositService.UpdateDepositImage(depositName, profileImage);
		return new ResponseEntity<Deposit>(deposit,OK);
	}
	//--------------------------------------------------
	@GetMapping(path = "/image/{depositName}/{fileName}",produces = IMAGE_JPEG_VALUE)
	public byte[] getProfileImage(@PathVariable("depositName") String depositName,@PathVariable("fileName") String fileName) throws IOException 
	{
		return Files.readAllBytes(Paths.get(DEPOSIT_FOLDER + depositName + FORWARD_SLASH + fileName));
	}
	//--------------------------------------------------
	@GetMapping(path = "/image/profile/{depositName}",produces = IMAGE_JPEG_VALUE)
	public byte[] getTempProfileImage(@PathVariable("depositName") String depositName) throws IOException 
	{
		URL url = new URL(TEMP_PROFILE_IMAGE_BASE_URL + depositName);
		ByteArrayOutputStream byteArrayOutputStream =new ByteArrayOutputStream();
		try(InputStream inputStream = url.openStream())
		{
			int bytesRead;
			byte[] chunk = new byte[1024];
			while ((bytesRead = inputStream.read(chunk)) > 0)
			{
				byteArrayOutputStream.write(chunk,0,bytesRead);
			}
		}
		return byteArrayOutputStream.toByteArray();
	}
	//================================================================================================================================
	private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
		
		return  new ResponseEntity<>(new HttpResponse(httpStatus.value(),httpStatus,httpStatus.getReasonPhrase(),message),OK );
	}

}
