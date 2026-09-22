package com.sg.flooring.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public class Order {

    private int orderNumber;
    private String customerName;
    private String state;
    private LocalDate orderDate;

    private BigDecimal taxRate;

    private String productType;
    private BigDecimal costPerSquareFoot;
    private BigDecimal laborCostPerSquareFoot;

    private BigDecimal area;

    private BigDecimal materialCost;
    private BigDecimal laborCost;
    private BigDecimal tax;
    private BigDecimal total;

    public Order() {
    }

    public Order(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public BigDecimal getCostPerSquareFoot() {
        return costPerSquareFoot;
    }

    public void setCostPerSquareFoot(BigDecimal costPerSquareFoot) {
        this.costPerSquareFoot = costPerSquareFoot;
    }

    public BigDecimal getLaborCostPerSquareFoot() {
        return laborCostPerSquareFoot;
    }

    public void setLaborCostPerSquareFoot(BigDecimal laborCostPerSquareFoot) {
        this.laborCostPerSquareFoot = laborCostPerSquareFoot;
    }

    public BigDecimal getArea() {
        return area;
    }

    public void setArea(BigDecimal area) {
        this.area = area;
    }

    public BigDecimal getMaterialCost() {
        return materialCost;
    }

    public void setMaterialCost(BigDecimal materialCost) {
        this.materialCost = materialCost;
    }

    public BigDecimal getLaborCost() {
        return laborCost;
    }

    public void setLaborCost(BigDecimal laborCost) {
        this.laborCost = laborCost;
    }

    public BigDecimal getTax() {
        return tax;
    }

    public void setTax(BigDecimal tax) {
        this.tax = tax;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "Order #" + orderNumber
                + "\nDate: " + orderDate
                + "\nCustomer: " + customerName
                + "\nState: " + state
                + "\nProduct: " + productType
                + "\nArea: " + area
                + "\nMaterial Cost: $" + materialCost
                + "\nLabor Cost: $" + laborCost
                + "\nTax: $" + tax
                + "\nTotal: $" + total;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Order)) {
            return false;
        }

        Order order = (Order) o;

        return orderNumber == order.orderNumber
                && Objects.equals(customerName, order.customerName)
                && Objects.equals(state, order.state)
                && Objects.equals(orderDate, order.orderDate)
                && Objects.equals(taxRate, order.taxRate)
                && Objects.equals(productType, order.productType)
                && Objects.equals(costPerSquareFoot,
                order.costPerSquareFoot)
                && Objects.equals(laborCostPerSquareFoot,
                order.laborCostPerSquareFoot)
                && Objects.equals(area, order.area)
                && Objects.equals(materialCost, order.materialCost)
                && Objects.equals(laborCost, order.laborCost)
                && Objects.equals(tax, order.tax)
                && Objects.equals(total, order.total);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                orderNumber,
                customerName,
                state,
                orderDate,
                taxRate,
                productType,
                costPerSquareFoot,
                laborCostPerSquareFoot,
                area,
                materialCost,
                laborCost,
                tax,
                total
        );
    }
}
