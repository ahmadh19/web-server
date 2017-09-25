package edu.wlu.cs.cs325;

public class FileFormatException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public FileFormatException() {
		super("ERROR! Unsupported file type!");
	}
	
}
