package com.sg.flooring.dao;

import com.sg.flooring.exceptions.NoSuchOrderException;
import com.sg.flooring.exceptions.PersistenceException;
import com.sg.flooring.model.Order;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

public class OrderDaoFileImpl implements OrderDao {

    private final String orderFolder;
    private static final String DELIMITER = "::";

    public OrderDaoFileImpl() {
        this.orderFolder = "Orders";
    }

    public OrderDaoFileImpl(String orderFolder) {
        this.orderFolder = orderFolder;
    }

    private static final String HEADER = "OrderNumber::CustomerName::State::TaxRate::ProductType::Area::" + "CostPerSquareFoot::LaborCostPerSquareFoot::" + "MaterialCost::LaborCost::Tax::Total";

    private static final DateTimeFormatter FILE_DATE_FORMATTER = DateTimeFormatter.ofPattern("MMddyyyy");

    private final Map<LocalDate, Map<Integer, Order>> orders = new HashMap<>();

    private String getOrderFileName(LocalDate date) {

        return orderFolder + File.separator + "Orders_" + date.format(FILE_DATE_FORMATTER) + ".txt";
    }

    private Order unmarshallOrder(String orderAsText, LocalDate orderDate) throws PersistenceException {

        String[] tokens = orderAsText.split(DELIMITER, -1);

        if (tokens.length != 12) {
            throw new PersistenceException("Invalid order data: " + orderAsText);
        }

        try {

            Order order = new Order();

            order.setOrderNumber(Integer.parseInt(tokens[0].trim()));

            order.setCustomerName(tokens[1].trim());

            order.setState(tokens[2].trim());

            order.setTaxRate(new BigDecimal(tokens[3].trim()));

            order.setProductType(tokens[4].trim());

            order.setArea(new BigDecimal(tokens[5].trim()));

            order.setCostPerSquareFoot(new BigDecimal(tokens[6].trim()));

            order.setLaborCostPerSquareFoot(new BigDecimal(tokens[7].trim()));

            order.setMaterialCost(new BigDecimal(tokens[8].trim()));

            order.setLaborCost(new BigDecimal(tokens[9].trim()));

            order.setTax(new BigDecimal(tokens[10].trim()));

            order.setTotal(new BigDecimal(tokens[11].trim()));

            order.setOrderDate(orderDate);

            return order;

        } catch (NumberFormatException e) {

            throw new PersistenceException("Invalid numeric order data: " + orderAsText, e);
        }
    }

    private String marshallOrder(Order order) {

        return order.getOrderNumber() + DELIMITER + order.getCustomerName() + DELIMITER + order.getState() + DELIMITER + order.getTaxRate() + DELIMITER + order.getProductType() + DELIMITER + order.getArea() + DELIMITER + order.getCostPerSquareFoot() + DELIMITER + order.getLaborCostPerSquareFoot() + DELIMITER + order.getMaterialCost() + DELIMITER + order.getLaborCost() + DELIMITER + order.getTax() + DELIMITER + order.getTotal();
    }

    private void loadOrdersForDate(LocalDate date) throws PersistenceException {

        Map<Integer, Order> ordersForDate = new HashMap<>();

        File orderFile = new File(getOrderFileName(date));

        if (!orderFile.exists()) {

            orders.put(date, ordersForDate);
            return;
        }

        try (Scanner scanner = new Scanner(orderFile)) {

            if (scanner.hasNextLine()) {
                scanner.nextLine();
            }

            while (scanner.hasNextLine()) {

                String currentLine = scanner.nextLine();

                if (currentLine.trim().isEmpty()) {
                    continue;
                }

                Order order = unmarshallOrder(currentLine, date);

                ordersForDate.put(order.getOrderNumber(), order);
            }

            orders.put(date, ordersForDate);

        } catch (FileNotFoundException e) {

            throw new PersistenceException("Could not load orders for " + date, e);
        }
    }

    private void writeOrdersForDate(LocalDate date) throws PersistenceException {

        File folder = new File(orderFolder);

        if (!folder.exists()) {

            boolean created = folder.mkdirs();

            if (!created) {
                throw new PersistenceException("Could not create Orders directory.");
            }
        }

        Map<Integer, Order> ordersForDate = orders.get(date);

        if (ordersForDate == null || ordersForDate.isEmpty()) {

            File existingFile = new File(getOrderFileName(date));

            if (existingFile.exists() && !existingFile.delete()) {

                throw new PersistenceException("Could not delete empty order file.");
            }

            return;
        }

        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(getOrderFileName(date))))) {

            out.println(HEADER);

            List<Order> sortedOrders = ordersForDate.values().stream().sorted((order1, order2) -> Integer.compare(order1.getOrderNumber(), order2.getOrderNumber())).collect(Collectors.toList());

            for (Order order : sortedOrders) {

                out.println(marshallOrder(order));
            }

        } catch (IOException e) {

            throw new PersistenceException("Could not save orders for " + date, e);
        }
    }

    @Override
    public List<Order> getOrdersForDate(LocalDate date) throws PersistenceException {

        loadOrdersForDate(date);

        Map<Integer, Order> ordersForDate = orders.get(date);

        return ordersForDate.values().stream().sorted((order1, order2) -> Integer.compare(order1.getOrderNumber(), order2.getOrderNumber())).collect(Collectors.toList());
    }

    @Override
    public Order getOrder(LocalDate date, int orderNumber) throws PersistenceException, NoSuchOrderException {

        loadOrdersForDate(date);
        Map<Integer, Order> ordersForDate = orders.get(date);

        Order order = ordersForDate.get(orderNumber);

        if (order == null) {

            throw new NoSuchOrderException("Order #" + orderNumber + " does not exist for " + date + ".");
        }

        return order;
    }

    @Override
    public Order addOrder(LocalDate date, Order order) throws PersistenceException {

        loadOrdersForDate(date);
        Map<Integer, Order> ordersForDate = orders.get(date);
        order.setOrderDate(date);
        ordersForDate.put(order.getOrderNumber(), order);

        writeOrdersForDate(date);

        return order;
    }

    @Override
    public Order editOrder(LocalDate date, Order order) throws PersistenceException, NoSuchOrderException {

        loadOrdersForDate(date);
        Map<Integer, Order> ordersForDate = orders.get(date);

        if (!ordersForDate.containsKey(order.getOrderNumber())) {

            throw new NoSuchOrderException("Order #" + order.getOrderNumber() + " does not exist for " + date + ".");
        }

        order.setOrderDate(date);
        ordersForDate.put(order.getOrderNumber(), order);

        writeOrdersForDate(date);

        return order;
    }

    @Override
    public Order removeOrder(LocalDate date, int orderNumber) throws PersistenceException, NoSuchOrderException {

        loadOrdersForDate(date);

        Map<Integer, Order> ordersForDate = orders.get(date);

        Order removed = ordersForDate.remove(orderNumber);

        if (removed == null) {

            throw new NoSuchOrderException("Order #" + orderNumber + " does not exist for " + date + ".");
        }

        writeOrdersForDate(date);

        return removed;
    }

    private void loadAllOrders() throws PersistenceException {

        orders.clear();

        File folder = new File(orderFolder);

        if (!folder.exists()) {
            return;
        }

        File[] files = folder.listFiles();

        if (files == null) {
            return;
        }

        for (File file : files) {

            String fileName = file.getName();

            if (!fileName.startsWith("Orders_") || !fileName.endsWith(".txt")) {
                continue;
            }

            String dateText = fileName.substring("Orders_".length(), fileName.length() - ".txt".length());

            try {

                LocalDate date = LocalDate.parse(dateText, FILE_DATE_FORMATTER);

                loadOrdersForDate(date);

            } catch (Exception e) {

                throw new PersistenceException("Invalid order filename: " + fileName, e);
            }
        }
    }

    @Override
    public int getNextOrderNumber() throws PersistenceException {

        loadAllOrders();

        int largestOrderNumber = orders.values().stream().flatMap(ordersForDate -> ordersForDate.values().stream()).mapToInt(Order::getOrderNumber).max().orElse(0);

        return largestOrderNumber + 1;
    }

    @Override
    public Map<LocalDate, Map<Integer, Order>> getAllOrders() throws PersistenceException {

        loadAllOrders();

        return new HashMap<>(orders);
    }


}