package com.burrito.matic.inventory;

import java.math.BigDecimal;

public class BurritoIngredient implements Ingredient {

	private IngredientType ingredientType;
	private boolean isPremium;
	private BigDecimal baseCost;
	private BigDecimal addOnCost;
	private boolean isWrap;
	private boolean isBowl;
	private String name;
	private String sku;
	private boolean isOptional;
	
	public static class Builder{
		private IngredientType ingredientType;
		private String name;
		private String sku;
		
		private BigDecimal baseCost;
		private BigDecimal addOnCost;
		private boolean isPremium;
		private boolean isWrap;
		private boolean isBowl;
		private boolean isOptional;
		
		public Builder(IngredientType ingredientType, String name, String sku) {
			this.ingredientType = ingredientType;
			this.name = name;
			this.sku = sku;
		}

		public Builder getBaseCost(BigDecimal baseCost) {
			this.baseCost = baseCost;
			return this;
		}

		public Builder getAddOnCost(BigDecimal addOnCost) {
			this.addOnCost = addOnCost;
			return this;
		}

		public Builder premium(boolean isPremium) {
			this.isPremium = isPremium;
			return this;
		}

		public Builder wrap(boolean isWrap) {
			this.isWrap = isWrap;
			return this;
		}

		public Builder bowl(boolean isBowl) {
			this.isBowl = isBowl;
			return this;
		}
		
		public Builder isOptional(boolean isOptional) {
			this.isOptional = isOptional;
			return this;
		}
		
		public Ingredient build() {
			return new BurritoIngredient(this);
		}
	}
	
	private BurritoIngredient(Builder builder) {
		this.ingredientType = builder.ingredientType;
		this.isPremium = builder.isPremium;
		this.baseCost = builder.baseCost;
		this.addOnCost = builder.addOnCost;
		this.isWrap = builder.isWrap;
		this.isBowl = builder.isBowl;
		this.name = builder.name;
		this.sku = builder.sku;
		this.isOptional = builder.isOptional;
	}

	@Override
	public IngredientType getType() {
		return ingredientType;
	}

	@Override
	public boolean isPremium() {
		return isPremium;
	}

	@Override
	public BigDecimal getBaseCost() {
		return baseCost;
	}

	@Override
	public BigDecimal getAddOnCost() {
		return addOnCost;
	}

	@Override
	public boolean isWrap() {
		return isWrap;
	}

	@Override
	public boolean isBowl() {
		return isBowl;
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
	public boolean isInitial() {
		if(isBowl() || isWrap()) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isOptional() {
		return isOptional;
	}
}
