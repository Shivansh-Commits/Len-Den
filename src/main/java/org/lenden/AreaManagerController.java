package org.lenden;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.lenden.dao.DaoImpl;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class AreaManagerController implements Initializable {

    @FXML
    VBox areasVbox;
    @FXML
    Label totalAreasLabel;
    @FXML
    Label totalTablesLabel;
    @FXML
    Label unsavedChangesLabel;


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

        unsavedChangesLabel.setVisible(false);

        totalAreasLabel.setText(    String.valueOf(areaAndTables.size())    );

        areasVbox.setSpacing(15);

        int totalTables = 0;
        int Areas=0;

        for(Map.Entry<String, Integer> entry : areaAndTables.entrySet())
        {
            String area = entry.getKey();
            int tables = entry.getValue();
            totalTables +=tables;

            HBox areaHbox = new HBox();
            areaHbox.setPadding(new Insets(15,15,15,15));
            areaHbox.setSpacing(20);
            areaHbox.setAlignment(Pos.CENTER);
            areaHbox.setStyle("-fx-background-color:white; -fx-border-color:grey; -fx-border-radius:5px");


            //Adding Text Field
            TextField areaName = new TextField();
            areaName.setPromptText("Area Name");
            areaName.setPrefWidth(200);
            areaName.setPrefHeight(43);
            areaName.setMaxHeight(Double.MAX_VALUE);
            areaName.setAlignment(Pos.CENTER);
            areaName.getStyleClass().add("area-name-label");
            areaName.setText(area);
            areaName.setOnAction(e -> unsavedChangesLabel.setVisible(true)); //Updating isSaved Flag


            //Adding Tables label and plus-minus button
            Label numOfTablesLabel = new Label();
            numOfTablesLabel.getStyleClass().add("tables-label");
            numOfTablesLabel.setText(tables+" Tables");

            Button plusButton = new Button();
            plusButton.setText("＋");
            plusButton.getStyleClass().add("plus-table-button");
            plusButton.setPrefWidth(40);
            plusButton.setPrefHeight(43);
            plusButton.setMaxHeight(Double.MAX_VALUE);
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

                //Updating Unsaved Label Visibility
                unsavedChangesLabel.setVisible(true);

            });

            Button minusButton = new Button();
            minusButton.setText("—");
            minusButton.getStyleClass().add("minus-table-button");
            minusButton.setPrefWidth(40);
            minusButton.setPrefHeight(43);
            minusButton.setMaxHeight(Double.MAX_VALUE);
            minusButton.setCursor(Cursor.HAND);
            minusButton.setOnAction( event -> {

                //Updating Total Tables Label
                int updatedTaables = Integer.parseInt(totalTablesLabel.getText())-1;
                totalTablesLabel.setText(   String.valueOf(  updatedTaables<0 ? 0 : updatedTaables) );

                int numOfTables = Integer.parseInt(numOfTablesLabel.getText().split(" ")[0]);

                int newNumOfTables = numOfTables - 1;
                if(newNumOfTables<0)
                    newNumOfTables=0;

                //Updating the Label
                numOfTablesLabel.setText(newNumOfTables+" Tables");

                //Updating Unsaved Label Visibility
                unsavedChangesLabel.setVisible(true);

            });

            HBox tablesCountHbox = new HBox();
            tablesCountHbox.setSpacing(35);
            tablesCountHbox.setAlignment(Pos.CENTER);
            tablesCountHbox.getChildren().addAll(minusButton,numOfTablesLabel,plusButton);

            //Adding Delete button
            Button deleteButton = new Button();
            deleteButton.setPrefWidth(200);
            deleteButton.setPrefHeight(43);
            deleteButton.setMaxHeight(Double.MAX_VALUE);
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
                            int newTables = Integer.parseInt(totalTablesLabel.getText()) - Integer.parseInt(numOfTablesLabel.getText().split(" ")[0]);
                            totalTablesLabel.setText(   String.valueOf( newTables < 0 ? 0 : newTables )  );

                            //Updating Total Areas Label
                            totalAreasLabel.setText(   String.valueOf( (Integer.parseInt(totalAreasLabel.getText()) - 1)< 0 ? 0 : (Integer.parseInt(totalAreasLabel.getText())-1) ));


                            Node parent = deleteButton.getParent();

                            if (parent instanceof HBox) {
                                HBox hBoxToRemove = (HBox) parent;
                                areasVbox.getChildren().remove(hBoxToRemove);
                            }

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


            areaHbox.getChildren().addAll(areaName,tablesCountHbox,deleteButton);

            areasVbox.getChildren().add(areasVbox.getChildren().size() - 2, areaHbox); // Add above the button

            Areas++;
        }

        totalTablesLabel.setText(String.valueOf(totalTables));
    }



    public void addAreaButtonListener()
    {
        HBox areaHbox = new HBox();
        areaHbox.setPadding(new Insets(15,15,15,15));
        areaHbox.setSpacing(20);
        areaHbox.setAlignment(Pos.CENTER);
        areaHbox.setStyle("-fx-background-color:white; -fx-border-color:grey; -fx-border-radius:5px");


        //Adding Text Field
        TextField areaName = new TextField();
        areaName.setPromptText("Area Name");
        areaName.setPrefWidth(200);
        areaName.setPrefHeight(43);
        areaName.setMaxHeight(Double.MAX_VALUE);
        areaName.setAlignment(Pos.CENTER);
        areaName.getStyleClass().add("area-name-label");
        areaName.setOnAction(e -> unsavedChangesLabel.setVisible(true));

        //Adding Tables label and plus-minus button
        Label numOfTablesLabel = new Label();
        numOfTablesLabel.getStyleClass().add("tables-label");
        numOfTablesLabel.setText("0 Tables");

        Button plusButton = new Button();
        plusButton.setText("＋");
        plusButton.getStyleClass().add("plus-table-button");
        plusButton.setPrefWidth(40);
        plusButton.setPrefHeight(43);
        plusButton.setMaxHeight(Double.MAX_VALUE);
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

            //Updating Unsaved Label Visibility
            unsavedChangesLabel.setVisible(true);
        });

        Button minusButton = new Button();
        minusButton.setText("—");
        minusButton.getStyleClass().add("minus-table-button");
        minusButton.setPrefWidth(40);
        plusButton.setPrefHeight(43);
        minusButton.setMaxHeight(Double.MAX_VALUE);
        minusButton.setCursor(Cursor.HAND);
        minusButton.setOnAction( event -> {

            //Updating Total Tables Label
            int updatedTaables = Integer.parseInt(totalTablesLabel.getText())-1;
            totalTablesLabel.setText(   String.valueOf(  updatedTaables<0 ? 0 : updatedTaables) );

            int numOfTables = Integer.parseInt(numOfTablesLabel.getText().split(" ")[0]);

            int newNumOfTables = numOfTables - 1;
            if(newNumOfTables<0)
                newNumOfTables=0;

            //Updating the Tables Label
            numOfTablesLabel.setText(newNumOfTables+" Tables");

            //Updating Unsaved Label Visibility
            unsavedChangesLabel.setVisible(true);
        });

        HBox tablesCountHbox = new HBox();
        tablesCountHbox.setSpacing(35);
        tablesCountHbox.setAlignment(Pos.CENTER);
        tablesCountHbox.getChildren().addAll(minusButton,numOfTablesLabel,plusButton);

        //Adding delete button
        Button deleteButton = new Button();
        deleteButton.setPrefWidth(200);
        deleteButton.setPrefHeight(43);
        deleteButton.setMaxHeight(Double.MAX_VALUE);
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
                    if(areaName.getText().isEmpty())
                    {
                        //Updating Total Tables Label
                        int newTables = Integer.parseInt(totalTablesLabel.getText()) - Integer.parseInt(numOfTablesLabel.getText().split(" ")[0]);
                        totalTablesLabel.setText(   String.valueOf( newTables < 0 ? 0 : newTables )  );

                        //Updating Total Areas Label
                        totalAreasLabel.setText(   String.valueOf( (Integer.parseInt(totalAreasLabel.getText()) - 1) ));

                        //areaAndTables.remove(areaName.getText());

                        Node parent = deleteButton.getParent();

                        if (parent instanceof HBox) {
                            HBox hBoxToRemove = (HBox) parent;
                            areasVbox.getChildren().remove(hBoxToRemove);
                        }

                    }
                    else if(daoimpl.deleteArea(areaName.getText()))
                    {
                        //Updating Total Tables Label
                        int newTables = Integer.parseInt(totalTablesLabel.getText()) - Integer.parseInt(numOfTablesLabel.getText().split(" ")[0]);
                        totalTablesLabel.setText(   String.valueOf( newTables < 0 ? 0 : newTables )  );

                        //Updating Total Areas Label
                        totalAreasLabel.setText(   String.valueOf( Integer.parseInt(totalAreasLabel.getText()) - 1 )  );

                        //areaAndTables.remove(areaName.getText());

                        Node parent = deleteButton.getParent();

                        if (parent instanceof HBox) {
                            HBox hBoxToRemove = (HBox) parent;
                            areasVbox.getChildren().remove(hBoxToRemove);
                        }

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

        areaHbox.getChildren().addAll(areaName,tablesCountHbox,deleteButton);

        areasVbox.getChildren().add(areasVbox.getChildren().size() - 2, areaHbox); // Add above the button

        //Updating total areas label
        int updatedAreas = Integer.parseInt(totalAreasLabel.getText())+1;
        totalAreasLabel.setText(String.valueOf(updatedAreas));

        //Updating Unsaved Label Visibility
        unsavedChangesLabel.setVisible(true);
    }

    public void saveButtonListener()
    {

        //Checking if atleast 1 area is added.
        if(areasVbox.getChildren().size()<=0)
        {
            Alert deleteAlert = new Alert(Alert.AlertType.WARNING, "No Areas Added", ButtonType.OK);
            deleteAlert.setHeaderText("Add Areas and Tables to be viewd in the table billing Page ");
            deleteAlert.setTitle("Alert!");
            deleteAlert.showAndWait();
            return;
        }

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

            //GET And populate all area and table details from fields
            areaAndTables.clear();
            for (Node node : areasVbox.getChildren()) {
                if (node instanceof HBox) {
                    HBox hBox = (HBox) node;
                    String areaName = null;
                    int numOfTables = 0;
                    for (Node childNode : hBox.getChildren()) {
                        if (childNode instanceof TextField) {
                            TextField textField = (TextField) childNode;
                            areaName = textField.getText();
                        }
                    }
                    // If we found the area name, search for the label inside the nested HBox
                    if (areaName != null) {
                        for (Node childNode : hBox.getChildren()) {
                            if (childNode instanceof HBox) {
                                HBox innerHBox = (HBox) childNode;
                                for (Node innerNode : innerHBox.getChildren()) {
                                    if (innerNode instanceof Label) {
                                        Label label = (Label) innerNode;
                                        numOfTables = Integer.parseInt( label.getText().split(" ")[0] );
                                    }
                                }
                            }
                        }
                    }
                    if (areaName != null && numOfTables >= 0) {
                        areaAndTables.put(areaName, numOfTables);
                    }
                }
            }


            if(daoimpl.updateAreaAndTables(areaAndTables))
            {
                Alert saveAlert = new Alert(Alert.AlertType.INFORMATION, "Changes Saved Successfully!", ButtonType.OK);
                saveAlert.setHeaderText("Saved!");
                saveAlert.setTitle("Information");
                saveAlert.showAndWait();

                //Updating Unsaved Label Visibility
                unsavedChangesLabel.setVisible(false);
            }
            else
            {
                displayDBConnectionErrorAlert();
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

    }


    public void displayDBConnectionErrorAlert()
    {
        Alert deleteAlert = new Alert(Alert.AlertType.ERROR, "Database Connection Error", ButtonType.OK);
        deleteAlert.setHeaderText("Database error occurred. Check you Internet Connection and if this error keeps on occurring, Contact Customer Support. ");
        deleteAlert.setTitle("Alert!");
        deleteAlert.showAndWait();
    }

}
