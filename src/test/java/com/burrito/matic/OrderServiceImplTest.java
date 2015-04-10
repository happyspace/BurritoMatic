package com.burrito.matic;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.burrito.matic.exception.OrderException;
import com.burrito.matic.inventory.Ingredient;
import com.burrito.matic.inventory.IngredientType;
import com.burrito.matic.order.Order;
import com.burrito.matic.order.OrderRequest;
import com.burrito.matic.order.OrderStatus;
import com.burrito.matic.order.OrderUpdate;
import com.burrito.matic.product.BurritoProduct;
import com.burrito.matic.product.Product;

@RunWith(JUnit4.class)
public class OrderServiceImplTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	InventoryServiceImpl inventoryService = InventoryServiceImpl.INSTANCE;
	OrderServiceImpl orderService = OrderServiceImpl.INSTANCE;

	private Random randomGenerator;


	@Before
	public void setUp() throws Exception {
		inventoryService.initilizeInventory();
		orderService.initilizeProducts();
	}

	@Test
	public void testCreateOrder() {
		inventoryService.restock();
		Map<String, BurritoProduct> products = orderService
				.getBurritoProducts();
		Collection<BurritoProduct> values = products.values();
		ArrayList<OrderUpdate> orderUpdates = new ArrayList<OrderUpdate>(
				values.size());
		for (BurritoProduct burritoProduct : values) {
			OrderUpdate createOrder = orderService.createOrder(burritoProduct);
			orderUpdates.add(createOrder);
		}
		for (OrderUpdate orderUpdate : orderUpdates) {
			Order order = orderUpdate.getOrder();

			assertEquals(order.getOrderStatus(), OrderStatus.NEW);
		}

		for (OrderUpdate orderUpdate : orderUpdates) {
			Order order = orderUpdate.getOrder();
			BurritoProduct currentBurrito = order.getCurrentBurrito();
			List<Ingredient> list = currentBurrito
					.getBurritoIngredientsAsList();
			assertEquals(list.size(), 1);
			Ingredient ingredient = list.get(0);
			assertTrue(ingredient.isInitial());
		}
	}

	@Test
	public void testAddIngredientMeat() {
		inventoryService.restock();

		Map<String, BurritoProduct> products = orderService
				.getBurritoProducts();
		Collection<BurritoProduct> values = products.values();

		List<Ingredient> list = inventoryService
				.ingredientsForType(IngredientType.MEAT);

		this.excerciseList(values, list, IngredientType.MEAT);
	}



	@Test
	public void testAddIngredientSalsa() {
		inventoryService.restock();

		Map<String, BurritoProduct> products = orderService
				.getBurritoProducts();
		Collection<BurritoProduct> values = products.values();

		List<Ingredient> list = inventoryService
				.ingredientsForType(IngredientType.SALSA);

		this.excerciseList(values, list, IngredientType.SALSA);
	}

	@Test
	public void testAddIngredientToppings() {
		inventoryService.restock();

		Map<String, BurritoProduct> products = orderService
				.getBurritoProducts();
		Collection<BurritoProduct> values = products.values();

		List<Ingredient> list = inventoryService
				.ingredientsForType(IngredientType.TOPPINGS);

		this.excerciseList(values, list, IngredientType.TOPPINGS);
	}
	
	private void excerciseList(Collection<BurritoProduct> values,
			List<Ingredient> list, IngredientType ingredientType) {
		for (Ingredient ingredient : list) {
			ArrayList<OrderUpdate> orderUpdates = new ArrayList<OrderUpdate>(
					values.size());
			for (BurritoProduct burritoProduct : values) {
				OrderUpdate createOrder = orderService
						.createOrder(burritoProduct);
				orderUpdates.add(createOrder);
			}
			
			ArrayList<OrderUpdate> orderUpdatesAdd = new ArrayList<OrderUpdate>(
					values.size());
			
			for (OrderUpdate orderUpdate : orderUpdates) {
				OrderRequest orderRequest = orderUpdate.getNexOrderRequest();
				orderRequest.setOrderable(ingredient);
				orderUpdatesAdd.add(orderService.addIngredient(orderRequest));
			}
			for (OrderUpdate orderUpdate : orderUpdates) {
				Order order = orderUpdate.getOrder();
				assertEquals(order.getOrderStatus(), OrderStatus.OPEN);
			}
			
			// Toppings are not valid on some burrito types. 
			for (OrderUpdate orderUpdate : orderUpdatesAdd) {
				Exception exceptionOrder = orderUpdate.getException();
				if(exceptionOrder != null) {
					//System.out.println(exceptionOrder.getMessage());
					//System.out.println(orderUpdate.getOrder().getCurrentBurrito().getName());
					// System.out.println(ingredientType);
					// assertEquals(exceptionOrder.getMessage(), "");
					assertEquals(exceptionOrder.getMessage(), "Ingredient is not valid of product or product state.");
					assertEquals(ingredientType, IngredientType.TOPPINGS);
				}
				else {
					Order order = orderUpdate.getOrder();
					BurritoProduct currentBurrito = order.getCurrentBurrito();
					List<Ingredient> listBp = currentBurrito.getBurritoIngredients()
							.get(ingredientType);

					assertEquals(listBp.size(), 1);
					assertTrue(listBp.get(0).getType().equals(ingredientType));
				}
			}
		}
	}

	@Test
	public void testAddProduct() {
		inventoryService.restock();
		
		Map<String, BurritoProduct> products = orderService
				.getBurritoProducts();
		Collection<BurritoProduct> values = products.values();
		ArrayList<OrderUpdate> orderUpdates = new ArrayList<OrderUpdate>(
				values.size());
		for (BurritoProduct burritoProduct : values) {
			OrderUpdate createOrder = orderService.createOrder(burritoProduct);
			orderUpdates.add(createOrder);
		}
		int i = 0;
		BurritoProduct[] burritoProducts = values
				.toArray(new BurritoProduct[values.size()]);
		ArrayList<OrderUpdate> orderUpdatesAddEx = new ArrayList<OrderUpdate>(
				values.size());
		for (OrderUpdate orderUpdate : orderUpdates) {
			OrderRequest orderRequest = orderUpdate.getNexOrderRequest();
			orderRequest.setOrderable(burritoProducts[i]);
			orderUpdatesAddEx.add(orderService.addProduct(orderRequest));
			i++;
		}

		// burrito not sufficient to purchase thus can not add the next burrito.
		for (OrderUpdate orderUpdate : orderUpdatesAddEx) {
			Exception orderException = orderUpdate.getException();
			assertNotNull(orderException);
			assertEquals(orderException.getMessage(),
					"Current burrito product must be completed.");
		}
		// fill up burritos
		List<Ingredient> meat = inventoryService
				.ingredientsForType(IngredientType.MEAT);
		ArrayList<OrderUpdate> fillBurritoMeat = this.fillBurrito(
				orderUpdatesAddEx, meat);

		List<Ingredient> salsa = inventoryService
				.ingredientsForType(IngredientType.SALSA);
		ArrayList<OrderUpdate> fillBurritoSalsa = this.fillBurrito(
				fillBurritoMeat, salsa);

		// two ingredient burritos will error here but that is OK.
		List<Ingredient> topping = inventoryService
				.ingredientsForType(IngredientType.TOPPINGS);
		ArrayList<OrderUpdate> fillBurritoTopping = this.fillBurrito(
				fillBurritoSalsa, topping);

		for (OrderUpdate orderUpdate : fillBurritoTopping) {
			BurritoProduct currentBurrito = orderUpdate.getOrder()
					.getCurrentBurrito();
			assertTrue(currentBurrito.isSufficient());
		}

		// now we can add another burrito product.
		int j = 0;
		ArrayList<OrderUpdate> orderUpdatesAddProd = new ArrayList<OrderUpdate>(
				values.size());
		for (OrderUpdate orderUpdate : fillBurritoTopping) {
			OrderRequest orderRequest = orderUpdate.getNexOrderRequest();
			orderRequest.setOrderable(burritoProducts[j]);
			orderUpdatesAddProd.add(orderService.addProduct(orderRequest));
			j++;
		}
		for (OrderUpdate orderUpdate : orderUpdatesAddProd) {
			Order order = orderUpdate.getOrder();
			List<Product> list = order.getProducts();
			assertEquals(list.size(), 2);
		}
	}

	private ArrayList<OrderUpdate> fillBurrito(
			ArrayList<OrderUpdate> orderUpdates, List<Ingredient> ingredients) {
		ArrayList<OrderUpdate> orderUpdatesAddIng = new ArrayList<OrderUpdate>(
				orderUpdates.size());
		for (OrderUpdate orderUpdate : orderUpdates) {
			randomGenerator = new Random();
			int nextInt = randomGenerator.nextInt(ingredients.size());
			OrderRequest orderRequest = orderUpdate.getOrderRequest();
			orderRequest.setOrderable(ingredients.get(nextInt));
			OrderUpdate addIngredient = orderService
					.addIngredient(orderRequest);
			orderUpdatesAddIng.add(addIngredient);

		}
		return orderUpdatesAddIng;
	}

	@Test
	public void purchase() throws OrderException {
		inventoryService.restock();
		
		Map<String, BurritoProduct> products = orderService
				.getBurritoProducts();
		Collection<BurritoProduct> values = products.values();
		ArrayList<OrderUpdate> orderUpdates = new ArrayList<OrderUpdate>(
				values.size());
		for (BurritoProduct burritoProduct : values) {
			OrderUpdate createOrder = orderService.createOrder(burritoProduct);
			orderUpdates.add(createOrder);
		}

		ArrayList<OrderUpdate> orderUpdatesAddEx = new ArrayList<OrderUpdate>(
				values.size());
		for (OrderUpdate orderUpdate : orderUpdates) {
			OrderRequest orderRequest = orderUpdate.getNexOrderRequest();
			orderUpdatesAddEx.add(orderService.purchaseOrder(orderRequest));
		}

		// burrito not sufficient to purchase.
		for (OrderUpdate orderUpdate : orderUpdatesAddEx) {
			Exception orderException = orderUpdate.getException();
			assertNotNull(orderException);
			assertEquals(orderException.getMessage(),
					"Illegal state: order is not open.");
		}

		// can not calculate total.
		for (OrderUpdate orderUpdate : orderUpdatesAddEx) {
			Order order = orderUpdate.getOrder();

			// exception.expect(OrderException.class);
			// exception.expectMessage("Illegal state: order is not completed.");

			try {
				BigDecimal calculateTotal = order.calculateTotal();
				fail("order exception not thrown.");
			} catch (OrderException e) {}
		}

		// fill up burritos
		List<Ingredient> meat = inventoryService
				.ingredientsForType(IngredientType.MEAT);
		ArrayList<OrderUpdate> fillBurritoMeat = this.fillBurrito(
				orderUpdatesAddEx, meat);

		List<Ingredient> salsa = inventoryService
				.ingredientsForType(IngredientType.SALSA);
		ArrayList<OrderUpdate> fillBurritoSalsa = this.fillBurrito(
				fillBurritoMeat, salsa);

		// two ingredient burritos will error here but that is OK.
		List<Ingredient> topping = inventoryService
				.ingredientsForType(IngredientType.TOPPINGS);
		ArrayList<OrderUpdate> fillBurritoTopping = this.fillBurrito(
				fillBurritoSalsa, topping);

		for (OrderUpdate orderUpdate : fillBurritoTopping) {
			BurritoProduct currentBurrito = orderUpdate.getOrder()
					.getCurrentBurrito();
			assertTrue(currentBurrito.isSufficient());
		}

		ArrayList<OrderUpdate> orderUpdatesPurchase = new ArrayList<OrderUpdate>(
				values.size());
		for (OrderUpdate orderUpdate : fillBurritoTopping) {
			OrderRequest orderRequest = orderUpdate.getNexOrderRequest();
			orderUpdatesPurchase.add(orderService.purchaseOrder(orderRequest));
		}

		// burrito sufficient to purchase.
		for (OrderUpdate orderUpdate : orderUpdatesPurchase) {
			Exception orderException = orderUpdate.getException();
			assertNull(orderException);
		}

		// can calculate total.
		for (OrderUpdate orderUpdate : orderUpdatesPurchase) {
			Order order = orderUpdate.getOrder();
			BigDecimal calculateTotal = order.calculateTotal();
			System.out.println("Purchase burrito calculate total: "
					+ calculateTotal);
		}
	}

}
