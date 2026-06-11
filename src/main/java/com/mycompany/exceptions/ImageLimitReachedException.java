package com.mycompany.exceptions;

public class ImageLimitReachedException extends RuntimeException {

	private static final long serialVersionUID = 4352386198616197200L;

	public ImageLimitReachedException(String message) {
		super(message);
	}

}
