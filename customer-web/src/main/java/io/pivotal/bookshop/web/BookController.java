package io.pivotal.bookshop.web;

import io.pivotal.bookshop.domain.BookMaster;
import io.pivotal.bookshop.services.DataService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
}
