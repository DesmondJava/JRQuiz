package com.jrquiz.controller;

import com.jrquiz.entity.User;
import com.jrquiz.entity.enums.Permissions;
import com.jrquiz.repository.UserRepository;
import com.jrquiz.service.UserService;
import com.jrquiz.support.web.MessageHelper;
import com.jrquiz.users.ChangeUserInfoForm;
import com.jrquiz.users.ChangeUserPasswordForm;
import com.jrquiz.users.ChangeUserPermitionsForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.security.Principal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Controller
@RequestMapping(value = UsersController.PATH)
public class UsersController extends TemplateController{

	protected static final String PATH = "/administration/users";
	@Autowired
	private UserService userService;

	@Autowired
	private UserRepository userRepository;

	@RequestMapping(method = RequestMethod.GET)
	public String showUserList(ModelMap model) {
		addHeaderAttributes(model);
		model.addAttribute("allUsers", userService.getAllUsers());
		return "users/users";
	}

	@RequestMapping(value = "/edit")
	public String editUser(@RequestParam(value = "id", required = true) User user, ModelMap model) {

		//User user = userRepository.findByEmail(id);
		if (user != null) {
			ChangeUserPermitionsForm changeUserPermitionsForm = new ChangeUserPermitionsForm();
			changeUserPermitionsForm.setEmail(user.getEmail());
			changeUserPermitionsForm.setEnable(user.isEnabled());
			changeUserPermitionsForm.setUsername(user.getUserName());

			if (user.getRole().contains("ROLE_ADMIN"))
				changeUserPermitionsForm.setPermissions(Permissions.ADMINISTRATOR);
			else if (user.getRole().contains("ROLE_MODERATOR"))
				changeUserPermitionsForm.setPermissions(Permissions.MODERATOR);
			else
				changeUserPermitionsForm.setPermissions(Permissions.USER);

			model.addAttribute(changeUserPermitionsForm);

			return "users/editUser";
		}
		// model.addAttribute("user", user);
		return "users/userList";
	}

	@RequestMapping(value = "/updateUserPermitions", method = RequestMethod.POST)
	public String updateUserPermitions(@Valid @ModelAttribute ChangeUserPermitionsForm changeUserPermitionsForm, Errors errors, RedirectAttributes ra) {

		System.out.println(changeUserPermitionsForm.isEnable());
		System.out.println(changeUserPermitionsForm.getUsername());
		System.out.println(changeUserPermitionsForm.getPermissions());
		if (errors.hasErrors()) {
			return "users/editUser";
		}
		User user = userRepository.findByEmail(changeUserPermitionsForm.getEmail());
		user.setEnabled(changeUserPermitionsForm.isEnable());
		user.setRole(changeUserPermitionsForm.getPermissions().getPermission());
		user.setUpdatetime(new Timestamp(new Date().getTime()));

		userRepository.save(user);
		MessageHelper.addSuccessAttribute(ra, "signup.success");

		MessageHelper.addSuccessAttribute(ra, "updateUserPermitions.success");
		return "redirect:users/userList";
	}




	@RequestMapping(value = "confirm")
	public String confirm(@RequestParam(value = "id", required = true) String id) {

		User user = userService.confirmUser(id);
		if (user == null) {
			return "signup/wrongRegistration";
		}

		// model.addAttribute("user", user);
		return "signup/confirmRegistration";
	}

	// @Secured("ROLE_ADMIN")
	@RequestMapping(value = "personalAccount")
	public String personalAccount() {
		return "users/personalAccount";
	}

	@RequestMapping(value = "changeUserInfo")
	public String changeUserInfo(Principal principal, Model model) {
		User user = userRepository.findByEmail(principal.getName());
		if (user == null)
			return "/";
		ChangeUserInfoForm changeUserInfoForm = new ChangeUserInfoForm();
		changeUserInfoForm.setEmail(user.getEmail());
		changeUserInfoForm.setUsername(user.getUserName());
		changeUserInfoForm.setFirstname(user.getFirstname());
		changeUserInfoForm.setLastname(user.getLastname());
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = user.getBirthday();
		String datestring = "";
		if (date != null)
			datestring = dateFormat.format(date);
		changeUserInfoForm.setBirthday(datestring);
		changeUserInfoForm.setAboutme(user.getAboutme());

		model.addAttribute(changeUserInfoForm);
		return "users/changeUserInfo";
	}

	@RequestMapping(value = "updateUserInfo", method = RequestMethod.POST)
	public String updateUserInfo(@Valid @ModelAttribute ChangeUserInfoForm changeUserInfoForm, Errors errors, RedirectAttributes ra) {
		if (errors.hasErrors()) {
			return "users/changeUserInfo";
		}
		User user = userRepository.findByEmail(changeUserInfoForm.getEmail());
		user.setFirstname(changeUserInfoForm.getFirstname());
		user.setLastname(changeUserInfoForm.getLastname());
		Date bday = new Date();
		try {
			bday = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(changeUserInfoForm.getBirthday());
		} catch (ParseException e) {
			bday = null;
		}
		user.setBirthday(bday);
		user.setAboutme(changeUserInfoForm.getAboutme());

		user = userRepository.save(user);
		MessageHelper.addSuccessAttribute(ra, "signup.success");
		return "redirect:personalAccount";
	}

	@RequestMapping(value = "changeUserPassword")
	public String changeUserPassword(Principal principal, Model model) {
		User user = userRepository.findByEmail(principal.getName());
		if (user == null)
			return "/";

		ChangeUserPasswordForm changeUserPasswordForm = new ChangeUserPasswordForm();
		changeUserPasswordForm.setEmail(user.getEmail());

		model.addAttribute(changeUserPasswordForm);
		return "users/changeUserPassword";
	}

	@RequestMapping(value = "updateUserPassword", method = RequestMethod.POST)
	public String signup(@Valid @ModelAttribute ChangeUserPasswordForm changeUserPasswordForm, Errors errors, RedirectAttributes ra) {
		if (errors.hasErrors()) {
			return "users/changeUserPassword";
		}
		if (!changeUserPasswordForm.getNewPassword().equals(changeUserPasswordForm.getConfirmPassword())) {
			return "users/changeUserPassword";
		}
		if (!userService.changeUserPassword(changeUserPasswordForm.getEmail(), changeUserPasswordForm.getLastPassword(), changeUserPasswordForm.getNewPassword())) {
			return "users/changeUserPassword";
		}

		MessageHelper.addSuccessAttribute(ra, "signup.success");
		return "redirect:personalAccount";
	}



}
