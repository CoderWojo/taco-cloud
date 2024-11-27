package tacos.web;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.hamcrest.Matchers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import tacos.data.IngredientRepository;
import tacos.data.OrderRepository;

// Adnotacja @WebMvcTest wymusza na Springu aby załadował wszystkie komponenty warstwy WEBOWEJ (wszystkie kontrolery)
@WebMvcTest // załaduj pełny kontekst aplikacji (gdy mamy samo W@), bo nie mamy Kontrollera HomeController, tylko jego konfigurację
public class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IngredientRepository ingredientRepo;
    
    @MockBean
    private OrderRepository orderRepo;
    
    @Test
    public void testHomePage() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get("/"))
        //^ zwraca ResultActions który umożliwia łączenie dalszych działań
            .andExpect(status().isOk())
            .andExpect(view().name("home"))
            .andExpect(content().string(Matchers.containsString("Welcome to...")));
    }
}
