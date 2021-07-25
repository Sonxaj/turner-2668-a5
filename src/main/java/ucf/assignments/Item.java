/*
 *  UCF COP3330 Summer 2021 Assignment 5 Solution
 *  Copyright 2021 Jonas Turner
 */

package ucf.assignments;

import java.math.BigDecimal;
import java.util.Locale;

public class Item {

    // item details
    private String value; // recommended over float and double for rounding reasons
    private String name;
    private String serialNumber;


    public Item(BigDecimal value, String serialNumber, String name){
        super();

        this.value = "$" + value.toString();

        // characters in length is [2, 256]
        this.name = name;

        // serial number should be uppercase
        this.serialNumber = serialNumber.toUpperCase();
    }



    // gets and sets

    public String getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = "$" + value.toString();
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber.toUpperCase(Locale.ROOT);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
