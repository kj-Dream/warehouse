package com.example.warehouse.controller;

import com.example.warehouse.dto.Result;
import com.example.warehouse.pojo.Customer;
import com.example.warehouse.service.CustomerService;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {
    @Resource private CustomerService customerService;

    @GetMapping("/page")
    public Result<Map<String,Object>> page(@RequestParam(defaultValue="1")Integer pageNum,
            @RequestParam(defaultValue="10")Integer pageSize,
            @RequestParam(required=false)String customerName,
            @RequestParam(required=false)Integer status){
        return Result.success(customerService.listCustomers(pageNum,pageSize,customerName,status));
    }
    @GetMapping("/{id}")
    public Result<Customer> getById(@PathVariable Integer id){return Result.success(customerService.getById(id));}
    @PostMapping
    public Result<Void> create(@RequestBody Customer c){customerService.create(c);return Result.success("客户创建成功",null);}
    @PutMapping
    public Result<Void> update(@RequestBody Customer c){customerService.update(c);return Result.success("客户修改成功",null);}
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id){customerService.delete(id);return Result.success("删除成功",null);}
}
