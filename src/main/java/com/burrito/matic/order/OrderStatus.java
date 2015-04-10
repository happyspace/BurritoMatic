package com.burrito.matic.order;

public enum OrderStatus {
	NEW(0),OPEN(1),COMPLETED(2),EXPIRED(3); 
	
	final int order;

	private OrderStatus(int order) {
		this.order = order;
	}
	
}
