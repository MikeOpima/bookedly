package io.github.mikeopima.bookedly.auth;

public class InvalidCredentialsException extends RuntimeException {

	public InvalidCredentialsException() {
		// Same message whether the email or the password was wrong,
		// so nobody can use log-in to find out which emails have accounts.
		super("Invalid email or password");
	}
}
