package com.burrito.matic.product;

import java.math.BigDecimal;

/**
 * A type that represents a dessert for example a cookie or a brownie.
 * 
 * @author ewarner
 *
 */
public class DessertProduct extends AbstractProduct {

	/**
	 * Create an instance of a dessert product.
	 * @param name
	 * @param sku
	 * @param baseCost
	 * @param category
	 */
	public DessertProduct(String name, String sku, BigDecimal baseCost,
			ProductCategory category) {
		super(name, sku, baseCost, category);
	}

	@Override
	public BigDecimal cost() {
		return super.getBaseCost();
	}
}
