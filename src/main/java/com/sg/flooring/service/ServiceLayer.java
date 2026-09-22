package com.sg.flooring.service;

import com.sg.flooring.exceptions.NoSuchOrderException;
import com.sg.flooring.exceptions.OrderValidationException;
import com.sg.flooring.exceptions.PersistenceException;
import com.sg.flooring.model.Order;
import com.sg.flooring.model.Product;
import com.sg.flooring.model.Tax;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ServiceLayer {

    List<Order> getOrdersForDate(LocalDate date)
            throws PersistenceException;

    Order getOrder(LocalDate date, int orderNumber)
            throws PersistenceException, NoSuchOrderException;

    List<Product> getAllProducts()
            throws PersistenceException;

    List<Tax> getAllTaxes()
            throws PersistenceException;

    Order createOrder(LocalDate date, String customerName, String state, String productType, BigDecimal area
    ) throws PersistenceException, OrderValidationException;

    Order saveOrder(LocalDate date, Order order)
            throws PersistenceException;

    Order updateOrder(LocalDate date, int orderNumber, String customerName, String state, String productType, BigDecimal area
    ) throws PersistenceException,
            NoSuchOrderException,
            OrderValidationException;

    Order saveEditedOrder(LocalDate date, Order order
    ) throws PersistenceException,
            NoSuchOrderException;

    Order removeOrder(LocalDate date, int orderNumber)
            throws PersistenceException, NoSuchOrderException;

    Map<LocalDate, Map<Integer, Order>> getAllOrders()
            throws PersistenceException;

    void exportAllData()
            throws PersistenceException;
}