package io.pivotal.bookshop.dao;

import io.pivotal.bookshop.domain.BookMaster;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.awt.print.Book;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class BookJdbcDao implements BookDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public BookJdbcDao(@Qualifier("dataSource") DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public BookMaster findById(Integer id) {
        return jdbcTemplate.queryForObject("select * from books where item_number = ?",
                new BookMapper(),
                id
        );
    }

    @Override
    public int save(BookMaster book) {
        if (bookExists(book)) {
            return updateBook(book);
        } else {
            return insertBook(book);
        }
    }

    @Override
    public boolean bookExists(BookMaster book) {
        return (jdbcTemplate.queryForObject("select count(*) from books where item_number=?",
                Integer.class, book.getItemNumber()) == 1);
    }

    @Override
    public List<BookMaster> findBooksByYearPublished(Integer yearPublished) {
        return jdbcTemplate.query("select * from books where publication_date >= ?",
                new BookMapper(),
                yearPublished);
    }

    @Override
    public int countBooks() {
        return jdbcTemplate.queryForObject("select count(*) from books", Integer.class);
    }

    @Override
    public void delete(Integer itemNumber) {
        jdbcTemplate.update("delete from books where item_number=?", itemNumber);
    }

    private int insertBook(BookMaster book) {
        return jdbcTemplate.update("insert into books (item_number, description, retail_cost, publication_date, author, title) values (?,?,?,?,?,?)",
                book.getItemNumber(),
                book.getDescription(),
                book.getRetailCost(),
                book.getYearPublished(),
                book.getAuthor(),
                book.getAuthor()
        );

    }

    private int updateBook(BookMaster book) {
        return  jdbcTemplate.update("update books set description=?, retail_cost=?, publication_date=?, author=?, title=? where item_number=?",
                book.getDescription(),
                book.getRetailCost(),
                book.getYearPublished(),
                book.getAuthor(),
                book.getTitle(),
                book.getItemNumber()
        );

    }

    @Override
    public List<BookMaster> listBooks(int maxResults) {
        return jdbcTemplate.query("select * from books limit ?", new BookMapper(), maxResults);
    }

    public class BookMapper implements RowMapper<BookMaster> {

        @Override
        public BookMaster mapRow(ResultSet rs, int i) throws SQLException {
            return new BookMaster(rs.getInt("item_number"),rs.getString("description"),rs.getFloat("retail_cost"),
                    rs.getInt("publication_date"),rs.getString("author"),rs.getString("title"));
        }
    }
}
