package com.example.warehouse.service.impl;

import com.example.warehouse.mapper.CustomerMapper;
import com.example.warehouse.pojo.Customer;
import com.example.warehouse.service.CustomerService;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import java.util.*;

@Service
public class CustomerServiceImpl implements CustomerService {
    @Resource private CustomerMapper customerMapper;

    @Override
    public Map<String,Object> listCustomers(Integer pageNum, Integer pageSize, String customerName, Integer status) {
        Map<String,Object> r=new HashMap<>();
        r.put("list",customerMapper.selectPage(customerName,status,(pageNum-1)*pageSize,pageSize));
        r.put("total",customerMapper.countPage(customerName,status));
        r.put("pageNum",pageNum);r.put("pageSize",pageSize);
        return r;
    }
    @Override public Customer getById(Integer id){return customerMapper.findById(id);}
    @Override public void create(Customer c){customerMapper.insert(c);}
    @Override public void update(Customer c){customerMapper.updateById(c);}
    @Override public void delete(Integer id){customerMapper.deleteById(id);}
    @Override public List<Customer> getAllActive(){return customerMapper.findAll();}
}
