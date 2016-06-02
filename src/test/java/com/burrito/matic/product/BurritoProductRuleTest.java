package com.burrito.matic.product;

import com.burrito.matic.inventory.Ingredient;
import com.burrito.matic.inventory.IngredientType;
import org.easymock.IAnswer;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertEquals;

public class BurritoProductRuleTest extends RuleUtilsTest {
	
	AlaCarteRule alaCarteRule = new AlaCarteRule();
	
	@Before
	public void setUp() {
		super.setUp();
	}

	@Test
	public void testCost() { 
		
		ingredients.clear();

		expect(meat.getBaseCost()).andReturn(BigDecimal.ZERO).anyTimes();
		expect(meat.getAddOnCost()).andReturn(new BigDecimal(".50")).anyTimes();
		expect(meat.isPremium()).andReturn(false).anyTimes();
		
		expect(salsa.getBaseCost()).andReturn(new BigDecimal("1.50")).anyTimes();
		expect(salsa.getAddOnCost()).andReturn(new BigDecimal("1.50")).anyTimes();
		expect(salsa.isPremium()).andReturn(true).anyTimes();
		
		expect(topping.getBaseCost()).andReturn(BigDecimal.ZERO).anyTimes();
		expect(topping.getAddOnCost()).andReturn(new BigDecimal(".33")).anyTimes();
		expect(topping.isPremium()).andReturn(false).anyTimes();
		
		expect(burritoProduct.getProductRule()).andReturn(alaCarteRule).anyTimes();
		expect(burritoProduct.getBurritoIngredients()).andReturn(ingredients).anyTimes();
		expect(burritoProduct.isBowl()).andReturn(alaCarteRule.isBowl()).anyTimes();
		expect(burritoProduct.getBaseCost()).andReturn(new BigDecimal("5.99")).anyTimes();
		expect(burritoProduct.getBurritoIngredientsAsList()).andAnswer(new IAnswer<List<Ingredient>>() {
		
			@Override
			public List<Ingredient> answer() throws Throwable {
				ArrayList<Ingredient> ing = new ArrayList<Ingredient>();
				
				Set<Entry<IngredientType, List<Ingredient>>> entrySet = ingredients.entrySet();
				for (Entry<IngredientType, List<Ingredient>> entry : entrySet) {
					List<Ingredient> list = entry.getValue();
					for (Ingredient ingredient : list) {
						ing.add(ingredient);
					}
				}
				return ing;
			}

		}).anyTimes();
		
		replay(burritoProduct, meat, salsa, topping);
		
		BigDecimal cost = alaCarteRule.cost(burritoProduct);
		
		assertEquals(new BigDecimal("5.99"), cost);
		
		// part of the base
		add(meat);
		cost = alaCarteRule.cost(burritoProduct);
		assertEquals(new BigDecimal("5.99"), cost);
		
		// premium ingregient has extra cost
		add(salsa);
		cost = alaCarteRule.cost(burritoProduct);
		assertEquals(new BigDecimal("7.49"), cost);
		
		// part of the base
		add(topping);
		cost = alaCarteRule.cost(burritoProduct);
		assertEquals(new BigDecimal("7.49"), cost);
		
		// add on cost
		add(topping);
		cost = alaCarteRule.cost(burritoProduct);
		assertEquals(new BigDecimal("7.82"), cost);
		
	}
}
