package com.burrito.matic.inventory;

import com.burrito.matic.order.Orderable;

import java.math.BigDecimal;

public interface Ingredient extends Orderable {
	public IngredientType getType();
	public boolean isPremium();
	public BigDecimal getBaseCost();
	public BigDecimal getAddOnCost();
	public boolean isWrap();
	public boolean isBowl();
	public boolean isInitial();
	public boolean isOptional();
}
