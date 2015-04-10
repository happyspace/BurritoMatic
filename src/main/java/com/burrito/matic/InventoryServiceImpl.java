package com.burrito.matic;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import com.burrito.matic.exception.InventoryException;
import com.burrito.matic.exception.OrderException;
import com.burrito.matic.exception.ProductException;
import com.burrito.matic.inventory.BurritoIngredient;
import com.burrito.matic.inventory.Ingredient;
import com.burrito.matic.inventory.IngredientStore;
import com.burrito.matic.inventory.IngredientType;
import com.burrito.matic.order.Order;
import com.burrito.matic.order.OrderStatus;
import com.burrito.matic.product.BurritoProduct;
import com.burrito.matic.product.Product;

public enum InventoryServiceImpl implements InventoryService {
	INSTANCE;

	private EnumMap<IngredientType, LinkedHashMap<String, IngredientStore>> inventory = new EnumMap<IngredientType, LinkedHashMap<String, IngredientStore>>(
			IngredientType.class);
	
	private AtomicBoolean initilized = new AtomicBoolean(false);
	
	private final String defaultBowlIngredientId = "Bowl";
	private final String defaultWrapIngredientId = "Torilla";
	
	private final short defaultRestockAmount = 20;
	private final short maxRestockAmount = 200;

	@Override
	public boolean initilizeInventory() {

		if(initilized.compareAndSet(false, true)) {
			// base
			LinkedHashMap<String, IngredientStore> type = new LinkedHashMap<String, IngredientStore>();
			inventory.put(IngredientType.BASE, type);

			Ingredient ingredient = new BurritoIngredient.Builder(
					IngredientType.BASE, defaultWrapIngredientId, "BI810")
					.getAddOnCost(BigDecimal.ZERO).getBaseCost(BigDecimal.ZERO).premium(false).wrap(true).bowl(false).build();

			IngredientStore is = new IngredientStore(ingredient, 20);
			type.put(ingredient.getName(), is);

			ingredient = new BurritoIngredient.Builder(
					IngredientType.BASE, defaultBowlIngredientId,"BI820")
					.getAddOnCost(BigDecimal.ZERO).getBaseCost(BigDecimal.ZERO).premium(false).wrap(false).bowl(true).build();

			is = new IngredientStore(ingredient, 20);
			type.put(ingredient.getName(), is);

			ingredient = new BurritoIngredient.Builder(
					IngredientType.BASE, "Rice","BI830")
					.getAddOnCost(BigDecimal.ZERO).getBaseCost(BigDecimal.ZERO).premium(false).wrap(false).bowl(false).build();

			is = new IngredientStore(ingredient, 20);
			type.put(ingredient.getName(), is);
			
			// meat 
			type = new LinkedHashMap<String, IngredientStore>();
			inventory.put(IngredientType.MEAT, type);
			ingredient = new BurritoIngredient.Builder(
					IngredientType.MEAT, "Chicken","BI910")
					.getAddOnCost(new BigDecimal(".50")).getBaseCost(BigDecimal.ZERO).premium(false).wrap(false).bowl(false).build();

			is = new IngredientStore(ingredient, 20);
			type.put(ingredient.getName(), is);
			
			ingredient = new BurritoIngredient.Builder(
					IngredientType.MEAT, "Steak","BI920")
					.getAddOnCost(new BigDecimal(".50")).getBaseCost(BigDecimal.ZERO).premium(false).wrap(false).bowl(false).build();

			is = new IngredientStore(ingredient, 20);
			type.put(ingredient.getName(), is);
			
			// salsa
			type = new LinkedHashMap<String, IngredientStore>();
			inventory.put(IngredientType.SALSA, type);
			ingredient = new BurritoIngredient.Builder(
					IngredientType.SALSA, "Red Salsa","BI910")
					.getAddOnCost(new BigDecimal(".50")).getBaseCost(BigDecimal.ZERO).premium(false).wrap(false).bowl(false).build();

			is = new IngredientStore(ingredient, 20);
			type.put(ingredient.getName(), is);
			
			ingredient = new BurritoIngredient.Builder(
					IngredientType.SALSA, "Green Salsa","BI920")
					.getAddOnCost(new BigDecimal(".50")).getBaseCost(BigDecimal.ZERO).premium(false).wrap(false).bowl(false).build();

			is = new IngredientStore(ingredient, 20);
			type.put(ingredient.getName(), is);
			
			ingredient = new BurritoIngredient.Builder(
					IngredientType.SALSA, "Queso","BI930")
					.getAddOnCost(new BigDecimal("1.50")).getBaseCost(new BigDecimal("1.50")).premium(true).wrap(false).bowl(false).build();

			is = new IngredientStore(ingredient, 20);
			type.put(ingredient.getName(), is);
			
			// toppings
			type = new LinkedHashMap<String, IngredientStore>();
			inventory.put(IngredientType.TOPPINGS, type);
			ingredient = new BurritoIngredient.Builder(
					IngredientType.TOPPINGS, "Grated Cheese","BI1010")
					.getAddOnCost(new BigDecimal(".33")).getBaseCost(BigDecimal.ZERO).premium(false).wrap(false).bowl(false).build();

			is = new IngredientStore(ingredient, 20);
			type.put(ingredient.getName(), is);
			
			ingredient = new BurritoIngredient.Builder(
					IngredientType.TOPPINGS, "Sour Cream","BI1020")
					.getAddOnCost(new BigDecimal(".33")).getBaseCost(new BigDecimal("0.0")).premium(false).wrap(false).bowl(false).build();

			is = new IngredientStore(ingredient, 20);
			type.put(ingredient.getName(), is);
			
			ingredient = new BurritoIngredient.Builder(
					IngredientType.TOPPINGS, "Gucamole","BI1030")
					.getAddOnCost(new BigDecimal("1.25")).getBaseCost(new BigDecimal("1.25")).premium(true).wrap(false).bowl(false).build();

			is = new IngredientStore(ingredient, 20);
			type.put(ingredient.getName(), is);
		}
		return initilized.get();
	}
	

	@Override
	public Ingredient getDefaultBowl() throws InventoryException {
		LinkedHashMap<String, IngredientStore> base = inventory.get(IngredientType.BASE);
		if(base != null) {
			IngredientStore ingredientStore = base.get(this.defaultBowlIngredientId);
			if(ingredientStore != null) {
				if(ingredientStore.reserveIngredient()) {
					return ingredientStore.getIngredient();
				}				
			}
		}
		return null;
	}
	
	public void restock() {
		try {
			this.restock(defaultRestockAmount);
		} catch (InventoryException e) {}
	}
	
	public void restock(short quantity) throws InventoryException {
		if(quantity <= 0 || quantity > maxRestockAmount) {
			throw new InventoryException("Restock quantity is not realistic.");
		}
		if(this.isInitilized()) {
			Set<Entry<IngredientType, LinkedHashMap<String, IngredientStore>>> entrySet = this.inventory.entrySet();
			Iterator<Entry<IngredientType, LinkedHashMap<String, IngredientStore>>> iterator = entrySet.iterator();
			while (iterator.hasNext()) {
				Entry<IngredientType, LinkedHashMap<String, IngredientStore>> next = iterator.next();
				LinkedHashMap<String, IngredientStore> value = next.getValue();
				Set<Entry<String, IngredientStore>> es = value.entrySet();
				Iterator<Entry<String, IngredientStore>> it = es.iterator();
				while (it.hasNext()) {
					Entry<String, IngredientStore> entry = it.next();
					IngredientStore store = entry.getValue();
					store.restockIngredient(quantity);					
				}				
			}
		}
	}
	

	@Override
	public Ingredient getDefaultWrap() throws InventoryException {
		LinkedHashMap<String, IngredientStore> base = inventory.get(IngredientType.BASE);
		if(base != null) {
			IngredientStore ingredientStore = base.get(this.defaultWrapIngredientId);
			if(ingredientStore != null) {
				if(ingredientStore.reserveIngredient()) {
					return ingredientStore.getIngredient();
				}
			}
		}
		return null;
	}

	@Override
	public boolean isInitilized() {
		return initilized.get();
	}

	@Override
	public List<Ingredient> ingredientsForType(IngredientType ingredientType) {
		LinkedHashMap<String, IngredientStore> linkedHashMap = inventory.get(ingredientType);
		ArrayList<Ingredient> ingredients = new ArrayList<Ingredient>();
		if(linkedHashMap != null) {
			
			Set<Entry<String, IngredientStore>> entrySet = linkedHashMap.entrySet();
			for (Entry<String, IngredientStore> entry : entrySet) {
				ingredients.add(entry.getValue().getIngredient());
			}
		}
		return ingredients;
	}

	@Override
	public boolean reserveIngredient(Ingredient ingredient) throws InventoryException {
		boolean success = false;
		IngredientType type = ingredient.getType();
		LinkedHashMap<String, IngredientStore> stores = inventory.get(type);
		IngredientStore store = stores.get(ingredient.getName());
		if(store != null) {
			success = store.reserveIngredient();
		}
		else {
			throw new InventoryException("Ingredient not found in model.");
		}
		
		return success;
	}
	
	@Override
	public boolean restoreIngredient(Ingredient ingredient)
			throws InventoryException {
		boolean success = false;
		IngredientType type = ingredient.getType();
		LinkedHashMap<String, IngredientStore> stores = inventory.get(type);
		IngredientStore store = stores.get(ingredient.getName());
		if(store != null) {
			store.restoreIngredient();
			success = true;
		}
		else {
			throw new InventoryException("Ingredient not found in model.");
		}
		
		return success;
	}
	


	@Override
	public boolean consumeIngredients(Order order) throws InventoryException, ProductException, OrderException {
		boolean success = false;		
		
		if(order.getOrderStatus() == OrderStatus.COMPLETED) {			
			synchronized (order) {
				List<Product> products = order.getProducts();
				for (Product product : products) {
					if (product instanceof BurritoProduct) {
						BurritoProduct bp = (BurritoProduct) product;
						List<Ingredient> ingredients = bp.getBurritoIngredientsAsList();
						for (Ingredient ingredient : ingredients) {
							this.consumeIngredient(ingredient);
						}
					}
					else {
						throw new ProductException("Product Exception: unsupported product");
					}
				}
			}
		}
		else {
			throw new OrderException("Illegal state: order is not completed."); 
		}
		
		return success;	
	}
	
	private boolean consumeIngredient(Ingredient ingredient) throws InventoryException {
		IngredientType type = ingredient.getType();
		LinkedHashMap<String, IngredientStore> stores = inventory.get(type);
		IngredientStore store = stores.get(ingredient.getName());
		if(store != null) {
			return store.consumeIngredient();
		}
		else {
			throw new InventoryException("Ingredient not found in model.");
		}		
	}

	@Override
	public boolean dispenseProduct(Product product) {
		throw new UnsupportedOperationException();
	}
	
	public String showInventory() {
		StringBuilder sb = new StringBuilder();
		Iterator<Entry<IngredientType, LinkedHashMap<String, IngredientStore>>> iterator = inventory.entrySet().iterator();
		
		while (iterator.hasNext()) {
			Entry<IngredientType, LinkedHashMap<String, IngredientStore>> next = iterator.next();
			sb.append(next.getKey().getName() + ": " + BurritoMatic.LINE);
			LinkedHashMap<String, IngredientStore> values = next.getValue();
			Iterator<Entry<String, IngredientStore>> stores = values.entrySet().iterator();
			while(stores.hasNext()) {
				Entry<String, IngredientStore> store = stores.next();
				sb.append("    " +  store.getKey() + ": " + "reserved: " + 
						store.getValue().getReservedQuantity() + " of " + 
						store.getValue().getQuantity() + BurritoMatic.LINE);
			}		
		}
		
		return sb.toString();
	}


}
