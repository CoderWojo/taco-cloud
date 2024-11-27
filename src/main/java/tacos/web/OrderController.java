// Controller which recieves http request after ordering a taco

package tacos.web;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import jakarta.validation.Valid;
import tacos.TacoOrder;
import tacos.data.OrderRepository;

// import lombok.extern.slf4j.Slf4j;

// @Slf4j
@Controller
@SessionAttributes("tacoOrder") // potrzebne aby kontroler OrderController rozpoznał że obiekt 'tacoOrder' jest przechowywany w sesji   // również w nowym kontrolerze. Spring nie udostępnia automatycznie obiektów sesji, innym kontrolerom!
@RequestMapping("/orders")  // dodajemy RequestMapping na poziomie klasy aby każdy wewnętrzny endpoint był czytelniejszy    // oraz aby inne metody wprost określały ich path
public class OrderController {

    // private Logger logger = LoggerFactory.getLogger(OrderController.class); // TO LOG DETAILS OF THE ORDER that is submitted

    private OrderRepository orderRepo;
    
    public OrderController(OrderRepository orderRepo) {
        this.orderRepo = orderRepo;
    }


    @GetMapping("/current") // odbierz GET na /orders/current   i zwróć widok 'orderForm'
    public String orderForm() {
        return "orderForm"; // zwracamy widok z drugim formularzem do wpisania danych adresowych i platnosci
        // *dodamy metodę zapisującą Taco'sa (1) do db ALE CHYBA W POSTMAPPING odbierającym drugi formularz, A NIE TU!!!!
    }

    //@ModelAttribute służy do pobierania danych z modelu i z sesji, a @SessionAttribute 
    @PostMapping // odbierz POST na ścieżkę /orders
    public String processOrder(@ModelAttribute @Valid TacoOrder tacoOrder, BindingResult result, SessionStatus session) {
        if(result.hasErrors()) {
            return "orderForm";
        }
        // ZAPISZ ZAMÓWIENIE W BAZIE
        orderRepo.saveOrder(tacoOrder);

        session.setComplete();

        return "redirect:/"; // przekieruj na stronę główną
    }
}
