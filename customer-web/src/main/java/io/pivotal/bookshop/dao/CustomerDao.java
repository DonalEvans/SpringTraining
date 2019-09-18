package io.pivotal.bookshop.dao;

import io.pivotal.bookshop.domain.Customer;

import java.util.List;

public interface CustomerDao {
    Customer findById(Integer customerNumber);
    int save(Customer customer);
    void delete(Integer customerNumber);

    List<Customer> listCustomers(int maxResults);

    Integer countCustomers();
    boolean customerExists(Customer customer);
}
