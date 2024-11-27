package tacos.web;

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
import tacos.data.IngredientRepository;

@Controller
@RequestMapping("/design")
@SessionAttributes("tacoOrder") // utrzymuj w sesji obiekt tacoOrder z modelu
// @Slf4j

public class DesignTacoController {
    
    // jawnie tworzony jest obiekt Loggera, można to uzyskać oznaczając klasę adnotację @Slf4j
    private static final Logger log = LoggerFactory.getLogger(DesignTacoController.class);
    
    // Spring automatycznie wstrzyknie instancję 'jdbcIngredientRepository' jako BEAN bo jest to jedyna klasa w kontekście która implementuje IngredientRepository

    private final IngredientRepository ingredientRepo;  

    public DesignTacoController(IngredientRepository ingredientRepo) {
        this.ingredientRepo = ingredientRepo;
    }

    // @ModelAttr. - Spring odczyta tacoOrder z modelu z jesli nie bedzie go, to z sesji
    // uruchom walidację
    // Gdy formularz jest przesyłany metodą POST, Spring automatycznie próbuje skonwertować obiekt FLTO na obiekt klasy Taco Flour Tortilla
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

    @ModelAttribute
    public void addIngredientsToModel(Model model) {
        // Pobeira listę składników z bazy i wysyła je do modelu, aby szablon mógł je odczytać
        // !(TEST) Mockowanie w testach nie definiuje zwracanej wartości dla 'ingredientRepo.findAll()!!!'
        List<Ingredient> ingredients = ingredientRepo.findAll();

        // dodaj do modelu kilka list, każda zawierająca tylko składniki określonego typu.
        Type[] types = Type.values();
        for(Type t : types) {
            model.addAttribute(t.toString().toLowerCase(), 
                filterByType(ingredients, t));
        }
    }

    public List<Ingredient> filterByType(List<Ingredient> ingredients, Type type) {
        return ingredients
            .stream()
            .filter((Ingredient x) -> x.getType().equals(type))
            .collect(Collectors.toList());
    } 
}
