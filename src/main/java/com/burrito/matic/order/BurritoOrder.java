package com.burrito.matic.order;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

import net.jcip.annotations.ThreadSafe;

import com.burrito.matic.exception.OrderException;
import com.burrito.matic.exception.ProductException;
import com.burrito.matic.inventory.Ingredient;
import com.burrito.matic.product.BurritoProduct;
import com.burrito.matic.product.Product;

@ThreadSafe
public class BurritoOrder implements Order {

	private AtomicReference<OrderStatus> status = new AtomicReference<OrderStatus>();
	private final long orderId;
	private final Date created;
	private AtomicReference<Date> updated = new AtomicReference<Date>();
	private AtomicReference<BigDecimal> total = new AtomicReference<BigDecimal>(BigDecimal.ZERO);
	
	private Object lock = new Object();
	
	private List<Product> products = new CopyOnWriteArrayList<Product>();
	
	private AtomicReference<BurritoProduct> currentBurritoProduct = new AtomicReference<BurritoProduct>(null);
	
	public BurritoOrder(BurritoProduct burritoProduct, long orderId) {
		this.currentBurritoProduct.set(burritoProduct); 
		this.products.add(burritoProduct);
		status.set(OrderStatus.NEW);
		this.orderId = orderId;
		created = new Date();
		updated.getAndSet(new Date());
		
	}

	@Override
	public List<Product> getProducts() {
		return Collections.unmodifiableList(products);
	}

	@Override
	public OrderStatus getOrderStatus() {
		return status.get();
	}

	@Override
	public boolean purchase() throws OrderException {
		boolean success = false;
		if(status.compareAndSet(OrderStatus.OPEN, OrderStatus.COMPLETED)) {
			success = true;
		}
		else {
			throw new OrderException("Illegal state: order is not open.");
		}
		return success;
	}

	@Override
	public BigDecimal calculateTotal() throws OrderException {
		BigDecimal total = BigDecimal.ZERO;
		
		synchronized (lock) {		
			if((status.get() == OrderStatus.COMPLETED)) {
				for (Product product : products) {
					total = total.add(product.cost());
				}
			}
			else {
				throw new OrderException("Illegal state: order is not completed."); 
			}
		}
		this.total.set(total);

		return this.total.get();
	}

	@Override
	public long getOrderId() {
		return this.orderId;
	}

	@Override
	public BurritoProduct getCurrentBurrito() {
		return currentBurritoProduct.get();
	}

	@Override
	public void addProduct(Product product) throws OrderException {
		synchronized (lock) {
			if (product instanceof BurritoProduct) {				
				if(this.currentBurritoProduct.get().isSufficient()) {
					this.currentBurritoProduct.set((BurritoProduct) product);
					this.products.add(product);					
				}
				else {
					throw new OrderException("Current burrito product must be completed.");
				}
			}
			else {
				this.products.add(product);
			}
		}
	}

	/**
	 * Add an ingredient to an order. The first ingredient changes the order 
	 * status to open. Orders must be open to add ingredients.
	 * 
	 * @see com.burrito.matic.order.Order#addIngredient(com.burrito.matic.inventory.Ingredient)
	 */
	@Override
	public boolean addIngredient(Ingredient ingredient) throws ProductException, OrderException {
		
		this.status.compareAndSet(OrderStatus.NEW, OrderStatus.OPEN);
		
		synchronized (lock) {
			if(this.status.get() == OrderStatus.OPEN) {
				return this.currentBurritoProduct.get().addIngredient(ingredient);
			}
			else {
				throw new OrderException("Order must be open to add ingredients.");
			}		
		}
	}

	@Override
	public Date getCreateDate() {
		return created;
	}

	@Override
	public Date getUpdatedDate() {
		return this.updated.get();
	}

	@Override
	public BigDecimal runningTotal() {
		BigDecimal total = BigDecimal.ZERO;
		for (Product product : products) {
			total = total.add(product.cost());
		}
		
		return total;
	}
}
