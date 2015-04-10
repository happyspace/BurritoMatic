package com.burrito.matic.product;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.burrito.matic.exception.ProductException;
import com.burrito.matic.inventory.Ingredient;
import com.burrito.matic.inventory.IngredientType;

public abstract class BurritoProductRule implements ProductRule {
	
	/**
	 *  Calculate the cost of each ingredient. Premium ingredients have a fixed 
	 *  cost in addition to the cost of the burrito. Ingredients added on to a 
	 *  Burrito add to the cost of the burrito. Ingredients that compose the 
	 *  Burrito are covered by the cost of the burrito.
	 */
	@Override
	public BigDecimal ingredientCost(final Ingredient ingredient, boolean isAddOn) {
		if(ingredient.isPremium()) {
			return ingredient.getBaseCost();
		}
		if(isAddOn) {
			return ingredient.getAddOnCost();
		}
		// part of the base product.
		return BigDecimal.ZERO;
	}
	
	/**
	 *  Calculate the cost of a burrito based on the constraints for that burrito.
	 *  
	 * @throws ProductException 
	 * 	
	 * 	
	 */
	@Override
	public BigDecimal cost(final BurritoProduct product)  {
		BigDecimal cost = product.getBaseCost();
		Map<IngredientType, List<Ingredient>> ingredients = product.getBurritoIngredients();
		// calculate add on ingredients.
		EnumMap<IngredientType, Integer> threeValidateBurritoMap = RuleUtils.THREE_VALIDATE_BURRITO_MAP;
		Set<IngredientType> keySet = ingredients.keySet();
		Iterator<IngredientType> iterator = keySet.iterator();
		
		while (iterator.hasNext()) {
			IngredientType ingredientType = (IngredientType) iterator.next();
			List<Ingredient> list = ingredients.get(ingredientType);
			Integer count = threeValidateBurritoMap.get(ingredientType);
			for (int i = 0; i < list.size(); i++) {
				Ingredient ingredient = list.get(i);
				if(i >= count) {
					cost = cost.add(this.ingredientCost(ingredient, true));
				}
				else {
					cost = cost.add(this.ingredientCost(ingredient, false));
				}			
			}		
		}
		return cost;
	}
	
	
	/**
	 * Sets a base constraint that a burrito may not exceed a certain number 
	 * of ingredients.
	 */
	@Override
	public boolean isComplete(final BurritoProduct product) {
		int s = product.getBurritoIngredientsAsList().size();
		return s >= RuleUtils.MAX_NUMBER_OF_INGREDIENTS;
	}
}
