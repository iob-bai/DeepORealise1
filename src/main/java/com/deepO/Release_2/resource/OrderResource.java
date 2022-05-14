package com.deepO.Release_2.resource;

import static org.springframework.http.HttpStatus.OK;

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

import com.deepO.Release_2.models.Commande;
import com.deepO.Release_2.service.inter.OrderService;
import com.deepO.Release_2.utility.domain.HttpResponse;
import com.deepO.Release_2.utility.exception.domain.deposit.DepositNameNotFoundException;
import com.deepO.Release_2.utility.exception.domain.order.OrderNotFoundByOrderId;
import com.deepO.Release_2.utility.exception.domain.users.UserNotFoundException;

@RestController
@RequestMapping(path = { "/", "/order" })
public class OrderResource {
	public static final String ORDER_DELETED_SUCESSFULLY = "The Order was been Deleted sucessfully";
	// --------------------------------------------------
	@Autowired
	private OrderService orderService;

	// --------------------------------------------------
	
	
	@GetMapping("/list")
	public ResponseEntity<List<Commande>> GetOrders(){
		List<Commande> commandes = orderService.getAllItem();
		return new ResponseEntity<List<Commande>>(commandes,OK);
	}
	
	
	
	
	
	
	// --------------------------------------------------
	@PostMapping("/add")
	public ResponseEntity<Commande> addNewOrder(@RequestParam("userName") String userName,
			@RequestParam("depositName") String depositName)
			throws UserNotFoundException, DepositNameNotFoundException {

		Commande order = orderService.addNewOrder(userName, depositName);
		return new ResponseEntity<Commande>(order, OK);
	}
	@PutMapping("/update")
	public ResponseEntity<Commande> updateOrder(@RequestParam("orderId") String orderId,
											@RequestParam("userName") String userName,
											@RequestParam("depositName") String depositName,
											@RequestParam("IsValidated") String IsValidated)
			throws UserNotFoundException, DepositNameNotFoundException, OrderNotFoundByOrderId {

		Commande order = orderService.updateOrder( orderId,userName, depositName,Boolean.parseBoolean(IsValidated));
		return new ResponseEntity<Commande>(order, OK);
	}
	@DeleteMapping("/delete/{orderId}")
	//@PreAuthorize("hasAnyAuthority('user:delete')")
	public ResponseEntity<HttpResponse> deleteOrder(@PathVariable("orderId") String orderId)
			throws  OrderNotFoundByOrderId {

		orderService.deleteOrder( orderId);
		return  response(OK, ORDER_DELETED_SUCESSFULLY);
	}
	
	
	// ================================================================================================================================
	private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {

		return new ResponseEntity<>(
				new HttpResponse(httpStatus.value(), httpStatus, httpStatus.getReasonPhrase(), message), OK);
	}

}
