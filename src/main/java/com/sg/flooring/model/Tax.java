package com.sg.flooring.model;

import java.math.BigDecimal;
import java.util.Objects;

public class Tax {

    private String state;
    private String stateName;
    private BigDecimal taxRate;

    public Tax() {
    }

    public Tax(String state,
               String stateName,
               BigDecimal taxRate) {
        this.state = state;
        this.stateName = stateName;
        this.taxRate = taxRate;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    @Override
    public String toString() {
        return state
                + " - "
                + stateName
                + " | Tax Rate: "
                + taxRate
                + "%";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Tax)) {
            return false;
        }

        Tax tax = (Tax) o;

        return Objects.equals(state, tax.state)
                && Objects.equals(stateName, tax.stateName)
                && Objects.equals(taxRate, tax.taxRate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(state, stateName, taxRate);
    }
}

