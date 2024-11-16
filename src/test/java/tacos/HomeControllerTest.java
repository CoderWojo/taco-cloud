package tacos;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.hamcrest.Matchers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

// nie musimy uruchamiać aplikacji 
@WebMvcTest
public class HomeControllerTest {
    // dzięki @Autowired, Spring automatycznie wstrzyknie instancję obiektu mockMvc i będzie nią zarządzać
    // klasa do uruchamiania żądań, bez konieczności uruchamiania serwera
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testHomePage() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get("/"))
        //^ zwraca ResultActions który umożliwia łączenie dalszych działań
            .andExpect(status().isOk())
            .andExpect(view().name("home"))
            .andExpect(content().string(Matchers.containsString("Welcome to...")));
    }
}
