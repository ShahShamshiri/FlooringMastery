package com.sg.flooring.dao;

import com.sg.flooring.exceptions.PersistenceException;
import com.sg.flooring.model.Order;

import java.time.LocalDate;
import java.util.Map;

public interface ExportDao {

    void exportAllData(
            Map<LocalDate, Map<Integer, Order>> allOrders
    ) throws PersistenceException;
}