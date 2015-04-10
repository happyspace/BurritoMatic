package com.burrito.matic;

import java.util.Map;

import com.burrito.matic.order.OrderRequest;
import com.burrito.matic.order.OrderUpdate;
import com.burrito.matic.product.BurritoProduct;
import com.burrito.matic.product.DessertProduct;
import com.burrito.matic.product.ProductCategory;
import com.burrito.matic.product.SodaProduct;


/**
 * An interface defining the services provided by an order service.
 * @author ewarner
 *
 */
public interface OrderService {
	
	/**
	 * Create an order with a burrito product. 
	 * @param product
	 * @return
	 */
	public OrderUpdate createOrder(BurritoProduct product);
	public OrderUpdate addIngredient(OrderRequest orderRequest);
	public OrderUpdate addProduct(OrderRequest orderRequest);
	public OrderUpdate purchaseOrder(OrderRequest orderRequest);
	
	public Map<String, BurritoProduct> getBurritoProducts();
	public Map<String, SodaProduct> getSodaProducts();
	public Map<String, DessertProduct> getDesertProducts();
	public Map<String, ProductCategory> getProductCategories();
	public ProductCategory getProductCategory(String name);
	public boolean initilizeProducts();
	public boolean isInitilized();
}