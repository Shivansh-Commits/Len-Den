package org.lenden;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.TilePane;
import org.lenden.dao.DaoImpl;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class TableSettingsController implements Initializable {

    @FXML
    TilePane tableAndAreaTilePane;
    @FXML
    Label totalAreasLabel;
    @FXML
    Label totalTablesLabel;

    DaoImpl daoimpl = new DaoImpl();

    HashMap<String, Integer> areaAndTables;

    int maxTablesAllowedInOneArea = 36;
    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        try
        {
            areaAndTables = daoimpl.fetchAreaAndTables();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Database operation Exception - "+e.getMessage(), ButtonType.OK);
            alert.setHeaderText("Failed");
            alert.setTitle("Error!");
            alert.showAndWait();
        }

        totalAreasLabel.setText(    String.valueOf(areaAndTables.size())    );

        tableAndAreaTilePane.setPadding(new Insets(15,0,0,0));

        int totalTables = 0;
        int Areas=0;

        for(Map.Entry<String, Integer> entry : areaAndTables.entrySet())
        {
            String area = entry.getKey();
            int tables = entry.getValue();
            totalTables +=tables;

            HBox mainHbox = new HBox();
            mainHbox.setPadding(new Insets(5,10,5,10));
            mainHbox.setStyle("-fx-background-color:white; -fx-border-color:black; -fx-border-width:0.5px; -fx-border-radius:5px");


            //Adding Text Field
            Label areaName = new Label();
            areaName.setPrefSize(200,55);
            areaName.setAlignment(Pos.CENTER);
            areaName.getStyleClass().add("area-name-label");
            areaName.setText(area);


            HBox hBox = new HBox();
            hBox.setAlignment(Pos.CENTER);
            hBox.getChildren().addAll(areaName);
            //hBox.setStyle("-fx-border-color:grey;"); //just for testing alignment

            mainHbox.getChildren().add(hBox);


            //Adding Tables label and plus-minus button
            Label numOfTablesLabel = new Label();
            numOfTablesLabel.getStyleClass().add("tables-label");
            numOfTablesLabel.setText(tables+" Tables");

            Button plusButton = new Button();
            plusButton.setText("＋");
            plusButton.getStyleClass().add("plus-table-button");
            plusButton.setPrefSize(Region.USE_COMPUTED_SIZE,30);
            plusButton.setCursor(Cursor.HAND);
            plusButton.setOnAction( event -> {

                totalTablesLabel.setText(   String.valueOf(Integer.parseInt(totalTablesLabel.getText())+1)  );

                int numOfTables = Integer.parseInt(numOfTablesLabel.getText().split(" ")[0]);

                int newNumOfTables = numOfTables + 1;

                if(newNumOfTables>maxTablesAllowedInOneArea) // Checking if table num is >= maxTablesAllowedInOneArea
                {
                    Alert saveAlert = new Alert(Alert.AlertType.WARNING, "Tables Limit per Area Exceeded!", ButtonType.OK);
                    saveAlert.setHeaderText("Max Tables per Area is "+maxTablesAllowedInOneArea);
                    saveAlert.setTitle("Warning!");
                    saveAlert.showAndWait();

                    newNumOfTables = maxTablesAllowedInOneArea;
                }

                //Updating the Label
                numOfTablesLabel.setText(newNumOfTables+" Tables");

                //Updating in the areaAndTables HashMap
                areaAndTables.put(area,newNumOfTables);
            });

            Button minusButton = new Button();
            minusButton.setText("—");
            minusButton.getStyleClass().add("minus-table-button");
            minusButton.setPrefSize(Region.USE_COMPUTED_SIZE,35);
            minusButton.setCursor(Cursor.HAND);
            minusButton.setOnAction( event -> {

                totalTablesLabel.setText(   String.valueOf(Integer.parseInt(totalTablesLabel.getText())-1)  );

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
            //hBox1.setStyle("fx-border-color:black");

            hBox1.setAlignment(Pos.CENTER);
            hBox1.getChildren().addAll(minusButton,numOfTablesLabel,plusButton);

            mainHbox.getChildren().add(hBox1);


            Button deleteButton = new Button();
            deleteButton.setPrefSize(200,40);
            deleteButton.setText("Delete Area");
            deleteButton.getStyleClass().add("saveEdit-button");
            deleteButton.setOnMouseClicked(event -> {

                try
                {
                    if(daoimpl.fetchOpenAndReservedTableCount()>0)
                    {
                        Alert deleteAlert = new Alert(Alert.AlertType.WARNING, "Open Tables Found!", ButtonType.OK);
                        deleteAlert.setHeaderText("Settle(close) the Open tables , Un-Reserve Tables and try again.Tables and Areas can be edited only when all tables are closed. ");
                        deleteAlert.setTitle("Alert!");
                        deleteAlert.showAndWait();
                        return;
                    }
                }
                catch (SQLException e)
                {
                    throw new RuntimeException(e);
                }


                Alert deleteAlert = new Alert(Alert.AlertType.CONFIRMATION, "Are you Sure ?", ButtonType.YES , ButtonType.NO);
                deleteAlert.setHeaderText("Area will be deleted from the Billing Page");
                deleteAlert.setTitle("Alert!");
                deleteAlert.showAndWait();

                if(deleteAlert.getResult() == ButtonType.YES)
                {
                    try
                    {
                        if(daoimpl.deleteArea(areaName.getText()))
                            {
                            //Updating Total Tables Label
                            totalTablesLabel.setText(   String.valueOf( Integer.parseInt(totalTablesLabel.getText()) - Integer.parseInt(numOfTablesLabel.getText().split(" ")[0]) )  );

                            //Updating Total Areas Label
                            totalAreasLabel.setText(   String.valueOf( Integer.parseInt(totalAreasLabel.getText()) - 1 )  );

                            areaName.setDisable(true);
                            plusButton.setDisable(true);
                            minusButton.setDisable(true);
                            numOfTablesLabel.setText("0 Tables");
                            numOfTablesLabel.setDisable(true);

                            deleteButton.setDisable(true);

                            areaAndTables.remove(areaName.getText());
                            }
                    }
                    catch (SQLException ex)
                    {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Database delete operation Exception - "+ex.getMessage(), ButtonType.OK);
                        alert.setHeaderText("Failed");
                        alert.setTitle("Error!");
                        alert.showAndWait();
                    }
                }
            });

            HBox hBox2 = new HBox();
            hBox2.setSpacing(10);
            hBox2.setAlignment(Pos.CENTER);
            hBox2.setPadding(new Insets(5,5,5,15));
            hBox2.getChildren().addAll(deleteButton);

            mainHbox.getChildren().add(hBox2);
            tableAndAreaTilePane.getChildren().add(mainHbox);

            Areas++;

        }

        totalTablesLabel.setText(String.valueOf(totalTables));

        while(Areas<5) // Loop for displaying the remaning areas (not being used) fields of the form & Less than 5 means, not more than 5 areas can exist
        {
            //Adding Text Field
            TextField areaName = new TextField();
            areaName.setPrefSize(200,40);
            areaName.getStyleClass().add("area-name-textField");
            areaName.setPromptText("Area Name");
            areaName.setDisable(true);
            areaName.setOnAction(event -> areaAndTables.put(areaName.getText(),0));

            HBox mainHbox = new HBox();
            mainHbox.setPadding(new Insets(5,10,5,10));
            mainHbox.setStyle("-fx-background-color:white; -fx-border-color:black; -fx-border-width:0.5px; -fx-border-radius:5px");

            HBox hBox = new HBox();
            hBox.setSpacing(10);
            hBox.setAlignment(Pos.CENTER);
            hBox.setPadding(new Insets(0,10,0,0));
            hBox.getChildren().addAll(areaName);
            //hBox.setStyle("-fx-background-color:grey;"); just for testing alignment

            mainHbox.getChildren().add(hBox);


            //Adding Tables label and plus-minus button
            Label numOfTablesLabel = new Label();
            numOfTablesLabel.getStyleClass().add("tables-label");
            numOfTablesLabel.setText("0 Tables");
            numOfTablesLabel.setDisable(true);

            Button plusButton = new Button();
            plusButton.setText("＋");
            plusButton.getStyleClass().add("plus-table-button");
            plusButton.setPrefSize(Region.USE_COMPUTED_SIZE,30);
            plusButton.setCursor(Cursor.HAND);
            plusButton.setDisable(true);
            plusButton.setOnAction( event -> {
                totalTablesLabel.setText(   String.valueOf(Integer.parseInt(totalTablesLabel.getText())+1)  );

                int numOfTables = Integer.parseInt(numOfTablesLabel.getText().split(" ")[0]);

                int newNumOfTables = numOfTables + 1;

                if(newNumOfTables>maxTablesAllowedInOneArea) // Checking if table num is >= maxTablesAllowedInOneArea
                {
                    Alert saveAlert = new Alert(Alert.AlertType.WARNING, "Tables Limit per Area Exceeded!", ButtonType.OK);
                    saveAlert.setHeaderText("Max Tables per Area is "+maxTablesAllowedInOneArea);
                    saveAlert.setTitle("Warning!");
                    saveAlert.showAndWait();

                    newNumOfTables = maxTablesAllowedInOneArea;
                }

                //Updating the Label
                numOfTablesLabel.setText(newNumOfTables+" Tables");

                //Updating in the areaAndTables HashMap
                areaAndTables.put(areaName.getText(), newNumOfTables);

            });

            Button minusButton = new Button();
            minusButton.setText("—");
            minusButton.getStyleClass().add("minus-table-button");
            minusButton.setPrefSize(Region.USE_COMPUTED_SIZE,35);
            minusButton.setCursor(Cursor.HAND);
            minusButton.setDisable(true);
            minusButton.setOnAction( event -> {
                totalTablesLabel.setText(   String.valueOf(Integer.parseInt(totalTablesLabel.getText())-1)  );

                int numOfTables = Integer.parseInt(numOfTablesLabel.getText().split(" ")[0]);

                int newNumOfTables = numOfTables - 1;

                if(newNumOfTables<0) // Checking if table num if > 0
                    newNumOfTables=0;

                //Updating the Label
                numOfTablesLabel.setText(newNumOfTables+" Tables");

                //Updating in the areaAndTables HashMap
                areaAndTables.put(areaName.getText() ,newNumOfTables);
            });

            HBox hBox1 = new HBox();
            hBox1.setSpacing(35);
            hBox1.setAlignment(Pos.CENTER);
            hBox1.getChildren().addAll(minusButton,numOfTablesLabel,plusButton);

            mainHbox.getChildren().add(hBox1);


            //Adding 'Add Area' Buttons
            Button addAreaButton = new Button();
            addAreaButton.setPrefSize(200,40);
            addAreaButton.setText("Add Area");
            addAreaButton.getStyleClass().add("saveEdit-button");
            addAreaButton.setOnMouseClicked(event -> {
                //Updating Total Areas Label
                totalAreasLabel.setText(   String.valueOf( Integer.parseInt(totalAreasLabel.getText()) + 1 )  );

                areaName.setDisable(false);
                //areaName.setStyle("-fx-border-color:black");
                areaName.requestFocus();

                plusButton.setDisable(false);
                minusButton.setDisable(false);
                numOfTablesLabel.setDisable(false);

                addAreaButton.setDisable(true);

            });

            HBox hBox2 = new HBox();
            hBox2.setSpacing(10);
            hBox2.setPadding(new Insets(5,5,5,15));
            hBox2.getChildren().addAll(addAreaButton);

            mainHbox.getChildren().add(hBox2);

            tableAndAreaTilePane.getChildren().add(mainHbox);

            Areas++;
        }

        //Adding 'Save Area' Buttons
        Button saveButton = new Button();
        saveButton.setPrefSize(200,40);
        saveButton.setText("Save");
        saveButton.setCursor(Cursor.HAND);
        saveButton.getStyleClass().add("saveEdit-button");
        saveButton.setOnMouseClicked(event -> {

            //Checking if all tables are closed
            try
            {
                if(daoimpl.fetchOpenAndReservedTableCount()>0)
                {
                    Alert deleteAlert = new Alert(Alert.AlertType.WARNING, "Open Tables Found!", ButtonType.OK);
                    deleteAlert.setHeaderText("Settle(close) the Open tables , Un-Reserve Tables and try again.Tables and Areas can be edited only when all tables are closed. ");
                    deleteAlert.setTitle("Alert!");
                    deleteAlert.showAndWait();
                    return;
                }
            }
            catch (SQLException e)
            {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Database operation Exception - "+e.getMessage(), ButtonType.OK);
                alert.setHeaderText("Failed");
                alert.setTitle("Error!");
                alert.showAndWait();
            }

            boolean isSaved = false;
            try
            {
                isSaved = daoimpl.updateAreaAndTables(areaAndTables);
            }
            catch (SQLException ex)
            {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Database update operation Exception - "+ex.getMessage(), ButtonType.OK);
                alert.setHeaderText("Failed");
                alert.setTitle("Error!");
                alert.showAndWait();
            }

            if(isSaved)
            {
                Alert saveAlert = new Alert(Alert.AlertType.INFORMATION, "Changes Saved Successfully!", ButtonType.OK);
                saveAlert.setHeaderText("Saved!");
                saveAlert.setTitle("Information");
                saveAlert.showAndWait();
            }
            else
            {
                displayDBConnectionErrorAlert();
            }
        });

        HBox hBox3 = new HBox();
        //hBox3.setStyle("-fx-border-color:black");
        hBox3.setSpacing(10);
        hBox3.setAlignment(Pos.CENTER);
        hBox3.getChildren().addAll(saveButton);

        tableAndAreaTilePane.getChildren().add(hBox3);

    }

    public void displayDBConnectionErrorAlert()
    {
        Alert deleteAlert = new Alert(Alert.AlertType.ERROR, "Database Connection Error", ButtonType.OK);
        deleteAlert.setHeaderText("Database error occurred. Check you Internet Connection and if this error keeps on occurring, Contact Customer Support. ");
        deleteAlert.setTitle("Alert!");
        deleteAlert.showAndWait();
    }

}
