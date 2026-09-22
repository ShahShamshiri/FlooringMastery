package com.sg.flooring.controller;

import com.sg.flooring.exceptions.NoSuchOrderException;
import com.sg.flooring.exceptions.OrderValidationException;
import com.sg.flooring.exceptions.PersistenceException;
import com.sg.flooring.model.Order;
import com.sg.flooring.model.Product;
import com.sg.flooring.service.ServiceLayer;
import com.sg.flooring.view.View;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class Controller {

    private final ServiceLayer service;
    private final View view;

    public Controller(ServiceLayer service, View view) {
        this.service = service;
        this.view = view;
    }

    public void run() {

        boolean keepGoing = true;
        try {

            while (keepGoing) {

                int menuSelection = view.displayMainMenuAndGetSelection();

                switch (menuSelection) {

                    case 1:
                        displayOrders();
                        break;

                    case 2:
                        addOrder();
                        break;

                    case 3:
                        editOrder();
                        break;

                    case 4:
                        removeOrder();
                        break;

                    case 5:
                        exportAllData();
                        break;

                    case 6:
                        keepGoing = false;
                        break;

                    default:
                        view.displayUnknownCommandMessage();
                }
            }

            view.displayExitMessage();

        } catch (Exception e) {

            view.displayErrorMessage("Error: : " + e.getMessage());
        }
    }

    private void displayOrders() {

        try {

            LocalDate date =
                    view.getDateInput("Enter order date (MM/DD/YYYY): ");

            List<Order> orders = service.getOrdersForDate(date);

            if (orders.isEmpty()) {

                view.displayErrorMessage("No orders exist for that date.");

                return;
            }

            view.displayOrders(orders);

        } catch (PersistenceException e) {
            view.displayErrorMessage(
                    e.getMessage()
            );
        }
    }

    private void addOrder() {

        view.displayAddOrderBanner();

        try {

            List<Product> products = service.getAllProducts();
            view.displayProducts(products);

        } catch (PersistenceException e) {

            view.displayErrorMessage(e.getMessage());
            return;
        }

        while (true) {

            try {

                LocalDate date = view.getDateInput("Enter order date (MM/DD/YYYY): ");
                String customerName = view.getCustomerNameInput();
                String state = view.getStateInput();
                String productType = view.getProductTypeInput();
                BigDecimal area = view.getAreaInput();

                Order newOrder = service.createOrder(date, customerName, state, productType, area);

                view.displayOrderSummary(newOrder);

                boolean confirmed = view.getConfirmation("Place this order?");

                if (confirmed) {

                    service.saveOrder(date, newOrder);
                    view.displayAddOrderSuccess();
                }

                return;

            } catch (OrderValidationException e) {

                view.displayErrorMessage(e.getMessage());
                view.displayErrorMessage("Please enter the order information again.");

            } catch (PersistenceException e) {

                view.displayErrorMessage(e.getMessage());
                return;
            }
        }
    }

    private void editOrder() {

        view.displayEditOrderBanner();

        LocalDate date = view.getDateInput("Enter order date (MM/DD/YYYY): ");
        int orderNumber = view.getOrderNumberInput();

        Order existingOrder;

        try {

            existingOrder = service.getOrder(date, orderNumber);

        } catch (NoSuchOrderException e) {

            view.displayErrorMessage(e.getMessage());
            return;

        } catch (PersistenceException e) {

            view.displayErrorMessage(e.getMessage());
            return;
        }

        view.displayOrderSummary(
                existingOrder
        );

        while (true) {

            try {

                String customerInput = view.getEditCustomerNameInput(existingOrder);
                String stateInput = view.getEditStateInput(existingOrder);
                String productInput = view.getEditProductTypeInput(existingOrder);

                String areaInput = view.getEditAreaInput(existingOrder);

                // If the input is empty first option, otherwise second
                String customerName = customerInput.trim().isEmpty() ?
                        existingOrder.getCustomerName() : customerInput;

                String state = stateInput.trim().isEmpty() ?
                        existingOrder.getState() : stateInput;

                String productType =
                        productInput.trim().isEmpty() ?
                                existingOrder.getProductType() : productInput;

                BigDecimal area;

                if (areaInput.trim().isEmpty()) {

                    area = existingOrder.getArea();

                } else {

                    try {

                        area = new BigDecimal(areaInput.trim());

                    } catch (NumberFormatException e) {

                        view.displayErrorMessage("Area must be a valid decimal number.");
                        continue;
                    }
                }

                Order editedOrder =
                        service.updateOrder(date, orderNumber, customerName, state,productType, area);

                view.displayOrderSummary(editedOrder);

                boolean saveEdit = view.getConfirmation("Save these changes?");

                if (saveEdit) {

                    service.saveEditedOrder(date, editedOrder);
                    view.displayEditOrderSuccess();
                }

                return;

            } catch (OrderValidationException e) {

                view.displayErrorMessage(e.getMessage());
                view.displayErrorMessage("Please enter the edited values again.");

            } catch (NoSuchOrderException e) {

                view.displayErrorMessage(e.getMessage());
                return;

            } catch (PersistenceException e) {

                view.displayErrorMessage(e.getMessage());
                return;
            }
        }
    }

    private void removeOrder() {

        view.displayRemoveOrderBanner();
        LocalDate date = view.getDateInput("Enter order date (MM/DD/YYYY): ");

        int orderNumber = view.getOrderNumberInput();

        try {

            Order order = service.getOrder(date, orderNumber);

            view.displayOrderSummary(order);

            boolean confirmed = view.getConfirmation("Are you sure you want to remove this order?");

            if (!confirmed) {
                return;
            }

            service.removeOrder(date, orderNumber);
            view.displayRemoveOrderSuccess();

        } catch (NoSuchOrderException e) {

            view.displayErrorMessage(e.getMessage());

        } catch (PersistenceException e) {

            view.displayErrorMessage(e.getMessage());
        }
    }

    private void exportAllData() {

        try {

            service.exportAllData();
            view.displayExportDataSuccess();

        } catch (PersistenceException e) {

            view.displayErrorMessage(
                    e.getMessage()
            );
        }
    }

}