package tacos.web;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import tacos.Ingredient;
import tacos.data.IngredientRepository;

@Component  // Niech Spring zarządza tą klasą jako BEAN i używa wtedy kiedy jest potrzeba konwersji
public class IngredientByIdConverter implements Converter<String, Ingredient>{

    private final IngredientRepository ingredientRepo;

    public IngredientByIdConverter(IngredientRepository ingredientRepo) {
        this.ingredientRepo = ingredientRepo;
    }

    @Override
    @Nullable
    public Ingredient convert(String id) {
        // jeśli nie znaleźliśmy składnika o identyfikatorze 'id' to zwróć NULL
        return ingredientRepo.findById(id).orElse(null);
    }
}
