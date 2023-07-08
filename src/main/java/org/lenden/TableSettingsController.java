package org.lenden;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import org.lenden.dao.DaoImpl;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class TableSettingsController implements Initializable {

    @FXML
    GridPane tableAndAreaGrid;
    DaoImpl daoimpl = new DaoImpl();

    HashMap<String, Integer> areaAndTables;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {

        areaAndTables = daoimpl.fetchAreaAndTables();
        int gridRowCounter = 0;

        for(Map.Entry<String, Integer> entry : areaAndTables.entrySet())
        {
            String area = entry.getKey();
            int tables = entry.getValue();

            //Adding Text Field
            TextField areaName = new TextField();
            areaName.setPrefSize(200,55);
            areaName.getStyleClass().add("area-name-textField");
            areaName.setText(area);
            areaName.setEditable(false);

            HBox hBox = new HBox();
            hBox.setSpacing(10);
            hBox.getChildren().addAll(areaName);
            //hBox.setStyle("-fx-background-color:grey;"); just for testing aligment

            GridPane.setMargin(hBox,new Insets(20));
            tableAndAreaGrid.add(hBox,0,gridRowCounter);


            //Adding Tables label and plus-minus button
            Label numOfTablesLabel = new Label();
            numOfTablesLabel.getStyleClass().add("tables-label");
            numOfTablesLabel.setText(Integer.toString(tables)+" Tables");

            Button plusButton = new Button();
            plusButton.setText("＋");
            plusButton.getStyleClass().add("plus-button");
            plusButton.setPrefSize(45,40);
            plusButton.setCursor(Cursor.HAND);
            plusButton.setOnAction( event -> {
                int numOfTables = Integer.parseInt(numOfTablesLabel.getText().split(" ")[0]);

                int newNumOfTables = numOfTables + 1;
                if(newNumOfTables<0)
                    newNumOfTables=0;

                //Updating the Label
                numOfTablesLabel.setText(newNumOfTables+" Tables");

                //Updating in the areaAndTables HashMap
                areaAndTables.put(area,newNumOfTables);
            });

            Button minusButton = new Button();
            minusButton.setText("—");
            minusButton.getStyleClass().add("minus-button");
            minusButton.setPrefSize(45,40);
            minusButton.setCursor(Cursor.HAND);
            minusButton.setOnAction( event -> {
                int numOfTables = Integer.parseInt(numOfTablesLabel.getText().split(" ")[0]);

                int newNumOfTables = numOfTables - 1;
                if(newNumOfTables<0)
                    newNumOfTables=0;

                //Updating the Label
                numOfTablesLabel.setText(newNumOfTables+" Tables");

                //Updating in the areaAndTables HashMap
                areaAndTables.put(area,newNumOfTables);
            });

            HBox hBox1 = new HBox();
            hBox1.setSpacing(35);
            hBox1.getChildren().addAll(minusButton,numOfTablesLabel,plusButton);

            GridPane.setMargin(hBox1,new Insets(25,55,25,0));
            tableAndAreaGrid.add(hBox1,1,gridRowCounter);


            //Adding EDIT Buttons
            Button editButton = new Button();
            editButton.setPrefSize(300,100);
            editButton.setText("Edit");
            editButton.getStyleClass().add("saveEdit-button");
            editButton.setOnMouseClicked(event -> {
                areaName.setEditable(true);
                areaName.setDisable(false);
                //areaName.setStyle("-fx-border-color:black");
                areaName.requestFocus();

                editButton.setDisable(true);
                areaName.requestFocus();
            });

            HBox hBox2 = new HBox();
            hBox2.setSpacing(10);
            hBox2.getChildren().addAll(editButton);

            GridPane.setMargin(hBox2,new Insets(20,20,20,35));
            tableAndAreaGrid.add(hBox2,2,gridRowCounter);

            gridRowCounter++;
        }

        while(gridRowCounter<5) // Loop for displaying the remaning areas (not being used) fields of the form & Less than 5 means, not more than 5 areas can exist
        {
            //Adding Text Field
            TextField areaName = new TextField();
            areaName.setPrefSize(200,55);
            areaName.getStyleClass().add("area-name-textField");
            areaName.setPromptText("Area Name");
            areaName.setDisable(true);

            HBox hBox = new HBox();
            hBox.setSpacing(10);
            hBox.getChildren().addAll(areaName);
            //hBox.setStyle("-fx-background-color:grey;"); just for testing aligment

            GridPane.setMargin(hBox,new Insets(20));
            tableAndAreaGrid.add(hBox,0,gridRowCounter);


            //Adding Tables label and plus-minus button
            Label numOfTablesLabel = new Label();
            numOfTablesLabel.getStyleClass().add("tables-label");
            numOfTablesLabel.setText("0 Tables");
            numOfTablesLabel.setDisable(true);

            Button plusButton = new Button();
            plusButton.setText("＋");
            plusButton.getStyleClass().add("plus-button");
            plusButton.setPrefSize(45,40);
            plusButton.setCursor(Cursor.HAND);
            plusButton.setDisable(true);
            plusButton.setOnAction( event -> {
                int numOfTables = Integer.parseInt(numOfTablesLabel.getText().split(" ")[0]);

                int newNumOfTables = numOfTables + 1;
                if(newNumOfTables<0)
                    newNumOfTables=0;

                //Updating the Label
                numOfTablesLabel.setText(newNumOfTables+" Tables");

                //Updating in the areaAndTables HashMap
                areaAndTables.put(areaName.getText(), newNumOfTables);
            });

            Button minusButton = new Button();
            minusButton.setText("—");
            minusButton.getStyleClass().add("minus-button");
            minusButton.setPrefSize(45,40);
            minusButton.setCursor(Cursor.HAND);
            minusButton.setDisable(true);
            minusButton.setOnAction( event -> {
                int numOfTables = Integer.parseInt(numOfTablesLabel.getText().split(" ")[0]);

                int newNumOfTables = numOfTables - 1;
                if(newNumOfTables<0)
                    newNumOfTables=0;

                //Updating the Label
                numOfTablesLabel.setText(newNumOfTables+" Tables");

                //Updating in the areaAndTables HashMap
                areaAndTables.put(areaName.getText() ,newNumOfTables);
            });

            HBox hBox1 = new HBox();
            hBox1.setSpacing(35);
            hBox1.getChildren().addAll(minusButton,numOfTablesLabel,plusButton);

            GridPane.setMargin(hBox1,new Insets(25,55,25,0));
            tableAndAreaGrid.add(hBox1,1,gridRowCounter);


            //Adding EDIT Buttons
            Button editAddButton = new Button();
            editAddButton.setPrefSize(300,100);
            editAddButton.setText("Add Area");
            editAddButton.getStyleClass().add("saveEdit-button");
            editAddButton.setOnMouseClicked(event -> {
                areaName.setDisable(false);
                //areaName.setStyle("-fx-border-color:black");
                areaName.requestFocus();

                plusButton.setDisable(false);
                minusButton.setDisable(false);
                numOfTablesLabel.setDisable(false);

                editAddButton.setDisable(true);
            });

            HBox hBox2 = new HBox();
            hBox2.setSpacing(10);
            hBox2.getChildren().addAll(editAddButton);

            GridPane.setMargin(hBox2,new Insets(20,20,20,35));
            tableAndAreaGrid.add(hBox2,2,gridRowCounter);

            gridRowCounter++;
        }

        //Adding 'Add Area' Buttons
        Button saveButton = new Button();
        saveButton.setPrefSize(300,100);
        saveButton.setText("Save");
        saveButton.getStyleClass().add("saveEdit-button");


        HBox hBox3 = new HBox();
        hBox3.setSpacing(10);
        hBox3.getChildren().addAll(saveButton);

        GridPane.setMargin(hBox3,new Insets(15));
        tableAndAreaGrid.add(hBox3,1,gridRowCounter);

    }




}
