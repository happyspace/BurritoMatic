package com.burrito.matic;

import java.util.List;

import com.burrito.matic.exception.InventoryException;
import com.burrito.matic.exception.OrderException;
import com.burrito.matic.exception.ProductException;
import com.burrito.matic.inventory.Ingredient;
import com.burrito.matic.inventory.IngredientType;
import com.burrito.matic.order.Order;
import com.burrito.matic.product.Product;


public interface InventoryService {
	boolean initilizeInventory();
	boolean isInitilized();
	
	boolean reserveIngredient(Ingredient ingredient) throws InventoryException;	
	
	boolean restoreIngredient(Ingredient ingredient) throws InventoryException;
	
	boolean consumeIngredients(Order order) throws InventoryException, ProductException, OrderException;
	
	boolean dispenseProduct(Product product); 
	
	Ingredient getDefaultBowl() throws InventoryException;
	Ingredient getDefaultWrap() throws InventoryException;
	
	List<Ingredient> ingredientsForType(IngredientType ingredientType);
}
