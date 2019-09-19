package io.pivotal.bookshop.web;

import io.pivotal.bookshop.domain.BookMaster;
import io.pivotal.bookshop.domain.Customer;
import io.pivotal.bookshop.services.DataService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
public class BookController {
    private final DataService service;

    public BookController(DataService service) { this.service =  service;}

    @GetMapping("/book")
    public List<BookMaster> listBooks() {
        return service.listBooks();
    }

    @GetMapping("/book/{itemNumber}")
    public HttpEntity<BookMaster> getBook(@PathVariable Integer itemNumber) {
        BookMaster book = service.getBookById(itemNumber);
        if (book != null)
            return ResponseEntity.ok(service.getBookById(itemNumber));
        else
            return ResponseEntity.notFound().build();
    }

    @PostMapping("/book")
    public ResponseEntity<Void> createBook(@RequestBody BookMaster book){
        service.saveBook(book);
        URI location = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/{childId}").buildAndExpand(book.getItemNumber())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @DeleteMapping("/book/{itemNumber}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBook(@PathVariable Integer itemNumber) {
        BookMaster bookToDelete = service.getBookById(itemNumber);
        service.removeBook(bookToDelete);
    }

    @PutMapping("/book/{itemNumber}/customer/{customerNumber}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> checkoutBook(@PathVariable Integer itemNumber, @PathVariable Integer customerNumber){
        BookMaster bookToCheckout = service.getBookById(itemNumber);
        if (!bookToCheckout.isCheckedOut()) {
            bookToCheckout.setCheckedOut(true);
            bookToCheckout.setCurrentOwner(customerNumber);
            service.saveBook(bookToCheckout);
            return ResponseEntity.ok(service.getBookById(itemNumber).toString());
        }
        else {
            return ResponseEntity.ok("Book already checked out.");
        }
    }

    @PutMapping("/displayBooks")
    public ResponseEntity<BookMaster> displayBooks(@PathVariable Integer customerId, @PathVariable Integer itemNumber){
        BookMaster bookToCheckout = service.getBookById(itemNumber);
        bookToCheckout.setCheckedOut(true);
        bookToCheckout.setCurrentOwner(customerId);
        service.saveBook(bookToCheckout);
        return ResponseEntity.ok(service.getBookById(itemNumber));
    }

    @GetMapping("/displayCheckedOutBooks/{customerNumber}")
    public List<BookMaster> displayCheckedOutBooks(@PathVariable Integer customerNumber) {
        return service.displayCheckedOutBooks(customerNumber);
    }

//    @PutMapping("/checkoutBook")
//    public String processCheckout(@RequestParam String itemNumber, @RequestParam String customerNumber, @CookieValue(name = "JSESSIONID", required = false) String sessionId, Model model) {
//        ResponseEntity<BookMaster> checkoutResponse = checkoutBook(new Integer(itemNumber), new Integer (customerNumber));
//        if(checkoutResponse.getStatusCode() == HttpStatus.OK) {
//            RequestEntity checkoutRequest;
//        }
//        return "displayCustomer";
//    }
}
