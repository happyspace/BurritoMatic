package com.burrito.matic;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jline.console.ConsoleReader;

import com.burrito.matic.inventory.Ingredient;
import com.burrito.matic.inventory.IngredientType;
import com.burrito.matic.order.Order;
import com.burrito.matic.order.OrderRequest;
import com.burrito.matic.order.OrderUpdate;
import com.burrito.matic.order.Orderable;
import com.burrito.matic.product.BurritoProduct;
import com.burrito.matic.product.Product;
import com.burrito.matic.product.ProductCategory;


public class BurritoMatic 
{
	private static final String PROMPT = "Borrito Matic> ";
	
	private static final String FIRST_LETTER_REGEX = "^([a-zA-Z].*)$";
	private static final String FIRST_NUMBER_REGEX = "^([0-9].*)$";
	
	private static final String[] SUPPORTED_PRODUCTS = {"Burrito"};
	
	public static final String LINE =  System.getProperty("line.separator");
	
	private static final Pattern FIRST_LETTER = Pattern.compile(FIRST_LETTER_REGEX);
	private static final Pattern FIRST_NUMBER = Pattern.compile(FIRST_NUMBER_REGEX);
	
    public static void main( String[] args ) throws IOException 
    {
  //      System.out.println( "Hello Burrito Matic!" );
        
     	ConsoleReader reader = new ConsoleReader();
     	reader.setPrompt(PROMPT);
        
        OrderServiceImpl.INSTANCE.initilizeProducts();
        
        InventoryServiceImpl.INSTANCE.initilizeInventory();
        
        BurritoMatic.terminalLoop(reader);
    }
    
    public static void terminalLoop(ConsoleReader reader) throws IOException {
    	
    	Menus currentMenu = Menus.BURRITO_PRODUCT;
    	OrderUpdate currentOrderUpdate = null;
    	
    	String line = " ";
		PrintWriter out = new PrintWriter(reader.getOutput());
		out.println("Welcome to Burrito Matic!");
		out.println("To create a tasty burrito please follow the directions on the screen.");
		out.println("");
		
		out.println("Please choose a burrito by entering the number preceeding the burrito's name. ");
		
		out.println(currentMenu.getMenu(currentOrderUpdate).createDisplay());
		out.flush();
		
		while ((line = reader.readLine()) != null) {
			if (line.equalsIgnoreCase("quit") || line.equalsIgnoreCase("exit")) {
				out.println("We at Burrito Matic look forward to serving you again!");
				out.flush();
				break;
			}			
			if (line.equalsIgnoreCase("inv")) {
				out.println("Inventory: " + InventoryServiceImpl.INSTANCE.showInventory());
				out.flush();
			}
			if (line.equalsIgnoreCase("orders")) {
				out.println("Orders: " + OrderServiceImpl.INSTANCE.showOrders());
				out.flush();
			}
			if (line.equalsIgnoreCase("restock")) {
				InventoryServiceImpl.INSTANCE.restock();
				out.println("Inventory: " + InventoryServiceImpl.INSTANCE.showInventory());
				out.flush();
			}
			
			line = line.trim();
			
			Matcher mn = FIRST_NUMBER.matcher(line);
			Matcher ml = FIRST_LETTER.matcher(line);
			
			com.burrito.matic.BurritoMatic.Menus.Menu menu = currentMenu.getMenu(currentOrderUpdate);
			
			if(mn.matches()) {
				short menuId = BurritoMatic.parseMenu(line);
				
				if(menu.getMenu().containsKey(menuId)) {
					
					currentOrderUpdate = currentMenu.doAction(menuId, currentOrderUpdate);
					
					if(currentOrderUpdate != null && currentOrderUpdate.getOrder().getCurrentBurrito().isComplete()) {
						if(menu.getNextMenu() != null) {
							currentMenu = menu.getNextMenu();
							menu.setNextMenu(null);
						}
						else {
							currentMenu = Menus.PURCHASE;
						}

						out.println(displayOrder(currentOrderUpdate.getOrder()));
					}
					else if (currentOrderUpdate != null && currentOrderUpdate.getOrder().getCurrentBurrito().isSufficient()){
						if(menu.getNextMenu() != null) {
							currentMenu = menu.getNextMenu();
							menu.setNextMenu(null);
						}
						else {
							currentMenu = Menus.ALACART;
						}

						out.println(displayOrder(currentOrderUpdate.getOrder()));
					}
					else {
						if(menu.getNextMenu() != null) {
							currentMenu = menu.getNextMenu();
							menu.setNextMenu(null);
						}
						else {
							currentMenu = currentMenu.getNextMenu();
						}

						out.println("Add to " + "(" + currentOrderUpdate.getOrder().getOrderId() + "): " + currentOrderUpdate.getOrder().getCurrentBurrito());
					}
					
					
					out.println(currentMenu.getMenu(currentOrderUpdate).createDisplay());
					out.flush();
				}
				else {
					out.println("Please choose an item from the menu.");
				}
			}
			else if(ml.matches()) {
				char subMenu = BurritoMatic.parseSubMenu(line);
				
				if(menu.getSubMenu().containsKey(subMenu)) {
					com.burrito.matic.BurritoMatic.Menus.SubMenuAction menuAction = menu.getSubMenu().get(subMenu);
					if(menuAction.equals(com.burrito.matic.BurritoMatic.Menus.SubMenuAction.SKIP)) {
						currentMenu = currentMenu.getNextMenu();
						out.println("Add to " + currentOrderUpdate.getOrder().getCurrentBurrito());
						// out.println(currentMenu.getMenu(currentOrderUpdate).createDisplay());
						// out.flush();
					}
					else if (menuAction.equals(com.burrito.matic.BurritoMatic.Menus.SubMenuAction.COMPLETE)) {
						currentMenu = Menus.PURCHASE;
						out.println(displayOrder(currentOrderUpdate.getOrder()));
					}
					else if (menuAction.equals(com.burrito.matic.BurritoMatic.Menus.SubMenuAction.PURCHASE)) {
						OrderRequest request = currentOrderUpdate.getNexOrderRequest();
						OrderUpdate orderUpdate = OrderServiceImpl.INSTANCE.purchaseOrder(request);
						currentOrderUpdate = null;
						currentMenu = Menus.BURRITO_PRODUCT;					
						
						out.println("Welcome to Burrito Matic!");
						out.println("To create a tasty burrito please follow the directions on the screen.");
						out.println("");
						
						out.println("Please choose a burrito by entering the number preceeding the burrito's name. ");
					}
					out.println(currentMenu.getMenu(currentOrderUpdate).createDisplay());
					out.flush();
				}
				
				else {
					out.println("Please choose an item from the menu.");
				}
			}
		}
    }
    
    private static String displayOrder(Order order) {
    	StringBuilder sb = new StringBuilder();
    	sb.append("Your order " + "#" + order.getOrderId() + ": " + LINE);
    	List<Product> products = order.getProducts();
    	
    	for (Product product : products) {
    		if (product instanceof BurritoProduct) {
				BurritoProduct bp = (BurritoProduct) product;
				sb.append("    " + bp + LINE);
			}
    		else {
    			sb.append("    " + product.getName() + " " + product.getBaseCost() + LINE);
    		}			
		}
    	
    	return sb.toString();
    }
    
    private enum Menus {
    	BURRITO_PRODUCT(1) {
			@Override
			Menu getMenu(OrderUpdate orderUpdate) {
			
				if(!menu.isInitilized()) {
					LinkedHashMap<Short, Orderable> mainMenu = menu.getMenu();
					Map<String, BurritoProduct> bp = OrderServiceImpl.INSTANCE.getBurritoProducts();
					Iterator<Entry<String, BurritoProduct>> products = bp.entrySet().iterator();
					short i = 1;
					while(products.hasNext()) {
						Entry<String, BurritoProduct> next = products.next();
						mainMenu.put(new Short(i), next.getValue());
						i++;
					}
				}
				
				return menu;
			}

			@Override
			OrderUpdate doAction(short actionId, OrderUpdate update) {
				Orderable product = menu.getMenu().get(actionId);
				if(update == null) {
					return OrderServiceImpl.INSTANCE.createOrder((BurritoProduct) product);
				}
				else {
					OrderRequest request = update.getNexOrderRequest();
					request.setOrderable(product);
					return OrderServiceImpl.INSTANCE.addProduct(request);
				}			
			}
		}, 
    	ADD_BASE(2) {
			@Override
			Menu getMenu(OrderUpdate orderUpdate) {				
				
				if(!menu.isInitilized()) {
					Menus.populateMainMenu(menu, IngredientType.BASE);  
					
					Map<Character, SubMenuAction> subMenu = menu.getSubMenu();
					if(subMenu.size() == 0) {
						subMenu.put(SubMenuAction.SKIP.getActionId(), SubMenuAction.SKIP);
					}
				}
				return menu;
			}

			@Override
			OrderUpdate doAction(short actionId, OrderUpdate orderUpdate) {
				Orderable ingredient = menu.getMenu().get(actionId);
				OrderRequest request = orderUpdate.getNexOrderRequest();
				request.setOrderable(ingredient);
				return OrderServiceImpl.INSTANCE.addIngredient(request);
			}
		},    	
    	ADD_MEAT(3) {
			@Override
			Menu getMenu(OrderUpdate orderUpdate) {

				if(!menu.isInitilized()) {
					Menus.populateMainMenu(menu, IngredientType.MEAT); 
				}
				return menu;
			}

			@Override
			OrderUpdate doAction(short actionId, OrderUpdate orderUpdate) {
				Orderable ingredient = menu.getMenu().get(actionId);
				OrderRequest request = orderUpdate.getNexOrderRequest();
				request.setOrderable(ingredient);
				return OrderServiceImpl.INSTANCE.addIngredient(request);
			}
		},   	
    	ADD_SALSA(4) {
			@Override
			Menu getMenu(OrderUpdate orderUpdate) {
				if(!menu.isInitilized()) {
					Menus.populateMainMenu(menu, IngredientType.SALSA); 
				}
				return menu;	
			}
			
			@Override
			OrderUpdate doAction(short actionId, OrderUpdate orderUpdate) {
				Orderable ingredient = menu.getMenu().get(actionId);
				OrderRequest request = orderUpdate.getNexOrderRequest();
				request.setOrderable(ingredient);
				return OrderServiceImpl.INSTANCE.addIngredient(request);
			}
		},		
    	ADD_TOPPING(5) {

			@Override
			Menu getMenu(OrderUpdate orderUpdate) {
				
				if(!menu.isInitilized()) {
					Menus.populateMainMenu(menu, IngredientType.TOPPINGS); 
				}
				return menu;
			}

			@Override
			OrderUpdate doAction(short actionId, OrderUpdate orderUpdate) {
				Orderable ingredient = menu.getMenu().get(actionId);
				OrderRequest request = orderUpdate.getNexOrderRequest();
				request.setOrderable(ingredient);
				return OrderServiceImpl.INSTANCE.addIngredient(request);
			}
		},

		PURCHASE(6) {
			@Override
			Menu getMenu(OrderUpdate orderUpdate) {
				
				if(!menu.isInitilized()) {
					
					Set<String> supportedProducts = new HashSet<String>(Arrays.asList(SUPPORTED_PRODUCTS));
					
					LinkedHashMap<Short, Orderable> mainMenu = menu.getMenu();
					menu.setPrefix("Add");
					Map<String, ProductCategory> productCategories = OrderServiceImpl.INSTANCE.getProductCategories();
					
					Iterator<Entry<String, ProductCategory>> pcs = productCategories.entrySet().iterator();
					short i = 1;
					while(pcs.hasNext()) {
						Entry<String, ProductCategory> next = pcs.next();
						if(supportedProducts.contains(next.getKey())) {
							mainMenu.put(new Short(i), next.getValue());
						}

						i++;
					}
					
					Map<Character, SubMenuAction> subMenu = menu.getSubMenu();
					if(subMenu.size() == 0) {
						subMenu.put(SubMenuAction.PURCHASE.getActionId(), SubMenuAction.PURCHASE);
					}
				}
				
				return menu;
			}

			@Override
			OrderUpdate doAction(short actionId, OrderUpdate orderUpdate) {
				Orderable orderable = menu.menu.get(actionId);
				menu.setNextMenu(productTypeMenu.get(orderable.getName()));
				return orderUpdate;
			}
		},
		
		ALACART(7) {
			@Override
			Menu getMenu(OrderUpdate orderUpdate) {
				
				if(! menu.isInitilized()) {
					menu.setPrefix("Add");
					LinkedHashMap<Short, Orderable> mainMenu = menu.getMenu();
					
					short i = 1;
					for (final IngredientType type : IngredientType.values()) {
						if(!(type == IngredientType.BASE)) {							
							OrderableWrapper orderable = new OrderableWrapper(type.getName(), type.getName());
							mainMenu.put(new Short(i), orderable);
							i++;
						}
					}
				}

				Map<Character, SubMenuAction> subMenu = menu.getSubMenu();
				if(subMenu.size() == 0) {
					subMenu.put(SubMenuAction.COMPLETE.getActionId(), SubMenuAction.COMPLETE);
				}
				
				
				return menu;
			}

			@Override
			OrderUpdate doAction(short actionId, OrderUpdate orderUpdate) {
				Orderable addIngedientMenu = menu.getMenu().get(actionId);
				IngredientType type = IngredientType.findByName(addIngedientMenu.getName());
				Menus menuForType = typeMenu.get(type);
				menu.setNextMenu(menuForType);
				return orderUpdate;
			}
		}
		;
		
		/**
		ADD_BEVERAGE(7){

			@Override
			String getMenu(OrderUpdate orderUpdate) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			OrderUpdate doAction(short actionId, OrderUpdate orderUpdate) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			int size() {
				// TODO Auto-generated method stub
				return 0;
			}
		},
		
		ADD_DESERT(8){

			@Override
			String getMenu(OrderUpdate orderUpdate) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			OrderUpdate doAction(short actionId, OrderUpdate orderUpdate) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			int size() {
				// TODO Auto-generated method stub
				return 0;
			}
			
		} **/
    	
    	int order;
   		Menu menu = new Menu();
    	private static final Map<Menus, Menus>  nextMenu = new EnumMap<Menus, Menus>(Menus.class);
    	private static final Map<IngredientType, Menus> typeMenu = new EnumMap<IngredientType, BurritoMatic.Menus>(IngredientType.class);
    	private static final Map<String, Menus> productTypeMenu = new HashMap<String, BurritoMatic.Menus>();
    	
    	static {
    		nextMenu.put(BURRITO_PRODUCT,ADD_BASE);
    		nextMenu.put(ADD_BASE, ADD_MEAT);
    		nextMenu.put(ADD_MEAT, ADD_SALSA);
    		nextMenu.put(ADD_SALSA, ADD_TOPPING);
    		nextMenu.put(ADD_TOPPING, BURRITO_PRODUCT);
    		
    		typeMenu.put(IngredientType.BASE, ADD_BASE);
    		typeMenu.put(IngredientType.MEAT, ADD_MEAT);
    		typeMenu.put(IngredientType.SALSA, ADD_SALSA);
    		typeMenu.put(IngredientType.TOPPINGS, ADD_TOPPING);
    		
    		productTypeMenu.put(SUPPORTED_PRODUCTS[0], BURRITO_PRODUCT);
    	}
		
		private Menus(int order) {
			this.order = order;
		}
    	
    	public Menus getNextMenu() {
    		return nextMenu.get(this);
    	}

		abstract Menu getMenu(OrderUpdate orderUpdate);
    	abstract OrderUpdate doAction(short actionId, OrderUpdate orderUpdate);
 
    	
    	protected enum SubMenuAction {
    		SKIP('S', "Skip"), PURCHASE('P', "Purchase"), COMPLETE('C', "Complete");
    		
    		private final char actionId;
    		private final String actionName;

    		private SubMenuAction(char actionId, String actionName) {
    			this.actionId = actionId;
    			this.actionName = actionName;
    		}

    		public char getActionId() {
    			return actionId;
    		}

    		public String getActionName() {
    			return actionName;
    		}
    	}
    	
    	class OrderableWrapper implements Orderable {
    		String name;
    		String SKU;
    		
			public OrderableWrapper(String name, String sKU) {
				this.name = name;
				SKU = sKU;
			}

			@Override
			public String getName() {
				return name;
			}

			@Override
			public String getSKU() {
				return SKU;
			}	
    	}
    	
    	
    	class Menu {

			LinkedHashMap<Short, Orderable> menu = new LinkedHashMap<Short, Orderable>();
    		Map<Character, SubMenuAction> subMenu = new LinkedHashMap<Character, SubMenuAction>();
    		Menus nextMenu;
    		
    		private String prefix = "";
    		private String suffix = "";

    		public LinkedHashMap<Short, Orderable> getMenu() {
    			return menu;
    		}
    		public Map<Character, SubMenuAction> getSubMenu() {
    			return subMenu;
    		}
    		
    		public boolean isInitilized() {
    			if(menu.size() > 0) {
    				return true;
    			}
    			return false;
    		}
    		
			public String createDisplay() {
    			StringBuilder sb = new StringBuilder();
				Iterator<Entry<Short, Orderable>> items = menu.entrySet().iterator();
				while (items.hasNext()) {
					Entry<Short, Orderable> next = items.next();
					sb.append("( " + next.getKey() + " ) ");
					if(!prefix.isEmpty()) {
						sb.append(prefix + " ");
					}
					sb.append(next.getValue().getName() + " " + suffix);
					sb.append(System.getProperty("line.separator"));	
				}
				
				Iterator<Entry<Character, SubMenuAction>> si = subMenu.entrySet().iterator();
				while (si.hasNext()) {
					sb.append(System.getProperty("line.separator"));
					Entry<Character, SubMenuAction> next = si.next();
					sb.append("( " + next.getKey() + " )" + " " + next.getValue().getActionName());
					sb.append(System.getProperty("line.separator"));	
				}
				return sb.toString();
			}
			
			

			public Menus getNextMenu() {
				return nextMenu;
			}
			public void setNextMenu(Menus nextMenu) {
				this.nextMenu = nextMenu;
			}
			
			public void setPrefix(String prefix) {
				this.prefix = prefix;
			}
			public void setSuffix(String suffix) {
				this.suffix = suffix;
			}
    	}
    		
    	private static void populateMainMenu(final Menu menu, IngredientType type) {
			List<Ingredient> base = InventoryServiceImpl.INSTANCE.ingredientsForType(type);
			LinkedHashMap<Short, Orderable> mm = menu.getMenu();
			short i = 1;
			for (Ingredient ingredient : base) {
				if(!ingredient.isInitial()) {
					mm.put(i, ingredient);
					i++;
				}
			}
    	}    	
    }
    
    private static char parseSubMenu(String line) {
    	char subMenu = 0;
    	if(line.length() > 0) {
        	char sm = line.charAt(0);
        	subMenu = Character.toUpperCase(sm);
    	}
    	return subMenu;
    }
    
	private static short parseMenu(String line) {
		short menuSelection = 0;
		
		Pattern num = Pattern.compile("\\d+");
		Matcher matcher = num.matcher(line);
		
		if(matcher.find()) {
			String group = matcher.group();
			try {
				menuSelection = Short.parseShort(group);
			} catch (Exception e) {}
		}

		return menuSelection;
	}
}
