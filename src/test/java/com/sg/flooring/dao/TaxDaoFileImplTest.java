package com.sg.flooring.dao;

import com.sg.flooring.exceptions.PersistenceException;
import com.sg.flooring.model.Tax;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TaxDaoFileImplTest {

    private TaxDaoFileImpl dao;
    private Path testFile;

    @BeforeEach
    public void setUp() throws IOException {

        testFile =
                Files.createTempFile(
                        "taxes-test",
                        ".txt"
                );

        Files.write(
                testFile,
                java.util.Arrays.asList(
                        "State::StateName::TaxRate",
                        "TX::Texas::4.45",
                        "WA::Washington::9.25",
                        "CA::California::25.00"
                )
        );

        dao =
                new TaxDaoFileImpl(
                        testFile.toString()
                );
    }

    @AfterEach
    public void tearDown() throws IOException {

        Files.deleteIfExists(testFile);
    }

    @Test
    public void testTaxDao()
            throws PersistenceException {

        List<Tax> taxes =
                dao.getAllTaxes();

        assertEquals(
                3,
                taxes.size()
        );

        Tax tx =
                dao.getTax("TX");

        assertNotNull(tx);

        assertEquals(
                "Texas",
                tx.getStateName()
        );

        assertEquals(
                new BigDecimal("4.45"),
                tx.getTaxRate()
        );

        // Case-insensitive lookup
        assertNotNull(
                dao.getTax("tx")
        );

        // Missing state
        assertNull(
                dao.getTax("ZZ")
        );
    }
}