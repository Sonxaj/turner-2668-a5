/*
 *  UCF COP3330 Summer 2021 Assignment 5 Solution
 *  Copyright 2021 Jonas Turner
 */

package ucf.assignments;

import java.math.BigDecimal;

public class Item {

    // item details
    private BigDecimal value; // recommended over float and double for rounding reasons
    private String name;
    private int serialNumber;


    public Item(BigDecimal value, String name, int serialNumber){
        this.value = value;

        // characters in length is [2, 256]
        this.name = name;

        this.serialNumber = serialNumber;
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
