package io.pivotal.bookshop.web;

        import io.pivotal.bookshop.domain.Customer;
        import io.pivotal.bookshop.services.DataService;
        import org.slf4j.Logger;
        import org.slf4j.LoggerFactory;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.http.HttpEntity;
        import org.springframework.http.ResponseEntity;
        import org.springframework.stereotype.Controller;
        import org.springframework.ui.Model;
        import org.springframework.web.bind.annotation.*;

        import java.util.List;

/**
 * A controller for the Customer domain object. This particular controller supports both RESTful interactions, as well
 * as web requests. This is to help illustrate the value of session state caching.
 */
@Controller
@SessionAttributes("customer")
public class CustomerController {
    private final DataService service;
    private Logger logger = LoggerFactory.getLogger("CustomerController");

    @Autowired
    public CustomerController(DataService service) {
        this.service = service;
    }

    // ================================== REST Operations ==================================

    @GetMapping("/customer")
    public @ResponseBody List<Customer> listCustomers() {
        return service.listCustomers();
    }

    @GetMapping("/customer/{customerId}")
    public HttpEntity<Customer> getCustomer(@PathVariable Integer customerId) {
        Customer customer = service.getCustomerById(customerId);
        if (customer != null)
            return ResponseEntity.ok(customer);
        else
            return ResponseEntity.notFound().build();
    }


    // ================================== WEB Operations ===================================

    @GetMapping("/")
    public String home(@SessionAttribute(value = "customer", required = false) Customer customer, Model model) {
        logger.info("In home()");
        if (customer == null) {
            logger.info("No customer found");
            return "enterCustomer";
        } else {
            model.addAttribute("customer", customer);
            logger.info("Found customer in session: " + customer);
            return "displayCustomer";
        }
    }

    @GetMapping("/enterCustomer")
    public String enterCustomer() {
        return "enterCustomer";
    }

    @PostMapping("/changeCustomer")
    public String changeCustomer(@RequestParam String customerNumber, @CookieValue(name = "JSESSIONID", required = false) String sessionId, Model model) {
        logger.info("In changeCustomer() processing customer number: " + customerNumber);
        logger.info("JSESSIONID = " + sessionId);

        Customer customer = service.getCustomerById(new Integer(customerNumber));
        if (customer != null) {
            model.addAttribute("customer", customer);
            model.addAttribute("sessionId", sessionId);
            return "displayCustomer";
        } else {
            logger.info("Customer not found for customerNumber: " + customerNumber);
            return "enterCustomer";
        }
    }

}
