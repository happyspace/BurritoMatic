package com.burrito.matic.order;

import java.util.Date;

public class OrderRequest {
	private final long transactionId;
	private final Order order;
	private final Date createDate;
	private Orderable orderable;
	
	public OrderRequest(Order order, long transactionId) {
		this.order = order;
		this.transactionId = transactionId;
		createDate = new Date();
	}

	public Orderable getOrderable() {
		return orderable;
	}

	public void setOrderable(Orderable orderable) {
		this.orderable = orderable;
	}

	public long getTransactionId() {
		return transactionId;
	}

	public Order getOrder() {
		return order;
	}

	public Date getCreateDate() {
		return createDate;
	}	
}
