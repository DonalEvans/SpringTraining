package io.pivotal.bookshop.dao;

import io.pivotal.bookshop.domain.Address;
import io.pivotal.bookshop.domain.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class CustomerJdbcDao implements CustomerDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired()
    public CustomerJdbcDao(@Qualifier("dataSource") DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public int save(Customer customer) {
        if (customerExists(customer)) {
           return updateCustomer(customer);
        } else {
            return insertCustomer(customer);
        }
    }

    private int insertCustomer(Customer customer) {
        return jdbcTemplate.update("udpate customers set first_name = ?, last_name = ?, address_line1 = ?, city = ?, state = ?, postalcode = ?, country = ?, telephone_number = ? where customer_number = ?)",
                customer.getFirstName(),
                customer.getLastName(),
                customer.getPrimaryAddress().getAddressLine1(),
                customer.getPrimaryAddress().getCity(),
                customer.getPrimaryAddress().getState(),
                customer.getPrimaryAddress().getPostalCode(),
                customer.getPrimaryAddress().getCountry(),
                customer.getTelephoneNumber(),
                customer.getCustomerNumber()
                );
    }

    private int updateCustomer(Customer customer) {
        return jdbcTemplate.update("insert into customers(customer_number, first_name, last_name, address_line1, city, state, postalcode, country,telephone_number values (?,?,?,?,?,?,?,?,?)",
                customer.getCustomerNumber(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getPrimaryAddress().getAddressLine1(),
                customer.getPrimaryAddress().getCity(),
                customer.getPrimaryAddress().getState(),
                customer.getPrimaryAddress().getPostalCode(),
                customer.getPrimaryAddress().getCountry(),
                customer.getTelephoneNumber());

    }

    @Override
    public void delete(Integer customerNumber) {
        jdbcTemplate.update("delete from customers where customer_number = ?", customerNumber);

    }

    @Override
    public List<Customer> listCustomers(int maxResults) {
        return jdbcTemplate.query("select * from customers limit ?", new CustomerMapper(), maxResults);
    }

    @Override
    public Customer findById(Integer customerId) {
        return jdbcTemplate.queryForObject("select * from customers where customer_number = ?",
                new CustomerMapper(),
                customerId
        );
    }

    @Override
    public Integer countCustomers() {
        return jdbcTemplate.queryForObject("select count(*) from customers", Integer.class);
    }

    @Override
    public boolean customerExists(Customer customer) {
        return (jdbcTemplate.queryForObject("select count(*) from customers where customer_number = ?",
                Integer.class, customer.getCustomerNumber()) == 1);
    }

    class CustomerMapper implements RowMapper<Customer> {
        @Nullable
        @Override
        public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
            Address a = new Address(rs.getString("address_line1"),
                    rs.getString("address_line2"),
                    rs.getString("address_line3"),
                    rs.getString("city"),
                    rs.getString("state"),
                    rs.getString("postalcode"),
                    rs.getString("country"),
                    rs.getString("telephone_number"),"");
            return new Customer(rs.getInt("customer_number"),
                    rs.getString("first_name"),
                    rs.getString("last_name"), a);
        }
    }
}
