package com.deepO.Release_2.models;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonBackReference;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Commande implements Serializable{
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false, updatable = false)
	 private Long id;
	
	private String orderId;
	private Date orderCreation;
	private boolean IsValidated;
	
	private String userName;
	private String depositName;
	
	
	
	
	@ManyToOne
	@JoinColumn(name = "user_fk")
	@JsonBackReference(value = "user-Commande")
	private User user;
	   
	
	@ManyToOne
	@JoinColumn(name = "deposit_fk")
	@JsonBackReference(value = "deposit-Commande")
	private Deposit deposit;
		
}
