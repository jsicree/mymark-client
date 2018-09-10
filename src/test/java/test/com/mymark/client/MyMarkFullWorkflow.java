package test.com.mymark.client;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mymark.customer.api.CustomerDto;
import com.mymark.mymarkcustomer.api.client.ClientException;
import com.mymark.mymarkcustomer.api.client.CustomerRestClient;
import com.mymark.mymarkproduct.api.client.ProductRestClient;
import com.mymark.mymarkshoppingcart.api.client.ShoppingCartRestClient;
import com.mymark.product.api.ProductDetailsDto;
import com.mymark.product.api.ProductDto;
import com.mymark.shoppingcart.api.CartLineItemDto;
import com.mymark.shoppingcart.api.ShoppingCartDto;

public class MyMarkFullWorkflow {

	private static CustomerRestClient custClient;

	private static ProductRestClient productClient;
	
	private static ShoppingCartRestClient cartClient;

	protected final static Logger log = LoggerFactory.getLogger(MyMarkFullWorkflow.class);
	
	public static String CUST_SERVICE_URL = "http://localhost:8081/api";
	public static String PROD_SERVICE_URL = "http://localhost:8082/api";
	public static String CART_SERVICE_URL = "http://localhost:8083/api";
	public static String USERNAME = "appuser";
	public static String PASSWORD = "password";

	public static final String[] customerData = { "Alex", "Lifeson", "lerxst", "lerxst@foo.com", "password1234" };
	
	@BeforeClass
	public static void setup() {
		custClient = new CustomerRestClient(CUST_SERVICE_URL, USERNAME, PASSWORD);
		productClient = new ProductRestClient(PROD_SERVICE_URL, USERNAME, PASSWORD);
		cartClient = new ShoppingCartRestClient(CART_SERVICE_URL, USERNAME, PASSWORD);
	}

	@Test
	public void testWorkflow01() {

		
		System.out.println("\nSTEP 1: Create a customer");
		CustomerDto customer = null;
		try {
			// Create a customer
			customer = custClient.createNewCustomer(customerData[0], customerData[1], customerData[2],
					customerData[3], customerData[4]);
			org.junit.Assert.assertNotNull("Create Customer has returned null.", customer);
			System.out.println("Customer created: " + prettyPrintCustomer(customer));

		} catch (ClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("\nSTEP 2: View Products");
		// View Products
		try {
			List<ProductDto> productList = productClient.getProducts();
			org.junit.Assert.assertNotNull("Get Product List has returned null.", productList);
			org.junit.Assert.assertNotEquals("Get Product List has returned no products.", productList.size(), 0);
			System.out.println(prettyPrintProductList(productList));
		} catch (com.mymark.mymarkproduct.api.client.ClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		System.out.println("\nSTEP 3: View Product Details for a Product");
		// View Product Details
		try {
			ProductDetailsDto prodDetails = productClient.getProductDetails("PROD-001");
			org.junit.Assert.assertNotNull("Get Product Details has returned null.", prodDetails);
			System.out.println(prettyPrintProductDetails(prodDetails));
		} catch (com.mymark.mymarkproduct.api.client.ClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("\nSTEP 4: View Customer's Shopping Cart");
		// View Cart
		ShoppingCartDto cartDto;
		try {
			cartDto = cartClient.getShoppingCart(customer.getIdentifier());
			System.out.println(prettyPrintShoppingCart(cartDto));
		} catch (com.mymark.mymarkshoppingcart.api.client.ClientException e) {
			e.printStackTrace();
		}

		System.out.println("\nSTEP 5: Add Items to Customer's Shopping Cart");
		// Add 2 items to Cart
		try {
			cartDto = cartClient.addItemToShoppingCart(customer.getIdentifier(), "PROD-001", 2);
			System.out.println(prettyPrintShoppingCart(cartDto));
		} catch (com.mymark.mymarkshoppingcart.api.client.ClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("\nSTEP 6: Remove an Item from Customer's Shopping Cart");
		// Remove 1 item from Cart
		try {
			cartDto = cartClient.removeItemFromShoppingCart(customer.getIdentifier(), "PROD-001", 1);
			System.out.println(prettyPrintShoppingCart(cartDto));
		} catch (com.mymark.mymarkshoppingcart.api.client.ClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("\nSTEP 7: Add New Items to Customer's Shopping Cart");
		try {
			cartDto = cartClient.addItemToShoppingCart(customer.getIdentifier(), "PROD-002", 3);
			System.out.println(prettyPrintShoppingCart(cartDto));
		} catch (com.mymark.mymarkshoppingcart.api.client.ClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		System.out.println("\nSTEP 8: Remove All Items from Customer's Shopping Cart");
//		try {
//			cartDto = cartClient.removeAllItemsFromShoppingCart(customer.getIdentifier());
//			System.out.println(prettyPrintShoppingCart(cartDto));
//		} catch (com.mymark.mymarkshoppingcart.api.client.ClientException e) {
//			e.printStackTrace();
//		}

		System.out.println("\nSTEP 9: Delete Customer and ShoppingCart");
		// Delete the customer			
		System.out.println("Deleting customer " + customer.getUserName());
		try {
			custClient.deleteCustomer(customer.getIdentifier());
		} catch (ClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String prettyPrintCartLineItem(CartLineItemDto dto) {
		StringBuilder builder = new StringBuilder();
		builder.append("CartLineItem [productCode = " + dto.getProductCode());
		builder.append(", price = " + dto.getLinePrice());
		builder.append(", quantity = " + dto.getQuantity());
		builder.append("]");
		return builder.toString();
	}

	
	private String prettyPrintShoppingCart(ShoppingCartDto dto) {
		StringBuilder builder = new StringBuilder();
		builder.append("Shopping Cart [");
		builder.append("\n\tcustomerIdentifier = " + dto.getCustomerIdentifier());
		builder.append("\n\tlineItems:");
		for (CartLineItemDto item : dto.getLineItems().getLineItems()) {
			builder.append("\n\t\t" + prettyPrintCartLineItem(item));
		}
		builder.append("\n\ttotalQuantity = " + dto.getTotalQuantity());
		builder.append("\n\ttotalPrice = " + dto.getTotalPrice());
		builder.append("\n]");		
		return builder.toString();
	}

	private String prettyPrintProductDetails(ProductDetailsDto dto) {
		StringBuilder builder = new StringBuilder();
		builder.append("ProductDetails [");
		builder.append("\n\tname = " + dto.getName());
		builder.append("\n\tproductCode = " + dto.getProductCode());
		builder.append("\n\tshortDesc = " + dto.getShortDescription());
		builder.append("\n\tlongDesc = " + dto.getLongDescription());
		builder.append("\n\tprice = " + dto.getPrice());
		builder.append("\n\tinStock = " + ((dto.getAvailableInventory() > 0L) ? "Yes" : "No"));
		builder.append("\n]");
		return builder.toString();
	}

	private String prettyPrintProductList(List<ProductDto> productList) {

		StringBuilder builder = new StringBuilder();
		builder.append("Product List [");
		for (ProductDto prod : productList) {
			builder.append("\n\t" + prettyPrintProduct(prod));
		}
		builder.append("\n]");		
		return builder.toString();
	}

	private String prettyPrintProduct(ProductDto dto) {
		
		StringBuilder builder = new StringBuilder();
		builder.append("Product [name = " + dto.getName());
		builder.append(", productCode = " + dto.getProductCode());
		builder.append(", shortDesc = " + dto.getShortDescription());
		builder.append(", price = " + dto.getPrice());
		builder.append("]");

		return builder.toString();
	}	
	
	private String prettyPrintCustomer(CustomerDto dto) {
		
		StringBuilder builder = new StringBuilder();
		builder.append("Customer [name = " + dto.getFirstName());
		builder.append(" " + dto.getLastName());
		builder.append(", username = " + dto.getUserName());
		builder.append(", email = " + dto.getEmail());
		builder.append(", identifier = " + dto.getIdentifier());
		builder.append("]");

		return builder.toString();
	}	
}
