package com.deepO.Release_2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.deepO.Release_2.models.Item;



@Repository
public interface ItemRepository extends JpaRepository<Item, Long>{
	Item findItemByItemTitle(String title);

}
