package com.burrito.matic.inventory;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import com.burrito.matic.exception.InventoryException;

import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class IngredientStore {
	private AtomicLong inventoryQuantity = new AtomicLong();
	private AtomicLong reserveQuantity = new AtomicLong();
	private final Ingredient ingredient;
	
	private Object lock = new Object();
	
	
	public IngredientStore(Ingredient ingredient) {
		this.ingredient = ingredient;
	}
	
	public IngredientStore(Ingredient ingredient, long quantity) {
		this.ingredient = ingredient;
		inventoryQuantity.addAndGet(quantity);
	}
	
	public boolean reserveIngredient() throws InventoryException {
		boolean success = false;
		
		synchronized (lock) {
			long remaining = inventoryQuantity.longValue() - reserveQuantity.longValue();
			if(remaining - 1 > 0) {
				reserveQuantity.addAndGet(1);
				success = true;
			}
			else {
				throw new InventoryException("Can not reserve ingredient: " + this.ingredient.getName());
			}
		}		
		return success;
	}
	
	public boolean consumeIngredient() throws InventoryException {
		boolean success = false;
		
		synchronized (lock) {
			if(inventoryQuantity.get() == 0 || reserveQuantity.get() == 0) {
				throw new InventoryException("Inconsistent inventory: no inventory to consume.");
			}
			
			inventoryQuantity.getAndDecrement();
			reserveQuantity.getAndDecrement();
			success = true;
		}
		
		return success;
	}
	
	public long restockIngredient(Short quantity) {
		return inventoryQuantity.addAndGet(quantity);
	}
	
	public Ingredient getIngredient() {
		return ingredient;
	}

	public void restoreIngredient() {		
		reserveQuantity.addAndGet(-1);		 
	}
	
	public long getQuantity() {
		return this.inventoryQuantity.get();
	}
	
	public long getReservedQuantity() {
		return this.reserveQuantity.get();
	}
}
