package com.mycompany.exceptions;

public class InvalidFileNameException extends Exception {

	private static final long serialVersionUID = -2172112016567989714L;

	public InvalidFileNameException(String m) {
		super(m);
	}

}
