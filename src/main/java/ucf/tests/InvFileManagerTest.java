package ucf.tests;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.*;

import ucf.assignments.InvFileManager;
import ucf.assignments.Item;

import java.io.*;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class InvFileManagerTest {

    // data
    ObservableList<Item> inventoryData = FXCollections.observableArrayList();

    // managers; only one since the type specifies the file extension for file chooser
    InvFileManager testManager = new InvFileManager("tsv");



    // setup and clear each run
    @BeforeEach
    void setupData(){
        inventoryData.addAll(
                new Item(new BigDecimal("23.45"), "ABC123DE45", "Test Item 1"),
                new Item(new BigDecimal("567.9"), "W1MWG1X3JB", "Test Item 2"),
                new Item(new BigDecimal("14.00"), "467KSHKWV2", "Test Item 3")
        );
    }

    @AfterEach
    void clearData(){
        inventoryData.removeAll(inventoryData);
    }



    @Test
    void dataConverterTSV_converts_data_to_TSV() {

        // create string using converter
        String actual = testManager.dataConverterTSV(inventoryData);

        String expected =
                "$23.45\t" +
                "ABC123DE45\t" +
                "Test Item 1\n" +

                "$567.9\t" +
                "W1MWG1X3JB\t" +
                "Test Item 2\n" +

                "$14.00\t" +
                "467KSHKWV2\t" +
                "Test Item 3\n";

        assertEquals(expected, actual);
    }

    @Test
    void dataConverterHTML_converts_data_to_valid_HTML() {
        // create string using converter
        String actual = testManager.dataConverterHTML(inventoryData);

        String expected =
                """
                <!DOCTYPE html>
                <html>
                <head>
                \t<meta charset ="utf-8" />
                \t<title>Inventory</title>
                \t<meta name="viewport" content="width=device-width, initial-scale=1">
                </head>
                <body>
                
                <table>
                \t<tr>
                \t\t<th>Value:</th>
                \t\t<th>Serial Number:</th>
                \t\t<th>Name:</th>
                \t</tr>
                \t<tr>
                \t\t<td>$23.45</td>
                \t\t<td>ABC123DE45</td>
                \t\t<td>Test Item 1</td>
                \t</tr>
                \t<tr>
                \t\t<td>$567.9</td>
                \t\t<td>W1MWG1X3JB</td>
                \t\t<td>Test Item 2</td>
                \t</tr>
                \t<tr>
                \t\t<td>$14.00</td>
                \t\t<td>467KSHKWV2</td>
                \t\t<td>Test Item 3</td>
                \t</tr>
                </table>
                """;

        assertEquals(expected, actual);
    }


    @Test
    void loadTSV_reads_file_and_inserts_data_into_list() {
        // load file
        File file = new File("files/input/test.tsv");

        // fetch the data
        testManager.loadTSV(file, inventoryData);

        // compare search for specific element in specific location
        var flag = inventoryData.get(4).getName().equalsIgnoreCase("Thing 2");
        assertEquals(true, flag);
    }

    @Test
    void loadJSON_reads_file_and_inserts_data_into_list() {
        // load file
        File file = new File("files/input/test2.json");

        // fetch the data
        testManager.loadJSON(file, inventoryData);

        // compare search for specific element in specific location
        var flag = inventoryData.get(5).getSerialNumber().equalsIgnoreCase("JOYN360246");
        assertEquals(true, flag);
    }

    @Test
    void loadHTML_reads_file_and_inserts_data_into_list() {
        // load file
        File file = new File("files/input/test3.html");

        // fetch the data
        testManager.loadHTML(file, inventoryData);

        // compare search for specific element in specific location
        var flag = inventoryData.get(3).getValue().equalsIgnoreCase("$12.00");
        assertEquals(true, flag);
    }
}