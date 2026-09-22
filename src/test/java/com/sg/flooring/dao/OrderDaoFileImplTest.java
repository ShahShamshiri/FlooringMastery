package com.sg.flooring.dao;

import com.sg.flooring.exceptions.NoSuchOrderException;
import com.sg.flooring.exceptions.PersistenceException;
import com.sg.flooring.model.Order;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;

public class OrderDaoFileImplTest {

    private OrderDaoFileImpl dao;
    private Path testDirectory;

    @BeforeEach
    public void setUp() throws IOException {
        testDirectory = Files.createTempDirectory("order-test");
        dao = new OrderDaoFileImpl(testDirectory.toString());
    }

    @AfterEach
    public void tearDown() throws IOException {
        try (java.util.stream.Stream<Path> paths = Files.walk(testDirectory)) {
            paths.sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }
    }

    private Order createOrder(int orderNumber, LocalDate date) {
        Order order = new Order();

        order.setOrderNumber(orderNumber);
        order.setOrderDate(date);
        order.setCustomerName("Test Customer");
        order.setState("TX");
        order.setTaxRate(new BigDecimal("4.45"));
        order.setProductType("Tile");
        order.setArea(new BigDecimal("200.00"));
        order.setCostPerSquareFoot(new BigDecimal("3.50"));
        order.setLaborCostPerSquareFoot(new BigDecimal("4.15"));
        order.setMaterialCost(new BigDecimal("700.00"));
        order.setLaborCost(new BigDecimal("830.00"));
        order.setTax(new BigDecimal("68.09"));
        order.setTotal(new BigDecimal("1598.09"));

        return order;
    }

    @Test
    public void testAddEditAndRemoveOrder()
            throws PersistenceException, NoSuchOrderException {

        LocalDate date = LocalDate.of(2027, 5, 5);
        Order order = createOrder(1, date);

        // Add
        dao.addOrder(date, order);

        Order retrieved = dao.getOrder(date, 1);
        assertEquals(order, retrieved);

        // Edit
        order.setCustomerName("Edited Customer");
        order.setArea(new BigDecimal("300.00"));

        dao.editOrder(date, order);

        retrieved = dao.getOrder(date, 1);

        assertEquals("Edited Customer", retrieved.getCustomerName());
        assertEquals(new BigDecimal("300.00"), retrieved.getArea());

        // Remove
        Order removed = dao.removeOrder(date, 1);

        assertEquals(order, removed);
        assertTrue(dao.getOrdersForDate(date).isEmpty());
    }

    @Test
    public void testNextOrderNumber() throws PersistenceException {
        // No orders
        assertEquals(1, dao.getNextOrderNumber());

        LocalDate firstDate = LocalDate.of(2027, 8, 1);
        LocalDate secondDate = LocalDate.of(2027, 8, 2);

        dao.addOrder(firstDate, createOrder(2, firstDate));
        dao.addOrder(secondDate, createOrder(7, secondDate));

        assertEquals(8, dao.getNextOrderNumber());
    }

    @Test
    public void testMissingOrderThrowsException()
            throws PersistenceException {

        LocalDate date = LocalDate.of(2027, 7, 1);

        assertThrows(
                NoSuchOrderException.class,
                () -> dao.getOrder(date, 999)
        );
    }
}