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
import javafx.stage.Stage;


import java.io.File;
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

    // for the file choosers in the file manager
    @FXML
    SplitPane splitPane;

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
    @FXML
    public void saveTSV(ActionEvent actionEvent){

        // set up data
        String dataToWrite = tsvManager.dataConverterTSV(inventoryData);

        // set up window
        Stage stage = tsvManager.windowSetup(splitPane, "save");

        // make file
        File fileSave = tsvManager.fileChooser.showSaveDialog(stage);

        // write data to file
        if(fileSave != null){
            tsvManager.saveInventory(fileSave, dataToWrite);
        }
    }

    @FXML
    public void saveJSON(ActionEvent actionEvent){
        String dataToWrite = jsonManager.dataConverterJSON(inventoryData);

        Stage stage = jsonManager.windowSetup(splitPane, "save");

        File fileToSave = jsonManager.fileChooser.showSaveDialog(stage);

        if(fileToSave != null){
            jsonManager.saveInventory(fileToSave, dataToWrite);
        }
    }

    @FXML
    public void saveHTML(ActionEvent actionEvent){
        String dataToWrite = htmlManager.dataConverterHTML(inventoryData);

        Stage stage = htmlManager.windowSetup(splitPane, "save");

        File fileSave = htmlManager.fileChooser.showSaveDialog(stage);

        if(fileSave != null){
            htmlManager.saveInventory(fileSave, dataToWrite);
        }
    }



    // load from file
    @FXML
    public void openTSV(ActionEvent actionEvent){
        // setup window
        Stage stage = tsvManager.windowSetup(splitPane, "load");

        File fileLoad = tsvManager.fileChooser.showOpenDialog(stage);

        // load file
        if(fileLoad != null){
            inventoryData.removeAll(inventoryData);
            tsvManager.loadTSV(fileLoad, inventoryData);
        }

        // update gui
        updateItemView();
    }

    @FXML
    public void openJSON(ActionEvent actionEvent){
        Stage stage = jsonManager.windowSetup(splitPane, "load");

        File fileToLoad = jsonManager.fileChooser.showOpenDialog(stage);

        if(fileToLoad != null){
            inventoryData.removeAll(inventoryData);
            jsonManager.loadJSON(fileToLoad, inventoryData);
        }

        updateItemView();
    }

    @FXML
    public void openHTML(ActionEvent actionEvent){
        Stage stage = htmlManager.windowSetup(splitPane, "load");

        File fileLoad = htmlManager.fileChooser.showOpenDialog(stage);

        if(fileLoad != null){
            inventoryData.removeAll(inventoryData);
            htmlManager.loadHTML(fileLoad, inventoryData);
        }

        updateItemView();
    }



    // insertion & modification

    // add & delete
    public void addItem(ActionEvent actionEvent){

        // check inputs first

        // the value text is invalid if there's no '.' or it contains something not a number
        // or if its empty
        if(!stringCheckNumbers(valueText.getText()) || valueText.getText().length() == 0){

            // tell user to input valid dollar amount
            System.out.println("failed check for numbers\n");
            return;
        }

        // serial numbers are purely alphanumeric, so check this too
        if(!stringCheckAlphanumeric(serialNumberText.getText()) || valueText.getText().length() == 0){

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
        if(stringCheckNumbers(editText)){
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
            if(!stringCheckAlphanumeric(editText)){

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
        if(stringCheckAlphanumeric(editText)){
            item.setSerialNumber(editText);

        }else{
            // tell user input is not accepted
        }

        updateItemView();
    }



    // input checking

    // checks a string if its purely alphanumeric;
    // returns true if alphanumeric; false if not
    public boolean stringCheckAlphanumeric(String toCheck){

        // flag for checking
        boolean flag = false;

        // check if character is alphanumeric; lambda ftw
        flag = toCheck.chars().allMatch(Character::isLetterOrDigit);

        return flag;
    }

    // checks a string if its purely numerical or a ".";
    // returns true if a string; false if not
    public boolean stringCheckNumbers(String toCheck){

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
