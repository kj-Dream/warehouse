package com.example.warehouse.service;

import com.example.warehouse.pojo.Customer;
import java.util.List;
import java.util.Map;

public interface CustomerService {
    Map<String,Object> listCustomers(Integer pageNum, Integer pageSize, String customerName, Integer status);
    Customer getById(Integer id);
    void create(Customer customer);
    void update(Customer customer);
    void delete(Integer id);
    List<Customer> getAllActive();
}
