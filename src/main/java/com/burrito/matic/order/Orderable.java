package com.burrito.matic.order;

/**
 * An interface that defines an orderable item.
 *
 * Orderables must have a name and a SKU.
 */
public interface Orderable {

    /**
     * @return product name.
     */
    public String getName();

    /**
     * @return product SKU.
     */
	public String getSKU();
}
