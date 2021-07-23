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


import java.math.BigDecimal;
import java.net.URL;
import java.util.Locale;
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
    TableColumn<Item, String> valueColumn;
    @FXML
    TableColumn<Item, String> nameColumn;
    @FXML
    TableColumn<Item, String> serialColumn;


    // buttons
    @FXML
    SplitMenuButton addButton;
    @FXML
    MenuItem delButton;

    @FXML
    MenuItem openTSV;
    @FXML
    MenuItem openJSON;
    @FXML
    MenuItem openHTML;

    @FXML
    MenuItem saveTSV;
    @FXML
    MenuItem saveJSON;
    @FXML
    MenuItem saveHTML;

    @FXML
    MenuItem showHelp;

    // text
    @FXML
    TextField searchField;
    @FXML
    TextField valueText;
    @FXML
    TextField serialNumberText;
    @FXML
    TextField nameText;


    // data
    private ObservableList<Item> inventoryData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb){

        // set up table and columns for editing and selection
        inventoryView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        inventoryView.setEditable(true);

        valueColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        serialColumn.setCellFactory(TextFieldTableCell.forTableColumn());


        // set up columns
        valueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        serialColumn.setCellValueFactory(new PropertyValueFactory<>("serialNumber"));


        // set data
        inventoryView.setItems(inventoryData);

        // resizable
        inventoryView.getColumns().get(0).prefWidthProperty().bind(inventoryView.widthProperty().multiply(0.20));
        inventoryView.getColumns().get(1).prefWidthProperty().bind(inventoryView.widthProperty().multiply(0.40));
        inventoryView.getColumns().get(2).prefWidthProperty().bind(inventoryView.widthProperty().multiply(0.40));

    }

    // file stuff

    // save to file
    public void saveTSV(ActionEvent actionEvent){

    }

    public void saveJSON(ActionEvent actionEvent){

    }

    public void saveHTML(ActionEvent actionEvent){

    }



    // load from file
    public void openTSV(ActionEvent actionEvent){

    }

    public void openJSON(ActionEvent actionEvent){

    }

    public void openHTML(ActionEvent actionEvent){

    }



    // insertion & modification

    // add & delete
    public void addItem(ActionEvent actionEvent){

        // check inputs first

        // the value text is invalid if there's no '.' or it contains something not a number
        // or if its empty
        if(!stringCheckForNumbers(valueText.getText()) || valueText.getText().length() == 0){

            // tell user to input valid dollar amount
            System.out.println("failed check for numbers\n");
            return;
        }

        // serial numbers are purely alphanumeric, so check this too
        if(!stringCheckForAlphanumeric(serialNumberText.getText()) || valueText.getText().length() == 0){

            // tell user to input valid serial number string
            System.out.println("failed check for alphanum\n");
            return;
        }

        // if we get here, we're good to go

        // get inputs
        BigDecimal value = new BigDecimal(valueText.getText());
        String name = nameText.getText();
        String serialNum = serialNumberText.getText().toUpperCase(Locale.ROOT);

        // create item
        Item item = new Item(value, name, serialNum);

        // add item
        inventoryData.add(item);

        // update gui
        updateItemView();
        System.out.println("added successfully\n");
    }

    public void delItem(ActionEvent actionEvent){
        // delete all highlighted tasks
        inventoryData.removeAll(
                inventoryView.getSelectionModel().getSelectedItems()
        );

        updateItemView();
    }



    // edit events

    public void changeValueCellEvent(TableColumn.CellEditEvent editEvent){
        // set edit event text for sanity
        String editText = editEvent.getNewValue().toString();

        // remove dollar sign from field (if able)
        editText = editText.replace("$", "");

        // get selected item
        Item item = inventoryView.getSelectionModel().getSelectedItem();

        // check the new input
        if(stringCheckForNumbers(editText)){
            // make big decimal for input
            BigDecimal input = new BigDecimal(editText);

            // update object
            item.setValue(input);
        }

        // update gui
        updateItemView();
    }

    public void changeNameCellEvent(TableColumn.CellEditEvent editEvent){
        // set edit event text for sanity
        String editText = editEvent.getNewValue().toString();

        // get selected item
        Item item = inventoryView.getSelectionModel().getSelectedItem();

        // check input for length
        if(editText.length() < 2 || editText.length() > 256){

            // tell user input length is not accepted
            return;

        }else{

            // now check input content
            if(!stringCheckForAlphanumeric(editText)){

                // tell user input content is not accepted
                return;

            }else{
                // update name field
                item.setName(editText);
            }
        }

        // update gui
        updateItemView();
    }

    public void changeSerialNumCellEvent(TableColumn.CellEditEvent editEvent){
        // set edit event text for sanity
        String editText = editEvent.getNewValue().toString();

        // get selected item
        Item item = inventoryView.getSelectionModel().getSelectedItem();

        // update serial number field; check if alphanumeric
        if(stringCheckForAlphanumeric(editText)){
            item.setSerialNumber(editText);

        }else{
            // tell user input is not accepted
        }

        updateItemView();
    }



    // input checking

    // checks a string if its purely alphanumeric;
    // returns true if a string; false if not
    public boolean stringCheckForAlphanumeric(String toCheck){

        // flag for checking
        boolean flag = false;

        // check if character is alphanumeric; lambda ftw
        flag = toCheck.chars().allMatch(Character::isLetterOrDigit);

        return flag;
    }

    // checks a string if its purely numerical or a ".";
    // returns true if a string; false if not
    public boolean stringCheckForNumbers(String toCheck){

        // flag for checking
        boolean flag = false;

        // check if character is numeric or is a '.'
        flag = toCheck.chars().allMatch(Character::isDigit);
        flag = toCheck.contains(".");

        return flag;
    }




    // updates tableview with current data
    public void updateItemView(){
        inventoryView.setItems(inventoryData);
    }
}
