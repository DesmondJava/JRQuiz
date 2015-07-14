package com.jrquiz.users;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import com.jrquiz.entity.User;
import com.jrquiz.entity.enums.Permissions;

public class ChangeUserInfoForm {

	private static final String NOT_BLANK_MESSAGE = "{notBlank.message}";
	private static final String EMAIL_MESSAGE = "{email.message}";

	@NotBlank(message = ChangeUserInfoForm.NOT_BLANK_MESSAGE)
	@Email(message = ChangeUserInfoForm.EMAIL_MESSAGE)
	private String email;

	// @NotBlank(message = SignupForm.NOT_BLANK_MESSAGE)
	private String username;

	// @NotBlank(message = SignupForm.NOT_BLANK_MESSAGE)
	private String firstname;

	// @NotBlank(message = SignupForm.NOT_BLANK_MESSAGE)
	private String lastname;

	// @NotBlank(message = SignupForm.NOT_BLANK_MESSAGE)
	private String birthday;

	// @NotBlank(message = SignupForm.NOT_BLANK_MESSAGE)
	private String aboutme;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getBirthday() {
		/*
		 * try { } catch (ParseException e) { // TODO Auto-generated catch block
		 * // e.printStackTrace(); }
		 */
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getAboutme() {
		return aboutme;
	}

	public void setAboutme(String aboutme) {
		this.aboutme = aboutme;
	}

	public User createAccount() {
		// return new User(getEmail(), getPassword(),
		// Permissions.USER.getPermission(), false);
		Date bday = new Date();
		try {
			bday = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(birthday);
		} catch (ParseException e) {
		}

		return new User(getUsername(), getEmail(), "", Permissions.USER.getPermission(), false, getFirstname(), getLastname(), bday, getAboutme(), "");
	}
}
