package com.burrito.matic.exception;

public class InventoryException extends Exception {

	public InventoryException() {
		super();
	}

	public InventoryException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public InventoryException(String message, Throwable cause) {
		super(message, cause);
	}

	public InventoryException(String message) {
		super(message);
	}

	public InventoryException(Throwable cause) {
		super(cause);
	}

}
