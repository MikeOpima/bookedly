package io.github.mikeopima.bookedly.auth;

public class EmailAlreadyUsedException extends RuntimeException {

	public EmailAlreadyUsedException() {
		super("An account with this email already exists");
	}
}
