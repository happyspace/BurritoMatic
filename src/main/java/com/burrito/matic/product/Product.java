package com.burrito.matic.product;

import com.burrito.matic.order.Orderable;

import java.math.BigDecimal;

/**
 * An interface which defines the basic attributes of a product.
 * 
 * @author ewarner
 *
 */
public interface Product extends Orderable {
    /**
     * @return the base cost of a product.
     */
    BigDecimal getBaseCost();

    /**
     * @return the product category.
     */
	ProductCategory getProductCategory();

    /**
     * @return the actual cost of a product.
     *
     * For example a product may have additional items
     * that increase the cost.
     */
	BigDecimal cost();
}
