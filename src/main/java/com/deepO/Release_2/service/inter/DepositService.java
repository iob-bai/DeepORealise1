package com.deepO.Release_2.service.inter;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.deepO.Release_2.models.Deposit;
import com.deepO.Release_2.models.User;
import com.deepO.Release_2.utility.exception.domain.deposit.DepositAdressAlreadyExistsException;
import com.deepO.Release_2.utility.exception.domain.deposit.DepositNameAlreadyExistsException;
import com.deepO.Release_2.utility.exception.domain.deposit.DepositNameNotFoundException;
import com.deepO.Release_2.utility.exception.domain.users.NotAnImageFileException;
import com.deepO.Release_2.utility.exception.domain.users.UserNotFoundException;

public interface DepositService {

	List<Deposit> getAllDeposit();

	Deposit findDepositByName(String depositName);
	Deposit findDepositByAdress(String depositAdess);
	Deposit addNewDeposit(String DepositName, String DepositAdress, String description, String category, double price, boolean isRent,
			boolean islocked, MultipartFile profileImage) throws DepositAdressAlreadyExistsException, DepositNameAlreadyExistsException, DepositNameNotFoundException, IOException, NotAnImageFileException ;
	Deposit updateDeposit( String CurrentDepositName,String DepositName, String DepositAdress, String description, String category, double price, boolean isRent,
			boolean islocked, MultipartFile profileImage) throws DepositAdressAlreadyExistsException, DepositNameAlreadyExistsException, DepositNameNotFoundException, IOException, NotAnImageFileException ;
	void deleteDeposit(String DepositName) throws IOException ;
	Deposit UpdateDepositImage(String DepositName, MultipartFile DepositImage) throws DepositAdressAlreadyExistsException, DepositNameAlreadyExistsException, DepositNameNotFoundException, IOException, NotAnImageFileException ;
	Deposit addDepositToUser( String DepositName,String userName ) throws UserNotFoundException, DepositAdressAlreadyExistsException, DepositNameAlreadyExistsException, DepositNameNotFoundException;
	Deposit changeDepositToRent(boolean isvalidated,Deposit deposit,User username) ;
	
}
