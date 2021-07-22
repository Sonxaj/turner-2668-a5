/*
 *  UCF COP3330 Summer 2021 Assignment 5 Solution
 *  Copyright 2021 Jonas Turner
 */

package ucf.assignments;

import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.cert.Extension;
import java.util.Scanner;

// class responsible for all file operations

public class InvFileManager {
    private FileChooser fileChooser;

    public InvFileManager(String type){
        this.fileChooser = new FileChooser();

        // setup file chooser based on required file extension

        // tsv
        if(type.equalsIgnoreCase("tsv")){
            FileChooser.ExtensionFilter extensionFilter =
            new FileChooser.ExtensionFilter("TSV files (*.tsv)", "*.tsv");

        // json
        }else if (type.equalsIgnoreCase("json")){
            FileChooser.ExtensionFilter extensionFilter =
            new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json");

        // html
        }else{
            FileChooser.ExtensionFilter extensionFilter =
            new FileChooser.ExtensionFilter("HTML files (*.html)", "*.html");

        }
    }

    // save operations
    public void saveInventoryAsTSV(File fileToSave){
        String fileText = "";

        // set up text to write to file as TSV; uses data from controller

        try{
            // write all the data to file
            FileWriter fileWriter = new FileWriter(fileToSave);
            PrintWriter printWriter = new PrintWriter(fileWriter);

            printWriter.write(fileText);

            // close writers
            printWriter.close();
            fileWriter.close();

        } catch (IOException e){
            e.printStackTrace();
        }

    }

    public void saveInventoryAsJSON(File fileToSave){
        String fileText = "";

        // set up text to write to file as JSON

        try{
            // write all the data to file
            FileWriter fileWriter = new FileWriter(fileToSave);
            PrintWriter printWriter = new PrintWriter(fileWriter);

            printWriter.write(fileText);

            // close writers
            printWriter.close();
            fileWriter.close();

        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void saveInventoryAsHTML(File fileToSave){
        String fileText = "";

        // set up text to write to file as HTML

        try{
            // write all the data to file
            FileWriter fileWriter = new FileWriter(fileToSave);
            PrintWriter printWriter = new PrintWriter(fileWriter);

            printWriter.write(fileText);

            // close writers
            printWriter.close();
            fileWriter.close();

        } catch (IOException e){
            e.printStackTrace();
        }
    }


    // load operations
    public void loadTSV(File fileToLoad){
        try{
            // assuming we got the file, start reading data
            Scanner reader = new Scanner(fileToLoad);

            // parse data

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void loadJSON(File fileToLoad){
        try{
            // assuming we got the file, start reading data
            Scanner reader = new Scanner(fileToLoad);

            // parse data

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void loadHTML(File fileToLoad){
        try{
            // assuming we got the file, start reading data
            Scanner reader = new Scanner(fileToLoad);

            // parse data

        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
