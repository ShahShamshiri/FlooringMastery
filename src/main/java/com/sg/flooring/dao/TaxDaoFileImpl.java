package com.sg.flooring.dao;

import com.sg.flooring.exceptions.PersistenceException;
import com.sg.flooring.model.Tax;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class TaxDaoFileImpl implements TaxDao {

    private final Map<String, Tax> allTaxes = new HashMap<>();

    private final String taxFile;
    private static final String DELIMITER = "::";

    public TaxDaoFileImpl() {
        this.taxFile = "Data/Taxes.txt";
    }

    public TaxDaoFileImpl(String taxFile) {
        this.taxFile = taxFile;
    }

    private void loadFile() throws PersistenceException {

        allTaxes.clear();

        Scanner scanner;

        try {
            scanner = new Scanner(new File(taxFile));
        } catch (FileNotFoundException e) {

            throw new PersistenceException(
                    "Could not load tax data.",
                    e
            );
        }

        // Skip header
        if (scanner.hasNextLine()) {
            scanner.nextLine();
        }

        while (scanner.hasNextLine()) {

            String currentLine = scanner.nextLine();

            if (currentLine.trim().isEmpty()) {
                continue;
            }

            Tax currentTax = unmarshallTax(currentLine);

            allTaxes.put(
                    currentTax.getState().toUpperCase(),
                    currentTax
            );
        }

        scanner.close();
    }

    private Tax unmarshallTax(String taxAsText)
            throws PersistenceException {

        String[] taxTokens =
                taxAsText.split(DELIMITER);

        if (taxTokens.length != 3) {
            throw new PersistenceException(
                    "Invalid tax data: " + taxAsText
            );
        }

        try {

            String state =
                    taxTokens[0].trim();

            String stateName =
                    taxTokens[1].trim();

            BigDecimal taxRate =
                    new BigDecimal(taxTokens[2].trim());

            return new Tax(
                    state,
                    stateName,
                    taxRate
            );

        } catch (NumberFormatException e) {

            throw new PersistenceException(
                    "Invalid tax data: "
                            + taxAsText,
                    e
            );
        }
    }

    @Override
    public List<Tax> getAllTaxes()
            throws PersistenceException {

        loadFile();

        return allTaxes.values()
                .stream()
                .sorted(
                        (tax1, tax2) ->
                                tax1.getState()
                                        .compareToIgnoreCase(
                                                tax2.getState()
                                        )
                )
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public Tax getTax(String state)
            throws PersistenceException {

        loadFile();

        if (state == null) {
            return null;
        }

        return allTaxes.get(
                state.trim().toUpperCase()
        );
    }
}