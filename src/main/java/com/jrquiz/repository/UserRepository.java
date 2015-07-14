package com.jrquiz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.jrquiz.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	@Query("select a from User a where (a.email = ?1 and a.enabled = true)")
	User findByEmail(String email);

	@Query("select a from User a where a.emailtoken = ?1")
	User findByEmailToken(String emailtoken);
}
