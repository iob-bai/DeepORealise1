package com.deepO.Release_2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.deepO.Release_2.models.User;


@Repository
public interface UserRepository  extends JpaRepository<User, Long>{
	User findUserByUsername(String userName);
	User findUserByEmail(String email);
}
