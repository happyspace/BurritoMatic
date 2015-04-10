package com.burrito.matic.product;

import com.burrito.matic.order.Orderable;

/**
 * Defines a type of product, for example Borrito, Soda or Dessert. 
 * @author ewarner
 *
 */
public class ProductCategory implements Orderable {

	/**
	 * Product category name.
	 */
	private final String name;
	/**
	 * Product category sku identifier.
	 */
	private final String sku;
	
	/**
	 * Create a product category.
	 * @param name
	 * @param sku
	 */
	public ProductCategory(String name, String sku) {
		this.name = name;
		this.sku = sku;
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
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProductCategory other = (ProductCategory) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (sku == null) {
			if (other.sku != null)
				return false;
		} else if (!sku.equals(other.sku))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((sku == null) ? 0 : sku.hashCode());
		return result;
	}
	
	
	
	
}
