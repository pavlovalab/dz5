package myprojects.automation.assignment5;


import myprojects.automation.assignment5.model.ProductData;
import myprojects.automation.assignment5.utils.Properties;
import myprojects.automation.assignment5.utils.logging.CustomReporter;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
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
        //      rows.get(index).click();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("main")));
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
        return new ProductData(product_name, 0, Float.parseFloat(product_price));
    }

    public void addProductToCart(ProductData product) throws InterruptedException {

//        Thread.sleep(2000);
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#quantity_wanted[style='display: block;']")));
        WebElement btn = driver.findElement(By.cssSelector(".add-to-cart"));

        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
//        driver.findElement(By.cssSelector("button.add-to-cart")).click();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("#blockcart-modal[style='display: block;']")));

        btn = driver.findElement(By.cssSelector(".cart-content>.btn-primary"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);

        List<WebElement> rows = driver.findElements(By.cssSelector(".cart-item"));
        assertTrue(!rows.isEmpty(), "Пустой список в корзине");

        assertEquals(rows.size(), 1, "В корзине больше одной строки");

        assertEquals(driver.findElement(By.cssSelector(".product-line-info>a")).getText().toUpperCase(), product.getName().toUpperCase(), "Название продукта не соответствует");

        List<WebElement> rows2 = driver.findElements(By.cssSelector(".product-line-info>.value"));

        assertTrue(driver.findElement(By.cssSelector(".product-price>strong")).getText().contains(product.getPrice()), "Не соответствует цена продукта");
        assertEquals(driver.findElement(By.name("product-quantity-spin")).getAttribute("value"), "1", "Не соответствует количество продукта");

        btn = driver.findElement(By.cssSelector("div.checkout>div>.btn"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);


        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("body#checkout")));
        driver.findElement(By.name("firstname")).sendKeys("Alex");
        driver.findElement(By.name("lastname")).sendKeys("Pav");
        driver.findElement(By.name("email")).sendKeys("alex@pav.by");

        btn = driver.findElement(By.cssSelector("button.continue"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);


        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("body#checkout")));
        driver.findElement(By.name("address1")).sendKeys("Rokossovskogo");
        driver.findElement(By.name("postcode")).sendKeys("220094");
        driver.findElement(By.name("city")).sendKeys("Minsk");

        btn = driver.findElement(By.cssSelector("button.continue"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
    }
}
