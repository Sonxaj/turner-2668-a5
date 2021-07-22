/*
 *  UCF COP3330 Summer 2021 Assignment 5 Solution
 *  Copyright 2021 Jonas Turner
 */

package ucf.assignments;

import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class InventoryController implements Initializable {

    // file managers for file operations
    private InvFileManager tsvManager = new InvFileManager("tsv");
    private InvFileManager jsonManager = new InvFileManager("json");
    private InvFileManager htmlManager = new InvFileManager("html");

    // FXML




    @Override
    public void initialize(URL url, ResourceBundle rb){

    }
}
