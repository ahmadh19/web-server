package edu.wlu.cs.cs325;

public class BadHTTPRequestException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public BadHTTPRequestException() {
		super("Error: Bad HTTP request...");
	}
	
}
