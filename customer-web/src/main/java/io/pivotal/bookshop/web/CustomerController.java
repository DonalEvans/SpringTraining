package io.pivotal.bookshop.web;

import io.pivotal.bookshop.domain.BookMaster;
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
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
    private final String BASE_URL = "https://customer-web-doevans.apps.pcfone.io";

    @Autowired
    public CustomerController(DataService service) {
        this.service = service;
    }

    // ================================== REST Operations ==================================

    @GetMapping("/customer")
    public @ResponseBody
    List<Customer> listCustomers() {
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
            return "customerLogin";
        } else {
            model.addAttribute("customer", customer);
            logger.info("Found customer in session: " + customer);
            return "displayCustomer";
        }
    }

    @GetMapping("/customerLogin")
    public String enterCustomer() {
        return "customerLogin";
    }

    @PostMapping("/displayCustomer")
    public String displayCustomer(@RequestParam(required = false) String itemNumber, @RequestParam String customerNumber, @CookieValue(name = "JSESSIONID", required = false) String sessionId, Model model) throws URISyntaxException, MalformedURLException {
        logger.info("In displayCustomer() processing customer number: " + customerNumber);
        logger.info("JSESSIONID = " + sessionId);
        if (itemNumber != null && !itemNumber.isEmpty()) {
            BookMaster book = service.getBookById(new Integer(itemNumber));
            if (book != null) {
                if (!book.isCheckedOut()) {
                    RestTemplate template = new RestTemplate();
                    String urLocation = BASE_URL + "/book/" + itemNumber + "/customer/" + customerNumber;
                    URL checkoutURI = new URL(urLocation);

                    template.put(checkoutURI.toURI(), null);
                    model.addAttribute("book", book);
                } else {
                    model.addAttribute("book", new BookMaster(book.getCurrentOwner(), "",
                            0, "***Book already checked out***", "***Not available***",
                            false, 0));
                }
            } else {
                model.addAttribute("book", new BookMaster(0, "",
                        0, "***Invalid book Id***", "***Not available***",
                        false, 0));
            }
        } else {
            model.addAttribute("book", new BookMaster(2019, "",
                    0, "TO THE LIBRARY", "WELCOME",
                    false, 0));
        }

        Customer customer = service.getCustomerById(new Integer(customerNumber));
        if (customer != null) {
            model.addAttribute("customer", customer);
            model.addAttribute("sessionId", sessionId);
            return "displayCustomer";
        } else {
            logger.info("Customer not found for customerNumber: " + customerNumber);
            return "customerLogin";
        }
    }

    @GetMapping("/displayBookSearch")
    public String bookSearch() {
        return "displayBookSearch";
    }

    @PostMapping("/displayBookSearch")
    public String displayBookSearch(@RequestParam(required = false, defaultValue = "*no author*") String author, @RequestParam(required = false, defaultValue = "*no title*") String title, @CookieValue(name = "JSESSIONID", required = false) String sessionId, Model model ) throws MalformedURLException, URISyntaxException {
        RestTemplate template = new RestTemplate();
        BookMaster[] emptyArray = {};

        String authorLocation = BASE_URL + "/findBook/author/" + author;
        URI authorUri = ServletUriComponentsBuilder.fromUriString(authorLocation).buildAndExpand().toUri();
        BookMaster[] booksByAuthor = template.getForObject(authorUri, BookMaster[].class);
        model.addAttribute("booksByAuthor", author.equals("*no author*") ? emptyArray : booksByAuthor);

        String titleLocation = BASE_URL + "/findBook/title/" + title;
        URI titleUri = ServletUriComponentsBuilder.fromUriString(titleLocation).buildAndExpand().toUri();
        BookMaster[] booksByTitle = template.getForObject(titleUri, BookMaster[].class);
        model.addAttribute("booksByTitle", title.equals("*no title*") ? emptyArray : booksByTitle);

        return "displayBookSearch";
    }
}
