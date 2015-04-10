package com.burrito.matic.order;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class OrderUpdate {
	private final OrderRequest orderRequest;
	private final Order order;
	private AtomicBoolean success = new AtomicBoolean(true);
	private AtomicReference<Exception> exception = new AtomicReference<Exception>();
	private final OrderRequest nexOrderRequest;
	
	public OrderUpdate(Order order, OrderRequest orderRequest, long nextTransactionId) {
		this.orderRequest = orderRequest;
		this.order = order;

		nexOrderRequest = new OrderRequest(order , nextTransactionId);
	}

	public OrderRequest getOrderRequest() {
		return orderRequest;
	}

	public Order getOrder() {
		return order;
	}

	public AtomicBoolean getSuccess() {
		return success;
	}

	public Exception getException() {
		return this.exception.get();
	}

	public void setException(Exception exception) {
		if(this.exception.compareAndSet(null, exception)) {
			success.set(false);
		}
	}

	public OrderRequest getNexOrderRequest() {
		return nexOrderRequest;
	}
}
