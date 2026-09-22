package com.sg.flooring.dao;

import com.sg.flooring.exceptions.PersistenceException;

public interface AuditDao {

    void writeAuditEntry(String entry)
            throws PersistenceException;
}