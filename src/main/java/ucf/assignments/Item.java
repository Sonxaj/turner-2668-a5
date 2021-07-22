/*
 *  UCF COP3330 Summer 2021 Assignment 5 Solution
 *  Copyright 2021 Jonas Turner
 */

package ucf.assignments;

import java.math.BigDecimal;

public class Item {

    // item details
    private BigDecimal value; // recommended over float and double for rounding reasons
    private int serialNumber;
    private String name;

    public Item(BigDecimal value, int serialNumber, String name){
        this.value = value;

        this.serialNumber = serialNumber;

        // characters in length is [2, 256]
        this.name = name;
    }



    // gets and sets

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public int getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(int serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
