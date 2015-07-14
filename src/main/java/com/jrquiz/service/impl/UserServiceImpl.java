package com.jrquiz.service.impl;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.jrquiz.entity.User;
import com.jrquiz.entity.enums.Permissions;
import com.jrquiz.repository.UserRepository;
import com.jrquiz.service.EmailService;
import com.jrquiz.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	// @Autowired
	private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	@Inject
	private EmailService emailService;

	@Override
	@Transactional
	@Caching(put = { @CachePut(value = "users", key = "#user.id") })
	public User addUser(User user) {
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.setEmailtoken(passwordEncoder.encode(user.getEmail()));

		userRepository.saveAndFlush(user);
		emailService.sendRegistrationEmail(user);
		return user;
	}

	@Override
	@Cacheable(value = "users")
	public User getUser(Long id) {
		return userRepository.findOne(id);
	}

	@Override
	public User getUser(Principal principal) {
		User user = null;
		if(principal != null)
		    user = userRepository.findByEmail(principal.getName());
		if (user == null)
			return getUser(1L); //TODO Заглушка на время тестирования, потом тут будет ексепшн
		else
			return user;
	}

	@PostConstruct
	protected void initialize() {
		User user = userRepository.findByEmail("user");
		if (user == null) {
			userRepository.save(new User("user", passwordEncoder.encode("user"), Permissions.USER.getPermission(), true));
		}
		user = userRepository.findByEmail("admin");
		if (user == null) {
			userRepository.save(new User("admin", passwordEncoder.encode("admin"), Permissions.ADMINISTRATOR.getPermission(), true));
		}
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByEmail(username);
		if (user == null) {
			throw new UsernameNotFoundException("user not found");
		}
		return createUser(user);
	}

	@Override
	public void signin(User user) {
		SecurityContextHolder.getContext().setAuthentication(authenticate(user));
	}

	private Authentication authenticate(User user) {
		return new UsernamePasswordAuthenticationToken(createUser(user), null, createAuthority(user));
	}

	private org.springframework.security.core.userdetails.User createUser(User user) {
		return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), createAuthority(user));
	}

	public static String removeChar(String s, char c) {
		String r = "";
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) != c)
				r += s.charAt(i);
		}
		return r;
	}

	private List<GrantedAuthority> createAuthority(User user) {
		String[] list = user.getRole().split(",");
		List<GrantedAuthority> arrList = new ArrayList<GrantedAuthority>();
		for (String s : list) {
			arrList.add(new SimpleGrantedAuthority(removeChar(s, ' ')));
		}
		return arrList;
	}

	@Transactional
	@Caching(put = { @CachePut(value = "users", key = "#user.id") })
	public User confirmUser(String emailToken) {
		User user = null;
		try {
			user = userRepository.findByEmailToken(emailToken);
		} catch (Exception e) {
		}
		if (user != null) {
			user.setEnabled(true);
			userRepository.saveAndFlush(user);
		}
		return user;
	}

	@Override
	public boolean changeUserPassword(String email, String lastPassword, String newPassword) {
		User user = userRepository.findByEmail(email);
		if (user == null) {
			return false;
		}

		if (!passwordEncoder.matches(lastPassword, user.getPassword())) {
			return false;
		}

		user.setPassword(passwordEncoder.encode(newPassword));
		userRepository.save(user);

		return true;

	}

	@Override
	public List<User> getAllUsers() {
		return userRepository.findAll();
	}
}
