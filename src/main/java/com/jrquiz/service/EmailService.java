package com.jrquiz.service;

import com.jrquiz.entity.User;

public interface EmailService {
	public boolean sendRegistrationEmail(User user);

	public boolean sendRemaindPasswordMail(User user);
}
