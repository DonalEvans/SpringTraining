package io.pivotal.bookshop.services;

import io.pivotal.bookshop.dao.BookDao;
import io.pivotal.bookshop.dao.CustomerDao;
import io.pivotal.bookshop.domain.BookMaster;
import io.pivotal.bookshop.domain.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service is designed to front the data repository interactions via the DAO layers that will be injected
 * in. Which implementation may change over the evolution of the project.
 */
@Service
public class DataService {

    private Logger logger = LoggerFactory.getLogger("CustomerService");
    private BookDao bookDao;
    private CustomerDao customerDao;


    @Autowired
    public DataService(  BookDao bookDao, CustomerDao customerDao) {
        this.bookDao = bookDao;
        this.customerDao = customerDao;
    }

    /**
     * A simple test to ensure that the tables have been initialized
     *
     * @return True if data is found in both customers and books table, false otherwise.
     */
    public boolean isDbInitialized() {
        try {
            return (customerDao.countCustomers() > 0 && bookDao.countBooks() > 0);
        } catch (DataAccessException dae) {
            logger.info("Exception thrown on query: " + dae);
            return false;
        }
    }

    // ================================= Customer Operations ================================

    public List<Customer> listCustomers() {
        return customerDao.listCustomers(10);
    }

    /**
     * Perform a basic fetch of a customer from the configured DAO
     *
     * @param id The customer_number to query for
     * @return A HashMap of the columns and values for the given customer or an empty HashMap on failure
     *
     */
    public Customer getCustomerById(int id) {
        try {
            return customerDao.findById(id);
        } catch (DataAccessException dae) {
            logger.info("Exception thrown on query: " + dae);
            return null;
        }
    }

    // ================================= BookMaster Operations ================================

    /**
     * List the first 10 books found
     */

    public List<BookMaster> listBooks() {
        return bookDao.listBooks(10);
    }

    /**
     * Perform a basic fetch of a book from the .
     * Will later be enhanced with a CacheAside solution.
     *
     * @param id The item_number to query for
     * @return A HashMap of the columns and values for the given customer or an empty HashMap on failure
     *
     */
    @Cacheable(value = "Books")
    public BookMaster getBookById(int id) {
        try {
            return bookDao.findById(id);
        } catch (DataAccessException dae) {
            logger.info("Exception thrown on query: " + dae);
            return null;
        }
    }

    /**
     * Handle the save operation, which could be either a table insert or update depending
     * on whether it exists in the table.
     *
     * @param book The book to save
     * @return The created/updated book
     *
     */
    @CachePut(value = "Books", key = "#result.itemNumber")
    public BookMaster saveBook(BookMaster book) {
        logger.info("Saving book: {}", book);
        bookDao.save(book);
        return book;
    }

    /**
     * Delete the book specified by the
     * @param bookToDelete
     */
    @CacheEvict(value = "Books", key="#bookToDelete.itemNumber")
    public void removeBook(BookMaster bookToDelete) {
        logger.info("Removing book for key: {}", bookToDelete.getItemNumber());
        if (bookDao.bookExists(bookToDelete)) {
            bookDao.delete(bookToDelete.getItemNumber());
        }
    }

}
