package com.burrito.matic.product;

import java.math.BigDecimal;


public class SodaProduct extends AbstractProduct  {

	/**
	 * Create a Soda Product.
	 * 
	 * @param name
	 * @param sku
	 * @param baseCost
	 * @param category
	 */
	public SodaProduct(String name, String sku, BigDecimal baseCost,
			ProductCategory category) {
		super(name, sku, baseCost, category);
	}
	
	@Override
	public BigDecimal cost() {
		return super.getBaseCost();
	}
}
