package myprojects.automation.assignment5.tests;

import myprojects.automation.assignment5.BaseTest;
import myprojects.automation.assignment5.model.ProductData;
import org.testng.annotations.Test;

public class PlaceOrderTest extends BaseTest {

    @Test
    public void checkSiteVersion() throws InterruptedException {
        // TODO open main page and validate website version
        actions.checkSiteVersion();
    }

    @Test
    public void createNewOrder() throws InterruptedException {
        // TODO implement order creation test

        System.out.println("createNewOrder");
        // open random product
        actions.openRandomProduct();

        // save product parameters
        ProductData product = actions.getOpenedProductInfo();

        // add product to Cart and validate product information in the Cart
        actions.addProductToCart(product);

        Thread.sleep(10000);
        // proceed to order creation, fill required information

        // place new order and validate order summary

        // check updated In Stock value
    }

}
