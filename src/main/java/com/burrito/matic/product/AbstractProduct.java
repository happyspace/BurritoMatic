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

    /**
     * Products are immutable after creation.
     * Add abstract constructor to allow assignment of variables.
     *
     * @param name the name of the product
     * @param sku the sku associated with the product
     * @param baseCost the base cost of the product with out additions
     * @param category the category of the product
     */
	public AbstractProduct(String name, String sku, BigDecimal baseCost,
			ProductCategory category) {
		this.name = name;
		this.sku = sku;
		this.baseCost = baseCost;
		this.category = category;
	}

    /**
     * Add a copy constructor.
     *
     * @param product a product to be copied.
     */
    protected AbstractProduct(Product product) {
		this.name = product.getName();
		this.sku = product.getSKU();
		this.baseCost = product.getBaseCost();
		this.category = product.getProductCategory();
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public String getName() {
		return name;
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public String getSKU() {
		return sku;
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public BigDecimal getBaseCost() {
		return baseCost;
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public ProductCategory getProductCategory() {
		return category;
	}

}
