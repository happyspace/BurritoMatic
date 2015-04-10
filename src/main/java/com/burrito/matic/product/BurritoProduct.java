package com.burrito.matic.product;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.jcip.annotations.NotThreadSafe;

import com.burrito.matic.exception.ProductException;
import com.burrito.matic.inventory.Ingredient;
import com.burrito.matic.inventory.IngredientType;


/**
 * Represents a burrito. Each burrito product receives constraints represented 
 * by a product rule. This class is not thread safe.
 * 
 * @author ewarner
 *
 */

@NotThreadSafe
public class BurritoProduct extends AbstractProduct {

	/**
	 * The rule that defines the constraints for this burrito.
	 */
	private final ProductRule productRule;
	
	/**
	 * Collection of ingredients that make up this burrito. Supports A la chart
	 * products.
	 */
	private final EnumMap<IngredientType, List<Ingredient>> ingredients;

	/**
	 * Constructs a burrito instance. 
	 * 
	 * @param name - the name of the burrito.
	 * @param sku - the product SKU for this burrito.
	 * @param productCategory - category for this product = burrito.
	 * @param productRule - the constraints for this product.
	 * @param baseCost - base cost for this product.
	 */
	public BurritoProduct(String name, 
			final String sku,
			final ProductCategory productCategory,
			final ProductRule productRule, 
			final BigDecimal baseCost ) {
		
		super(name, sku, baseCost, productCategory);
		
		this.productRule = productRule;
		this.ingredients = new EnumMap<IngredientType, List<Ingredient>>(IngredientType.class);
	}
	
	public BurritoProduct(BurritoProduct burritoProduct) {
		super(burritoProduct.getName(), burritoProduct.getSKU(), burritoProduct.getBaseCost(), burritoProduct.getProductCategory());
		
		this.productRule = burritoProduct.productRule;
		this.ingredients = new EnumMap<IngredientType, List<Ingredient>>(IngredientType.class);
	}
	
	/**
	 * Returns an unmodifiableMap of current ingredients.
	 * @return
	 */
	public Map<IngredientType, List<Ingredient>> getBurritoIngredients() {
		return Collections.unmodifiableMap(ingredients);
	}
	
	
	/**
	 * Initializes this burrito with either a bowl or a wrap (something to put 
	 * ingredients in). Validates that the ingredient has an attribute of 
	 * isInitial = true.
	 * 
	 * @param ingredient - an initial ingredient. 
	 * @return
	 * @throws ProductException - if ingredient is not initial. 
	 */
	public boolean initilizeProduct(Ingredient ingredient) throws ProductException {
		boolean initilized = false;
		if(productRule.validateIngredient(this, ingredient, true)) {
			this.add(ingredient);
			initilized = true;
		}
		return initilized;
	}

	/**
	 * Returns an unmodifiable list of the ingredient that make up this burrito.
	 * @return
	 */
	public List<Ingredient> getBurritoIngredientsAsList() {
		ArrayList<Ingredient> ing = new ArrayList<Ingredient>();
		
		Set<Entry<IngredientType, List<Ingredient>>> entrySet = ingredients.entrySet();
		for (Entry<IngredientType, List<Ingredient>> entry : entrySet) {
			List<Ingredient> list = entry.getValue();
			for (Ingredient ingredient : list) {
				ing.add(ingredient);
			}
		}
		return Collections.unmodifiableList(ing);
	}
	
	/**
	 * Returns the number of ingredients held by this burrito.
	 * @return
	 */
	public int size() {
		return this.getBurritoIngredientsAsList().size();
	}
	
	/**
	 * Adds an ingredient to this burrito. Will throw an exception if the 
	 * ingredient would create an invalid burrito.
	 * 
	 * @param ingredient - the ingredient to be added. 
	 * @return
	 * @throws ProductException - if the ingredient would create an invalid 
	 * burrito. 
	 */
	public boolean addIngredient(Ingredient ingredient) throws ProductException {
		boolean added = false;
		
		if(productRule.validateIngredient(this, ingredient, false)) {
			added = this.add(ingredient);
		}
		else {
			throw new ProductException("Ingredient is not valid of product or product state.");
		}
		return added;
	}
	
	/**
	 * Utility method to populate the ingredient map.
	 * 
	 * @param ingredient
	 * @return
	 */
	protected boolean add(Ingredient ingredient) {
		boolean added = false;
		IngredientType type = ingredient.getType();
		List<Ingredient> list = ingredients.get(type);
		if (list != null) {
			added = list.add(ingredient);
		}
		else {
			list = new ArrayList<Ingredient>();
			added = list.add(ingredient);
			ingredients.put(ingredient.getType(), list);
			added = true;
		}
		return added;
	}
	
	/**
	 * Return if the burrito is complete. A complete burrito may be purchased.
	 * A complete burrito may not accept any additional ingredients. 
	 * @return
	 */
	public boolean isComplete() {
		return productRule.isComplete(this);
	}
	
	/**
	 * Return if the burrito is sufficient. A sufficient burrito may be purchased.
	 * A sufficient burrito may accept additional ingredients. 
	 * @return
	 */
	public boolean isSufficient() {
		return productRule.isSufficient(this);
	}
	
	public BigDecimal cost() {
		return productRule.cost(this);
	}
	
	/**
	 * Returns if the product is bowl based. 
	 * @return
	 */
	public boolean isBowl() {
		return productRule.isBowl();
	}
	
	/**
	 * Returns the product rule for this borrito. 
	 * @return
	 */
	public ProductRule getProductRule() {
		return productRule;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getName()).append(" with: ");
		int length = sb.length();
		Set<Entry<IngredientType, List<Ingredient>>> entrySet = ingredients.entrySet();
		for (Entry<IngredientType, List<Ingredient>> entry : entrySet) {
			if(entry.getValue() != null) {
				List<Ingredient> list = entry.getValue();
				for (Ingredient ingredient : list) {
					if(sb.length() > length) {
						sb.append(", ");
					}
					sb.append(ingredient.getName());
				}
			}
		}
		
		sb.append(" - " + this.cost());
		
		if(isComplete()) {
			sb.append(" Complete ");
		}
		
		if(isSufficient()) {
			sb.append(" + ");
		}
		
		return sb.toString();
	}
}
