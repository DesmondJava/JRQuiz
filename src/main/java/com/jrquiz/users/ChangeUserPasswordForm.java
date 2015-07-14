package com.jrquiz.users;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

public class ChangeUserPasswordForm {

	private static final String NOT_BLANK_MESSAGE = "{notBlank.message}";
	private static final String EMAIL_MESSAGE = "{email.message}";

	@NotBlank(message = ChangeUserPasswordForm.NOT_BLANK_MESSAGE)
	@Email(message = ChangeUserPasswordForm.EMAIL_MESSAGE)
	private String email;

	// @NotBlank(message = SignupForm.NOT_BLANK_MESSAGE)
	private String lastPassword;

	// @NotBlank(message = SignupForm.NOT_BLANK_MESSAGE)
	private String newPassword;

	// @NotBlank(message = SignupForm.NOT_BLANK_MESSAGE)
	private String confirmPassword;

	public String getLastPassword() {
		return lastPassword;
	}

	public void setLastPassword(String lastPassword) {
		this.lastPassword = lastPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
