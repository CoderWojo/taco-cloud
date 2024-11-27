package tacos.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import tacos.Ingredient;
import tacos.Ingredient.Type;
import tacos.data.IngredientRepository;

public class IngredientByIdConverterTest {
    // Będziemy testować pobieranie składnika z bazy danych o
        // 1. poprawnym ID
        // 2. niepoprawnym ID
    
    // chcemy testować IngredientRepository.findById(String id)
    private IngredientByIdConverter converter;

    @BeforeEach
    public void setup() {
        IngredientRepository ingredientRepo = Mockito.mock(
            IngredientRepository.class);
            // Składni z id=AAAA, jest obecny
        when(ingredientRepo.findById("AAAA"))
            .thenReturn(Optional.of(new Ingredient("AAAA", "TEST INGREDIENT", Type.CHEESE)));

            // Zakładamy że nie ma w bazie składnika z ID="ZZZZ"
        when(ingredientRepo.findById("ZZZZ"))
            .thenReturn(Optional.empty());
        
            this.converter = new IngredientByIdConverter(ingredientRepo);
    }

    @Test
    public void shouldReturnValueWhenPresent() {
        assertThat(converter.convert("AAAA"))
            .isEqualTo(new Ingredient("AAAA", "TEST INGREDIENT", Type.CHEESE));
    }

    @Test
    public void shouldReturnNullValueWhenMissing() {
        assertThat(converter.convert("ZZZZ"))
            .isNull();
    }
}
