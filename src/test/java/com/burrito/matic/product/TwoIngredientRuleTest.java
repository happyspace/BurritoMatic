package com.burrito.matic.product;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.burrito.matic.exception.ProductException;
import com.burrito.matic.product.TwoIngredientRule;

public class TwoIngredientRuleTest extends RuleUtilsTest {

	protected TwoIngredientRule ingredientRule = new TwoIngredientRule();
	
	@Before
	public void setUp() {
		super.setUp();
		
	}

	@Test
	public void testValidateProduct() {
		expect(burritoProduct.getProductRule()).andReturn(ingredientRule).anyTimes();
		expect(burritoProduct.getBurritoIngredients()).andReturn(ingredients).anyTimes();
		expect(burritoProduct.isBowl()).andReturn(ingredientRule.isBowl()).anyTimes();
		
		replay(burritoProduct, meat, salsa, wrap);
		
		boolean tir = ingredientRule.validateProduct(burritoProduct);
		assertFalse(tir);
		
		add(meat);
		add(salsa);
		
		tir = ingredientRule.validateProduct(burritoProduct);
		assertFalse(tir);
		
		add(wrap);
		
		tir = ingredientRule.validateProduct(burritoProduct);
		assertTrue(tir);

	}

	@Test
	public void testValidateIngredient() throws ProductException {
		expect(burritoProduct.getProductRule()).andReturn(ingredientRule).anyTimes();
		expect(burritoProduct.getBurritoIngredients()).andReturn(ingredients).anyTimes();
		expect(burritoProduct.isBowl()).andReturn(ingredientRule.isBowl()).anyTimes();
		
		replay(burritoProduct, wrap, meat, salsa, topping);
		// init with rule.
		boolean validateIngredient = ingredientRule.validateIngredient(burritoProduct, wrap, true);
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
