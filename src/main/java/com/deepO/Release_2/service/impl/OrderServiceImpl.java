package com.deepO.Release_2.service.impl;

import static com.deepO.Release_2.utility.constant.DepositExceptionConstant.NO_DEPOSIT_FOUND_BY_DEPOSIT_NAME;
import static com.deepO.Release_2.utility.constant.UserExceptionConstant.NO_USER_FOUND_BY_USERNAME;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.deepO.Release_2.models.Commande;
import com.deepO.Release_2.models.Deposit;
import com.deepO.Release_2.models.User;
import com.deepO.Release_2.repository.OrderRepository;
import com.deepO.Release_2.service.inter.DepositService;
import com.deepO.Release_2.service.inter.OrderService;
import com.deepO.Release_2.service.inter.UserService;
import com.deepO.Release_2.utility.exception.domain.deposit.DepositNameNotFoundException;
import com.deepO.Release_2.utility.exception.domain.order.OrderAlreadyExistsByOrderId;
import com.deepO.Release_2.utility.exception.domain.order.OrderNotFoundByOrderId;
import com.deepO.Release_2.utility.exception.domain.users.UserNotFoundException;

@Service
@Transactional
public class OrderServiceImpl implements OrderService{
	
	private static String ORDER_NOTFOUNDBY_ORDERID = "Order not found By orderId";
	private static String ORDER_ALREADYEXISTS_ORDERID = "Order Already Exists By orderId";
	
	//-----------------------------------------------------------------------------
	@Autowired
	private OrderRepository orderRepository;
	 @Autowired
	 private DepositService depositService;
	 @Autowired
	 private UserService userService;
	//-----------------------------------------------------------------------------
	@Override
	public List<Commande> getAllItem() {
		
		return orderRepository.findAll();
	}

	@Override
	public Commande findOrderByOrderId(String OrderId) {
		
		return orderRepository.findOrderByOrderId(OrderId);
	}

	@Override
	public Commande addNewOrder(String userName,String depositName) throws UserNotFoundException, DepositNameNotFoundException {
		User user = ifExistUser(userName);
		Deposit deposit = ifExistDeposit(depositName);
		String generateId = ifAlresdyExistOrder(generateId());
		
		Commande order= new Commande();
		
		
		
		order.setOrderId(generateId);
		order.setOrderCreation(new Date());
		order.setUserName(user.getUsername());
		order.setDepositName(deposit.getDepositName());		
		order.setIsValidated(false);
		
		
		order.setDeposit(deposit);
		order.setUser(user);
		orderRepository.save(order);
		return order;
	}

	@Override
	public Commande updateOrder( String orderId,String userName,String depositName,boolean IsValidated) throws OrderNotFoundByOrderId, UserNotFoundException, DepositNameNotFoundException {
		Commande order = ifExistOrder(orderId);
		User user = ifExistUser(userName);
		Deposit deposit = ifExistDeposit(depositName);
		
		
		order.setUserName(user.getUsername());
		order.setDepositName(deposit.getDepositName());	
		order.setIsValidated(IsValidated);
		
		//---------
		deposit = ifIsValidatedTrue(IsValidated,deposit,user);
		//---------
		order.setDeposit(deposit);
		order.setUser(user);

		orderRepository.save(order);
		
		
		
		return order;
	}

	@Override
	public void deleteOrder(String OrderId) throws OrderNotFoundByOrderId {
		Commande order = ifExistOrder(OrderId);
		orderRepository.deleteById(order.getId());		
	}
	
	//==============================================================================
	private String generateId() {
		return RandomStringUtils.randomNumeric(14);
	}
	//------------------------------------------------------------------------------
	private Commande ifExistOrder(String OrderId) throws OrderNotFoundByOrderId
	{

		if (StringUtils.isNotEmpty(OrderId)) {
			Commande oldOrder = findOrderByOrderId(OrderId);
			if (oldOrder == null) {
				throw new OrderNotFoundByOrderId(ORDER_NOTFOUNDBY_ORDERID);
			}
			return oldOrder;
		}
		return null;		
	}
	private String ifAlresdyExistOrder(String OrderId)  
	{

		if (StringUtils.isNotEmpty(OrderId)) {
			Commande oldOrder = findOrderByOrderId(OrderId);
			if (oldOrder != null) {
				return generateId();
			}
			return OrderId;	
		}
		return null;	
	}

	// ---------------------------------------------
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
	private User ifExistUser(String username ) throws UserNotFoundException 
	{
		
		if (StringUtils.isNotEmpty(username)) {
			User CurrentUser = userService.findUserByUsername(username);
			if (CurrentUser == null) {
				throw new UserNotFoundException(NO_USER_FOUND_BY_USERNAME + username);
			}
			return CurrentUser;
		}
		return null;
	
		
	}
	
	//--------------------------------------------(IsValidated,deposit,user,order)-
	private Deposit ifIsValidatedTrue(boolean isvalidated,Deposit deposit,User user) throws OrderNotFoundByOrderId{
		
		return depositService.changeDepositToRent(isvalidated, deposit,user);
	}


}
