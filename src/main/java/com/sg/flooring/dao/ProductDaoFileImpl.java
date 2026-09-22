package com.sg.flooring.dao;

import com.sg.flooring.exceptions.PersistenceException;
import com.sg.flooring.model.Product;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ProductDaoFileImpl implements ProductDao {

    private final Map<String, Product> allProducts = new HashMap<>();

    private final String productFile;
    private static final String DELIMITER = "::";

    public ProductDaoFileImpl() {
        this.productFile = "Data/Products.txt";
    }

    public ProductDaoFileImpl(String productFile) {
        this.productFile = productFile;
    }

    private void loadFile() throws PersistenceException {

        allProducts.clear();

        Scanner scanner;

        try {
            scanner = new Scanner(new File(productFile));
        } catch (FileNotFoundException e) {
            throw new PersistenceException(
                    "Could not load product data.",
                    e
            );
        }

        // Skip header row
        if (scanner.hasNextLine()) {
            scanner.nextLine();
        }

        while (scanner.hasNextLine()) {

            String currentLine = scanner.nextLine();

            if (currentLine.trim().isEmpty()) {
                continue;
            }

            Product currentProduct = unmarshallProduct(currentLine);

            allProducts.put(
                    currentProduct.getProductType().toLowerCase(),
                    currentProduct
            );
        }

        scanner.close();
    }

    private Product unmarshallProduct(String productAsText)
            throws PersistenceException {

        String[] productTokens =
                productAsText.split(DELIMITER);

        if (productTokens.length != 3) {
            throw new PersistenceException(
                    "Invalid product data: " + productAsText
            );
        }

        try {

            String productType = productTokens[0].trim();

            BigDecimal costPerSquareFoot =
                    new BigDecimal(productTokens[1].trim());

            BigDecimal laborCostPerSquareFoot =
                    new BigDecimal(productTokens[2].trim());

            return new Product(
                    productType,
                    costPerSquareFoot,
                    laborCostPerSquareFoot
            );

        } catch (NumberFormatException e) {

            throw new PersistenceException(
                    "Invalid numeric product data: "
                            + productAsText,
                    e
            );
        }
    }

    @Override
    public List<Product> getAllProducts()
            throws PersistenceException {

        loadFile();

        return allProducts.values()
                .stream()
                .sorted(
                        (product1, product2) ->
                                product1.getProductType()
                                        .compareToIgnoreCase(
                                                product2.getProductType()
                                        )
                )
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public Product getProduct(String productType)
            throws PersistenceException {

        loadFile();

        if (productType == null) {
            return null;
        }

        return allProducts.get(
                productType.trim().toLowerCase()
        );
    }
}