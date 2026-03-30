package com.hotel.ui;

import com.hotel.model.Customer;
import com.hotel.service.CustomerService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

public class CustomerTab {

    private CustomerService customerService;

    private TableView<Customer> tableView;
    private ObservableList<Customer> customerData;

    private TextField txtCustomerID;
    private TextField txtName;
    private TextField txtContact;
    private TextField txtRemoveID;
    private Label lblStatus;

    public CustomerTab(CustomerService customerService) {
        this.customerService = customerService;
    }

    public Tab buildTab() {
        Tab tab = new Tab("Customers");
        tab.setClosable(false);

        // --- Add Customer Form ---
        GridPane form = new GridPane();
        form.setHgap(12);
        form.setVgap(10);
        form.setPadding(new Insets(16));

        form.add(new Label("Customer ID:"), 0, 0);
        txtCustomerID = new TextField();
        txtCustomerID.setPromptText("Enter customer ID");
        form.add(txtCustomerID, 1, 0);

        form.add(new Label("Name:"), 0, 1);
        txtName = new TextField();
        txtName.setPromptText("Enter name");
        form.add(txtName, 1, 1);

        form.add(new Label("Contact Number:"), 0, 2);
        txtContact = new TextField();
        txtContact.setPromptText("Enter contact number");
        form.add(txtContact, 1, 2);

        Button btnAdd = new Button("Add Customer");

        // --- Remove Form ---
        form.add(new Label("Remove Customer ID:"), 0, 3);
        txtRemoveID = new TextField();
        txtRemoveID.setPromptText("Enter ID to remove");
        form.add(txtRemoveID, 1, 3);

        Button btnRemove = new Button("Remove Customer");
        btnRemove.setStyle("-fx-text-fill: #cc0000;");

        HBox buttons = new HBox(10, btnAdd, btnRemove);
        buttons.setPadding(new Insets(0, 16, 8, 16));
        buttons.setAlignment(Pos.CENTER_LEFT);

        lblStatus = new Label();
        lblStatus.setStyle("-fx-text-fill: green;");
        lblStatus.setPadding(new Insets(0, 16, 8, 16));

        // --- TableView ---
        tableView = new TableView<>();
        customerData = FXCollections.observableArrayList(customerService.getAllCustomers());
        tableView.setItems(customerData);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Customer, String> colID = new TableColumn<>("Customer ID");
        colID.setCellValueFactory(new PropertyValueFactory<>("customerID"));

        TableColumn<Customer, String> colName = new TableColumn<>("Name");
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Customer, String> colContact = new TableColumn<>("Contact");
        colContact.setCellValueFactory(new PropertyValueFactory<>("contactNumber"));

        TableColumn<Customer, String> colRoom = new TableColumn<>("Allocated Room");
        colRoom.setCellValueFactory(new PropertyValueFactory<>("allocatedRoomDisplay"));

        tableView.getColumns().addAll(colID, colName, colContact, colRoom);
        VBox.setVgrow(tableView, Priority.ALWAYS);

        // --- Actions ---
        btnAdd.setOnAction(e -> handleAddCustomer());
        btnRemove.setOnAction(e -> handleRemoveCustomer());

        VBox content = new VBox(form, buttons, lblStatus, tableView);
        tab.setContent(content);
        return tab;
    }

    private void handleAddCustomer() {
        lblStatus.setStyle("-fx-text-fill: red;");
        String id = txtCustomerID.getText().trim();
        String name = txtName.getText().trim();
        String contact = txtContact.getText().trim();

        if (id.isEmpty() || name.isEmpty() || contact.isEmpty()) {
            lblStatus.setText("All fields are required.");
            return;
        }

        if (customerService.customerIDExists(id)) {
            lblStatus.setText("Customer ID '" + id + "' already exists.");
            return;
        }

        Customer c = new Customer(id, name, contact);
        customerService.addCustomer(c);
        customerData.setAll(customerService.getAllCustomers());

        clearForm();
        lblStatus.setStyle("-fx-text-fill: green;");
        lblStatus.setText("Customer '" + name + "' added successfully.");
    }

    private void handleRemoveCustomer() {
        lblStatus.setStyle("-fx-text-fill: red;");
        String id = txtRemoveID.getText().trim();
        if (id.isEmpty()) {
            lblStatus.setText("Enter a Customer ID to remove.");
            return;
        }

        Customer c = customerService.findByID(id);
        if (c == null) {
            lblStatus.setText("Customer ID '" + id + "' not found.");
            return;
        }

        if (c.getAllocatedRoomNumber() != -1) {
            lblStatus.setText("Cannot remove: customer is currently in room " + c.getAllocatedRoomNumber() + ". Checkout first.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Removal");
        confirm.setHeaderText(null);
        confirm.setContentText("Remove customer '" + c.getName() + "'? This cannot be undone.");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                customerService.removeCustomer(id);
                customerData.setAll(customerService.getAllCustomers());
                txtRemoveID.clear();
                lblStatus.setStyle("-fx-text-fill: green;");
                lblStatus.setText("Customer '" + c.getName() + "' removed.");
            }
        });
    }

    private void clearForm() {
        txtCustomerID.clear();
        txtName.clear();
        txtContact.clear();
    }

   public void refresh() {
    if (customerData != null) {
        customerData.setAll(customerService.getAllCustomers());
    }
}
}
