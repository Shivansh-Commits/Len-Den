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

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {

        HashMap<String, Integer> areaAndTables = daoimpl.fetchAreaAndTables();
        int gridRowCounter = 0;

        for(Map.Entry<String, Integer> entry : areaAndTables.entrySet())
        {
            String area = entry.getKey();
            int tables = entry.getValue();

            //Adding Text Field
            TextField areaName = new TextField();
            areaName.setPrefSize(150,55);
            areaName.getStyleClass().add("area-name-textField");
            areaName.setText(area);
            areaName.setEditable(false);

            Button editButton = new Button();
            editButton.setPrefSize(150,55);
            editButton.setText("Edit");
            editButton.getStyleClass().add("saveEdit-button");
            editButton.setCursor(Cursor.HAND);
            editButton.setOnAction(event -> {
                areaName.setEditable(true);
                areaName.setStyle("-fx-border-color:black;");
            });

            HBox hBox = new HBox();
            hBox.setSpacing(10);
            hBox.getChildren().addAll(areaName,editButton);
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

            GridPane.setMargin(hBox1,new Insets(25));
            tableAndAreaGrid.add(hBox1,1,gridRowCounter);


            //Adding SAVE Buttons
            Button saveButton = new Button();
            saveButton.setPrefSize(300,100);
            saveButton.setText("Save");
            saveButton.getStyleClass().add("saveEdit-button");

            HBox hBox2 = new HBox();
            hBox2.setSpacing(10);
            hBox2.getChildren().addAll(saveButton);

            GridPane.setMargin(hBox2,new Insets(15));
            tableAndAreaGrid.add(hBox2,2,gridRowCounter);

            gridRowCounter++;
        }

        //Adding 'Add Area' Buttons
        Button addButton = new Button();
        addButton.setPrefSize(300,100);
        addButton.setText("Add Area");
        addButton.getStyleClass().add("saveEdit-button");
        addButton.setOnAction(e -> {

        });

        HBox hBox3 = new HBox();
        hBox3.setSpacing(10);
        hBox3.getChildren().addAll(addButton);

        GridPane.setMargin(hBox3,new Insets(15));
        tableAndAreaGrid.add(hBox3,1,gridRowCounter);

    }




}
