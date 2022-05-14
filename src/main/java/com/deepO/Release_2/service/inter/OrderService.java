package com.deepO.Release_2.service.inter;

import java.util.List;

import com.deepO.Release_2.models.Commande;
import com.deepO.Release_2.utility.exception.domain.deposit.DepositNameNotFoundException;
import com.deepO.Release_2.utility.exception.domain.order.OrderAlreadyExistsByOrderId;
import com.deepO.Release_2.utility.exception.domain.order.OrderNotFoundByOrderId;
import com.deepO.Release_2.utility.exception.domain.users.UserNotFoundException;

public interface OrderService {
	List<Commande> getAllItem();

	Commande findOrderByOrderId(String OrderId);
	
	Commande addNewOrder( String userName,String depositName) throws UserNotFoundException, DepositNameNotFoundException;
	Commande updateOrder(String orderId,String userName,String depositName,boolean IsValidated) throws OrderNotFoundByOrderId, UserNotFoundException, DepositNameNotFoundException;
	void deleteOrder(String OrderId) throws OrderNotFoundByOrderId;
	

}
