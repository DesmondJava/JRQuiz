package com.jrquiz.entity.enums;

public enum Permissions {
	USER("ROLE_USER"), MODERATOR("ROLE_USER, ROLE_MODERATOR"), ADMINISTRATOR("ROLE_USER, ROLE_MODERATOR, ROLE_ADMIN");

	private String permission;

	private Permissions(String permission) {
		this.permission = permission;
	}

	public String getPermission() {
		return permission;
	}

}
