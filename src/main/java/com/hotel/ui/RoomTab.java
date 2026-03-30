package com.hotel.ui;

import com.hotel.model.Room;
import com.hotel.model.RoomType;
import com.hotel.service.RoomService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

public class RoomTab {

    private RoomService roomService;

    private TableView<Room> tableView;
    private ObservableList<Room> roomData;

    private TextField txtRoomNumber;
    private ComboBox<RoomType> cmbRoomType;
    private TextField txtPrice;
    private Label lblStatus;

    public RoomTab(RoomService roomService) {
        this.roomService = roomService;
    }

    public Tab buildTab() {
        Tab tab = new Tab("Rooms");
        tab.setClosable(false);

        // --- Form ---
        GridPane form = new GridPane();
        form.setHgap(12);
        form.setVgap(10);
        form.setPadding(new Insets(16));

        form.add(new Label("Room Number:"), 0, 0);
        txtRoomNumber = new TextField();
        txtRoomNumber.setPromptText("Enter room number");
        form.add(txtRoomNumber, 1, 0);

        form.add(new Label("Room Type:"), 0, 1);
        cmbRoomType = new ComboBox<>(FXCollections.observableArrayList(RoomType.values()));
        cmbRoomType.setPromptText("Select type");
        form.add(cmbRoomType, 1, 1);

        form.add(new Label("Price/Night (₹):"), 0, 2);
        txtPrice = new TextField();
        txtPrice.setPromptText("Auto-filled or override");
        form.add(txtPrice, 1, 2);

        // Auto-fill price when type selected
        cmbRoomType.setOnAction(e -> {
            RoomType rt = cmbRoomType.getValue();
            if (rt != null) txtPrice.setText(String.valueOf(rt.getPricePerNight()));
        });

        // --- Buttons ---
        Button btnAdd = new Button("Add Room");
        Button btnShowAll = new Button("Show All");
        Button btnShowAvailable = new Button("Available Only");
        
        ComboBox<RoomType> cmbFilter = new ComboBox<>(FXCollections.observableArrayList(RoomType.values()));
        cmbFilter.setPromptText("Filter by Type");

        HBox buttons = new HBox(10, btnAdd, btnShowAll, btnShowAvailable, cmbFilter);
        buttons.setPadding(new Insets(0, 16, 8, 16));
        buttons.setAlignment(Pos.CENTER_LEFT);

        lblStatus = new Label();
        lblStatus.setStyle("-fx-text-fill: green;");
        lblStatus.setPadding(new Insets(0, 16, 8, 16));

        // --- TableView ---
        tableView = new TableView<>();
        roomData = FXCollections.observableArrayList(roomService.getAllRooms());
        tableView.setItems(roomData);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Room, Integer> colNum = new TableColumn<>("Room #");
        colNum.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));

        TableColumn<Room, RoomType> colType = new TableColumn<>("Type");
        colType.setCellValueFactory(new PropertyValueFactory<>("roomType"));

        TableColumn<Room, Double> colPrice = new TableColumn<>("Price/Night");
        colPrice.setCellValueFactory(new PropertyValueFactory<>("pricePerNight"));
        colPrice.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("₹%.2f", item));
            }
        });

        TableColumn<Room, Boolean> colAvail = new TableColumn<>("Available");
        colAvail.setCellValueFactory(new PropertyValueFactory<>("available"));
        colAvail.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item ? "Yes" : "No");
                setStyle(item ? "-fx-text-fill: green; -fx-font-weight: bold;"
                              : "-fx-text-fill: red; -fx-font-weight: bold;");
            }
        });

        tableView.getColumns().addAll(colNum, colType, colPrice, colAvail);
        VBox.setVgrow(tableView, Priority.ALWAYS);

        // --- Actions ---
        btnAdd.setOnAction(e -> handleAddRoom());

        btnShowAll.setOnAction(e -> {
            roomData.setAll(roomService.getAllRooms());
            lblStatus.setText("Showing all " + roomData.size() + " rooms.");
            cmbFilter.getSelectionModel().clearSelection();
        });

        btnShowAvailable.setOnAction(e -> {
            roomData.setAll(roomService.getAvailableRooms());
            lblStatus.setText("Showing " + roomData.size() + " available room(s).");
            cmbFilter.getSelectionModel().clearSelection();
        });

        cmbFilter.setOnAction(e -> {
            RoomType rt = cmbFilter.getValue();
            if (rt != null) {
                java.util.List<Room> filtered = roomService.getAllRooms().stream()
                        .filter(r -> r.getRoomType() == rt)
                        .toList();
                roomData.setAll(filtered);
                lblStatus.setText("Showing " + filtered.size() + " " + rt.name() + " room(s).");
            }
        });



        VBox content = new VBox(form, buttons, lblStatus, tableView);
        tab.setContent(content);
        return tab;
    }

    private void handleAddRoom() {
        lblStatus.setStyle("-fx-text-fill: red;");
        String numText = txtRoomNumber.getText().trim();
        RoomType type = cmbRoomType.getValue();
        String priceText = txtPrice.getText().trim();

        if (numText.isEmpty() || type == null || priceText.isEmpty()) {
            lblStatus.setText("Please fill in all fields.");
            return;
        }

        int roomNum;
        double price;
        try {
            roomNum = Integer.parseInt(numText);
            price = Double.parseDouble(priceText);
        } catch (NumberFormatException ex) {
            lblStatus.setText("Room number and price must be numeric.");
            return;
        }

        if (roomService.roomNumberExists(roomNum)) {
            lblStatus.setText("Room #" + roomNum + " already exists.");
            return;
        }

        Room newRoom = new Room(roomNum, type, price);
        roomService.addRoom(newRoom);
        roomData.setAll(roomService.getAllRooms());

        clearForm();
        lblStatus.setStyle("-fx-text-fill: green;");
        lblStatus.setText("Room #" + roomNum + " added successfully.");
    }

    private void clearForm() {
        txtRoomNumber.clear();
        cmbRoomType.setValue(null);
        txtPrice.clear();
    }

  public void refresh() {
    if (roomData != null) {
        roomData.setAll(roomService.getAllRooms());
    }
}

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
