package tacos;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import tacos.Ingredient.Type;
import tacos.web.DesignTacoController;

@WebMvcTest(DesignTacoController.class)   // Testujemy warstwę webową aplikacji - DesignTacoController
// @ExtendWith(SpringExtension.class)  // zapewniamy dostęp do kontekstu Springa - NIEKONIECZNE BO ADNOTACJA @WebMvcTest już to zapewnia
public class DesignTacoControllerTest {
    // do symulacji żądań http do kontrolera. NIE uruchamia całej aplikacji
    // Spring zainicjuje automatycznie, bo MockMvc jest częścią konfiguracji kontekstu testowego dla warstwy webowej
    // DZIAŁA TO TYLKO Z KAŻDYM OBIEKTEM ZARZĄDZANYM PRZEZ SPRING'a
    @Autowired
    private MockMvc mockMvc;
    private List<Ingredient> ingredients;   // oczekiwane składniki

    // @BeforeAll // do jednej operacji kosztownej np. połączenia się z db, będzie wykonana metoda raz przed wszystkimi testami z naszej klasy
    @BeforeEach // metoda zostanie wywołana przed każdym testem z naszej klasy
    public void setup() {
        ingredients = Arrays.asList(
            new Ingredient("FLTO", "Flour Tortilla", Type.WRAP),
            new Ingredient("COTO", "Corn Tortilla", Type.WRAP),
            new Ingredient("GRBF", "Ground Beef", Type.PROTEIN),
            new Ingredient("CARN", "Carnitas", Type.PROTEIN),
            new Ingredient("TMTO", "Diced Tomatoes", Type.VEGGIES),
            new Ingredient("LETC", "Lettuce", Type.VEGGIES),
            new Ingredient("CHED", "Cheddar", Type.CHEESE),
            new Ingredient("JACK", "Monterrey Jack", Type.CHEESE),
            new Ingredient("SLSA", "Salsa", Type.SAUCE),
            new Ingredient("SRCR", "Sour Cream", Type.SAUCE)
        );
    }

    // sprawdz czy wszystkie składniki zostały załadowane poprawnie
    @Test
    public void testShowDesignForm() throws Exception{
        // test pokazania formularza do design'u taco, czyli czy dane zostały załadowane poprawnie
        mockMvc.perform(MockMvcRequestBuilders.get("/design"))
            .andExpect(MockMvcResultMatchers.status().isOk())   // czy status OK
            .andExpect(MockMvcResultMatchers.view().name("design")) // czy nazwa widoku jest design
            .andExpect(MockMvcResultMatchers.model().attribute("wrap", ingredients.subList(0, 2)))// czy lista z modelu odpowiada liście powyższej dla wrapow
            .andExpect(MockMvcResultMatchers.model().attribute("protein", ingredients.subList(2, 4)))
            .andExpect(MockMvcResultMatchers.model().attribute("veggies", ingredients.subList(4, 6)))
            .andExpect(MockMvcResultMatchers.model().attribute("cheese", ingredients.subList(6, 8)))
            .andExpect(MockMvcResultMatchers.model().attribute("sauce", ingredients.subList(8, 10)));
    }

    // sprawdz czy po wysłaniu formularza użytkownik zostaje przekierowany na odpowiednią stronę (czyli przekierowanie jest właściwe)
    @Test
    public void processTaco() throws Exception{
        // dane dla żądania POST są kodowane w formacie nazwa=wartość&nazwa2=wartosc2
        mockMvc.perform(MockMvcRequestBuilders.post("/design")
        // ustal wnętrze żądania http post (tak jakbys wypelnil formularz)
        .content("name=Test+Taco&ingredients=FLTO,GRBF,CHED")
        // ustal typ danych - SPRINGU, traktuj dane które otrzymasz jako z formularza
        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
        // czy nr statusu to przekierowanie
        .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
        // sprawdz nagłowek reprezentujący lokację gdzie użytkownik ma zostać przekierowany po wypełnieniu formularza
        .andExpect(MockMvcResultMatchers.header().string("Location", "/orders/current"));
    }

}
