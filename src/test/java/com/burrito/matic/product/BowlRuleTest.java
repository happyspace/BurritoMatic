package com.burrito.matic.product;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.burrito.matic.product.BowlRule;

public class BowlRuleTest extends RuleUtilsTest {
	
	protected BowlRule ingredientRule = new BowlRule();

	@Before
	public void setUp() {
		super.setUp();
		
	}

	@Test
	public void testValidateIngredient() {
		expect(burritoProduct.getProductRule()).andReturn(ingredientRule).anyTimes();
		expect(burritoProduct.getBurritoIngredients()).andReturn(ingredients).anyTimes();
		expect(burritoProduct.isBowl()).andReturn(ingredientRule.isBowl()).anyTimes();
		
		replay(burritoProduct, meat, salsa, bowl);
		
		boolean tir = ingredientRule.validateProduct(burritoProduct);
		assertFalse(tir);
		
		add(meat);
		add(salsa);
		
		tir = ingredientRule.validateProduct(burritoProduct);
		assertFalse(tir);
		
		add(bowl);
		
		tir = ingredientRule.validateProduct(burritoProduct);
		assertTrue(tir);
	}

	@Test
	public void testValidateProduct() {
		expect(burritoProduct.getProductRule()).andReturn(ingredientRule).anyTimes();
		expect(burritoProduct.getBurritoIngredients()).andReturn(ingredients).anyTimes();
		expect(burritoProduct.isBowl()).andReturn(ingredientRule.isBowl()).anyTimes();
		
		replay(burritoProduct, bowl, meat, salsa, topping);
		
		// init with rule.
		boolean validateIngredient = ingredientRule.validateIngredient(burritoProduct, bowl, true);
		assertTrue(validateIngredient);
		
		// follow on ingredients
		validateIngredient = ingredientRule.validateIngredient(burritoProduct, meat, false);
		assertTrue(validateIngredient);
		
		// follow on ingredients
		validateIngredient = ingredientRule.validateIngredient(burritoProduct, salsa, false);
		assertTrue(validateIngredient);
		
		// follow on ingredients
		validateIngredient = ingredientRule.validateIngredient(burritoProduct, topping, false);
		assertFalse(validateIngredient);
	}

}
