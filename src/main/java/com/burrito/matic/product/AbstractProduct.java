package com.burrito.matic.product;

import java.math.BigDecimal;

/**
 * An abstract product to enforce the creation of valid products. products must 
 * have a name, sku, base cost and a product category.
 * 
 * @author ewarner
 *
 */
public abstract class AbstractProduct implements Product {

	private final String name;
	private final String  sku;
	private final BigDecimal  baseCost;
	private final ProductCategory category;
	
	
	public AbstractProduct(String name, String sku, BigDecimal baseCost,
			ProductCategory category) {
		this.name = name;
		this.sku = sku;
		this.baseCost = baseCost;
		this.category = category;
	}
	
	public AbstractProduct(Product product) {
		this.name = product.getName();
		this.sku = product.getSKU();
		this.baseCost = product.getBaseCost();
		this.category = product.getProductCategory();
	} 

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getSKU() {
		return sku;
	}

	@Override
	public BigDecimal getBaseCost() {
		return baseCost;
	}

	@Override
	public ProductCategory getProductCategory() {
		return category;
	}

}
