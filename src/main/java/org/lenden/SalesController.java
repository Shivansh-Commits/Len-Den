package org.lenden;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import org.lenden.dao.DaoImpl;
import org.lenden.model.Bill;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;


public class SalesController implements Initializable
{
    @FXML
    BorderPane mainBorderPane;
    @FXML
    TableView<Bill> billsTable;
    @FXML
    Label netSalesLabel;
    @FXML
    Label totalOrdersLabel;

    DaoImpl daoImpl = new DaoImpl();
    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        //FETCHING ALL BILLS
        ObservableList<Bill> bills = FXCollections.observableArrayList();
        try {
            //Fetching all Bills
            bills = daoImpl.fetchAllBills();
            bills = bills.filtered(bill -> bill.getBillnumber() != 0);
        }
        catch (Exception ex)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Database operation Exception - "+ex.getMessage(), ButtonType.OK);
            alert.setHeaderText("Failed");
            alert.setTitle("Error!");
            alert.showAndWait();
        }



        //DISPLAYING TODAY'S BILLS AND SALES DATA
        displayTodaysBillData(bills);



        //DISPLAYING BILLS IN TABLE
        // Create a cell value factory for the Bill No. column
        TableColumn<Bill, Integer> billNoCol = new TableColumn<>("Bill No.");
        billNoCol.setCellValueFactory(new PropertyValueFactory<>("billnumber"));
        billNoCol.setPrefWidth(100);
        billNoCol.setStyle("-fx-alignment: CENTER;");
        billNoCol.setSortType(TableColumn.SortType.DESCENDING);

        // Create a cell value factory for the Date column
        TableColumn<Bill, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateCol.setPrefWidth(170);


        // Create a cell value factory for the Grand Total column
        TableColumn<Bill, Double> grandTotalCol = new TableColumn<>("Grand Total");
        grandTotalCol.setCellValueFactory(new PropertyValueFactory<>("grandTotal"));
        grandTotalCol.setPrefWidth(170);
        grandTotalCol.setStyle("-fx-alignment: CENTER;");

        // Create a cell value factory for the Date column
        TableColumn<Bill, String> subTotalCol = new TableColumn<>("Sub-Total");
        subTotalCol.setCellValueFactory(new PropertyValueFactory<>("subTotal"));
        subTotalCol.setPrefWidth(170);
        subTotalCol.setStyle("-fx-alignment: CENTER;");

        // Create a cell value factory for the Mode Of Payment column
        TableColumn<Bill, String> modeOfPaymentCol = new TableColumn<>("Mode of Payment");
        modeOfPaymentCol.setCellValueFactory(new PropertyValueFactory<>("modeOfpayment"));
        modeOfPaymentCol.setPrefWidth(170);
        modeOfPaymentCol.setStyle("-fx-alignment: CENTER;");

        // Create a cell value factory for the Status column
        TableColumn<Bill, String> statusCol = new TableColumn<>("Order Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(170);

        bills = bills.filtered(bill -> bill.getBillnumber() != 0);


        // Set the cell value factories for the table columns
        billsTable.getColumns().setAll(billNoCol, dateCol ,grandTotalCol, subTotalCol , modeOfPaymentCol ,statusCol );
        billsTable.setItems(bills);
        billsTable.getSortOrder().add(billNoCol);
        billsTable.sort();

        statusCol.setCellFactory(column -> new TableCell<Bill, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setText("");
                    setStyle("");
                }
                else {
                    setText(item);
                    if (item.equals("SUCCESS"))
                    {
                        // Set the background color of the cell to green if status is SUCCESS
                        setStyle("-fx-background-color: #c9f5c9;");
                        setAlignment(Pos.CENTER);
                    }
                    else if(item.equals("IN PROGRESS"))
                    {
                        // Set the background color of the cell to yellow if status is IN PROGRESS
                        setStyle("-fx-background-color: #dffc74;");
                        setAlignment(Pos.CENTER);
                    }
                    else
                    {
                        // Set the background color of the cell to red if status is CANCELLED
                        setStyle("-fx-background-color: #f5c9c9;");
                        setAlignment(Pos.CENTER);
                    }
                }
            }
        });

    }

    public boolean isSameDay(String dateString1, String dateString2) {
        // Extract day, month, and year components from both date strings
        String[] components1 = dateString1.split(" ")[1].split("/");
        String[] components2 = dateString2.split(" ")[1].split("/");

        int day1 = Integer.parseInt(components1[0]);
        int month1 = Integer.parseInt(components1[1]);
        int year1 = Integer.parseInt(components1[2]);

        int day2 = Integer.parseInt(components2[0]);
        int month2 = Integer.parseInt(components2[1]);
        int year2 = Integer.parseInt(components2[2]);

        // Compare day, month, and year components
        return (day1 == day2) && (month1 == month2) && (year1 == year2);
    }

    public void displayTodaysBillData(ObservableList<Bill> bills)
    {
        //FILTERING ONLY TODAY'S Bills
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy");
        String todaysDate = dateFormat.format( new Date());

        ArrayList<Bill> todaysBills = new ArrayList<>();
        for(Bill bill:bills)
        {
            if(isSameDay(bill.getDate(),todaysDate))
            {
                todaysBills.add(bill);
            }
        }

        double netSales = todaysBills.stream()
                .filter(bill -> !bill.getStatus().equals("CANCELLED"))
                .mapToDouble(Bill::getGrandTotal)
                .sum();
        netSalesLabel.setText(String.format("%.2f", netSales));

        int totalOrders = todaysBills.size();
        totalOrdersLabel.setText(String.valueOf(totalOrders));
    }


}
