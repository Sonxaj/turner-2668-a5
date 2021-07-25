/*
 *  UCF COP3330 Summer 2021 Assignment 5 Solution
 *  Copyright 2021 Jonas Turner
 */

package ucf.assignments;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
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
import java.util.*;


public class InventoryController implements Initializable {

    // file managers for file operations
    private InvFileManager tsvManager = new InvFileManager("tsv");
    private InvFileManager jsonManager = new InvFileManager("json");
    private InvFileManager htmlManager = new InvFileManager("html");

    // dialog manager for dialog popups
    private DialogManager dialogManager = new DialogManager();


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
        //inventoryView.setItems(inventoryData);

        // resizable
        inventoryView.getColumns().get(0).prefWidthProperty().bind(inventoryView.widthProperty().multiply(0.20));
        inventoryView.getColumns().get(1).prefWidthProperty().bind(inventoryView.widthProperty().multiply(0.40));
        inventoryView.getColumns().get(2).prefWidthProperty().bind(inventoryView.widthProperty().multiply(0.40));

        // create filtered list
        FilteredList<Item> filteredInvList = new FilteredList<>(inventoryData);
        inventoryView.setItems(filteredInvList);

        // set up search bar
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            inventoryView.setItems(filterList(inventoryData, newValue.toLowerCase()));
        });


    }



    // search bar

    // finds a serial number or name
    private boolean searchFindsItem(Item item, String searchText){

        boolean out =
                item.getSerialNumber().toLowerCase().contains(searchText.toLowerCase()) ||
                        item.getName().toLowerCase().contains(searchText.toLowerCase());
        return out;
    }

    private ObservableList<Item> filterList (ObservableList<Item> inventoryData, String search){
        List<Item> out = new ArrayList<>();

        for(Item item: inventoryData){
            if(searchFindsItem(item, search)){
                out.add(item);
            }
        }
        return FXCollections.observableArrayList(out);
    }



    // show help
    @FXML
    public void showHelp(ActionEvent actionEvent){
        dialogManager.showDialog(dialogManager.helpText(), splitPane);
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

        // value text

        // the value text is invalid if it contains something not a number or if its empty
        if(!stringCheckNumbers(valueText.getText()) || valueText.getText().length() == 0){

            // tell user to input valid dollar amount
            dialogManager.showDialog(dialogManager.invalidDollarAmount(), splitPane);
            return;
        }


        // serial number

        // serial numbers are purely alphanumeric, so check this too
        if(!stringCheckAlphanumeric(serialNumberText.getText())){

            // tell user to input valid serial number content
            dialogManager.showDialog(dialogManager.invalidSerialString(), splitPane);
            return;

        }else if (serialNumberText.getText().length() != 10){

            // tell user to input valid length
            dialogManager.showDialog(dialogManager.invalidSerialStringLength(), splitPane);
            return;
        }



        // name

        // length check for name
        if(nameText.getText().length() < 2 || nameText.getText().length() > 265){

            // tell user to input string of valid length
            dialogManager.showDialog(dialogManager.invalidNameStringLength(), splitPane);
        }

        // input

        // get inputs
        String decimal = valueText.getText();

        // check for a decimal
        if(!decimal.contains(".")){
            // append double zero if no decimal
            decimal += ".00";
        }
        BigDecimal value = new BigDecimal(decimal);

        // setup name
        String name = nameText.getText();

        // serial number
        String serialNum = serialNumberText.getText().toUpperCase();

        // create item
        Item item = new Item(value, serialNum, name);

        // add item
        inventoryData.add(item);

        // update gui
        updateItemView();
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

            // check input for a decimal
            if(!editText.contains(".")){
                // append double zero if no decimal
                editText += ".00";
            }

            // make big decimal for input
            BigDecimal input = new BigDecimal(editText);

            // update object
            item.setValue(input);

        }else{
            // tell user input is not a valid number
            dialogManager.showDialog(dialogManager.invalidDollarAmount(), splitPane);
            return;
        }

        // update gui
        updateItemView();
    }

    public void changeNameCellEvent(TableColumn.CellEditEvent editEvent){
        // set edit event text for sanity
        String editText = editEvent.getNewValue().toString();

        // get selected item
        Item item = inventoryView.getSelectionModel().getSelectedItem();

        // first check for duplicates
        if(duplicateCheck(editText, true)){

            // scream at user
            dialogManager.showDialog(dialogManager.duplicateName(), splitPane);
            return;
        }

        // check input for length
        if(editText.length() < 2 || editText.length() > 256){

            // tell user input length is not accepted
            dialogManager.showDialog(dialogManager.invalidNameStringLength(), splitPane);
            return;

        }else{

            // update name field
            item.setName(editText);

        }
        updateItemView();
    }

    public void changeSerialNumCellEvent(TableColumn.CellEditEvent editEvent){
        // set edit event text for sanity
        String editText = editEvent.getNewValue().toString();

        // get selected item
        Item item = inventoryView.getSelectionModel().getSelectedItem();

        // first check for duplicates
        if(duplicateCheck(editText, false)){

            // tell user duplicate not acceptable
            dialogManager.showDialog(dialogManager.duplicateSerialNum(), splitPane);
            return;
        }

        // now check length and if alphanumeric; update serial number field if true
        if(!stringCheckAlphanumeric(editText)){

            // tell user input content is not accepted
            dialogManager.showDialog(dialogManager.invalidSerialString(), splitPane);
            return;

        }else{

            if(editText.length() != 10){
                // scream for bad length
                dialogManager.showDialog(dialogManager.invalidSerialStringLength(), splitPane);
                return;

            }else{
                item.setSerialNumber(editText);
            }
            updateItemView();
        }
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

        return flag;
    }

    public boolean duplicateCheck(String toCheck, boolean isName){

        /*
            theoretically a pretty fast search for really big inventory sizes
         */

        // create hash set
        HashSet<String> invSet = new HashSet();

        // add data based on requirement
        if(isName){

            // put names in map if we're searching for names
            for(Item i: inventoryData){
                invSet.add(i.getName());
            }

        }else{

            // put serial numbers otherwise
            for(Item i: inventoryData){
                invSet.add(i.getSerialNumber());
            }
        }

        // evaluate search
        boolean out = invSet.contains(toCheck);

        return out;
    }


    // updates tableview with current data
    public void updateItemView(){
        inventoryView.setItems(inventoryData);
    }
}
