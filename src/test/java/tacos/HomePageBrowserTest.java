package tacos; // src/test/java   to standardowa lokalizacja dla kodu testowego

import java.time.Duration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT) // określamy jak ma zostać uruchomiony serwer/aplikacja 
public class HomePageBrowserTest {

    @LocalServerPort
    private int port;
    private static HtmlUnitDriver browser;

    @BeforeAll
    public static void setup() {
        browser = new HtmlUnitDriver();

        // zaczekaj na załadowanie całej strony
        browser.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));
    }

    @AfterAll
    public static void teardown() {
        // rozbiórka
        browser.quit();
    }

    @Test
    public void testHomePage() {
        System.out.println("PORTNUMBER = " + port);
        String homePage = "http://localhost:" + port;
        browser.get(homePage);

        String titleText = browser.getTitle();
        Assertions.assertEquals("Taco Cloud", titleText);

        String h1Text = browser.findElement(By.tagName("h1")).getText();// tekst <h1>
        Assertions.assertEquals("Welcome to...", h1Text);

        String imgSrc = browser.findElement(By.tagName("img")).getAttribute("src");
        Assertions.assertTrue(imgSrc.contains("/images/TacoCloud.png"));
    }

}