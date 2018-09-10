package myprojects.automation.assignment5;

import myprojects.automation.assignment5.utils.logging.EventHandler;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

/**
 * Base script functionality, can be used for all Selenium scripts.
 */
public abstract class BaseTest {
    protected EventFiringWebDriver driver;
    protected GeneralActions actions;
    protected boolean isMobileTesting;

    /**
     *
     * @param browser Driver type to use in tests.
     *
     * @return New instance of {@link WebDriver} object.
     */
    private WebDriver getDriver(String browser) {
        switch (browser) {
            case "firefox":
                System.setProperty(
                        "webdriver.gecko.driver",
//нет в през                        getResource("/geckodriver.exe"));
               //добавила
                        new File(BaseTest.class.getResource("/geckodriver.exe").getFile()).getPath());
                //               System.setProperty("webdriver.gecko.driver", MainClass.class.getResource("geckodriver.exe").getPath());
                return new FirefoxDriver();
            case "ie":
            case "internet explorer":
                System.setProperty(
                        "webdriver.ie.driver",
//нет в през                        getResource("/IEDriverServer.exe"));
                //добавила
                        new File(BaseTest.class.getResource("/IEDriverServer.exe").getFile()).getPath());
                InternetExplorerOptions options = new InternetExplorerOptions();
                options.setCapability(InternetExplorerDriver.NATIVE_EVENTS, true);//в презент false
                options.setCapability(InternetExplorerDriver.REQUIRE_WINDOW_FOCUS, true);
                options.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS,true);
                options.setCapability(InternetExplorerDriver.IE_ENSURE_CLEAN_SESSION,true);
                options.setCapability(InternetExplorerDriver.IGNORE_ZOOM_SETTING,true);
                return new InternetExplorerDriver(options);//в презент пустые скобки
            case "chrome":
            default:
                System.setProperty(
                        "webdriver.chrome.driver",
// я добавила из презент
                        new File(BaseTest.class.getResource("/chromedriver.exe").getFile()).getPath());
//нет в презент                getResource("/chromedriver.exe"));
                return new ChromeDriver();

//я из презент  case"headless-chrome":
//я из презент                System.setProperty(
//я из презент                        "webdriver.chrome.driver",
//я из презент                        new File(BaseTest.class.getResource("/chromedriver.exe").getFile()).getPath());
//я из презент                ChromeOptions options1 = new ChromeOptions();
//я из презент                options1.addArguments("headless");
// я из презент               options1.addArguments("window-size=800x600");
//я из презент                return new ChromeDriver(options1);

//я из презент для удаленного           case"remote-chrome":
//я из презент                ChromeOptions optionsRemote = new ChromeOptions();
//я из презент                try {
//я из презент                   return new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"),optionsRemote);
//я из презент                }catch (MalformedURLException e){
//я из презент                    e.printStackTrace();
//я из презент                }
//я из презент                return null;

//я из презент             case"mobile":
//я из презент                 System.setProperty(
//я из презент                         "webdriver.chrome.driver",
//я из презент                         new File(BaseTest.class.getResource("/chromedriver.exe").getFile()).getPath());
//я из презент                 Map<String,String> mobileEmulation = new HashMap<>();
//я из презент                 mobileEmulation.put("deviceName","iPhone6");
//я из презент                 ChromeOptions optionsRemote = new ChromeOptions();
//я из презент                 ChromeOptions.setExperimentalOption ("mobileEmulation",mobileEmulation);
// я из презент                return new ChromeDriver(ChromeOptions);
        }
    }

    /**
     * @param resourceName The name of the resource
     * @return Path to resource
     */
    private String getResource(String resourceName) {
        try {
            return Paths.get(BaseTest.class.getResource(resourceName).toURI()).toFile().getPath();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return resourceName;
    }

    /**
     * Prepares {@link WebDriver} instance with timeout and browser window configurations.
     *
     * Driver type is based on passed parameters to the automation project,
     * creates {@link ChromeDriver} instance by default.
     *
     */
    @BeforeClass
    @Parameters({"selenium.browser", "selenium.grid"})
    public void setUp(@Optional("firefox") String browser, @Optional("") String gridUrl) {
        // TODO create WebDriver instance according to passed parameters
        driver = new EventFiringWebDriver(getDriver(browser));
        driver.register(new EventHandler());

        driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
        driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
        // unable to maximize window in mobile mode
        if (!isMobileTesting(browser))
            driver.manage().window().maximize();

// из презент        wait = new WebDriverWait(driver,5);

        isMobileTesting = isMobileTesting(browser);

        actions = new GeneralActions(driver);
    }

    /**
     * Closes driver instance after test class execution.
     */
    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    /**
     *
     * @return Whether required browser displays content in mobile mode.
     */
    private boolean isMobileTesting(String browser) {
        switch (browser) {
            case "android":
                return true;
            case "firefox":
            case "ie":
            case "internet explorer":
            case "chrome":
            default:
                return false;
        }
    }
}
