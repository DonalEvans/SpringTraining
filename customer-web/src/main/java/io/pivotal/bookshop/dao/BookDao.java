package io.pivotal.bookshop.dao;

import io.pivotal.bookshop.domain.BookMaster;

import java.util.List;

public interface BookDao {
    BookMaster findById(Integer id);
    int save(BookMaster book);
    void delete(Integer itemNumber);

    List<BookMaster> listBooks(int maxResults);
    List<BookMaster> findBooksByYearPublished(Integer yearPublished);

    boolean bookExists(BookMaster book);
    int countBooks();

}
