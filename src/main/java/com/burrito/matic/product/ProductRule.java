package com.burrito.matic.product;

import com.burrito.matic.exception.ProductException;
import com.burrito.matic.inventory.Ingredient;

import java.math.BigDecimal;

/**
 * An interface that defines a product rule.
 * 
 * @author ewarner
 *
 */
public interface ProductRule {

	/**
	 * Validate that a burrito is valid.
	 * @param product
	 * @return
	 */
	public boolean validateProduct(BurritoProduct product);

	
	/**
	 * Validate that an ingredient can be added to a burrito.
	 * @param product
	 * @param ingredient
	 * @param isInitial
	 * @return
	 */
	boolean validateIngredient(BurritoProduct product, Ingredient ingredient, boolean isInitial) throws ProductException;
	
	
	/**
	 * Calculate the cost of each ingredient in the burrito. Add on ingredients
	 * generally have a separate cost.
	 * @param ingredient
	 * @param isAddOn
	 * @return
	 */
	BigDecimal ingredientCost(Ingredient ingredient, boolean isAddOn);
	
	/**
	 * The borrito as the maximum number of ingredients.
	 * @param product
	 * @return
	 */
	boolean isComplete(BurritoProduct product);
	
	/**
	 * Can the product be puchased. In the case of the A la carte burrito this
	 * would be when the product has sufficient ingredients. For most burritos
	 * this will be the same as complete.
	 * @param product
	 * @return
	 */
	boolean isSufficient(BurritoProduct product);
	
	
	/**
	 * Total cost of the product.
	 * @param product
	 * @return
	 * @throws ProductException 
	 */
	BigDecimal cost(BurritoProduct product);
	
	/**
	 * Is the product based on a bowl.
	 * @return
	 */
	boolean isBowl();
	
}
