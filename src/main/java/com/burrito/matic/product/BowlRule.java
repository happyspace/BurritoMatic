package com.burrito.matic.product;

import com.burrito.matic.inventory.Ingredient;

/**
 * Encapsulates the rules for a burrito in a bowl product.
 * 
 * @author ewarner
 *
 */
public class BowlRule extends BurritoProductRule implements ProductRule {

	
	@Override
	public boolean isComplete(BurritoProduct product) {
		return RuleUtils.isCompleteTwoIngredient(product);
	}

	@Override
	public boolean isBowl() {
		return true;
	}

	@Override
	public boolean isSufficient(BurritoProduct product) {
		return isComplete(product);
	}

	@Override
	public boolean validateIngredient(BurritoProduct product,
			Ingredient ingredient, boolean isInitial) {
		if(isInitial) {
			return RuleUtils.isValidInitialIngredient(product, ingredient);
		}
		else {
			return RuleUtils.isValidIngredient(product, ingredient, RuleUtils.TWO_VALIDATE_INGREDIENT);
		}
	}

	@Override
	public boolean validateProduct(BurritoProduct product) {
		return RuleUtils.isValidBurrito(product, RuleUtils.TWO_VALIDATE_BURRITO);
	}
}
