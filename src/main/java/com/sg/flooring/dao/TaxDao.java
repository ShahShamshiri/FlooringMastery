package com.sg.flooring.dao;

import com.sg.flooring.exceptions.PersistenceException;
import com.sg.flooring.model.Tax;

import java.util.List;

public interface TaxDao {

    List<Tax> getAllTaxes() throws PersistenceException;

    Tax getTax(String state) throws PersistenceException;
}