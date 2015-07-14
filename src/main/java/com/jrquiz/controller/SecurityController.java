package com.jrquiz.controller;

import java.security.Principal;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.jrquiz.entity.User;
import com.jrquiz.repository.UserRepository;
import com.jrquiz.service.EmailService;
import com.jrquiz.service.UserService;
import com.jrquiz.support.web.MessageHelper;
import com.jrquiz.users.SignupForm;

@Controller
public class SecurityController {
	// Sign UP
	private static final String SIGNUP_VIEW_NAME = "signup/signup";
	private static boolean Security = false;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserService userService;

	@Autowired
	EmailService emailService;

	// Security
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String index(Principal principal) {
		if (Security) {
			return principal != null ? "home/homeSignedIn" : "home/homeNotSignedIn";
		}

		return "redirect:hello";
	}

	// Sign in
	@RequestMapping(value = "signin")
	public String signin() {
		return "signin/signin";
	}

	// Sign UP
	@RequestMapping(value = "signup")
	public String signup(Model model) {
		model.addAttribute(new SignupForm());
		return SIGNUP_VIEW_NAME;
	}

	@RequestMapping(value = "signup", method = RequestMethod.POST)
	public String signup(@Valid @ModelAttribute SignupForm signupForm, Errors errors, RedirectAttributes ra) {
		if (errors.hasErrors()) {
			return SIGNUP_VIEW_NAME;
		}
		User user = signupForm.createAccount();
		if (userRepository.findByEmail(user.getEmail()) != null) {
			return SIGNUP_VIEW_NAME;
		}

		user = userService.addUser(user);
		// userService.signin(user);
		// see /WEB-INF/i18n/messages.properties and
		// /WEB-INF/views/homeSignedIn.html
		MessageHelper.addSuccessAttribute(ra, "signup.success");
		return "redirect:/";
	}

	@RequestMapping(value = "accessDenied")
	public String accessDenied() {
		return "error/accessDenied";
	}
}
