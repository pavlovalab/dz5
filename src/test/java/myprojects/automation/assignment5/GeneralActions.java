package myprojects.automation.assignment5;


import myprojects.automation.assignment5.model.ProductData;
import myprojects.automation.assignment5.utils.Properties;
import myprojects.automation.assignment5.utils.logging.CustomReporter;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.Random;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Contains main script actions that may be used in scripts.
 */
public class GeneralActions {
    private WebDriver driver;
    private WebDriverWait wait;

    public GeneralActions(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, 30);
    }

    /**
     * Waits until page loader disappears from the page
     */
    public void waitForContentLoad() {
        wait.until((ExpectedCondition<Boolean>) wd ->
                ((JavascriptExecutor) wd).executeScript("return document.readyState").equals("complete"));
    }

    public void navigate(String url) {
        driver.navigate().to(url);
        waitForContentLoad();
    }

    public void checkSiteVersion(boolean mobile) {
        // TODO open main page and validate website version
        CustomReporter.log("Проверка открываемой версии магазина");
        navigate(Properties.getBaseUrl());

        List<WebElement> rows = driver.findElements(By.cssSelector("div.mobile"));
        if (mobile) assertFalse(rows.isEmpty(), "Не мобильная версия сайта");
        else assertTrue(rows.isEmpty(), "Не полная версия сайта");
    }

    /**
     * Extracts product information from opened product details page.
     *
     * @return
     */
    public ProductData getOpenedProductInfo() {
        CustomReporter.logAction("Get information about currently opened product");

        String product_name = driver.findElement(By.cssSelector(".row>div>h1")).getText();
        String product_price = driver.findElement(By.cssSelector(".current-price>span")).getAttribute("content");


        WebElement qtyW = driver.findElement(By.cssSelector("#product-availability"));
        assertTrue(qtyW.getText().toLowerCase().contains("in stock"), "Нет в наличии выбранного товара");

        qtyW = driver.findElement(By.cssSelector(".nav-link[href='#product-details']"));

        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", qtyW);

        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".nav-link.active[href='#product-details']")));

        int index = -1;
        String qtyS = "";
        do {
            qtyS = driver.findElement(By.cssSelector(".product-quantities>span")).getText();
            index = qtyS.indexOf(" ");
        } while (index == -1);

        int qty = Integer.parseInt(qtyS.substring(0, index).trim());
        return new ProductData(product_name, qty, Float.parseFloat(product_price), driver.getCurrentUrl());
    }

    public void openRandomProduct() {
        // TODO implement logic to open random product before purchase
        navigate(Properties.getBaseUrl());

        CustomReporter.log("Проверка появления нового продукта на сайте");

        WebElement all_button = driver.findElement(By.className("all-product-link"));
        assertTrue(all_button.isEnabled(), "Невозможно перейти к спику всех продуктов");

        String next_href1 = all_button.getAttribute("href");
        navigate(next_href1);

        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("category")));

        boolean flag = false;
        WebElement item = null;

        List<WebElement> rows = driver.findElements(By.cssSelector(".product-description>h1>a"));
        assertTrue(!rows.isEmpty(), "Пустой список продуктов");

        Random random = new Random();
        int index = random.nextInt(rows.size());

        WebElement prod = rows.get(index);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", prod);

        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("main")));
    }

    public void addProductToCart(ProductData product) throws InterruptedException {

        CustomReporter.log("Добавить продукт в корзину");

        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".ui-autocomplete")));

        String prod_url = driver.getCurrentUrl();

        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".add-to-cart")));
        WebElement btn = driver.findElement(By.cssSelector(".add-to-cart"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
//        driver.findElement(By.cssSelector("button.add-to-cart")).click();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("#blockcart-modal[style='display: block;']")));

        btn = driver.findElement(By.cssSelector(".cart-content>.btn-primary"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);

        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".ui-autocomplete")));

        List<WebElement> rows = driver.findElements(By.cssSelector(".cart-item"));
        assertTrue(!rows.isEmpty(), "Пустой список в корзине");

        assertEquals(rows.size(), 1, "В корзине больше одной строки");

        assertEquals(driver.findElement(By.cssSelector(".product-line-info>a")).getText().toUpperCase(), product.getName().toUpperCase(), "Название продукта не соответствует");

        assertTrue(driver.findElement(By.cssSelector(".product-price>strong")).getText().contains(product.getPrice()), "Не соответствует цена продукта в корзине");
        assertEquals(driver.findElement(By.name("product-quantity-spin")).getAttribute("value"), "1", "Не соответствует количество продукта в корзине");
    }

    public void orderCreation() {

        CustomReporter.log("Оформление заказа");

        WebElement btn = driver.findElement(By.cssSelector("div.checkout>div>.btn"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);

        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".ui-autocomplete")));

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("firstname")));
        driver.findElement(By.name("firstname")).sendKeys(Keys.BACK_SPACE);
        driver.findElement(By.name("firstname")).sendKeys("Alex");
        driver.findElement(By.name("lastname")).sendKeys("Pav");
        driver.findElement(By.name("email")).sendKeys("alex@pav.ua");

        btn = driver.findElement(By.cssSelector("button[name='continue'][data-link-action='register-new-customer']"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);

        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".ui-autocomplete")));

        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("body#checkout")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("address1")));
        driver.findElement(By.name("address1")).sendKeys(Keys.BACK_SPACE);
        driver.findElement(By.name("address1")).sendKeys("пер. Кияновский 12");
        driver.findElement(By.name("postcode")).sendKeys("22009");
        driver.findElement(By.name("city")).sendKeys("Киев");

        btn = driver.findElement(By.cssSelector("button[name='confirm-addresses']"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);

        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".ui-autocomplete")));

        btn = driver.findElement(By.cssSelector("button[name='confirmDeliveryOption']"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);

        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".ui-autocomplete")));

        btn = driver.findElement(By.cssSelector("#payment-option-1"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);

        btn = driver.findElement(By.cssSelector("input[name='conditions_to_approve[terms-and-conditions]']"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);

        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#payment-confirmation>div>button")));

        btn = driver.findElement(By.cssSelector("#payment-confirmation>div>button"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
    }

    public void orderValidate(ProductData product) {

        CustomReporter.log("Проверка заказа");

        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".ui-autocomplete")));

        WebElement mes = driver.findElement(By.cssSelector("#content-hook_order_confirmation>.card-block>div>div>.card-title"));
        assertTrue(mes.getText().toLowerCase().contains("ваш заказ подтверждён"), "Нет сообщения о подтверждении заказа");

        List<WebElement> rows2 = driver.findElements(By.cssSelector(".order-line"));
        assertTrue(!rows2.isEmpty(), "Пустой список в заказе");

        assertEquals(rows2.size(), 1, "В заказе больше одной строки");

        WebElement prod = rows2.get(0);

        assertTrue(prod.findElement(By.cssSelector(".details")).getText().toUpperCase().contains(product.getName().toUpperCase()), "Название продукта не соответствует в заказе");

        assertTrue(prod.findElement(By.cssSelector(".qty>.row>.text-xs-left")).getText().contains(product.getPrice()), "Не соответствует цена продукта в заказе");
//        assertTrue(driver.findElement(By.cssSelector(".product-quantities>span")).getText().contains(newProduct.getQty().toString()), "Не соответствует количество продукта");

//
    }

    public void CheckUpdate(ProductData product) {

        CustomReporter.log("Проверка изменения наличия");

        navigate(product.getUrl());

        WebElement qtyW = driver.findElement(By.cssSelector(".nav-link[href='#product-details']"));

        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", qtyW);

        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".nav-link.active[href='#product-details']")));

        int index = -1;
        String qtyS = "";
        do {
            qtyS = driver.findElement(By.cssSelector(".product-quantities>span")).getText();
            index = qtyS.indexOf(" ");
        } while (index == -1);
        int qty = Integer.parseInt(qtyS.substring(0, index).trim()) + 1;

        assertEquals(qty, product.getQty(), "Не уменьшилось количество продукта в наличии");
     }
}
