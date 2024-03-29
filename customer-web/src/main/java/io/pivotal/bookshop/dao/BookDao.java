package io.pivotal.bookshop.dao;

import io.pivotal.bookshop.domain.BookMaster;

import java.util.List;

public interface BookDao {
    BookMaster findById(Integer id);
    int save(BookMaster book);
    void delete(Integer itemNumber);

    List<BookMaster> listBooks(int maxResults);
    List<BookMaster> findBooksByYearPublished(Integer yearPublished);
    List<BookMaster> findBooksByCurrentOwner(Integer customerNumber);
    List<BookMaster> findBooksByAuthor(String author);
    List<BookMaster> findBooksByTitle(String title);

    boolean bookExists(BookMaster book);
    int countBooks();

}
