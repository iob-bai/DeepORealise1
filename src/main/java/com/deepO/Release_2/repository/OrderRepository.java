package com.deepO.Release_2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.deepO.Release_2.models.Commande;


@Repository
public interface OrderRepository extends JpaRepository<Commande, Long>{
	
	Commande findOrderByOrderId(String orderId);
	
}
