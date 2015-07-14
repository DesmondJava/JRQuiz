package com.jrquiz.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.jrquiz.entity.User;

import java.security.Principal;
import java.util.List;

public interface UserService extends UserDetailsService {
	User addUser(User user);

	User getUser(Long id);

	User getUser(Principal principal);

	List<User> getAllUsers();

	void signin(User user);

	User confirmUser(String emailToken);

	boolean changeUserPassword(String email, String lastPassword, String newPassword);
}
