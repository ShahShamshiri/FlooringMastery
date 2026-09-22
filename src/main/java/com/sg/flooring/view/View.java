package com.sg.flooring.view;



import com.sg.flooring.model.Order;
import com.sg.flooring.model.Product;
import com.sg.flooring.model.Tax;
import com.sg.flooring.view.UserIO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class View {

    private final UserIO io;

    public View(UserIO io) {
        this.io = io;
    }

    public int displayMainMenuAndGetSelection() {

        io.print("");
        io.print("==============================");
        io.print("== Flooring Program ==");
        io.print("1. Display Orders");
        io.print("2. Add an Order");
        io.print("3. Edit an Order");
        io.print("4. Remove an Order");
        io.print("5. Export All Data");
        io.print("6. Quit");
        io.print("==============================");

        return io.readInt(
                "Please select an option: ",
                1,
                6
        );
    }

    public LocalDate getDateInput(String prompt) {
        return io.readLocalDate(prompt);
    }

    public int getOrderNumberInput() {
        return io.readInt("Enter order number: ");
    }

    public String getCustomerNameInput() {
        return io.readString("Enter customer name: ");
    }

    public String getStateInput() {
        return io.readString("Enter state abbreviation: ");
    }

    public String getProductTypeInput() {
        return io.readString("Enter product type: ");
    }

    public BigDecimal getAreaInput() {
        return io.readBigDecimal("Enter area in square feet: ");
    }

    public boolean getConfirmation(String message) {
        return io.readYesNo(message + " (Y/N): ");
    }

    public void displayOrders(List<Order> orders) {

        io.print("");
        io.print("===== Orders =====");

        for (Order order : orders) {
            displayOrderInfo(order);
            io.print("------------------------------");
        }
    }

    public void displayOrderInfo(Order order) {

        io.print("Order Number: " + order.getOrderNumber());
        io.print("Customer Name: " + order.getCustomerName());
        io.print("State: " + order.getState());
        io.print("Tax Rate: " + order.getTaxRate() + "%");
        io.print("Product Type: " + order.getProductType());
        io.print("Area: " + order.getArea());
        io.print("Cost Per Square Foot: $"
                + order.getCostPerSquareFoot());
        io.print("Labor Cost Per Square Foot: $"
                + order.getLaborCostPerSquareFoot());
        io.print("Material Cost: $"
                + order.getMaterialCost());
        io.print("Labor Cost: $"
                + order.getLaborCost());
        io.print("Tax: $" + order.getTax());
        io.print("Total: $" + order.getTotal());
    }

    public void displayProducts(List<Product> products) {

        io.print("");
        io.print("===== Available Products =====");

        for (Product product : products) {

            io.print(
                    product.getProductType()
                            + " | Material: $"
                            + product.getCostPerSquareFoot()
                            + "/sq ft"
                            + " | Labor: $"
                            + product.getLaborCostPerSquareFoot()
                            + "/sq ft"
            );
        }
    }

    public void displayTaxes(List<Tax> taxes) {

        io.print("");
        io.print("===== Available States =====");

        for (Tax tax : taxes) {

            io.print(
                    tax.getState()
                            + " - "
                            + tax.getStateName()
                            + " | Tax Rate: "
                            + tax.getTaxRate()
                            + "%"
            );
        }
    }

    public void displayAddOrderBanner() {
        io.print("");
        io.print("===== Add Order =====");
    }

    public void displayEditOrderBanner() {
        io.print("");
        io.print("===== Edit Order =====");
    }

    public void displayRemoveOrderBanner() {
        io.print("");
        io.print("===== Remove Order =====");
    }

    public void displayAddOrderSuccess() {
        io.print("Order added successfully.");
    }

    public void displayEditOrderSuccess() {
        io.print("Order updated successfully.");
    }

    public void displayRemoveOrderSuccess() {
        io.print("=== Order removed success ===");
    }

    public void displayExportDataSuccess() {
        io.print("All order data exported successfully.");
    }

    public void displayExitMessage() {
        io.print("==== Goodbye ==== ");
    }

    public void displayErrorMessage(String message) {
        io.print("ERROR: " + message);
    }

    public void displayUnknownCommandMessage() {
        io.print("Unknown command.");
    }

    public String getEditCustomerNameInput(Order order) {

        return io.readString(
                "Enter customer name ("
                        + order.getCustomerName()
                        + "): "
        );
    }

    public String getEditStateInput(Order order) {

        return io.readString("Enter state (" + order.getState() + "): "
        );
    }

    public String getEditProductTypeInput(Order order) {

        return io.readString(
                "Enter product type ("
                        + order.getProductType()
                        + "): "
        );
    }

    public String getEditAreaInput(Order order) {

        return io.readString(
                "Enter area ("
                        + order.getArea()
                        + "): "
        );
    }

    public void displayOrderSummary(Order order) {

        io.print("");
        io.print("===== Order Summary =====");

        displayOrderInfo(order);

        io.print("===========Summary end==============");
    }

}