/*
 *  UCF COP3330 Summer 2021 Assignment 5 Solution
 *  Copyright 2021 Jonas Turner
 */

package ucf.assignments;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;


public class DialogManager {

    public void showDialog(String out, SplitPane splitPane){
        // text
        Label helpLabel = new Label(out);

        StackPane secondaryLayout = new StackPane();
        secondaryLayout.getChildren().add(helpLabel);

        Scene secondScene = new Scene(secondaryLayout, 600, 500);

        // the new window
        Stage newWindow = new Stage();
        newWindow.setTitle("Help");
        newWindow.setScene(secondScene);

        // Set position of second window, related to primary window.
        newWindow.setX(splitPane.getScene().getWindow().getX() + 200);
        newWindow.setY(splitPane.getScene().getWindow().getY() + 100);

        newWindow.show();
    }


    // show user help text


    // tell user to input valid dollar amount
    public String invalidDollarAmount(){
        return "";
    }

    // tell user to input valid serial number string

    // tell user input length is not accepted

    // tell user about duplicate serial number

    //
}
