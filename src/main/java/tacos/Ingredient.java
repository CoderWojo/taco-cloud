package tacos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data   // wygeneruje gdy wszystkie pola są oznaczone 'final'
@AllArgsConstructor
public class Ingredient {
    private final String id;
    private final String name;
    private final Type type;

    public enum Type {
        WRAP, PROTEIN, VEGGIES, CHEESE, SAUCE
    }
}
