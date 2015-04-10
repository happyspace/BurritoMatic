package com.burrito.matic.product;

import com.burrito.matic.inventory.Ingredient;
import com.burrito.matic.inventory.IngredientType;
import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.MapContext;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * Utility class to collect expressions and functions used to validate burritos.
 * 
 * @author ewarner
 * 
 */
public class RuleUtils {
	/**
	 * Expression defining a complete two ingredient burrito.
	 */
	protected static final String TWO_ING_COMPLETE = "meat >= 1 and salsa >= 1";

	/**
	 * Expression defining a complete three ingredient burrito.
	 */
	protected static final String THREE_ING_COMPLETE = "meat >= 1 and salsa >= 1 and toppings >= 1";

	/**
	 * Expression defining a valid two ingredient burrito.
	 */
	protected static final String TWO_VALIDATE_BORRITO = "meat == 1 and salsa == 1 and toppings == 0 and base <= 2";

	/**
	 * Expression defining a valid three ingredient burrito.
	 */
	protected static final String THREE_VALIDATE_BURRITO = "meat == 1 and salsa == 1 and toppings == 1 and base <= 2";
	
	/**
	 * Map defining a valid three ingredient burrito.
	 */
	protected static final EnumMap<IngredientType, Integer> THREE_VALIDATE_BURRITO_MAP = new EnumMap<IngredientType, Integer>(IngredientType.class);

	/**
	 * Expression defining if adding an ingredient will create a valid burrito.
	 */
	protected static final String TWO_VALIDATE_INGREDIENT = "meat <= 1 and salsa <= 1 and toppings == 0 and base <= 2";

	/**
	 * Expression defining if adding an ingredient will create a valid burrito.
	 */
	protected static final String THREE_VALIDATE_INGREDIENT = "meat <= 1 and salsa <= 1 and toppings <= 1 and base <= 2";

	/**
	 * Expression defining a valid initialized burrito bowl.
	 */
	protected static final String BOWL = "bowl == 1 and wrap == 0";

	/**
	 * Expression defining a valid initialized burrito wrap.
	 */
	protected static final String WRAP = "bowl == 0 and wrap == 1";

	/**
	 * Maximum number of ingredients.
	 */
	protected static int MAX_NUMBER_OF_INGREDIENTS = 12;

	
	static {
		THREE_VALIDATE_BURRITO_MAP.put(IngredientType.BASE, 2);
		THREE_VALIDATE_BURRITO_MAP.put(IngredientType.MEAT, 1);
		THREE_VALIDATE_BURRITO_MAP.put(IngredientType.SALSA, 1);
		THREE_VALIDATE_BURRITO_MAP.put(IngredientType.TOPPINGS, 1);
	}
	
	
	/**
	 * Evaluate a burrito for completeness against a two ingredient rule.
	 * 
	 * @param product
	 * @return
	 */
	protected static boolean isCompleteTwoIngredient(BurritoProduct product) {
		Map<IngredientType, List<Ingredient>> ingredients = product
				.getBurritoIngredients();

		JexlEngine jexl = new JexlEngine();
		Expression expression = jexl
				.createExpression(RuleUtils.TWO_ING_COMPLETE);

		JexlContext jc = new MapContext();
		int x = (ingredients.get(IngredientType.MEAT) != null) ? ingredients
				.get(IngredientType.MEAT).size() : 0;
		int y = (ingredients.get(IngredientType.SALSA) != null) ? ingredients
				.get(IngredientType.SALSA).size() : 0;

		jc.set("meat", x);
		jc.set("salsa", y);

		Boolean c = (Boolean) expression.evaluate(jc);
		return c;
	}

	/**
	 * Evaluate an ingredient against a BurritoProduct to determine if the
	 * Burrito would be valid after the ingredient is added.
	 * 
	 * @param product
	 * @param ingredient
	 * @param expression
	 * @return
	 */
	protected static boolean isValidIngredient(final BurritoProduct product,
			final Ingredient ingredient, String expression) {
		boolean valid = false;
		if (!ingredient.isInitial()) {
			Map<IngredientType, List<Ingredient>> ingredients = product
					.getBurritoIngredients();

			JexlEngine jexl = new JexlEngine();
			Expression exp = jexl.createExpression(expression);

			JexlContext jc = new MapContext();
			int w = ingredientCount(ingredients, IngredientType.BASE,
					ingredient.getType());
			int x = ingredientCount(ingredients, IngredientType.MEAT,
					ingredient.getType());
			int y = ingredientCount(ingredients, IngredientType.SALSA,
					ingredient.getType());
			int z = ingredientCount(ingredients, IngredientType.TOPPINGS,
					ingredient.getType());

			jc.set("base", w);
			jc.set("meat", x);
			jc.set("salsa", y);
			jc.set("toppings", z);

			valid = (Boolean) exp.evaluate(jc);
		}
		return valid;
	}

	/**
	 * Evaluate a borrito product against an expression to determine if the 
	 * product is valid.
	 * 
	 * @param product
	 * @param expression
	 * @return
	 */
	protected static boolean isValidBorrito(final BurritoProduct product,
			String expression) {
		boolean valid = false;
		
		if(isInitilized(product)) {
			Map<IngredientType, List<Ingredient>> ingredients = product
					.getBurritoIngredients();

			JexlEngine jexl = new JexlEngine();
			Expression exp = jexl.createExpression(expression);

			JexlContext jc = new MapContext();
			int w = (ingredients.get(IngredientType.BASE) != null) ? ingredients
					.get(IngredientType.MEAT).size() : 0;
			int x = (ingredients.get(IngredientType.MEAT) != null) ? ingredients
					.get(IngredientType.MEAT).size() : 0;
			int y = (ingredients.get(IngredientType.SALSA) != null) ? ingredients
					.get(IngredientType.SALSA).size() : 0;
			int z = (ingredients.get(IngredientType.TOPPINGS) != null) ? ingredients
					.get(IngredientType.TOPPINGS).size() : 0;

			jc.set("base", w);
			jc.set("meat", x);
			jc.set("salsa", y);
			jc.set("toppings", z);

			valid = (Boolean) exp.evaluate(jc);
		}
		
		return valid;
	}
	


	/**
	 * Calculate the number of time an ingredient occurs within a list of
	 * ingredients.
	 * 
	 * @param ingredients
	 * @param source
	 * @param target
	 * @return count of ingredient
	 */
	private static int ingredientCount(
			final Map<IngredientType, List<Ingredient>> ingredients,
			final IngredientType source, final IngredientType target) {
		int count = 0;
		count = (ingredients.get(source) != null) ? ingredients.get(source)
				.size() : 0;
		if (source.equals(target)) {
			count++;
		}

		return count;
	}

	/**
	 * Evaluates a product for completeness against the three ingredient
	 * expression. A complete products can be purchased.
	 * 
	 * @param product
	 *            to be evaluated
	 * @return boolean result of the evaluation.
	 * 
	 */
	protected static boolean isCompleteThreeIngredient(
			final BurritoProduct product) {
		Map<IngredientType, List<Ingredient>> ingredients = product
				.getBurritoIngredients();

		JexlEngine jexl = new JexlEngine();
		Expression expression = jexl
				.createExpression(RuleUtils.THREE_ING_COMPLETE);

		JexlContext jc = new MapContext();
		int x = (ingredients.get(IngredientType.MEAT) != null) ? ingredients
				.get(IngredientType.MEAT).size() : 0;
		int y = (ingredients.get(IngredientType.SALSA) != null) ? ingredients
				.get(IngredientType.SALSA).size() : 0;
		int z = (ingredients.get(IngredientType.TOPPINGS) != null) ? ingredients
				.get(IngredientType.TOPPINGS).size() : 0;

		jc.set("meat", x);
		jc.set("salsa", y);
		jc.set("toppings", z);

		Boolean c = (Boolean) expression.evaluate(jc);
		return c;
	}
	
	/**
	 * Check that a borrito has been initized (has a bowl or tortilla).
	 * 
	 * @param product
	 * @return
	 */
	protected static boolean isInitilized(final BurritoProduct product) {
		Map<IngredientType, List<Ingredient>> ingredients = product
				.getBurritoIngredients();
		
		boolean valid = false;
		int bowls = 0;
		int wrapps = 0;
		List<Ingredient> list = ingredients.get(IngredientType.BASE);
		if (list != null) {
			for (Ingredient ing : list) {
				if (ing.isBowl()) {
					bowls++;
				}
				if (ing.isWrap()) {
					wrapps++;
				}
			}
		}

		JexlEngine jexl = new JexlEngine();
		String exp = product.isBowl() ? BOWL : WRAP;
		Expression expression = jexl.createExpression(exp);

		JexlContext jc = new MapContext();

		jc.set("bowl", bowls);
		jc.set("wrap", wrapps);

		valid = (Boolean) expression.evaluate(jc);
		
		return valid;
	}

	/**
	 * Based on the product constraint and the ingredient could the 
	 * ingredient serve as a base for the borrito.
	 * @param product
	 * @param ingredient
	 * @return
	 */
	protected static boolean isValidInitialIngredient(BurritoProduct product,
			Ingredient ingredient) {
		boolean valid = false;
		if (ingredient.isInitial()) {
			Map<IngredientType, List<Ingredient>> ingredients = product
					.getBurritoIngredients();

			int bowls = 0;
			int wrapps = 0;
			List<Ingredient> list = ingredients.get(IngredientType.BASE);
			if (list != null) {
				for (Ingredient ing : list) {
					if (ing.isBowl()) {
						bowls++;
					}
					if (ing.isWrap()) {
						wrapps++;
					}
				}
			}

			if (ingredient.isBowl()) {
				bowls++;
			} else {
				wrapps++;
			}

			JexlEngine jexl = new JexlEngine();
			String exp = product.isBowl() ? BOWL : WRAP;
			Expression expression = jexl.createExpression(exp);

			JexlContext jc = new MapContext();

			jc.set("bowl", bowls);
			jc.set("wrap", wrapps);

			valid = (Boolean) expression.evaluate(jc);

		}
		return valid;
	}

}
