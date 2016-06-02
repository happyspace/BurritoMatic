package com.burrito.matic.product;

import com.burrito.matic.inventory.Ingredient;
import net.jcip.annotations.NotThreadSafe;

/**
 * Encapsulates the rules for a two ingredient burrito product.
 * 
 * @author ewarner
 *
 */
@NotThreadSafe
public class TwoIngredientRule extends BurritoProductRule implements ProductRule {
	

	@Override
	public boolean validateProduct(BurritoProduct product) {
		return RuleUtils.isValidBurrito(product, RuleUtils.TWO_VALIDATE_BURRITO);
	}


	@Override
	public boolean isComplete(BurritoProduct product) {
		return 	RuleUtils.isCompleteTwoIngredient(product);
	}


	@Override
	public boolean isBowl() {
		return false;
	}


	@Override
	public boolean isSufficient(BurritoProduct product) {
		return isComplete(product);
	}


	@Override
	public boolean validateIngredient(final BurritoProduct product,
			final Ingredient ingredient, boolean isInitial) {
		if(isInitial) {
			return RuleUtils.isValidInitialIngredient(product, ingredient);
		}
		else {
			return RuleUtils.isValidIngredient(product, ingredient, RuleUtils.TWO_VALIDATE_INGREDIENT);
		}
	}
}
