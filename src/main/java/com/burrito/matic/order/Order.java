package com.burrito.matic.order;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.burrito.matic.exception.OrderException;
import com.burrito.matic.exception.ProductException;
import com.burrito.matic.inventory.Ingredient;
import com.burrito.matic.product.BurritoProduct;
import com.burrito.matic.product.Product;

public interface Order {
	
	public List<Product> getProducts();
	public long getOrderId();
	public void addProduct(Product product) throws OrderException ;
	public boolean addIngredient(Ingredient ingredient) throws ProductException, OrderException;
	public BurritoProduct getCurrentBurrito();
	
	public OrderStatus getOrderStatus();
	public boolean purchase() throws OrderException;
	
	public BigDecimal calculateTotal() throws OrderException;
	public BigDecimal runningTotal();
	public Date getCreateDate();
	public Date getUpdatedDate();
}
