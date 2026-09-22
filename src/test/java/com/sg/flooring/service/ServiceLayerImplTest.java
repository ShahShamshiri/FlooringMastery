package com.sg.flooring.service;

import com.sg.flooring.dao.*;
import com.sg.flooring.exceptions.NoSuchOrderException;
import com.sg.flooring.exceptions.OrderValidationException;
import com.sg.flooring.exceptions.PersistenceException;
import com.sg.flooring.model.Order;
import com.sg.flooring.model.Product;
import com.sg.flooring.model.Tax;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceLayerImplTest {

    private ServiceLayer service;
    private TestOrderDao orderDao;
    private TestExportDao exportDao;

    @BeforeEach
    public void setUp() {

        orderDao = new TestOrderDao();
        exportDao = new TestExportDao();

        service = new ServiceLayerImpl(
                orderDao,
                new TestProductDao(),
                new TestTaxDao(),
                exportDao,
                entry -> { }
        );
    }


    @Test
    public void testCreateOrderAndCalculations()
            throws PersistenceException, OrderValidationException {

        orderDao.nextNumber = 5;

        Order order = service.createOrder(
                LocalDate.now().plusDays(5),
                "Acme, Inc.",
                "TX",
                "Tile",
                new BigDecimal("200")
        );

        assertEquals(5, order.getOrderNumber());
        assertEquals("Acme, Inc.", order.getCustomerName());
        assertEquals("Tile", order.getProductType());

        assertEquals(
                new BigDecimal("700.00"),
                order.getMaterialCost()
        );

        assertEquals(
                new BigDecimal("830.00"),
                order.getLaborCost()
        );

        assertEquals(
                new BigDecimal("68.09"),
                order.getTax()
        );

        assertEquals(
                new BigDecimal("1598.09"),
                order.getTotal()
        );
    }


    @Test
    public void testInvalidOrdersRejected() {

        LocalDate future =
                LocalDate.now().plusDays(5);

        // Date must be in the future
        assertThrows(
                OrderValidationException.class,
                () -> service.createOrder(
                        LocalDate.now(),
                        "Customer",
                        "TX",
                        "Tile",
                        new BigDecimal("200")
                )
        );

        // State must exist
        assertThrows(
                OrderValidationException.class,
                () -> service.createOrder(
                        future,
                        "Customer",
                        "ZZ",
                        "Tile",
                        new BigDecimal("200")
                )
        );

        // Product must exist
        assertThrows(
                OrderValidationException.class,
                () -> service.createOrder(
                        future,
                        "Customer",
                        "TX",
                        "Stone",
                        new BigDecimal("200")
                )
        );

        // Minimum area is 100
        assertThrows(
                OrderValidationException.class,
                () -> service.createOrder(
                        future,
                        "Customer",
                        "TX",
                        "Tile",
                        new BigDecimal("50")
                )
        );
    }


    @Test
    public void testSaveEditRemoveAndExport()
            throws PersistenceException,
            OrderValidationException,
            NoSuchOrderException {

        LocalDate date =
                LocalDate.now().plusDays(5);

        Order order = service.createOrder(
                date,
                "Original Customer",
                "TX",
                "Tile",
                new BigDecimal("200")
        );

        service.saveOrder(date, order);

        assertEquals(
                order,
                service.getOrder(
                        date,
                        order.getOrderNumber()
                )
        );

        Order edited = service.updateOrder(
                date,
                order.getOrderNumber(),
                "Edited Customer",
                "TX",
                "Tile",
                new BigDecimal("300")
        );

        assertEquals(
                "Edited Customer",
                edited.getCustomerName()
        );

        assertEquals(
                new BigDecimal("1050.00"),
                edited.getMaterialCost()
        );

        service.saveEditedOrder(
                date,
                edited
        );

        service.removeOrder(
                date,
                edited.getOrderNumber()
        );

        assertTrue(
                service.getOrdersForDate(date)
                        .isEmpty()
        );

        service.exportAllData();

        assertTrue(exportDao.exported);
    }


    // --------------------------------------------------
    // Minimal test DAOs
    // --------------------------------------------------

    private static class TestProductDao
            implements ProductDao {

        @Override
        public List<Product> getAllProducts() {
            return Arrays.asList(
                    getProduct("Tile")
            );
        }

        @Override
        public Product getProduct(String type) {

            if (!"Tile".equalsIgnoreCase(type)) {
                return null;
            }

            return new Product(
                    "Tile",
                    new BigDecimal("3.50"),
                    new BigDecimal("4.15")
            );
        }
    }


    private static class TestTaxDao
            implements TaxDao {

        @Override
        public List<Tax> getAllTaxes() {
            return Arrays.asList(
                    getTax("TX")
            );
        }

        @Override
        public Tax getTax(String state) {

            if (!"TX".equalsIgnoreCase(state)) {
                return null;
            }

            return new Tax(
                    "TX",
                    "Texas",
                    new BigDecimal("4.45")
            );
        }
    }


    private static class TestOrderDao
            implements OrderDao {

        private final Map<Integer, Order> orders =
                new HashMap<>();

        private LocalDate orderDate;

        private int nextNumber = 1;

        @Override
        public List<Order> getOrdersForDate(
                LocalDate date
        ) {
            return new ArrayList<>(
                    orders.values()
            );
        }

        @Override
        public Order getOrder(
                LocalDate date,
                int orderNumber
        ) throws NoSuchOrderException {

            Order order =
                    orders.get(orderNumber);

            if (order == null) {
                throw new NoSuchOrderException(
                        "Order not found."
                );
            }

            return order;
        }

        @Override
        public Order addOrder(
                LocalDate date,
                Order order
        ) {
            orderDate = date;
            orders.put(
                    order.getOrderNumber(),
                    order
            );
            return order;
        }

        @Override
        public Order editOrder(
                LocalDate date,
                Order order
        ) {
            orders.put(
                    order.getOrderNumber(),
                    order
            );
            return order;
        }

        @Override
        public Order removeOrder(
                LocalDate date,
                int orderNumber
        ) {
            return orders.remove(orderNumber);
        }

        @Override
        public int getNextOrderNumber() {
            return nextNumber;
        }

        @Override
        public Map<LocalDate, Map<Integer, Order>>
        getAllOrders() {

            Map<LocalDate, Map<Integer, Order>>
                    result = new HashMap<>();

            if (orderDate != null) {
                result.put(
                        orderDate,
                        new HashMap<>(orders)
                );
            }

            return result;
        }
    }


    private static class TestExportDao
            implements ExportDao {

        private boolean exported;

        @Override
        public void exportAllData(
                Map<LocalDate, Map<Integer, Order>>
                        orders
        ) {
            exported = true;
        }
    }
}