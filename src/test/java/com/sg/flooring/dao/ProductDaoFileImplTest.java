package com.sg.flooring.dao;

import com.sg.flooring.exceptions.PersistenceException;
import com.sg.flooring.model.Product;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ProductDaoFileImplTest {

    private ProductDaoFileImpl dao;
    private Path testFile;

    @BeforeEach
    public void setUp() throws IOException {

        testFile =
                Files.createTempFile(
                        "products-test",
                        ".txt"
                );

        Files.write(
                testFile,
                java.util.Arrays.asList(
                        "ProductType::CostPerSquareFoot::LaborCostPerSquareFoot",
                        "Carpet::2.25::2.10",
                        "Tile::3.50::4.15",
                        "Wood::5.15::4.75"
                )
        );

        dao = new ProductDaoFileImpl(testFile.toString());
    }

    @AfterEach
    public void tearDown() throws IOException {

        Files.deleteIfExists(testFile);
    }

    @Test
    public void testProductDao()
            throws PersistenceException {

        List<Product> products =
                dao.getAllProducts();

        assertEquals(
                3,
                products.size()
        );

        Product tile =
                dao.getProduct("Tile");

        assertNotNull(tile);

        assertEquals(
                "Tile",
                tile.getProductType()
        );

        assertEquals(
                new BigDecimal("3.50"),
                tile.getCostPerSquareFoot()
        );

        assertEquals(
                new BigDecimal("4.15"),
                tile.getLaborCostPerSquareFoot()
        );

        // Case-insensitive lookup
        assertNotNull(
                dao.getProduct("tile")
        );

        // Missing product
        assertNull(
                dao.getProduct("Stone")
        );
    }
}