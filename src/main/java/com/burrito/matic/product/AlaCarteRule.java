package com.burrito.matic.product;

import java.math.BigDecimal;
import java.util.List;

import net.jcip.annotations.NotThreadSafe;

import com.burrito.matic.inventory.Ingredient;

/**
 * Encapsulates the rules for an A la CarteRule burrito product.
 * 
 * @author ewarner
 *
 */
@NotThreadSafe
public class AlaCarteRule extends BurritoProductRule implements ProductRule {

	@Override
	public boolean validateProduct(BurritoProduct product) {
		boolean valid = false;		
		// has been initialized
		if(RuleUtils.isInitilized(product)) {
			// is sufficient
			if(this.isSufficient(product)) {
				int s = product.getBurritoIngredientsAsList().size();
				if(s <= RuleUtils.MAX_NUMBER_OF_INGREDIENTS) {
					valid = true;
				}
			}
		}		
		
		return valid;
	}
	
	@Override
	public boolean isComplete(BurritoProduct product) {
		return super.isComplete(product);
	}

	@Override
	public boolean isBowl() {
		return false;
	}

	@Override
	public boolean isSufficient(BurritoProduct product) {
		return RuleUtils.isCompleteThreeIngredient(product);
	}

	@Override
	public boolean validateIngredient(BurritoProduct product,
			Ingredient ingredient, boolean isInitial) {
		boolean valid = false;
		if(isInitial) {
			valid =  RuleUtils.isValidInitialIngredient(product, ingredient);
		}
		else {
			if(!ingredient.isInitial() && ! this.isComplete(product)) {
				valid = true;
			}
		}
		return valid;
	}
}
