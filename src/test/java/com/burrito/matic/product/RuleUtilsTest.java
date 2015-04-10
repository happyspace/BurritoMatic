package com.burrito.matic.product;

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.burrito.matic.inventory.Ingredient;
import com.burrito.matic.inventory.IngredientType;
import com.burrito.matic.product.BurritoProduct;
import com.burrito.matic.product.RuleUtils;


@RunWith(JUnit4.class)
public class RuleUtilsTest {
	
	public Ingredient initialIngredient;
	public BurritoProduct burritoProduct;
	public EnumMap<IngredientType, List<Ingredient>> ingredients = new EnumMap<IngredientType, List<Ingredient>>(IngredientType.class);
	public Ingredient meat;
	public Ingredient salsa;
	public Ingredient topping;
	public Ingredient bowl;
	public Ingredient wrap;
	
	
	@Before
	public void setUp() {
		initialIngredient = createNiceMock(Ingredient.class);
		burritoProduct = createNiceMock(BurritoProduct.class);
		
		meat = createNiceMock(Ingredient.class);
		expect(meat.getType()).andReturn(IngredientType.MEAT).anyTimes();
		expect(meat.isInitial()).andReturn(false).anyTimes();
//		replay(meat);
		
		salsa = createNiceMock(Ingredient.class);
		expect(salsa.getType()).andReturn(IngredientType.SALSA).anyTimes();
		expect(salsa.isInitial()).andReturn(false).anyTimes();
//		replay(salsa);
		
		topping = createNiceMock(Ingredient.class);
		expect(topping.getType()).andReturn(IngredientType.TOPPINGS).anyTimes();
		expect(topping.isInitial()).andReturn(false).anyTimes();
//		replay(topping);	
		
		bowl = createNiceMock(Ingredient.class);
		expect(bowl.getType()).andReturn(IngredientType.BASE).anyTimes();
		expect(bowl.isInitial()).andReturn(true).anyTimes();
		expect(bowl.isBowl()).andReturn(true).anyTimes();
//		replay(bowl);
		
		wrap = createNiceMock(Ingredient.class);
		expect(wrap.getType()).andReturn(IngredientType.BASE).anyTimes();
		expect(wrap.isInitial()).andReturn(true).anyTimes();
		expect(wrap.isWrap()).andReturn(true).anyTimes();
//		replay(wrap);
	}
	
	@Test
	public void testInitialIngedient() {
		expect(initialIngredient.getType()).andReturn(IngredientType.BASE);
		expect(initialIngredient.isInitial()).andReturn(true);
		
		EasyMock.replay(initialIngredient);
		boolean validIngredient = RuleUtils.isValidIngredient(burritoProduct, initialIngredient, RuleUtils.TWO_ING_COMPLETE);
		assertFalse(validIngredient);
	}
	
	@Test
	public void testNonInitialIngedient() {
		expect(initialIngredient.getType()).andReturn(IngredientType.BASE);
		expect(initialIngredient.isInitial()).andReturn(false);
		
		EasyMock.replay(initialIngredient);
		boolean validInitialIngredient = RuleUtils.isValidInitialIngredient(burritoProduct, initialIngredient);
		assertFalse(validInitialIngredient);
	}
	
	@Test
	public void testTwoIngredientComplete() {		
		expect(burritoProduct.getBurritoIngredients()).andReturn(ingredients).anyTimes();
		
		EasyMock.replay(burritoProduct, meat, salsa);
		
		boolean empty = RuleUtils.isCompleteTwoIngredient(burritoProduct);
		assertFalse(empty);		
		
		this.add(meat);	
		boolean meat1 = RuleUtils.isCompleteTwoIngredient(burritoProduct);
		assertFalse(meat1);
		
		this.add(salsa);	
		boolean salsa1 = RuleUtils.isCompleteTwoIngredient(burritoProduct);
		assertTrue(salsa1);
		
	}
	
	@Test
	public void testThreeIngredientComplete() {		
		expect(burritoProduct.getBurritoIngredients()).andReturn(ingredients).anyTimes();
		
		EasyMock.replay(burritoProduct, meat, salsa, topping);
		
		boolean empty = RuleUtils.isCompleteThreeIngredient(burritoProduct);
		assertFalse(empty);		
		
		this.add(meat);	
		boolean meat1 = RuleUtils.isCompleteThreeIngredient(burritoProduct);
		assertFalse(meat1);
		
		this.add(salsa);	
		boolean salsa1 = RuleUtils.isCompleteThreeIngredient(burritoProduct);
		assertFalse(salsa1);
		
		this.add(topping);	
		boolean topping1 = RuleUtils.isCompleteThreeIngredient(burritoProduct);
		assertTrue(topping1);
		
	}
	
	@Test
	public void testIsValidIngredientAddTwice() {
		expect(burritoProduct.getBurritoIngredients()).andReturn(ingredients).anyTimes();
		
		EasyMock.replay(burritoProduct, meat);
		
		boolean validIngredient = RuleUtils.isValidIngredient(burritoProduct, meat, RuleUtils.TWO_VALIDATE_INGREDIENT);
		assertTrue(validIngredient);
		
		this.add(meat);	
		
		validIngredient = RuleUtils.isValidIngredient(burritoProduct, meat, RuleUtils.TWO_VALIDATE_INGREDIENT);
		assertFalse(validIngredient);
	}
	
	@Test
	public void testIsValidIngredientAddExcluded() {
		expect(burritoProduct.getBurritoIngredients()).andReturn(ingredients).anyTimes();
		
		EasyMock.replay(burritoProduct, topping);
		
		boolean validIngredient = RuleUtils.isValidIngredient(burritoProduct, topping, RuleUtils.TWO_VALIDATE_INGREDIENT);
		assertFalse(validIngredient);
	}
	
	@Test
	public void isValidInitialIngredientBowl() {
		expect(burritoProduct.getBurritoIngredients()).andReturn(ingredients).anyTimes();
		expect(burritoProduct.isBowl()).andReturn(true);
		
		EasyMock.replay(burritoProduct, bowl);
		
		boolean bowl1 = RuleUtils.isValidInitialIngredient(burritoProduct, bowl);
		assertTrue(bowl1);
	}
	
	@Test
	public void isValidInitialIngredientWrap() {
		expect(burritoProduct.getBurritoIngredients()).andReturn(ingredients).anyTimes();
		expect(burritoProduct.isBowl()).andReturn(false);
		
		EasyMock.replay(burritoProduct, wrap);
		
		boolean wrap1 = RuleUtils.isValidInitialIngredient(burritoProduct, wrap);
		assertTrue(wrap1);
	}
	
	@Test
	public void isValidInitialIngredientMismatch() {
		expect(burritoProduct.getBurritoIngredients()).andReturn(ingredients).anyTimes();
		expect(burritoProduct.isBowl()).andReturn(true);
		
		EasyMock.replay(burritoProduct, wrap);
		
		boolean wrap1 = RuleUtils.isValidInitialIngredient(burritoProduct, wrap);
		assertFalse(wrap1);
	}
	
	@Test
	public void isValidInitialIngredientTwice() {
		expect(burritoProduct.getBurritoIngredients()).andReturn(ingredients).anyTimes();
		expect(burritoProduct.isBowl()).andReturn(false);
		
		EasyMock.replay(burritoProduct, wrap);
		
		boolean wrap1 = RuleUtils.isValidInitialIngredient(burritoProduct, wrap);
		assertTrue(wrap1);
		
		this.add(wrap);	
		
		boolean wrap2 = RuleUtils.isValidInitialIngredient(burritoProduct, wrap);
		assertFalse(wrap2);
	}
	
	@Test
	public void isValidBorrito() {
		expect(burritoProduct.getBurritoIngredients()).andReturn(ingredients).anyTimes();
		expect(burritoProduct.isBowl()).andReturn(false);
		EasyMock.replay(burritoProduct, wrap, meat, salsa, topping);
		
		this.add(wrap);	
		this.add(meat);
		this.add(salsa);		
		// two ingredient
		boolean b2 = RuleUtils.isValidBorrito(burritoProduct, RuleUtils.TWO_VALIDATE_BORRITO);
		assertTrue(b2);
		// three ingredient
		this.add(topping);
		boolean b3 = RuleUtils.isValidBorrito(burritoProduct, RuleUtils.THREE_VALIDATE_BURRITO);
		assertTrue(b3);
		
	}
	
	public boolean add(Ingredient ingredient) {
		boolean added = false;
		IngredientType type = ingredient.getType();
		List<Ingredient> list = ingredients.get(type);
		if(list != null) {
			added = list.add(ingredient);
		}
		else {
			list = new ArrayList<Ingredient>();
			list.add(ingredient);
			ingredients.put(type, list);
			added = true;
		}
		return added;
	}
}
