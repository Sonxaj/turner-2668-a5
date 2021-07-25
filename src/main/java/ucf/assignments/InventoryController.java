/*
 *  UCF COP3330 Summer 2021 Assignment 5 Solution
 *  Copyright 2021 Jonas Turner
 */

package ucf.assignments;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
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
import java.util.stream.Collectors;


public class InventoryController implements Initializable {

    // file managers for file operations
    public InvFileManager tsvManager = new InvFileManager("tsv");
    public InvFileManager jsonManager = new InvFileManager("json");
    public InvFileManager htmlManager = new InvFileManager("html");

    // dialog manager for dialog popups
    public DialogManager dialogManager = new DialogManager();


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
        searchField.textProperty().addListener((observable, oldValue, newValue) ->
                inventoryView.setItems(filterList(inventoryData, newValue.toLowerCase())));

    }



    // search bar

    // finds a serial number or name
    private boolean searchFindsItem(Item item, String searchText){

        return item.getSerialNumber().toLowerCase().contains(searchText.toLowerCase()) ||
                item.getName().toLowerCase().contains(searchText.toLowerCase());
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
    public void showHelp(){
        dialogManager.showDialog(dialogManager.helpText(), splitPane);
    }



    // file stuff

    // save to file
    @FXML
    public void saveTSV(){

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
    public void saveJSON(){
        String dataToWrite = jsonManager.dataConverterJSON(inventoryData);

        Stage stage = jsonManager.windowSetup(splitPane, "save");

        File fileToSave = jsonManager.fileChooser.showSaveDialog(stage);

        if(fileToSave != null){
            jsonManager.saveInventory(fileToSave, dataToWrite);
        }
    }

    @FXML
    public void saveHTML(){
        String dataToWrite = htmlManager.dataConverterHTML(inventoryData);

        Stage stage = htmlManager.windowSetup(splitPane, "save");

        File fileSave = htmlManager.fileChooser.showSaveDialog(stage);

        if(fileSave != null){
            htmlManager.saveInventory(fileSave, dataToWrite);
        }
    }

    // load from file

    @FXML
    public void openTSV(){
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
    public void openJSON(){
        Stage stage = jsonManager.windowSetup(splitPane, "load");

        File fileToLoad = jsonManager.fileChooser.showOpenDialog(stage);

        if(fileToLoad != null){
            inventoryData.removeAll(inventoryData);
            jsonManager.loadJSON(fileToLoad, inventoryData);
        }

        updateItemView();
    }

    @FXML
    public void openHTML(){
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
    public void addItem(){

        // check inputs first

        // value text

        // the value text is invalid if it contains something not a number or if its empty
        if(!stringIsNumbers(valueText.getText()) || valueText.getText().length() == 0){

            // tell user to input valid dollar amount
            dialogManager.showDialog(dialogManager.invalidDollarAmount(), splitPane);
            return;
        }


        // serial number

        // serial numbers are purely alphanumeric, so check this too
        if(stringIsNotAlphanumeric(serialNumberText.getText())){

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

        if(decimal.contains(".")){
            // make big decimal for input; round to two places if able

            // split by the first "."
            String[] temp = decimal.split("\\.");

            // truncate cents to two places
            String cents = temp[1];
            cents = cents.substring(0, Math.min(cents.length(), 2));

            // put it back
            decimal = temp[0] + '.' + cents;

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

    public void delItem(){
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
        if(stringIsNumbers(editText)){

            // remove duplicate decimals if possible
            editText = removeDuplicatePeriods(editText);


            // append decimal if none
            if(!editText.contains(".")){
                // append double zero if no decimal
                editText += ".00";
            }

            // make big decimal for input; round to two places if able

            // split by the first "."
            String[] temp = editText.split("\\.");

            // truncate cents to two places
            String cents = temp[1];
            cents = cents.substring(0, Math.min(cents.length(), 2));

            // put it back
            editText = temp[0] + '.' + cents;

            // make big decimal
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
        if(stringIsNotAlphanumeric(editText)){

            // tell user input content is not accepted
            dialogManager.showDialog(dialogManager.invalidSerialString(), splitPane);

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
    // returns false if alphanumeric; true if not
    public boolean stringIsNotAlphanumeric(String toCheck){

        // flag for checking
        boolean flag;

        // check if character is alphanumeric; lambda ftw
        flag = toCheck.chars().allMatch(Character::isLetterOrDigit);

        return !flag;
    }

    // checks a string if its purely numerical or a ".";
    // returns true if a string; false if not
    public boolean stringIsNumbers(String toCheck){

        // flag for checking
        boolean flag;

        // check if input is a valid dollar string

        // if they input a "."
        if(toCheck.contains(".")){

            // split by the first "."
            String[] temp = toCheck.split("\\.");
            String dollars = temp[0];
            String cents = temp[1];

            // check each half
            flag = dollars.chars().allMatch(Character::isDigit) &&
                    cents.chars().allMatch(Character::isDigit);

        }else{
            // if no ".", check whole input
            flag = toCheck.chars().allMatch(Character::isDigit);
        }

        return flag;
    }

    // a theoretically  pretty fast search for really big inventory sizes
    public boolean duplicateCheck(String toCheck, boolean isName){

        // create hash set
        HashSet<String> invSet = new HashSet<>();

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
        return invSet.contains(toCheck);
    }

    public String removeDuplicatePeriods(String editText){
        // check input for multiple decimals
        if(editText.contains(".")){

            // make string into list
            List<Character> textAsList = editText
                    .chars()
                    .mapToObj(e -> (char)e)
                    .collect(Collectors.toList());

            // index for first period
            int first = 0;

            // list of indexes to remove
            List<Integer> indexes = new ArrayList<>();


            // search the input for the first '.'
            for (int i=0; i<textAsList.size(); i++){

                if(textAsList.get(i).equals('.')){
                    first = i;
                    break;
                }
            }

            // add the rest to the removal list
            for(int j=first+1; j<textAsList.size(); j++){

                if(textAsList.get(j).equals('.')){
                    indexes.add(j);
                }
            }

            // now set targets for removal
            for (Integer i:indexes) {
                textAsList.set(i, 'X');
            }
            textAsList.removeAll(Collections.singleton('X'));

            // update input
            editText = textAsList.toString()
                    .replace("[", "")
                    .replace("]", "")
                    .replace(", ", "");
        }
        return editText;
    }


    // updates tableview with current data
    public void updateItemView(){
        inventoryView.setItems(inventoryData);
    }
}
