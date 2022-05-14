package com.deepO.Release_2.service.inter;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.deepO.Release_2.models.Deposit;
import com.deepO.Release_2.models.Item;
import com.deepO.Release_2.models.User;
import com.deepO.Release_2.utility.exception.domain.deposit.DepositNameNotFoundException;
import com.deepO.Release_2.utility.exception.domain.item.ItemByTitleNotFoundException;
import com.deepO.Release_2.utility.exception.domain.item.ItemTitleAlreadyExistsException;
import com.deepO.Release_2.utility.exception.domain.users.NotAnImageFileException;

public interface ItemService {
	List<Item> getAllItem();

	Item findItemtByTitle(String itemTitle);
	
	Item addNewItem(String itemTitle, String itemCategory, String itemDescription, double itemPrice,int quantity, MultipartFile itemImage,String depositName) throws ItemByTitleNotFoundException, ItemTitleAlreadyExistsException, IOException, NotAnImageFileException, DepositNameNotFoundException ;
	Item updateItem( String CurrentitemTitle,String itemTitle, String itemCategory, String itemDescription, double itemPrice, int quantity,MultipartFile itemImage,String depositName) throws ItemByTitleNotFoundException, ItemTitleAlreadyExistsException, IOException, NotAnImageFileException, DepositNameNotFoundException;
	void deleteItem(String itemTitle) throws ItemByTitleNotFoundException, ItemTitleAlreadyExistsException, IOException;
	Item UpdateItemImage(String itemTitle, MultipartFile profileImage) throws IOException, NotAnImageFileException, ItemByTitleNotFoundException, ItemTitleAlreadyExistsException ;
	

}
