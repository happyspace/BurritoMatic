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
	protected static final String TWO_VALIDATE_BURRITO = "meat == 1 and salsa == 1 and toppings == 0 and base <= 2";

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
	 * @param product the burrito to be evaluated
	 * @return if the burrito is complete
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

		return (Boolean) expression.evaluate(jc);
	}

	/**
	 * Evaluate an ingredient against a BurritoProduct to determine if the
	 * Burrito would be valid after the ingredient is added.
	 * 
	 * @param product the product to be evaluated
	 * @param ingredient the ingredient to be added
	 * @param expression the expression defining a valid burrito
	 * @return if the ingredient is valid
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
	 * Evaluate a burrito product against an expression to determine if the
	 * product is valid.
	 * 
	 * @param product the product to be evaluated
	 * @param expression the expression to be used for evaluation
	 * @return is the product is valid
	 */
	protected static boolean isValidBurrito(final BurritoProduct product,
			String expression) {
		boolean valid = false;
		
		if(isInitialized(product)) {
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
     * Support validation for addition ingredients.
     * Count number of ingredients, add one if the target ingredient is the same as the source ingredient.
     *
	 * @param ingredients the map of ingredients in a product
	 * @param source an ingredient type
	 * @param target the ingredient to be added
	 * @return count of ingredients
	 */
	private static int ingredientCount(
			final Map<IngredientType, List<Ingredient>> ingredients,
			final IngredientType source, final IngredientType target) {

		int count = (ingredients.get(source) != null) ? ingredients.get(source)
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

		return (Boolean) expression.evaluate(jc);
	}
	
	/**
	 * Check that a burrito has been initialized (has a bowl or tortilla).
	 * 
	 * @param product product to be evaluated
	 * @return if the product has been initialized
	 */
	protected static boolean isInitialized(final BurritoProduct product) {
		Map<IngredientType, List<Ingredient>> ingredients = product
				.getBurritoIngredients();

		int bowls = 0;
		int wraps = 0;
		List<Ingredient> list = ingredients.get(IngredientType.BASE);
		if (list != null) {
			for (Ingredient ing : list) {
				if (ing.isBowl()) {
					bowls++;
				}
				if (ing.isWrap()) {
					wraps++;
				}
			}
		}

		JexlEngine jexl = new JexlEngine();
		String exp = product.isBowl() ? BOWL : WRAP;
		Expression expression = jexl.createExpression(exp);

		JexlContext jc = new MapContext();

		jc.set("bowl", bowls);
		jc.set("wrap", wraps);
		
		return (Boolean) expression.evaluate(jc);
	}

	/**
	 * Based on the product constraint and the ingredient could the 
	 * ingredient serve as a base for the burrito.
	 * @param product product to be evaluated
	 * @param ingredient ingredient to be evaluated
	 * @return if the ingredient can be an initial ingredient
	 */
	protected static boolean isValidInitialIngredient(BurritoProduct product,
			Ingredient ingredient) {
		boolean valid = false;
		if (ingredient.isInitial()) {
			Map<IngredientType, List<Ingredient>> ingredients = product
					.getBurritoIngredients();

			int bowls = 0;
			int wraps = 0;
			List<Ingredient> list = ingredients.get(IngredientType.BASE);
			if (list != null) {
				for (Ingredient ing : list) {
					if (ing.isBowl()) {
						bowls++;
					}
					if (ing.isWrap()) {
						wraps++;
					}
				}
			}

			if (ingredient.isBowl()) {
				bowls++;
			} else {
				wraps++;
			}

			JexlEngine jexl = new JexlEngine();
			String exp = product.isBowl() ? BOWL : WRAP;
			Expression expression = jexl.createExpression(exp);

			JexlContext jc = new MapContext();

			jc.set("bowl", bowls);
			jc.set("wrap", wraps);

			valid = (Boolean) expression.evaluate(jc);

		}
		return valid;
	}

}
