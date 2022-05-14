package com.deepO.Release_2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.deepO.Release_2.models.Deposit;


@Repository
public interface DepositRepository extends JpaRepository<Deposit, Long>{
	Deposit findDepositByDepositName(String depositName);
	Deposit findDepositByDepositAdress(String depositAdress);
	
}
