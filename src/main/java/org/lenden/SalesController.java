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
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;


public class SalesController implements Initializable
{
    @FXML
    BorderPane mainBorderPane;
    @FXML
    TableView<Bill> billsTable;
    @FXML
    Label filterIdentifierLabelA;
    @FXML
    Label filterIdentifierLabelB;
    @FXML
    Label filterIdentifierLabelC;
    @FXML
    Label netSalesLabel;
    @FXML
    Label totalOrdersLabel;
    @FXML
    Label netProfitLabel;
    @FXML
    ToggleButton todaysFilterToggleButton;
    @FXML
    ToggleButton weeklyFilterToggleButton;
    @FXML
    ToggleButton monthlyFilterToggleButton;
    @FXML
    ToggleButton quarterlyFilterToggleButton;
    @FXML
    ToggleButton yearlyFilterToggleButton;
    @FXML
    TextField searchField;
    @FXML
    ComboBox<String> searchOptionsComboBox;

    ObservableList<Bill> bills = FXCollections.observableArrayList();
    DaoImpl daoImpl = new DaoImpl();
    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        //--------------FETCHING ALL BILLS------------------------

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

        //----------------DISPLAYING DEFAULT (TODAY'S BILLS AND SALES) DATA---------------------

        displayTodaysBillData(bills);
        todaysFilterToggleButton.setSelected(true);


        //-----------------ADDING EVENT LISTENER TO SEARCH FIELD-------------------------

        searchField.textProperty().addListener((observable, searchFieldOldValue, searchFieldNewValue) -> {
            searchTableBy(searchFieldNewValue);
        });


        //----------------SETTING/ADDING SEARCH OPTIONS IN COMBO BOX---------------------

        ArrayList<String> searchOptions = new ArrayList<>();
        searchOptions.add("Bill No.");
        searchOptions.add("Status");
        searchOptions.add("Mode of Payment");
        searchOptions.add("Date");
        searchOptions.add("Grand Total");
        searchOptionsComboBox.getItems().addAll(searchOptions);


        //---------------SETTING THE FUNCTIONALITY OF ALL FILTER (TOGGLE) BUTTONS----------------
        todaysFilterToggleButton.setTooltip(new javafx.scene.control.Tooltip("Filter Bills generated Today"));
        todaysFilterToggleButton.setOnAction(event -> {
            if (todaysFilterToggleButton.isSelected())
            {
                displayTodaysBillData(bills);
                searchField.clear();
            }
        });

        weeklyFilterToggleButton.setTooltip(new javafx.scene.control.Tooltip("Filter Bills of Current Week"));
        weeklyFilterToggleButton.setOnAction(event -> {
            if (weeklyFilterToggleButton.isSelected())
            {
                displayThisWeeksBillsData(bills);
                searchField.clear();
            }
        });

        monthlyFilterToggleButton.setTooltip(new javafx.scene.control.Tooltip("Filter Bills of Current Month"));
        monthlyFilterToggleButton.setOnAction(event -> {
            if (monthlyFilterToggleButton.isSelected())
            {
                displayThisMonthsBillsData(bills);
                searchField.clear();
            }
        });

        quarterlyFilterToggleButton.setTooltip(new javafx.scene.control.Tooltip("Filter Bills of Current Quarter i.e. Last 3 Months"));
        quarterlyFilterToggleButton.setOnAction(event -> {
            if (quarterlyFilterToggleButton.isSelected())
            {
                displayQuarterlyBillsData(bills);
                searchField.clear();
            }
        });

        yearlyFilterToggleButton.setTooltip(new javafx.scene.control.Tooltip("Shows current years Bills"));
        yearlyFilterToggleButton.setOnAction(event -> {
            if (yearlyFilterToggleButton.isSelected())
            {
                displayCurrentYearsBillsData(bills);
                searchField.clear();
            }
        });

    }

    private void searchTableBy(String searchText)
    {
        //Null check
        if(searchOptionsComboBox.getValue() == null)
        {
            searchOptionsComboBox.requestFocus();
            searchOptionsComboBox.show();
            searchOptionsComboBox.setValue("Status");

            return;
        }

        // If the search text is empty, display all bills
        if (searchText == null || searchText.trim().isEmpty())
        {
            if (weeklyFilterToggleButton.isSelected())
                displayThisWeeksBillsData(bills);
            else if (monthlyFilterToggleButton.isSelected())
                displayThisMonthsBillsData(bills);
            else if (quarterlyFilterToggleButton.isSelected())
                displayQuarterlyBillsData(bills);
            else if(yearlyFilterToggleButton.isSelected())
                displayCurrentYearsBillsData(bills);
            else
                displayTodaysBillData(bills);

            return;
        }

        if(searchOptionsComboBox.getValue().equals("Bill No."))
        {

            // Filter bills based on the Bill No. containing the search text (case insensitive)
            ObservableList<Bill> billsDisplayedInTable = billsTable.getItems();
            ObservableList<Bill> filteredBills = billsDisplayedInTable.filtered(bill ->
                    String.valueOf(bill.getBillnumber()).toLowerCase().contains(searchText.toLowerCase())
            );

            // Update the TableView with the filtered results
            billsTable.setItems(filteredBills);
        }
        else if(searchOptionsComboBox.getValue().equals("Status"))
        {

            // Filter bills based on the status containing the search text (case insensitive)
            ObservableList<Bill> billsDisplayedInTable = billsTable.getItems();
            ObservableList<Bill> filteredBills = billsDisplayedInTable.filtered(bill ->
                    bill.getStatus().toLowerCase().contains(searchText.toLowerCase())
            );

            // Update the TableView with the filtered results
            billsTable.setItems(filteredBills);
        }
        else if(searchOptionsComboBox.getValue().equals("Mode of Payment"))
        {

            // Filter bills based on the Mode of payment containing the search text (case insensitive)
            ObservableList<Bill> billsDisplayedInTable = billsTable.getItems();
            ObservableList<Bill> filteredBills = billsDisplayedInTable.filtered(bill ->
                    bill.getModeOfpayment().toLowerCase().contains(searchText.toLowerCase())
            );

            // Update the TableView with the filtered results
            billsTable.setItems(filteredBills);
        }
        else if(searchOptionsComboBox.getValue().equals("Date"))
        {

            // Filter bills based on the Date containing the search text (case insensitive)
            ObservableList<Bill> billsDisplayedInTable = billsTable.getItems();
            ObservableList<Bill> filteredBills = billsDisplayedInTable.filtered(bill ->
                    bill.getDate().toLowerCase().contains(searchText.toLowerCase())
            );

            // Update the TableView with the filtered results
            billsTable.setItems(filteredBills);
        }
        else if(searchOptionsComboBox.getValue().equals("Grand Total"))
        {

            // Filter bills based on the Grand Total containing the search text (case insensitive)
            ObservableList<Bill> billsDisplayedInTable = billsTable.getItems();
            ObservableList<Bill> filteredBills = billsDisplayedInTable.filtered(bill ->
                    String.valueOf(bill.getGrandTotal()).toLowerCase().contains(searchText.toLowerCase())
            );

            // Update the TableView with the filtered results
            billsTable.setItems(filteredBills);
        }
    }



    public boolean isSameDay(String dateString1, String dateString2)
    {
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

        ObservableList<Bill> todaysBills = FXCollections.observableArrayList();
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

        double netProfit = todaysBills.stream()
                .filter(bill -> !bill.getStatus().equals("CANCELLED"))
                .mapToDouble(Bill::getTotal)
                .sum();
        netProfitLabel.setText(String.format("%.2f", netProfit));

        long totalOrders = todaysBills.stream()
                .filter(bill -> !bill.getStatus().equals("CANCELLED"))
                .count();
        totalOrdersLabel.setText(String.valueOf(totalOrders));

        filterIdentifierLabelA.setText("Today's");
        filterIdentifierLabelB.setText("Today's");
        filterIdentifierLabelC.setText("Today's");

        //------------------DISPLAYING BILLS IN TABLE------------------------
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
        TableColumn<Bill, Double> totalCol = new TableColumn<>("Total");
        totalCol.setCellValueFactory(new PropertyValueFactory<>("total"));
        totalCol.setPrefWidth(170);
        totalCol.setStyle("-fx-alignment: CENTER;");

        // Create a cell value factory for the Mode Of Payment column
        TableColumn<Bill, String> modeOfPaymentCol = new TableColumn<>("Mode of Payment");
        modeOfPaymentCol.setCellValueFactory(new PropertyValueFactory<>("modeOfpayment"));
        modeOfPaymentCol.setPrefWidth(170);
        modeOfPaymentCol.setStyle("-fx-alignment: CENTER;");

        // Create a cell value factory for the Status column
        TableColumn<Bill, String> statusCol = new TableColumn<>("Order Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(170);


        // Set the cell value factories for the table columns
        billsTable.getColumns().setAll(billNoCol, dateCol ,grandTotalCol, totalCol , modeOfPaymentCol ,statusCol );
        billsTable.setItems(todaysBills);
        billsTable.getSortOrder().add(billNoCol);
        billsTable.sort();

        statusCol.setCellFactory(column -> new TableCell<Bill, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty)
                {
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


    public boolean isSameWeek(String dateString1, String dateString2)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.ENGLISH);
        try {
            Date date1 = dateFormat.parse(dateString1);
            Date date2 = dateFormat.parse(dateString2);

            Calendar cal1 = Calendar.getInstance();
            cal1.setTime(date1);
            int week1 = cal1.get(Calendar.WEEK_OF_YEAR);

            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(date2);
            int week2 = cal2.get(Calendar.WEEK_OF_YEAR);

            return week1 == week2;
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }
    public void displayThisWeeksBillsData(List<Bill> bills)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.ENGLISH);
        String currentDate = dateFormat.format(new Date());

        ObservableList<Bill> thisWeeksBills = FXCollections.observableArrayList();
        for (Bill bill : bills) {
            if (isSameWeek(bill.getDate(), currentDate)) {
                thisWeeksBills.add(bill);
            }
        }

        double netSales = thisWeeksBills.stream()
                .filter(bill -> !bill.getStatus().equals("CANCELLED"))
                .mapToDouble(Bill::getGrandTotal)
                .sum();
        netSalesLabel.setText(String.format("%.2f", netSales));

        double netProfit = thisWeeksBills.stream()
                .filter(bill -> !bill.getStatus().equals("CANCELLED"))
                .mapToDouble(Bill::getTotal)
                .sum();
        netProfitLabel.setText(String.format("%.2f", netProfit));

        long totalOrders = thisWeeksBills.stream()
                .filter(bill -> !bill.getStatus().equals("CANCELLED"))
                .count();
        totalOrdersLabel.setText(String.valueOf(totalOrders));

        filterIdentifierLabelA.setText("Current Week's");
        filterIdentifierLabelB.setText("Current Week's");
        filterIdentifierLabelC.setText("Current Week's");

        //------------------DISPLAYING BILLS IN TABLE------------------------
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
        TableColumn<Bill, Double> totalCol = new TableColumn<>("Total");
        totalCol.setCellValueFactory(new PropertyValueFactory<>("total"));
        totalCol.setPrefWidth(170);
        totalCol.setStyle("-fx-alignment: CENTER;");

        // Create a cell value factory for the Mode Of Payment column
        TableColumn<Bill, String> modeOfPaymentCol = new TableColumn<>("Mode of Payment");
        modeOfPaymentCol.setCellValueFactory(new PropertyValueFactory<>("modeOfpayment"));
        modeOfPaymentCol.setPrefWidth(170);
        modeOfPaymentCol.setStyle("-fx-alignment: CENTER;");

        // Create a cell value factory for the Status column
        TableColumn<Bill, String> statusCol = new TableColumn<>("Order Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(170);


        // Set the cell value factories for the table columns
        billsTable.getColumns().setAll(billNoCol, dateCol ,grandTotalCol, totalCol , modeOfPaymentCol ,statusCol );
        billsTable.setItems(thisWeeksBills);
        billsTable.getSortOrder().add(billNoCol);
        billsTable.sort();

        statusCol.setCellFactory(column -> new TableCell<Bill, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty)
                {
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


    public boolean isSameMonth(String dateString1, String dateString2)
    {
        // Extract month and year components from both date strings
        String[] components1 = dateString1.split(" ")[1].split("/");
        String[] components2 = dateString2.split(" ")[1].split("/");

        int month1 = Integer.parseInt(components1[1]);
        int year1 = Integer.parseInt(components1[2]);

        int month2 = Integer.parseInt(components2[1]);
        int year2 = Integer.parseInt(components2[2]);

        // Compare month and year components
        return (month1 == month2) && (year1 == year2);
    }
    public void displayThisMonthsBillsData(ObservableList<Bill> bills)
    {
        // FILTERING ONLY THIS MONTH'S Bills
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy");
        String currentDate = dateFormat.format(new Date());

        ObservableList<Bill> thisMonthsBills = FXCollections.observableArrayList();
        for (Bill bill : bills) {
            if (isSameMonth(bill.getDate(), currentDate)) {
                thisMonthsBills.add(bill);
            }
        }

        double netSales = thisMonthsBills.stream()
                .filter(bill -> !bill.getStatus().equals("CANCELLED"))
                .mapToDouble(Bill::getGrandTotal)
                .sum();
        netSalesLabel.setText(String.format("%.2f", netSales));

        double netProfit = thisMonthsBills.stream()
                .filter(bill -> !bill.getStatus().equals("CANCELLED"))
                .mapToDouble(Bill::getTotal)
                .sum();
        netProfitLabel.setText(String.format("%.2f", netProfit));

        long totalOrders = thisMonthsBills.stream()
                .filter(bill -> !bill.getStatus().equals("CANCELLED"))
                .count();
        totalOrdersLabel.setText(String.valueOf(totalOrders));

        filterIdentifierLabelA.setText("Current Month's");
        filterIdentifierLabelB.setText("Current Month's");
        filterIdentifierLabelC.setText("Current Month's");


        //------------------DISPLAYING BILLS IN TABLE------------------------
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
        TableColumn<Bill, Double> totalCol = new TableColumn<>("Total");
        totalCol.setCellValueFactory(new PropertyValueFactory<>("total"));
        totalCol.setPrefWidth(170);
        totalCol.setStyle("-fx-alignment: CENTER;");

        // Create a cell value factory for the Mode Of Payment column
        TableColumn<Bill, String> modeOfPaymentCol = new TableColumn<>("Mode of Payment");
        modeOfPaymentCol.setCellValueFactory(new PropertyValueFactory<>("modeOfpayment"));
        modeOfPaymentCol.setPrefWidth(170);
        modeOfPaymentCol.setStyle("-fx-alignment: CENTER;");

        // Create a cell value factory for the Status column
        TableColumn<Bill, String> statusCol = new TableColumn<>("Order Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(170);


        // Set the cell value factories for the table columns
        billsTable.getColumns().setAll(billNoCol, dateCol ,grandTotalCol, totalCol , modeOfPaymentCol ,statusCol );
        billsTable.setItems(thisMonthsBills);
        billsTable.getSortOrder().add(billNoCol);
        billsTable.sort();

        statusCol.setCellFactory(column -> new TableCell<Bill, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty)
                {
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


    public boolean isLastThreeMonths(String dateString1, String dateString2)
    {
        // Extract month and year components from both date strings
        String[] components1 = dateString1.split(" ")[1].split("/");
        String[] components2 = dateString2.split(" ")[1].split("/");

        int month1 = Integer.parseInt(components1[1]);
        int year1 = Integer.parseInt(components1[2]);

        int month2 = Integer.parseInt(components2[1]);
        int year2 = Integer.parseInt(components2[2]);

        // Calculate the difference in months
        int monthsDifference = (year2 - year1) * 12 + month2 - month1;

        // Check if the difference is less than or equal to 3
        return monthsDifference >= 0 && monthsDifference <= 2;
    }
    public void displayQuarterlyBillsData(ObservableList<Bill> bills)
    {
        // FILTERING ONLY LAST 3 MONTHS' Bills
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy");
        String currentDate = dateFormat.format(new Date());

        ObservableList<Bill> quarterlyBills = FXCollections.observableArrayList();
        for (Bill bill : bills) {
            if (isLastThreeMonths(bill.getDate(), currentDate)) {
                quarterlyBills.add(bill);
            }
        }

        double netSales = quarterlyBills.stream()
                .filter(bill -> !bill.getStatus().equals("CANCELLED"))
                .mapToDouble(Bill::getGrandTotal)
                .sum();
        netSalesLabel.setText(String.format("%.2f", netSales));

        double netProfit = quarterlyBills.stream()
                .filter(bill -> !bill.getStatus().equals("CANCELLED"))
                .mapToDouble(Bill::getTotal)
                .sum();
        netProfitLabel.setText(String.format("%.2f", netProfit));

        long totalOrders = quarterlyBills.stream()
                .filter(bill -> !bill.getStatus().equals("CANCELLED"))
                .count();
        totalOrdersLabel.setText(String.valueOf(totalOrders));

        filterIdentifierLabelA.setText("Current Quarter's");
        filterIdentifierLabelB.setText("Current Quarter's");
        filterIdentifierLabelC.setText("Current Quarter's");

        //------------------DISPLAYING BILLS IN TABLE------------------------
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
        TableColumn<Bill, Double> totalCol = new TableColumn<>("Total");
        totalCol.setCellValueFactory(new PropertyValueFactory<>("total"));
        totalCol.setPrefWidth(170);
        totalCol.setStyle("-fx-alignment: CENTER;");

        // Create a cell value factory for the Mode Of Payment column
        TableColumn<Bill, String> modeOfPaymentCol = new TableColumn<>("Mode of Payment");
        modeOfPaymentCol.setCellValueFactory(new PropertyValueFactory<>("modeOfpayment"));
        modeOfPaymentCol.setPrefWidth(170);
        modeOfPaymentCol.setStyle("-fx-alignment: CENTER;");

        // Create a cell value factory for the Status column
        TableColumn<Bill, String> statusCol = new TableColumn<>("Order Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(170);


        // Set the cell value factories for the table columns
        billsTable.getColumns().setAll(billNoCol, dateCol ,grandTotalCol, totalCol , modeOfPaymentCol ,statusCol );
        billsTable.setItems(quarterlyBills);
        billsTable.getSortOrder().add(billNoCol);
        billsTable.sort();

        statusCol.setCellFactory(column -> new TableCell<Bill, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty)
                {
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


    public boolean isSameYear(String dateString1, String dateString2) {
        // Extract year components from both date strings
        String[] components1 = dateString1.split(" ")[1].split("/");
        String[] components2 = dateString2.split(" ")[1].split("/");

        int year1 = Integer.parseInt(components1[2]);
        int year2 = Integer.parseInt(components2[2]);

        // Compare year components
        return year1 == year2;
    }

    public void displayCurrentYearsBillsData(ObservableList<Bill> bills) {
        // FILTERING ONLY THIS YEAR'S Bills
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy");
        String currentDate = dateFormat.format(new Date());

        ObservableList<Bill> thisYearsBills = FXCollections.observableArrayList();
        for (Bill bill : bills) {
            if (isSameYear(bill.getDate(), currentDate)) {
                thisYearsBills.add(bill);
            }
        }

        double netSales = thisYearsBills.stream()
                .filter(bill -> !bill.getStatus().equals("CANCELLED"))
                .mapToDouble(Bill::getGrandTotal)
                .sum();
        netSalesLabel.setText(String.format("%.2f", netSales));

        double netProfit = thisYearsBills.stream()
                .filter(bill -> !bill.getStatus().equals("CANCELLED"))
                .mapToDouble(Bill::getTotal)
                .sum();
        netProfitLabel.setText(String.format("%.2f", netProfit));

        long totalOrders = thisYearsBills.stream()
                .filter(bill -> !bill.getStatus().equals("CANCELLED"))
                .count();
        totalOrdersLabel.setText(String.valueOf(totalOrders));

        filterIdentifierLabelA.setText("This Year's");
        filterIdentifierLabelB.setText("This Year's");
        filterIdentifierLabelC.setText("This Year's");

        //------------------DISPLAYING BILLS IN TABLE------------------------
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
        TableColumn<Bill, Double> totalCol = new TableColumn<>("Total");
        totalCol.setCellValueFactory(new PropertyValueFactory<>("total"));
        totalCol.setPrefWidth(170);
        totalCol.setStyle("-fx-alignment: CENTER;");

        // Create a cell value factory for the Mode Of Payment column
        TableColumn<Bill, String> modeOfPaymentCol = new TableColumn<>("Mode of Payment");
        modeOfPaymentCol.setCellValueFactory(new PropertyValueFactory<>("modeOfpayment"));
        modeOfPaymentCol.setPrefWidth(170);
        modeOfPaymentCol.setStyle("-fx-alignment: CENTER;");

        // Create a cell value factory for the Status column
        TableColumn<Bill, String> statusCol = new TableColumn<>("Order Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(170);

        // Set the cell value factories for the table columns
        billsTable.getColumns().setAll(billNoCol, dateCol, grandTotalCol, totalCol, modeOfPaymentCol, statusCol);
        billsTable.setItems(thisYearsBills);
        billsTable.getSortOrder().add(billNoCol);
        billsTable.sort();

        statusCol.setCellFactory(column -> new TableCell<Bill, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setText("");
                    setStyle("");
                } else {
                    setText(item);
                    if (item.equals("SUCCESS")) {
                        // Set the background color of the cell to green if status is SUCCESS
                        setStyle("-fx-background-color: #c9f5c9;");
                        setAlignment(Pos.CENTER);
                    } else if (item.equals("IN PROGRESS")) {
                        // Set the background color of the cell to yellow if status is IN PROGRESS
                        setStyle("-fx-background-color: #dffc74;");
                        setAlignment(Pos.CENTER);
                    } else {
                        // Set the background color of the cell to red if status is CANCELLED
                        setStyle("-fx-background-color: #f5c9c9;");
                        setAlignment(Pos.CENTER);
                    }
                }
            }
        });
    }


}


