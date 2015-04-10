package com.burrito.matic;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import com.burrito.matic.exception.InventoryException;
import com.burrito.matic.exception.OrderException;
import com.burrito.matic.exception.ProductException;
import com.burrito.matic.inventory.Ingredient;
import com.burrito.matic.order.BurritoOrder;
import com.burrito.matic.order.Order;
import com.burrito.matic.order.OrderRequest;
import com.burrito.matic.order.OrderUpdate;
import com.burrito.matic.order.Orderable;
import com.burrito.matic.product.AlaCarteRule;
import com.burrito.matic.product.BowlRule;
import com.burrito.matic.product.BurritoProduct;
import com.burrito.matic.product.DessertProduct;
import com.burrito.matic.product.Product;
import com.burrito.matic.product.ProductCategory;
import com.burrito.matic.product.SodaProduct;
import com.burrito.matic.product.ThreeIngredientRule;
import com.burrito.matic.product.TwoIngredientRule;

/**
 * A singleton service for processing burrito orders.
 * 
 * @author ewarner
 * 
 */
public enum OrderServiceImpl implements OrderService {

	/**
	 * Singleton instance.
	 */
	INSTANCE;

	/**
	 * Generate order IDs.
	 */
	private final AtomicLong orderId;

	/**
	 * Generate transaction IDs
	 */
	private final AtomicLong transactionId;

	/**
	 * Indicates that product definition have been loaded.
	 */
	private AtomicBoolean initilized = new AtomicBoolean(false);

	/**
	 * A dictionary of orders.
	 */
	private final ConcurrentHashMap<Long, Order> orders = new ConcurrentHashMap<Long, Order>();

	/**
	 * A dictionary of transactions.
	 */
	private final ConcurrentHashMap<Long, OrderUpdate> transactions = new ConcurrentHashMap<Long, OrderUpdate>();

	/**
	 * A dictionary of burritos.
	 */
	private final Map<String, BurritoProduct> burritoProducts = new LinkedHashMap<String, BurritoProduct>();

	/**
	 * A dictionary of soda products.
	 */
	private final Map<String, SodaProduct> sodaProducts = new LinkedHashMap<String, SodaProduct>();

	/**
	 * A dictionary of dessert products.
	 */
	private final Map<String, DessertProduct> dessertProducts = new LinkedHashMap<String, DessertProduct>();

	/**
	 * A dictionary of product categories.
	 */
	private final Map<String, ProductCategory> productCategories = new LinkedHashMap<String, ProductCategory>();

	/**
	 * Initialize counters.
	 */
	private OrderServiceImpl() {
		orderId = new AtomicLong(0);
		transactionId = new AtomicLong(0);
	}

	/**
	 * Creates an order. Orders must start with a burrito product. The burrito
	 * product is initialized with either a bowl or a wrap.
	 * 
	 * @Return - an order update object to the client indicating success and
	 *         includes the next request identifier for the client to use.
	 */
	public OrderUpdate createOrder(BurritoProduct product) {
		
		BurritoProduct myBurritoProduct = new BurritoProduct(product);
		long id = orderId.incrementAndGet();

		BurritoOrder order = new BurritoOrder(myBurritoProduct, id);
		OrderUpdate orderUpdate = new OrderUpdate(order, null,
				this.transactionId.getAndIncrement());

		this.orders.put(id, order);
		this.initilizeProduct(myBurritoProduct, orderUpdate);

		return orderUpdate;
	}

	/**
	 * Assigns a burrito product either a wrap or a bowl. 
	 * @param product
	 * @param orderUpdate
	 */
	private void initilizeProduct(final BurritoProduct product,
			final OrderUpdate orderUpdate) {
		InventoryServiceImpl inventoryService = InventoryServiceImpl.INSTANCE;

		if (product.isBowl()) {
			try {
				Ingredient defaultBowl = inventoryService.getDefaultBowl();

				try {
					product.initilizeProduct(defaultBowl);
				} catch (ProductException e) {
					orderUpdate.setException(e);
					InventoryServiceImpl.INSTANCE.restoreIngredient(defaultBowl);
				}

			} catch (InventoryException e) {
				orderUpdate.setException(e);
			}

		} else {
			try {
				Ingredient defaultWrap;
				defaultWrap = inventoryService.getDefaultWrap();

				try {
					product.initilizeProduct(defaultWrap);
				} catch (ProductException e) {
					orderUpdate.setException(e);
					InventoryServiceImpl.INSTANCE.restoreIngredient(defaultWrap);
				} 
			} catch (InventoryException e) {
				orderUpdate.setException(e);
			}
		}
	}

	@Override
	public boolean isInitilized() {
		return initilized.get();
	}

	@Override
	public Map<String, BurritoProduct> getBurritoProducts() {
		return Collections.unmodifiableMap(this.burritoProducts);
	}

	@Override
	public Map<String, SodaProduct> getSodaProducts() {
		return Collections.unmodifiableMap(this.sodaProducts);
	}

	@Override
	public Map<String, DessertProduct> getDesertProducts() {
		return Collections.unmodifiableMap(this.dessertProducts);
	}

	@Override
	public Map<String, ProductCategory> getProductCategories() {
		return Collections.unmodifiableMap(this.productCategories);
	}

	/** 
	 * Adds an ingredient to the burrito currently under construction for
	 * the order referenced in the OrderRequest. Ingredients are reserved in 
	 * this step and comsumed in the purchase step. 
	 */
	@Override
	public OrderUpdate addIngredient(final OrderRequest request) {
		Order order = this.orders.get(request.getOrder().getOrderId());
		OrderUpdate orderUpdate = new OrderUpdate(order, request,
				this.transactionId.getAndIncrement());

		Orderable orderable = request.getOrderable();
		if (orderable instanceof Ingredient) {
			Ingredient ingredient = (Ingredient) orderable;
			try {
				if (InventoryServiceImpl.INSTANCE.reserveIngredient(ingredient)) {
					try {
						order.addIngredient(ingredient);
					} catch (ProductException e) {
						orderUpdate.setException(e);
						InventoryServiceImpl.INSTANCE.restoreIngredient(ingredient);
					} catch (OrderException e) {
						orderUpdate.setException(e);
						InventoryServiceImpl.INSTANCE.restoreIngredient(ingredient);
					} 
				} else {
					orderUpdate
							.setException(new InventoryException(
									"Inventory Exception: unable to reserve ingredient."));
				}

			} catch (InventoryException e) {
				orderUpdate.setException(e);
			}
		}

		return orderUpdate;
	}

	@Override
	public OrderUpdate addProduct(final OrderRequest request) {
		Order order = this.orders.get(request.getOrder().getOrderId());

		OrderUpdate orderUpdate = new OrderUpdate(order, request,
				this.transactionId.getAndIncrement());

		Orderable orderable = request.getOrderable();
		if (orderable instanceof Product) {
			Product product = (Product) orderable;
			if (product instanceof BurritoProduct) {
				BurritoProduct burritoProduct = (BurritoProduct) product;
				
				BurritoProduct myBurritoProduct = new BurritoProduct(burritoProduct);
				this.initilizeProduct(myBurritoProduct, orderUpdate);
				product = myBurritoProduct;
			}
			try {
				order.addProduct(product);
			} catch (OrderException e) {
				orderUpdate.setException(e);
			}
		} else {
			orderUpdate.setException(new OrderException(
					
					"Order Exception: Only products can be added to orders."));
		}

		return orderUpdate;
	}

	@Override
	public ProductCategory getProductCategory(final String name) {
		if (name != null && productCategories.containsKey(name)) {
			return productCategories.get(name);
		}
		return null;
	}

	@Override
	public OrderUpdate purchaseOrder(final OrderRequest request) {
		Order order = this.orders.get(request.getOrder().getOrderId());
		OrderUpdate orderUpdate = new OrderUpdate(order, request,
				this.transactionId.getAndIncrement());
		try {
			order.purchase();
			InventoryServiceImpl.INSTANCE.consumeIngredients(order);

		} catch (OrderException e) {
			orderUpdate.setException(e);
		} catch (InventoryException e) {
			orderUpdate.setException(e);
		} catch (ProductException e) {
			orderUpdate.setException(e);
		}

		return orderUpdate;
	}

	@Override
	public boolean initilizeProducts() {

		if (initilized.compareAndSet(false, true)) {

			ProductCategory burritoCategory = new ProductCategory("Burrito",
					"C02000");
			productCategories.put(burritoCategory.getName(), burritoCategory);
			BurritoProduct bp = new BurritoProduct("A-la-Carte Burrito",
					"B01234", burritoCategory, new AlaCarteRule(),
					new BigDecimal("5.99"));
			burritoProducts.put("B01234", bp);

			bp = new BurritoProduct("Burrito-in-a-bowl", "B02233",
					burritoCategory, new BowlRule(), new BigDecimal("3.99"));
			burritoProducts.put("B02233", bp);

			bp = new BurritoProduct("Regular Burrito", "B03344",
					burritoCategory, new TwoIngredientRule(), new BigDecimal(
							"4.99"));
			burritoProducts.put("B03344", bp);

			bp = new BurritoProduct("Super Burrito", "B03360", burritoCategory,
					new ThreeIngredientRule(), new BigDecimal("5.99"));
			burritoProducts.put("B03360", bp);

			burritoCategory = new ProductCategory("Soda", "C03000");
			productCategories.put("Soda", burritoCategory);

			burritoCategory = new ProductCategory("Desert", "C03000");
			productCategories.put("Desert", burritoCategory);

		}

		return initilized.get();
	}
	
	public String showOrders() {
		StringBuilder sb = new StringBuilder();
		
		Set<Entry<Long, Order>> entrySet = orders.entrySet();
		Iterator<Entry<Long, Order>> iterator = entrySet.iterator();
		BigDecimal total = BigDecimal.ZERO;
		while (iterator.hasNext()) {
			Entry<Long, Order> next = iterator.next();
			Order order = next.getValue();
			sb.append("Order " + next.getKey() + ": " + order.getOrderStatus().name() + " " + order.runningTotal() + BurritoMatic.LINE);
			total = total.add(order.runningTotal());
			
			List<Product> products = order.getProducts();
			
	    	for (Product product : products) {
	    		if (product instanceof BurritoProduct) {
					BurritoProduct bp = (BurritoProduct) product;
					sb.append("    " + bp + BurritoMatic.LINE);
				}
	    		else {
	    			sb.append("    " + product.getName() + " " + product.getBaseCost() + BurritoMatic.LINE);
	    		}
		}	
	}
		sb.append("Total: " + total);
		return sb.toString();
	}
}
