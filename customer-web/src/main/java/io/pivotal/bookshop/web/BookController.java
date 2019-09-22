package io.pivotal.bookshop.web;

import io.pivotal.bookshop.domain.BookMaster;
import io.pivotal.bookshop.domain.Customer;
import io.pivotal.bookshop.services.DataService;
import org.hibernate.validator.constraints.pl.REGON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
public class BookController {
    private final DataService service;
    private Logger logger = LoggerFactory.getLogger("BookController");
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

    @GetMapping("/findBook/author/{author}")
    public BookMaster[] findBookByAuthor(@PathVariable(required = false) String author, @CookieValue(name = "JSESSIONID", required = false) String sessionId, Model model) {
        List<BookMaster> books = service.getBookByAuthor(author);
        if (!books.isEmpty()) {
            return books.stream().toArray(BookMaster[]::new);
        }
        else {
            return new BookMaster[] {new BookMaster(0, "",
                    0, "***No books found***", "***No books found***",
                    false, 0)};
        }
    }

    @GetMapping("/findBook/title/{title}")
    public BookMaster[] findBookByTitle(@PathVariable(required = false) String title, @CookieValue(name = "JSESSIONID", required = false) String sessionId, Model model) {
        List<BookMaster> books = service.getBookByTitle(title);
        if (!books.isEmpty()) {
            return books.stream().toArray(BookMaster[]::new);
        }
        else {
            return new BookMaster[] {new BookMaster(0, "",
                    0, "***No books found***", "***No books found***",
                    false, 0)};
        }
    }

    @GetMapping("/findBook/customer/{customerNumber}")
    public BookMaster[] findBookByCustomer(@PathVariable Integer customerNumber, @CookieValue(name = "JSESSIONID", required = false) String sessionId, Model model) {
        List<BookMaster> books = service.displayCheckedOutBooks(customerNumber);
        if (!books.isEmpty()) {
            return books.stream().toArray(BookMaster[]::new);
        }
        else {
            return new BookMaster[] {new BookMaster(0, "",
                    0, "***No books found***", "***No books found***",
                    false, 0)};
        }
    }
}
