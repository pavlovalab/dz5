package myprojects.automation.assignment5.tests;

import myprojects.automation.assignment5.BaseTest;
import myprojects.automation.assignment5.model.ProductData;
import myprojects.automation.assignment5.utils.logging.CustomReporter;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.Test;

public class PlaceOrderTest extends BaseTest {

    @Test
    public void checkSiteVersion() throws InterruptedException {
        // TODO open main page and validate website version
        actions.checkSiteVersion(isMobileTesting);
    }

    @Test
    public void createNewOrder() throws InterruptedException {
        // TODO implement order creation test

        // open random product
        actions.openRandomProduct();

        // save product parameters
        ProductData product = actions.getOpenedProductInfo();

        // add product to Cart and validate product information in the Cart
        actions.addProductToCart(product);

        // proceed to order creation, fill required information
        actions.orderCreation();

        // place new order and validate order summary
        actions.orderValidate(product);

        // check updated In Stock value
        actions.CheckUpdate(product);

        CustomReporter.log("Тест пройден успешно");

    }

}
