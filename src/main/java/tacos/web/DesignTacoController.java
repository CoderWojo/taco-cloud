package tacos.web;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import jakarta.validation.Valid;
import tacos.Ingredient;
import tacos.Taco;
import tacos.TacoOrder;
// import lombok.extern.slf4j.Slf4j;
import tacos.Ingredient.Type;

@Controller
@RequestMapping("/design")
@SessionAttributes("tacoOrder") // utrzymuj w sesji obiekt tacoOrder z modelu
// @Slf4j

public class DesignTacoController {
    
    // jawnie tworzony jest obiekt Loggera, można to uzyskać oznaczając klasę adnotację @Slf4j
    private static final Logger log = LoggerFactory.getLogger(DesignTacoController.class);

    // @ModelAttr. - Spring odczyta tacoOrder z modelu z jesli nie bedzie go, to z sesji
    // uruchom walidację
    @PostMapping
    public String processTaco(@ModelAttribute @Valid Taco taco, BindingResult result, @ModelAttribute TacoOrder tacoOrder) {// pobierz tacoOrder z sesji
        if(result.hasErrors()) {
            return "design";
        }

        tacoOrder.addTaco(taco);    // (!) Dane z modelu są String'ami, wiec musimy je skonwertowac na Ingredient aby dodać je do List<Ingredient> 
        log.info("Processing taco: {}" + taco);// formularz nr1 wysłany

        return "redirect:/orders/current";//get
        // przesyłamy na adres z zamowieniem/aktualnym I DOPIERO TAM ZAPISUJEMY DO DB bo klient musi jeszcze wypełnić dane adresowe
    } 

    @GetMapping
    public String showDesignForm() {
        return "design";
    }

    // Niech Spring umieści obiekt tacoOrder w modelu
    @ModelAttribute(name = "tacoOrder")// niech obiekt TacoOrder w modelu nosi nazwę tacoOrder
    public TacoOrder order() {
        return new TacoOrder();
    }

    @ModelAttribute(name = "taco")
    public Taco taco() {
        return new Taco();
    }

    // Buduje listę składników i umieszcza je w modelu, normalnie pobierałby je z db
    @ModelAttribute
    public void addIngredientsToModel(Model model) {
        List<Ingredient> ingredients = Arrays.asList(
            new Ingredient("FLTO", "Flour Tortilla", Ingredient.Type.WRAP),
            new Ingredient("COTO", "Corn Tortilla", Ingredient.Type.WRAP),
            new Ingredient("GRBF", "Ground Beef", Ingredient.Type.PROTEIN),
            new Ingredient("CARN", "Carnitas", Ingredient.Type.PROTEIN),
            new Ingredient("TMTO", "Diced Tomatoes", Ingredient.Type.VEGGIES),
            new Ingredient("LETC", "Lettuce", Ingredient.Type.VEGGIES),
            new Ingredient("CHED", "Cheddar", Ingredient.Type.CHEESE),
            new Ingredient("JACK", "Monterrey Jack", Ingredient.Type.CHEESE),
            new Ingredient("SLSA", "Salsa", Ingredient.Type.SAUCE),
            new Ingredient("SRCR", "Sour Cream", Ingredient.Type.SAUCE)
        );

        Type[] types = Type.values();
        for(Type type : types) {
            model.addAttribute(type.toString().toLowerCase(), 
                filterByType(ingredients, type));
        } 
        // dodaj do modelu kilka list, każda zawierająca tylko składniki określonego typu.

    }

    public List<Ingredient> filterByType(List<Ingredient> ingredients, Type type) {
        return ingredients
            .stream()
            .filter((Ingredient x) -> x.getType().equals(type))
            .collect(Collectors.toList());
    } 
}
