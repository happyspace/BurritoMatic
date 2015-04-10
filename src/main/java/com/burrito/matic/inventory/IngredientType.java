package com.burrito.matic.inventory;


public enum IngredientType {
	BASE(1, "Base"), MEAT(2, "Meat"), SALSA(3, "Salsa"), TOPPINGS(4, "Toppings");
	
	private int choiceOrder;
	private String name;

	private IngredientType(int choiceOrder, String name) {
		this.choiceOrder = choiceOrder;
		this.name = name;
	}

	public int getChoiceOrder() {
		return choiceOrder;
	}

	public String getName() {
		return name;
	}
	
	public static IngredientType findByName(String name) {
		for(IngredientType type: values()) {
			if(type.getName().equals(name)) {
				return type;
			}
		}
		return null;
	}
}
