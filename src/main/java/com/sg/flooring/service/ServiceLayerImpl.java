package com.sg.flooring.service;

import com.sg.flooring.dao.*;
import com.sg.flooring.exceptions.NoSuchOrderException;
import com.sg.flooring.exceptions.OrderValidationException;
import com.sg.flooring.exceptions.PersistenceException;
import com.sg.flooring.model.Order;
import com.sg.flooring.model.Product;
import com.sg.flooring.model.Tax;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class ServiceLayerImpl implements ServiceLayer {

    private final OrderDao orderDao;
    private final ProductDao productDao;
    private final TaxDao taxDao;
    private final ExportDao exportDao;
    private final AuditDao auditDao;

    private static final BigDecimal MINIMUM_AREA = new BigDecimal("100");
    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

    public ServiceLayerImpl(OrderDao orderDao, ProductDao productDao, TaxDao taxDao, ExportDao exportDao, AuditDao auditDao
    ) {
        this.orderDao = orderDao;
        this.productDao = productDao;
        this.taxDao = taxDao;
        this.exportDao = exportDao;
        this.auditDao = auditDao;
    }

    private void validateOrderDate(LocalDate date)
            throws OrderValidationException {

        if (date == null) {
            throw new OrderValidationException("Order date is required.");
        }

        if (!date.isAfter(LocalDate.now())) {
            throw new OrderValidationException("Order date must be in the future.");
        }
    }

    private void validateCustomerName(String customerName)
            throws OrderValidationException {

        if (customerName == null
                || customerName.trim().isEmpty()) {

            throw new OrderValidationException("Customer name may not be blank.");
        }

        String trimmedName = customerName.trim();

        if (!trimmedName.matches("[a-zA-Z0-9., ]+")) {

            throw new OrderValidationException("Customer name may only contain " + "letters, numbers, spaces, " + "periods, and commas.");
        }
    }

    private void validateArea(BigDecimal area)
            throws OrderValidationException {

        if (area == null) {

            throw new OrderValidationException("Area is required.");
        }

        if (area.compareTo(BigDecimal.ZERO) <= 0) {

            throw new OrderValidationException("Area must be positive.");
        }

        if (area.compareTo(MINIMUM_AREA) < 0) {
            throw new OrderValidationException("Minimum order size is 100 square feet.");
        }
    }

    private Tax validateAndGetTax(String state)
            throws PersistenceException,
            OrderValidationException {

        if (state == null
                || state.trim().isEmpty()) {

            throw new OrderValidationException("State is required.");
        }

        Tax tax = taxDao.getTax(state.trim().toUpperCase());

        if (tax == null) {

            throw new OrderValidationException("We cannot sell to state: " + state);
        }

        return tax;
    }

    private Product validateAndGetProduct(String productType)
            throws PersistenceException,
            OrderValidationException {

        if (productType == null
                || productType.trim().isEmpty()) {

            throw new OrderValidationException(
                    "Product type is required."
            );
        }

        Product product =
                productDao.getProduct(productType.trim());

        if (product == null) {

            throw new OrderValidationException("Invalid product type: " + productType);
        }

        return product;
    }

    private void calculateOrder(Order order, Tax taxInfo, Product productInfo) {

        BigDecimal materialCost = order.getArea().multiply(productInfo.getCostPerSquareFoot());

        BigDecimal laborCost = order.getArea().multiply(productInfo.getLaborCostPerSquareFoot());

        BigDecimal taxRateDecimal = taxInfo.getTaxRate().divide(ONE_HUNDRED, 10, RoundingMode.HALF_UP);

        BigDecimal tax = materialCost.add(laborCost).multiply(taxRateDecimal);

        BigDecimal total = materialCost.add(laborCost).add(tax);

        order.setMaterialCost(materialCost.setScale(2, RoundingMode.HALF_UP));
        order.setLaborCost(laborCost.setScale(2, RoundingMode.HALF_UP));
        order.setTax(tax.setScale(2, RoundingMode.HALF_UP));
        order.setTotal(total.setScale(2, RoundingMode.HALF_UP)
        );
    }

    private Order buildOrder(int orderNumber, LocalDate date, String customerName, String state, String productType, BigDecimal area
    ) throws PersistenceException,
            OrderValidationException {

        validateOrderDate(date);
        validateCustomerName(customerName);
        validateArea(area);

        Tax taxInfo = validateAndGetTax(state);

        Product productInfo = validateAndGetProduct(productType);

        Order order = new Order();
        order.setOrderNumber(orderNumber);
        order.setOrderDate(date);
        order.setCustomerName(customerName.trim());
        order.setState(taxInfo.getState());
        order.setTaxRate(taxInfo.getTaxRate());

        order.setProductType(productInfo.getProductType());
        order.setArea(area.setScale(2, RoundingMode.HALF_UP));
        order.setCostPerSquareFoot(productInfo.getCostPerSquareFoot());
        order.setLaborCostPerSquareFoot(productInfo.getLaborCostPerSquareFoot());
        calculateOrder(order, taxInfo, productInfo);

        return order;
    }

    @Override
    public Order createOrder(LocalDate date, String customerName, String state, String productType, BigDecimal area
    ) throws PersistenceException,
            OrderValidationException {

        int nextOrderNumber = orderDao.getNextOrderNumber();

        return buildOrder(nextOrderNumber, date, customerName, state, productType, area
        );
    }

    @Override
    public Order saveOrder(LocalDate date, Order order)
            throws PersistenceException {

        Order savedOrder = orderDao.addOrder(date, order);

        auditDao.writeAuditEntry("Added order #" + savedOrder.getOrderNumber() + " for " + date + ".");

        return savedOrder;
    }

    @Override
    public Order updateOrder(
            LocalDate date,
            int orderNumber,
            String customerName,
            String state,
            String productType,
            BigDecimal area
    ) throws PersistenceException,
            NoSuchOrderException,
            OrderValidationException {

        Order existingOrder = orderDao.getOrder(date, orderNumber);

        validateCustomerName(customerName);
        validateArea(area);

        Tax taxInfo = validateAndGetTax(state);

        Product productInfo = validateAndGetProduct(productType);


        Order editedOrder = new Order();

        editedOrder.setOrderNumber(existingOrder.getOrderNumber());
        editedOrder.setOrderDate(existingOrder.getOrderDate());
        editedOrder.setCustomerName(customerName.trim());
        editedOrder.setState(taxInfo.getState());
        editedOrder.setTaxRate(taxInfo.getTaxRate());
        editedOrder.setProductType(productInfo.getProductType());
        editedOrder.setArea(area.setScale(2, RoundingMode.HALF_UP));
        editedOrder.setCostPerSquareFoot(productInfo.getCostPerSquareFoot());
        editedOrder.setLaborCostPerSquareFoot(productInfo.getLaborCostPerSquareFoot());
        calculateOrder(editedOrder, taxInfo, productInfo
        );

        return editedOrder;
    }

    @Override
    public List<Order> getOrdersForDate(LocalDate date)
            throws PersistenceException {

        return orderDao.getOrdersForDate(date);
    }

    @Override
    public Order getOrder(LocalDate date, int orderNumber)
            throws PersistenceException, NoSuchOrderException {

        return orderDao.getOrder(date, orderNumber);
    }

    @Override
    public List<Product> getAllProducts()
            throws PersistenceException {

        return productDao.getAllProducts();
    }

    @Override
    public List<Tax> getAllTaxes()
            throws PersistenceException {

        return taxDao.getAllTaxes();
    }

    @Override
    public Map<LocalDate, Map<Integer, Order>> getAllOrders()
            throws PersistenceException {

        return orderDao.getAllOrders();
    }

    @Override
    public Order saveEditedOrder(LocalDate date, Order order

    ) throws PersistenceException,
            NoSuchOrderException {

        Order savedOrder =
                orderDao.editOrder(date, order);

        auditDao.writeAuditEntry("Edited order #" + savedOrder.getOrderNumber() + " for " + date + ".");

        return savedOrder;
    }

    @Override
    public Order removeOrder(LocalDate date, int orderNumber)
            throws PersistenceException,
            NoSuchOrderException {

        Order removedOrder = orderDao.removeOrder(date, orderNumber);

        auditDao.writeAuditEntry("Removed order #" + removedOrder.getOrderNumber() + " for " + date + ".");
        return removedOrder;
    }

    @Override
    public void exportAllData()
            throws PersistenceException {

        Map<LocalDate, Map<Integer, Order>> allOrders =
                orderDao.getAllOrders();

        exportDao.exportAllData(allOrders);

        auditDao.writeAuditEntry("Exported all order data.");
    }


}