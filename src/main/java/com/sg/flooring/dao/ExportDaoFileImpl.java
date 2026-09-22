package com.sg.flooring.dao;

import com.sg.flooring.exceptions.PersistenceException;
import com.sg.flooring.model.Order;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class ExportDaoFileImpl implements ExportDao {

    private static final String BACKUP_FOLDER =
            "Backup";

    private static final String EXPORT_FILE =
            "Backup" + File.separator + "DataExport.txt";

    private static final String DELIMITER =
            "::";

    private static final String HEADER =
            "OrderDate::OrderNumber::CustomerName::State::TaxRate::"
                    + "ProductType::Area::CostPerSquareFoot::"
                    + "LaborCostPerSquareFoot::MaterialCost::"
                    + "LaborCost::Tax::Total";

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("MM-dd-yyyy");

    @Override
    public void exportAllData(
            Map<LocalDate, Map<Integer, Order>> allOrders
    ) throws PersistenceException {

        createBackupFolder();

        try (
                PrintWriter out =
                        new PrintWriter(
                                new BufferedWriter(
                                        new FileWriter(EXPORT_FILE)
                                )
                        )
        ) {

            out.println(HEADER);

            allOrders.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(dateEntry -> {

                        LocalDate date =
                                dateEntry.getKey();

                        dateEntry.getValue()
                                .values()
                                .stream()
                                .sorted(
                                        (order1, order2) ->
                                                Integer.compare(
                                                        order1.getOrderNumber(),
                                                        order2.getOrderNumber()
                                                )
                                )
                                .forEach(order ->
                                        out.println(
                                                marshallOrder(
                                                        date,
                                                        order
                                                )
                                        )
                                );
                    });

        } catch (IOException e) {

            throw new PersistenceException(
                    "Could not export order data.",
                    e
            );
        }
    }

    private void createBackupFolder()
            throws PersistenceException {

        File folder = new File(BACKUP_FOLDER);

        if (!folder.exists()) {

            boolean created = folder.mkdirs();

            if (!created) {

                throw new PersistenceException(
                        "Could not create Backup directory."
                );
            }
        }
    }

    private String marshallOrder(LocalDate date, Order order) {

        return date.format(DATE_FORMATTER)
                + DELIMITER
                + order.getOrderNumber()
                + DELIMITER
                + order.getCustomerName()
                + DELIMITER
                + order.getState()
                + DELIMITER
                + order.getTaxRate()
                + DELIMITER
                + order.getProductType()
                + DELIMITER
                + order.getArea()
                + DELIMITER
                + order.getCostPerSquareFoot()
                + DELIMITER
                + order.getLaborCostPerSquareFoot()
                + DELIMITER
                + order.getMaterialCost()
                + DELIMITER
                + order.getLaborCost()
                + DELIMITER
                + order.getTax()
                + DELIMITER
                + order.getTotal();
    }
}