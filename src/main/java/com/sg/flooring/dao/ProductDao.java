package com.sg.flooring.dao;

import com.sg.flooring.exceptions.PersistenceException;
import com.sg.flooring.model.Product;

import java.util.List;

public interface ProductDao {

    List<Product> getAllProducts() throws PersistenceException;

    Product getProduct(String productType) throws PersistenceException;
}