package com.deepO.Release_2.service.impl;

import static com.deepO.Release_2.utility.constant.DepositExceptionConstant.NO_DEPOSIT_FOUND_BY_DEPOSIT_NAME;
import static com.deepO.Release_2.utility.constant.FileConstant.DEFAULT_ITEM_IMAGE_PATH;
import static com.deepO.Release_2.utility.constant.FileConstant.DIRECTORY_CREATED;
import static com.deepO.Release_2.utility.constant.FileConstant.DOT;
import static com.deepO.Release_2.utility.constant.FileConstant.FILE_SAVED_IN_FILE_SYSTEM;
import static com.deepO.Release_2.utility.constant.FileConstant.FORWARD_SLASH;
import static com.deepO.Release_2.utility.constant.FileConstant.ITEM_FOLDER;
import static com.deepO.Release_2.utility.constant.FileConstant.ITEM_IMAGE_PATH;
import static com.deepO.Release_2.utility.constant.FileConstant.JPG_EXTENSION;
import static com.deepO.Release_2.utility.constant.FileConstant.NOT_AN_IMAGE_FILE;
import static com.deepO.Release_2.utility.constant.ItemExceptionConstant.ITEM_TITLE_ALREADY_EXISTS;
import static com.deepO.Release_2.utility.constant.ItemExceptionConstant.NO_ITEM_FOUND_BY_ITEM_NAME;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.springframework.http.MediaType.IMAGE_GIF_VALUE;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.deepO.Release_2.models.Deposit;
import com.deepO.Release_2.models.Item;
import com.deepO.Release_2.repository.ItemRepository;
import com.deepO.Release_2.service.inter.DepositService;
import com.deepO.Release_2.service.inter.ItemService;
import com.deepO.Release_2.utility.exception.domain.deposit.DepositNameNotFoundException;
import com.deepO.Release_2.utility.exception.domain.item.ItemByTitleNotFoundException;
import com.deepO.Release_2.utility.exception.domain.item.ItemTitleAlreadyExistsException;
import com.deepO.Release_2.utility.exception.domain.users.NotAnImageFileException;

@Service
@Transactional
public class ItemServiceImpl implements ItemService{
	private Logger LOGGER = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private ItemRepository itemRepository;
	 @Autowired
	 private DepositService depositService;
		
	 
	 
	//================================================================================== 


	
	@Override
	public Item addNewItem(String itemTitle, String itemCategory, String itemDescription, double itemPrice,int quantity, MultipartFile itemImage,String depositName) throws ItemByTitleNotFoundException, ItemTitleAlreadyExistsException, IOException, NotAnImageFileException, DepositNameNotFoundException {
		ifExistItem(EMPTY,itemTitle);
		Deposit deposit =  ifExistDeposit(depositName);
		Item item = new Item();
		item.setItemId(generateItemId());
		item.setItemTitle(itemTitle);
		item.setItemCategory(itemCategory);
		item.setItemDescription(itemDescription);
		item.setItemPrice(itemPrice);
		item.setItemCreation(new Date());
		item.setItemImage(getTemporaryItemImageUrl(itemTitle));
		item.setQuantity(quantity);
		//----------------------------------------------------------
		item.setItemDeposit(deposit);
		item.setDepositName(deposit.getDepositName());
		
		//----------------------------------------------------------
		 
		itemRepository.save(item);
		saveItemImage(item, itemImage);
		return item;
	}

	@Override
	public Item updateItem(String CurrentitemTitle, String itemTitle, String itemCategory, String itemDescription,
			double itemPrice, int quantity,MultipartFile itemImage,String depositName) throws ItemByTitleNotFoundException, ItemTitleAlreadyExistsException, IOException, NotAnImageFileException, DepositNameNotFoundException {
		
		Deposit deposit =  ifExistDeposit(depositName);
		Item item = ifExistItem(CurrentitemTitle,itemTitle);
		
		item.setItemId(generateItemId());
		item.setItemTitle(itemTitle);
		item.setItemCategory(itemCategory);
		item.setItemDescription(itemDescription);
		item.setItemPrice(itemPrice);
		item.setItemCreation(new Date());
		item.setItemImage(getTemporaryItemImageUrl(itemTitle));
		item.setItemDeposit(deposit);
		item.setDepositName(deposit.getDepositName());
		item.setQuantity(quantity);
		//----------------------------------------------------------
		itemRepository.save(item);
		saveItemImage(item, itemImage);
		return item;
	}

	@Override
	public void deleteItem(String itemTitle) throws ItemByTitleNotFoundException, ItemTitleAlreadyExistsException, IOException {
		Item item = itemRepository.findItemByItemTitle(itemTitle);
		Path userFolder = Paths.get(ITEM_FOLDER + item.getItemTitle()).toAbsolutePath().normalize();
		FileUtils.deleteDirectory(new File(userFolder.toString()));
		itemRepository.deleteById(item.getId());
		
	}

	@Override
	public Item UpdateItemImage(String itemTitle, MultipartFile profileImage) throws IOException, NotAnImageFileException, ItemByTitleNotFoundException, ItemTitleAlreadyExistsException {
		Item item = ifExistItem(itemTitle, null);
		saveItemImage(item, profileImage);
		return item;
	}
	@Override
	public List<Item> getAllItem() {
		
		return itemRepository.findAll();
	}

	@Override
	public Item findItemtByTitle(String itemTitle) {
		 
		return itemRepository.findItemByItemTitle(itemTitle);
	}

	// ==============================================================================
	private Item ifExistItem(String CurrentItemTitle, String newItemTitle) throws ItemByTitleNotFoundException, ItemTitleAlreadyExistsException
			{

		if (StringUtils.isNotEmpty(CurrentItemTitle)) {
			Item oldItem = findItemtByTitle(CurrentItemTitle);
			if (oldItem == null) {
				throw new ItemByTitleNotFoundException(NO_ITEM_FOUND_BY_ITEM_NAME);
			}
			return oldItem;
		}
		Item NewItem = findItemtByTitle(newItemTitle);
	
		if (NewItem != null) {
			throw new ItemTitleAlreadyExistsException(ITEM_TITLE_ALREADY_EXISTS);
		}
	
		return null;

	}
	private Deposit ifExistDeposit(String itemName ) throws DepositNameNotFoundException 
	{
		
		if (StringUtils.isNotEmpty(itemName)) {
			Deposit CurrentDeposit = depositService.findDepositByName(itemName);
			if (CurrentDeposit == null) {
				throw new DepositNameNotFoundException(NO_DEPOSIT_FOUND_BY_DEPOSIT_NAME);
			}
			return CurrentDeposit;
		}
		return null;
	
		
	}

	// ---------------------------------------------
	private String generateItemId() {
		return RandomStringUtils.randomNumeric(10);
	}

	// ---------------------------------------------
	private String getTemporaryItemImageUrl(String depositName) {
		return ServletUriComponentsBuilder.fromCurrentContextPath().path(DEFAULT_ITEM_IMAGE_PATH + depositName).toUriString();
	}

	// ---------------------------------------------
	private String setItemImageUrl(String depositName) {
		
		//-------------------------------------------------------------------------------
		//if(itemName.endsWith(" ")) { itemName =  itemName.substring(0,itemName.length()-1);};
		//-------------------------------------------------------------------------------
		return ServletUriComponentsBuilder.fromCurrentContextPath()
				.path(ITEM_IMAGE_PATH + depositName + FORWARD_SLASH + depositName + DOT + JPG_EXTENSION)
				.toUriString();
	}

	// ---------------------------------------------
	private void saveItemImage(Item item, MultipartFile itemImage)throws IOException, NotAnImageFileException {
		if (itemImage != null) {
			if (!Arrays.asList(IMAGE_JPEG_VALUE, IMAGE_PNG_VALUE, IMAGE_GIF_VALUE)
					.contains(itemImage.getContentType())) {
				throw new NotAnImageFileException(itemImage.getOriginalFilename() + NOT_AN_IMAGE_FILE);
			}
			Path ItemFolder = Paths.get(ITEM_FOLDER + item.getItemTitle()).toAbsolutePath().normalize();
			if (!Files.exists(ItemFolder)) {
				Files.createDirectories(ItemFolder);
				LOGGER.info(DIRECTORY_CREATED + ItemFolder);
			}
			Files.deleteIfExists(Paths.get(item.getItemTitle() + DOT + JPG_EXTENSION));
			Files.copy(itemImage.getInputStream(),
					ItemFolder.resolve(item.getItemTitle() + DOT + JPG_EXTENSION), REPLACE_EXISTING);
			item.setItemImage(setItemImageUrl(item.getItemTitle()));
			itemRepository.save(item);
			LOGGER.info(FILE_SAVED_IN_FILE_SYSTEM + itemImage.getOriginalFilename());
		}
	}







}
