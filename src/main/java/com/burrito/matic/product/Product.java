package com.burrito.matic.product;

import java.math.BigDecimal;

import com.burrito.matic.order.Orderable;

/**
 * An interface which defines the basic attributes of a product.
 * 
 * @author ewarner
 *
 */
public interface Product extends Orderable {
	BigDecimal getBaseCost();
	ProductCategory getProductCategory();
	BigDecimal cost();
}
