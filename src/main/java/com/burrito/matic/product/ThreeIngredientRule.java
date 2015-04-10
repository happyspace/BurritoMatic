package com.burrito.matic.product;

import net.jcip.annotations.NotThreadSafe;

import com.burrito.matic.inventory.Ingredient;

/**
 *
 * Encapsulates the rules for a three ingredient burrito product.
 * 
 * @author ewarner
 *
 */
@NotThreadSafe
public class ThreeIngredientRule extends BurritoProductRule implements ProductRule {

	@Override
	public boolean validateProduct(BurritoProduct product) {
		return RuleUtils.isValidBorrito(product, RuleUtils.THREE_VALIDATE_BURRITO);
	}

	@Override
	public boolean isComplete(BurritoProduct product) {
		boolean isComplete = super.isComplete(product);
		if(!isComplete) {
			isComplete = RuleUtils.isCompleteThreeIngredient(product);
		}
		return isComplete;
	}

	@Override
	public boolean isBowl() {
		return false;
	}

	@Override
	public boolean isSufficient(BurritoProduct product) {
		return this.isComplete(product);
	}

	@Override
	public boolean validateIngredient(BurritoProduct product,
			Ingredient ingredient, boolean isInitial) {
		if(isInitial) {
			return RuleUtils.isValidInitialIngredient(product, ingredient);
		}
		else {
			return RuleUtils.isValidIngredient(product, ingredient, RuleUtils.THREE_VALIDATE_INGREDIENT);
		}
	}
}
