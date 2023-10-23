package org.lenden;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;
import org.lenden.dao.DaoImpl;
import org.lenden.model.Bill;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class SalesController implements Initializable
{
    @FXML
    BorderPane mainBorderPane;

    DaoImpl daoImpl = new DaoImpl();
    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        PieChart pieChart = new PieChart();
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

        try
        {
            ArrayList<Bill> billsData = daoImpl.fetchAllBills();

            for(Bill bill : billsData)
            {
                pieChartData.add(new PieChart.Data("TEST", bill.getGrandTotal()));
            }

            pieChart.setData(pieChartData);
            pieChart.setLegendVisible(true);
            pieChart.setLabelsVisible(true);
        }
        catch (Exception ex)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Database operation Exception - "+ex.getMessage(), ButtonType.OK);
            alert.setHeaderText("Failed");
            alert.setTitle("Error!");
            alert.showAndWait();
        }

        mainBorderPane.setCenter(pieChart);
    }
}
