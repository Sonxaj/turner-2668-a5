/*
 *  UCF COP3330 Summer 2021 Assignment 5 Solution
 *  Copyright 2021 Jonas Turner
 */

package ucf.assignments;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;


import java.net.URL;
import java.util.ResourceBundle;

public class InventoryController implements Initializable {

    // file managers for file operations
    private InvFileManager tsvManager = new InvFileManager("tsv");
    private InvFileManager jsonManager = new InvFileManager("json");
    private InvFileManager htmlManager = new InvFileManager("html");


    // FXML
    // tableview
    @FXML
    TableView<Item> inventoryView;
    @FXML
    TableColumn<Item, String> priceColumn;
    @FXML
    TableColumn<Item, String> nameColumn;
    @FXML
    TableColumn<Item, String> serialColumn;

    // data
    private ObservableList<Item> inventoryData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb){

        // set up table and columns for editing and selection
        inventoryView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        inventoryView.setEditable(true);

        priceColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        serialColumn.setCellFactory(TextFieldTableCell.forTableColumn());


        // set up columns
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        serialColumn.setCellValueFactory(new PropertyValueFactory<>("serialNumber"));


        // set data
        inventoryView.setItems(inventoryData);

        // resizable
        inventoryView.getColumns().get(0).prefWidthProperty().bind(inventoryView.widthProperty().multiply(0.20));
        inventoryView.getColumns().get(1).prefWidthProperty().bind(inventoryView.widthProperty().multiply(0.40));
        inventoryView.getColumns().get(2).prefWidthProperty().bind(inventoryView.widthProperty().multiply(0.40));

    }

    public void addItem(ActionEvent actionEvent){

    }

    public void changeNameCellEvent(TableColumn.CellEditEvent editEvent){
        // get selected item
        Item item = inventoryView.getSelectionModel().getSelectedItem();

        // update name field; check input for length
        if(editEvent.getNewValue().toString().length() < 2 ||
                editEvent.getNewValue().toString().length() > 256){

            // tell user input is not accepted

        }else{
            item.setName(editEvent.getNewValue().toString());
        }

        updateItemView();
    }

    public void changeSerialNumCellEvent(TableColumn.CellEditEvent editEvent){
        // get selected item
        Item item = inventoryView.getSelectionModel().getSelectedItem();

        // update serial number field; check if alphanumeric
        if(stringCheck(editEvent.getNewValue().toString())){
            item.setName(editEvent.getNewValue().toString());
        }else{
            // tell user input is not accepted
        }

        updateItemView();
    }


    // checks a string if its purely alphanumeric;
    // returns true if a string; false if not
    public boolean stringCheck(String toCheck){
        // convert to char array
        char[] chars = toCheck.toCharArray();

        // assume we have a string
        boolean flag = true;

        // check if character is alphanumeric
        for(char c: chars){
            if(!Character.isLetterOrDigit(c)){
                // flag it then leave
                flag = false;
                break;
            }
        }
        return flag;
    }

    // updates tableview with current data
    public void updateItemView(){
        inventoryView.setItems(inventoryData);
    }
}
