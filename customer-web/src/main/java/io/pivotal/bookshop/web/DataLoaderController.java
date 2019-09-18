package io.pivotal.bookshop.web;

import io.pivotal.bookshop.services.DataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * This is the main controller providing a minimal REST endpoint for verifying correct database initialization
 * and setup.
 */
@RestController
public class DataLoaderController {
    private Logger logger = LoggerFactory.getLogger("CustomerController");

    private final DataService service;

    public DataLoaderController(DataService service) { this.service = service;}

    @GetMapping("/isInitialized")
    public String isDbInitialized() {
        if (service.isDbInitialized() ) {
            return "Database has been initialized";
        } else {
            return "Database was not initialized!!";
        }
    }

}
