package tacos;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.validator.constraints.CreditCardNumber;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class TacoOrder {
    // określamy jak ma przebiegać, jakie warunki obejmuje walidacja
    @NotBlank(message = "Delivery name is required")
    private String deliveryName;

    @NotBlank(message = "Street is required")
    private String deliveryStreet;

    @NotBlank(message = "City is required")
    private String deliveryCity;

    @NotBlank(message = "State is required")
    private String deliveryState;

    @NotBlank(message = "Zip code is required")
    private String deliveryZip;

    @CreditCardNumber(message = "Incorrect credit card number")
    private String ccNumber;

    // ^ oznacza początek wzorca, $ oznacza koniec wzorca   czyli poprawne odnalezienie nie moze miec znakow przed i po
    @Pattern(regexp = "^(0[1-9]|1[0-2])/([2-9][5-9])$", message = "Your credit card is probably out of date")
    private String ccExpiration;
    
    @Digits(integer = 3, fraction = 0, message = "Invalid CVV")    // 3 cyfry reprezentujące liczbę całkowitą przed przecinkiem, 0 cyfr po przecinku 
    private String ccCVV;

    private List<Taco> tacos = new ArrayList<>();

    public void addTaco(Taco t) {
        tacos.add(t);
    }
}
