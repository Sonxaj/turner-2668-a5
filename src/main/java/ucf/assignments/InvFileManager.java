/*
 *  UCF COP3330 Summer 2021 Assignment 5 Solution
 *  Copyright 2021 Jonas Turner
 */

package ucf.assignments;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.*;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.SplitPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.math.BigDecimal;
import java.util.Scanner;
// class responsible for all file operations

public class InvFileManager {

    public FileChooser fileChooser;

    public InvFileManager(String type){
        this.fileChooser = new FileChooser();

        // setup file chooser based on required file extension

        // tsv
        if(type.equalsIgnoreCase("tsv")){
            FileChooser.ExtensionFilter extensionFilter =
            new FileChooser.ExtensionFilter("TSV files (*.tsv)", "*.tsv");
            this.fileChooser.getExtensionFilters().add(extensionFilter);

        // json
        }else if (type.equalsIgnoreCase("json")){
            FileChooser.ExtensionFilter extensionFilter =
            new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json");
            this.fileChooser.getExtensionFilters().add(extensionFilter);

        // html
        }else{
            FileChooser.ExtensionFilter extensionFilter =
            new FileChooser.ExtensionFilter("HTML files (*.html)", "*.html");
            this.fileChooser.getExtensionFilters().add(extensionFilter);
        }


    }


    // data converters; takes data from internal list and makes string for saving

    // TSV
    public String dataConverterTSV(ObservableList<Item> inventoryData){
        // create out
        String out = "";

        // loop thru data; print as TSV
        for(Item item: inventoryData){
            out +=  item.getValue() + "\t" +
                    item.getSerialNumber() + "\t" +
                    item.getName() + "\n";
        }
        return out;
    }

    // JSON
    public String dataConverterJSON(ObservableList<Item> inventoryData){

        // create out
        String out = "{\n" +
                "\"inventory\":";

        // object mapper
        ObjectMapper mapper = new ObjectMapper();

        try {
            // write inventory as json
            out += mapper.writerWithDefaultPrettyPrinter().writeValueAsString(inventoryData);

        } catch (IOException e) {
            e.printStackTrace();
        }

        // append closing brace
        out += "}";

        return out;
    }

    // HTML
    public String dataConverterHTML(ObservableList<Item> inventoryData){
        // create out
        String out = "";
        String data = "";


        // setup data for html
        for(Item item: inventoryData){
            data += "\t<tr>" + "\n" +
                    "\t\t<td>" + item.getValue() + "</td>\n" +
                    "\t\t<td>" + item.getSerialNumber() + "</td>\n" +
                    "\t\t<td>" + item.getName() + "</td>\n" +
                    "\t</tr>" + "\n";
        }

        // setup html; insert data into table data tag
        out +=  "<!DOCTYPE html>" + "\n" +
                "<html>" + "\n" +
                "<head>" + "\n" +
                    "\t<meta charset =\"utf-8\" />" + "\n" +
                    "\t<title>Inventory</title>\n" +
                    "\t<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">" + "\n" +
                "</head>" + "\n" +
                "<body>" + "\n\n" +
                    "<table>" + "\n" +

                        // first row header
                        "\t<tr>" + "\n" +
                            "\t\t<th>Value:</th>" + "\n" +
                            "\t\t<th>Serial Number:</th>" + "\n" +
                            "\t\t<th>Name:</th>" + "\n" +
                        "\t</tr>" + "\n" +

                        // data; line 16
                        data +

                    "</table>" + "\n";
        return out;
    }



    // save operations

    public void saveInventory(File fileToSave, String textToSave){
        try{
            // write all the data to file
            FileWriter fileWriter = new FileWriter(fileToSave);
            PrintWriter printWriter = new PrintWriter(fileWriter);

            printWriter.write(textToSave);

            // close writers
            printWriter.close();
            fileWriter.close();

        } catch (IOException e){
            e.printStackTrace();
        }
    }


    // load operations

    // TSV
    public void loadTSV(File fileToLoad, ObservableList<Item> inventoryData){
        try{
            // assuming we got the file, start reading data
            Scanner reader = new Scanner(fileToLoad);

            // parse data
            while (reader.hasNextLine()){

                // split into string array
                String[] currentLine = reader.nextLine().split("\t");

                // create item using data
                String decimal = currentLine[0].replace("$", "");
                Item item = new Item(new BigDecimal(decimal), currentLine[1], currentLine[2]);

                // add to list
                inventoryData.add(item);
            }

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    // JSON
    public void loadJSON(File fileToLoad, ObservableList<Item> inventoryData) {
        try{
            // json object
            JsonObject inputJson = JsonParser.parseReader(new FileReader(fileToLoad)).getAsJsonObject();

            // setup as json array
            JsonArray jsonArray = inputJson.get("inventory").getAsJsonArray();

            for(JsonElement itemJson: jsonArray){
                // get current item as object
                JsonObject itemObj = itemJson.getAsJsonObject();

                // create item using parsed data
                String valueString = itemObj.get("value").getAsString().replace("$", "");
                BigDecimal value = new BigDecimal(valueString);
                String name = itemObj.get("name").getAsString();
                String serialNumber = itemObj.get("serialNumber").getAsString();

                // create item
                Item item = new Item(value, serialNumber, name);

                // add item to data
                inventoryData.add(item);
            }

        } catch (IOException e) {
            e.printStackTrace();

        }
    }


    // HTML
    public void loadHTML(File fileToLoad, ObservableList<Item> inventoryData){
        // line number for determining start point
        int line = 1;

        // string for storing data
        String data = "";

        // flags for starting and clearing cache
        boolean isItemAdded = false;
        boolean start = false;

        try{
            // assuming we got the file, start reading data
            Scanner reader = new Scanner(fileToLoad);

            // clear current data
            inventoryData.removeAll(inventoryData);
            System.out.println("inventory cleared\n");

            // parse data
            while(reader.hasNextLine()){

                // store current line
                String currentLine = reader.nextLine();

                // flag to avoid very first header tags
                if(line == 16){
                    start = true;
                }

                // check if current line will contain data
                if(currentLine.contains("<td>") && start){

                    // take current line's data; # as delimiter
                    data += currentLine + "#";

                }

                // now to create object with data
                if(currentLine.contains("</tr>") && start){

                    // clean out the tabs, tags, and dollar sign so only what's needed remains
                    data = data.replace("\t\t", "");
                    data = data.replace("<td>", "");
                    data = data.replace("</td>", "");
                    data = data.replace("$", "");

                    // split using delimiter
                    String[] chunk = data.split("#");

                    // create item using parsed data
                    String valueString = chunk[0];
                    BigDecimal value = new BigDecimal(valueString);

                    String serialNumber = chunk[1];
                    String name = chunk[2];

                    Item item = new Item(value, serialNumber, name);

                    // add to list
                    inventoryData.add(item);
                    isItemAdded = true;
                }

                // clear current line cache if operation was completed
                if (isItemAdded && start){
                    data = "";
                    isItemAdded = false;
                }

                // increment line count
                line++;
            }

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    // window setup
    public Stage windowSetup(SplitPane splitPane, String operation){
        String text;
        if(operation.equalsIgnoreCase("save")){
            text = "Save Inventory";
        }else{
            text = "Load Inventory";
        }

        this.fileChooser.setTitle(text);
        return (Stage)splitPane.getScene().getWindow();
    }
}
