package com.deepO.Release_2.models;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity

public class Deposit implements Serializable{
	 @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false, updatable = false)
    private Long id;
	 
    private String depotId;
    private String depositName;
    private String depositAdress;
    private String depositDescription;
	private String depositCategory;
    private Date depositCreation;
    private boolean isRent;
    private boolean islocked;
    private double depositPrice;
	
    private String depotImageUrl;
    
    private String DepositUsername;
    
    
   @JsonBackReference(value="user-deposit")
	@ManyToOne
	@JoinColumn(name="user_fk")
    private User depositUser;
    
   @JsonManagedReference(value="item-deposit")
    @OneToMany(mappedBy = "itemDeposit",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private Set<Item> item;

   @JsonManagedReference(value = "deposit-Commande")
   @OneToMany(mappedBy = "deposit",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
   private Set<Commande> commande;


}
