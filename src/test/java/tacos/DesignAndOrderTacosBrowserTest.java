package tacos;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;

/*1. Uruchamia całą aplikację Spring Boot do testów
 * 2. Uruchamiamy serwer na lsowym porcie co eliminuje konflikt z innymi procesami
 * To wszystko po to aby warunki były zbliżone do tych rzeczywistych
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT) // potrzebujemy serwera http
public class DesignAndOrderTacosBrowserTest {
    private static ChromeDriver browser;
    private static ChromeOptions options;

    @LocalServerPort
    private int port;

    // metoda musi być STATIC bo domyślnie w JUNIT5, każdy test (@Test) jest uruchamiany na NOWEJ INSTANCJI klasy testowej 
    @BeforeAll
    public static void setup() {
        options = new ChromeOptions();
        options.addArguments("--window-size=1920,1080");
        browser = new ChromeDriver(options);
        browser.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
    }

    @AfterAll
    public static void closebrowser() {
        browser.quit(); // zamknij wszystkie okna
    }
    
    @Test
    public void testDesignATacoPage_HappyPath() throws Exception{
        browser.get(homePageUrl());

        // 1. przejdź do formularza taco
        clickDesignATaco();

        // 2. sprawdź poprawność składników
        assertDesignPageElements();
        
        // 3. zbuduj taco
        buildAndSubmitATaco("Basic Taco", "FLTO", "GRBF", "CHED", "TMTO", "SLSA");
    
        // 4. kliknij w budowanie kolejnego taco
        clickDesignAnotherTaco();

        // 5. zbuduj another taco
        buildAndSubmitATaco("Another Taco", "COTO", "CARN", "JACK", "LETC", "SRCR");

        // 6. wypełnij formularz niepoprawnymi danymi
        fillInAndSubmitOrderForm();

        System.out.println("AKTUALNY URL: " + browser.getCurrentUrl());
        System.out.println("OCZEKIWANY z homePageUrl(): " + homePageUrl());
        // 7. sprawdź adres url (home)
        assertThat(browser.getCurrentUrl()).isEqualTo(homePageUrl());
        
    }


    @Test
    public void testDesignATacoPage_EmptyDesignAndInfo() throws Exception{
        browser.get(homePageUrl());
        // browser.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        clickDesignATaco();// 1. kliknij odnośnik do modelowania taco
        
        assertDesignPageElements(); // 2. sprawdz poprawność składników
        
        submitEmptyDesignForm();    // 3. pusty taco
        
        buildAndSubmitATaco("Basic Taco", "FLTO", "GRBF", "CHED", "TMTO", "SLSA");  // 4. poprawny taco
        
        submitEmptyOrderForm(); // 5. pusty formularz informacyjny
        fillInAndSubmitOrderForm(); // 6. poprawny form
        assertThat(browser.getCurrentUrl()).isEqualTo(homePageUrl());   // 7. correct url
    }

    @Test
    public void testDesignATacoPage_InvalidOrderInfo() {
        browser.get(homePageUrl());

        clickDesignATaco();
        assertDesignPageElements();
        buildAndSubmitATaco("a", "FLTO", "CHED"); // wrong name, correct ingredients
        assertThat(browser.findElement(By.className("validationError")).getText()).isEqualTo("Name must be at least 3 characters long");
        buildAndSubmitATaco("Basic Taco", "FLTO", "GRBF", "CHED", "TMTO", "SLSA");  // poprawny taco
        submitInvalidOrderForm();
        fillInAndSubmitOrderForm();
        assertThat(browser.getCurrentUrl()).isEqualTo(homePageUrl());
        
    }
    //
    // Browser test methods
    //

    private void submitEmptyDesignForm() {
        // 1. sprawdź url
        assertThat(browser.getCurrentUrl()).isEqualTo(designPageUrl());
        browser.findElement(By.className("button")).click();

        // 2. sprawdź komunikaty walidacyjne
        List<String> errorMessages = getErrorMessages();
        assertThat(errorMessages.size()).isEqualTo(2);
        
        assertThat(errorMessages).containsExactlyInAnyOrder(
            "Name must be at least 3 characters long",
            "You must chose at least 1 ingredient"
        );
    }

    private void submitEmptyOrderForm() throws Exception{
        assertThat(browser.getCurrentUrl()).isEqualTo(orderDetailsPageUrl() + "/current");
        
        // od razu wyślij pusty formularz
        browser.findElement(By.cssSelector("input[value='Submit']"))
            .submit();

        // sprawdź ponownie czy url jest poprawny
        assertThat(browser.getCurrentUrl()).isEqualTo(orderDetailsPageUrl());
        
        List<String> errorMessages = getErrorMessages();
        assertThat(errorMessages.size()).isEqualTo(9);
        // sprawdź czy komunikaty walidacyjne są takie jak określone w klasie domenowej (TacoOrder)
        assertThat(errorMessages).containsExactlyInAnyOrder(
            "Please correct the problems below and resubmit.",//
            "Delivery name is required",
            "Street is required",
            "City is required",
            "State is required",
            "Zip code is required",
            "Incorrect credit card number",//
            "Your credit card is probably out of date",//
            "Invalid CVV"//
        );
    }

    private List<String> getErrorMessages() {
        List<WebElement> validationErrorElements = browser.findElements(By.className("validationError"));
        // System.out.println("dlugosc validationErrorElements = " + validationErrorElements.size());
        List<String> validationErrors = validationErrorElements.stream()
            .map((element) -> element.getText())
            .collect(Collectors.toList());
        
            System.out.println("XDDD: " + validationErrors);
        return validationErrors; 
    }
    
    private void submitInvalidOrderForm() {
        assertThat(browser.getCurrentUrl()).isEqualTo(orderDetailsPageUrl() + "/current");//1. url

        // 2. wypełnienie pól BŁĘDNYMI danymi 
        fillField("input#deliveryName", "I");
        fillField("input#deliveryStreet", "1");
        fillField("input#deliveryCity", "F");
        fillField("input#deliveryState", "C");
        fillField("input#deliveryZip", "8");
        fillField("input#ccNumber", "1234432112344322");
        fillField("input#ccExpiration", "14/91");
        fillField("input#ccCVV", "1234");

        // 3. submit
        browser.findElement(By.cssSelector("input[value='Submit']")).submit();

        // // 4. sprawdź dane walidacyjne
        assertThat(getErrorMessages()).containsExactlyInAnyOrder(
            "Please correct the problems below and resubmit.",
            "Incorrect credit card number",
            "Your credit card is probably out of date",
            "Invalid CVV"
        );
    }

    private void fillInAndSubmitOrderForm() {
        // 1. sprawdź url
        assertThat(browser.getCurrentUrl()).startsWith(orderDetailsPageUrl());

        // wypełnij pola poprawnymi danymi
        fillField("input#deliveryName", "Hipo");
        fillField("input#deliveryStreet", "House num 1.");
        fillField("input#deliveryCity", "Hipolandia");
        fillField("input#deliveryState", "PL");
        fillField("input#deliveryZip", "81019");
        fillField("input#ccNumber", "4111111111111111");
        fillField("input#ccExpiration", "12/35");
        fillField("input#ccCVV", "123");

        browser.findElement(By.cssSelector("input[value='Submit']")).click();
    }

    private void fillField(String cssFieldName, String value) {
        WebElement element = browser.findElement(By.cssSelector(cssFieldName));

        element.click();
        
        element.clear();
        element.sendKeys(value);
    }

    private void clickDesignAnotherTaco() {
        // 1. sprawdź poprawność adresu URL
        assertThat(browser.getCurrentUrl()).isEqualTo(orderDetailsPageUrl() + "/current");
        browser.findElement(By.cssSelector("a#another")).click();
    }

    private void buildAndSubmitATaco(String name, String ... ingredients_IDs) {
        // 1. odszukaj i klikaj
        for(String id : ingredients_IDs) {
            browser.findElement(By.cssSelector("input[value='" + id + "']")).click();
        }

        // wpisz nazwę taco
        fillField("input#name", name);
        browser.findElement(By.className("button")).submit();
    }

    private void clickDesignATaco() {
                // Asercje w AssertJ są "fluent" tzn. dzielą się na 2 etapy:
            // 1. przekazanie wartości lub obiektu do asercji,
            // 2. doprecyzowanie jakiego warunku oczekujesz
        // przejdź do formularza kreacji taco, gdy:
            // 1. adres na któym jesteśmy, będzie odpowiedni.

        assertThat(browser.getCurrentUrl())
            .isEqualTo(homePageUrl());
        
            // przejdź do formularza
        browser.findElement(By.id("start"))
            .click();
    }

    private void assertDesignPageElements() {
        // 1. czy adres jest odpowiadający stronie z formularzem
        assertThat(browser.getCurrentUrl()).isEqualTo(designPageUrl());

        // 2. pobierz wszystkie obiekty o class= 'ingredient-group' i sprawdz czy jest ich 5
        List<WebElement> ingredient_groups = browser.findElements(By.className("ingredient-group"));
        assertThat(ingredient_groups.size()).isEqualTo(5);

        // 3. znajdź 'wraps' i 
        //      czy ich ilosc jest rowna 2, we wszystkich wrapach znajdz wszystkie DIV
        //      czy jest to Flour Tortilla oraz Corn Tortilla
        WebElement wrapGroup = browser.findElement(By.cssSelector("div.ingredient-group#wraps"));
        List<WebElement> wraps = wrapGroup.findElements(By.tagName("div"));// pojedyńcze składniki

        assertThat(wraps.size()).isEqualTo(2);
            // sprawdź składniki
        
        assertIngredient(wraps.get(0), "FLTO", "Flour Tortilla");
        assertIngredient(wraps.get(1), "COTO", "Corn Tortilla");

        WebElement proteinGroup = browser.findElement(By.cssSelector("div.ingredient-group#proteins"));
        List<WebElement> proteins = proteinGroup.findElements(By.tagName("div"));
        assertThat(proteins.size()).isEqualTo(2);
        assertIngredient(proteins.get(0), "GRBF", "Ground Beef");
        assertIngredient(proteins.get(1), "CARN", "Carnitas");

        WebElement cheeseGroup = browser.findElement(By.cssSelector("div.ingredient-group#cheeses"));
        List<WebElement> cheeses = cheeseGroup.findElements(By.tagName("div"));
        assertThat(cheeses.size()).isEqualTo(2);
        assertIngredient(cheeses.get(0), "CHED", "Cheddar");
        assertIngredient(cheeses.get(1), "JACK", "Monterrey Jack");

        WebElement veggieGroup = browser.findElement(By.cssSelector("div.ingredient-group#veggies"));
        List<WebElement> veggies = veggieGroup.findElements(By.tagName("div"));
        assertThat(veggies.size()).isEqualTo(2);
        assertIngredient(veggies.get(0), "TMTO", "Diced Tomatoes");
        assertIngredient(veggies.get(1), "LETC", "Lettuce");

        WebElement sauceGroup = browser.findElement(By.cssSelector("div.ingredient-group#sauces"));
        List<WebElement> sauces = sauceGroup.findElements(By.tagName("div"));
        assertThat(sauces.size()).isEqualTo(2);
        assertIngredient(sauces.get(0), "SLSA", "Salsa");
        assertIngredient(sauces.get(1), "SRCR", "Sour Cream");
    }

    private void assertIngredient(WebElement ingredient, String id, String name) {
        // porównaj właściwości ingredient (id, name)
        assertThat(ingredient.findElement(By.tagName("input")).getAttribute("value"))
            .isEqualTo(id);

        //ingredient
        // TH:TEXT działa tak że umieszcza tekst pomiędzy znacznikami HTML
        assertThat(ingredient.findElement(By.tagName("span")).getText())
            .isEqualTo(name);

    }

    private String orderDetailsPageUrl() {
        return homePageUrl() + "orders";
    }

    private String designPageUrl() {
        return homePageUrl() + "design";
    }
    
    private String homePageUrl() {
        return "http://localhost:" + port + "/";
    }
}
