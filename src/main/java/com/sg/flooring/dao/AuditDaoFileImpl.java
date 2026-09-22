package com.sg.flooring.dao;

import com.sg.flooring.exceptions.PersistenceException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AuditDaoFileImpl implements AuditDao {

    private static final String AUDIT_FILE = "Data" + File.separator + "audit.txt";
    private static final String DELIMITER = "::";
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");

    @Override
    public void writeAuditEntry(String entry)
            throws PersistenceException {

        File dataFolder = new File("Data");

        if (!dataFolder.exists()) {

            boolean created = dataFolder.mkdirs();

            if (!created) {

                throw new PersistenceException(
                        "Could not create Data directory."
                );
            }
        }

        try (
                PrintWriter out =
                        new PrintWriter(
                                new BufferedWriter(
                                        new FileWriter(
                                                AUDIT_FILE,
                                                true
                                        )
                                )
                        )
        ) {

            String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);

            out.println(timestamp + DELIMITER + entry);

        } catch (IOException e) {

            throw new PersistenceException(
                    "Could not write audit entry.",
                    e
            );
        }
    }
}