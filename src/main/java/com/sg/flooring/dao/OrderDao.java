package com.sg.flooring.dao;

import com.sg.flooring.exceptions.NoSuchOrderException;
import com.sg.flooring.exceptions.PersistenceException;
import com.sg.flooring.model.Order;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface OrderDao {

    List<Order> getOrdersForDate(LocalDate date)
            throws PersistenceException;

    Order getOrder(LocalDate date, int orderNumber)
            throws PersistenceException, NoSuchOrderException;

    Order addOrder(LocalDate date, Order order)
            throws PersistenceException;

    Order editOrder(LocalDate date, Order order)
            throws PersistenceException, NoSuchOrderException;

    Order removeOrder(LocalDate date, int orderNumber)
            throws PersistenceException, NoSuchOrderException;

    int getNextOrderNumber()
            throws PersistenceException;

    Map<LocalDate, Map<Integer, Order>> getAllOrders()
            throws PersistenceException;
}