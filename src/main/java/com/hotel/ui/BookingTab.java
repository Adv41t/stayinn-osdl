package com.hotel.ui;

import com.hotel.model.Booking;
import com.hotel.model.Room;
import com.hotel.service.BookingService;
import com.hotel.service.CustomerService;
import com.hotel.service.RoomService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.time.LocalDate;

public class BookingTab {

    private BookingService bookingService;
    private RoomService roomService;
    private CustomerService customerService;

    private RoomTab roomTab;
    private CustomerTab customerTab;

    private TableView<Booking> tableView;
    private ObservableList<Booking> bookingData;

    private TextField txtCustID;
    private TextField txtRoomNum;
    private DatePicker dpCheckIn;
    private DatePicker dpCheckOut;

    private TextField txtCheckoutRoom;
    private Label lblStatus;
    private Label lblProcessing;

    public BookingTab(BookingService bookingService, RoomService roomService,
                      CustomerService customerService, RoomTab roomTab, CustomerTab customerTab) {
        this.bookingService = bookingService;
        this.roomService = roomService;
        this.customerService = customerService;
        this.roomTab = roomTab;
        this.customerTab = customerTab;
    }

    public Tab buildTab() {
        Tab tab = new Tab("Bookings");
        tab.setClosable(false);

        // ---- Book a Room form ----
        Label sectionBook = new Label("Book a Room");
        sectionBook.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        sectionBook.setPadding(new Insets(16, 16, 4, 16));

        GridPane bookForm = new GridPane();
        bookForm.setHgap(12);
        bookForm.setVgap(10);
        bookForm.setPadding(new Insets(8, 16, 4, 16));

        bookForm.add(new Label("Customer ID:"), 0, 0);
        txtCustID = new TextField();
        txtCustID.setPromptText("Enter customer ID");
        bookForm.add(txtCustID, 1, 0);

        bookForm.add(new Label("Room Number:"), 0, 1);
        txtRoomNum = new TextField();
        txtRoomNum.setPromptText("Enter room number");
        bookForm.add(txtRoomNum, 1, 1);

        bookForm.add(new Label("Check-in Date:"), 0, 2);
        dpCheckIn = new DatePicker(LocalDate.now());
        bookForm.add(dpCheckIn, 1, 2);

        bookForm.add(new Label("Check-out Date:"), 0, 3);
        dpCheckOut = new DatePicker(LocalDate.now().plusDays(1));
        bookForm.add(dpCheckOut, 1, 3);

        Button btnBook = new Button("Confirm Booking");
        btnBook.setStyle("-fx-base: #2980b9; -fx-text-fill: white;");

        lblProcessing = new Label();
        lblProcessing.setStyle("-fx-text-fill: #2980b9; -fx-font-style: italic;");

        HBox bookButtons = new HBox(10, btnBook, lblProcessing);
        bookButtons.setPadding(new Insets(4, 16, 8, 16));
        bookButtons.setAlignment(Pos.CENTER_LEFT);

        // ---- Checkout form ----
        Label sectionCheckout = new Label("Checkout");
        sectionCheckout.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        sectionCheckout.setPadding(new Insets(8, 16, 4, 16));

        GridPane checkoutForm = new GridPane();
        checkoutForm.setHgap(12);
        checkoutForm.setVgap(10);
        checkoutForm.setPadding(new Insets(8, 16, 4, 16));

        checkoutForm.add(new Label("Room Number:"), 0, 0);
        txtCheckoutRoom = new TextField();
        txtCheckoutRoom.setPromptText("Room to checkout");
        checkoutForm.add(txtCheckoutRoom, 1, 0);

        Button btnCheckout = new Button("Checkout Room");
        btnCheckout.setStyle("-fx-base: #27ae60; -fx-text-fill: white;");

        HBox checkoutButtons = new HBox(10, btnCheckout);
        checkoutButtons.setPadding(new Insets(4, 16, 8, 16));

        lblStatus = new Label();
        lblStatus.setStyle("-fx-text-fill: green;");
        lblStatus.setPadding(new Insets(0, 16, 4, 16));

        // ---- Booking History Table ----
        Label sectionHistory = new Label("Booking History");
        sectionHistory.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        sectionHistory.setPadding(new Insets(8, 16, 4, 16));

        tableView = new TableView<>();
        bookingData = FXCollections.observableArrayList(bookingService.getAllBookings());
        tableView.setItems(bookingData);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Booking, String> colBID = new TableColumn<>("Booking ID");
        colBID.setCellValueFactory(new PropertyValueFactory<>("bookingID"));

        TableColumn<Booking, String> colCust = new TableColumn<>("Customer");
        colCust.setCellValueFactory(new PropertyValueFactory<>("customerName"));

        TableColumn<Booking, Integer> colRoom = new TableColumn<>("Room #");
        colRoom.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));

        TableColumn<Booking, String> colIn = new TableColumn<>("Check-in");
        colIn.setCellValueFactory(new PropertyValueFactory<>("checkInDate"));

        TableColumn<Booking, String> colOut = new TableColumn<>("Check-out");
        colOut.setCellValueFactory(new PropertyValueFactory<>("checkOutDate"));

        TableColumn<Booking, Double> colCost = new TableColumn<>("Total Cost");
        colCost.setCellValueFactory(new PropertyValueFactory<>("totalCost"));
        colCost.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("₹%.2f", item));
            }
        });

        tableView.getColumns().addAll(colBID, colCust, colRoom, colIn, colOut, colCost);
        VBox.setVgrow(tableView, Priority.ALWAYS);

        // ---- Wire actions ----
        btnBook.setOnAction(e -> handleBookRoom(btnBook));
        btnCheckout.setOnAction(e -> handleCheckout());

        VBox content = new VBox(
                sectionBook, bookForm, bookButtons,
                new Separator(),
                sectionCheckout, checkoutForm, checkoutButtons,
                lblStatus,
                new Separator(),
                sectionHistory,
                tableView
        );
        tab.setContent(content);
        return tab;
    }

    private void handleBookRoom(Button btnBook) {
        lblStatus.setStyle("-fx-text-fill: red;");
        String custID = txtCustID.getText().trim();
        String roomStr = txtRoomNum.getText().trim();
        LocalDate checkIn = dpCheckIn.getValue();
        LocalDate checkOut = dpCheckOut.getValue();

        if (custID.isEmpty() || roomStr.isEmpty() || checkIn == null || checkOut == null) {
            lblStatus.setText("All booking fields are required.");
            return;
        }

        int roomNum;
        try {
            roomNum = Integer.parseInt(roomStr);
        } catch (NumberFormatException ex) {
            lblStatus.setText("Room number must be a number.");
            return;
        }

        // Validate eagerly before background thread
        Room room = roomService.findRoomByNumber(roomNum);
        if (room == null) { lblStatus.setText("Room " + roomNum + " does not exist."); return; }
        if (!room.isAvailable()) {
            showAlert(Alert.AlertType.ERROR, "Room Occupied",
                    "Room " + roomNum + " is already occupied and cannot be booked.");
            return;
        }

        // Perform booking directly (removed simulated delay thread)
        btnBook.setDisable(true);
lblProcessing.setText("Processing booking...");

Thread bookingThread = new Thread(() -> {
    try {
        Thread.sleep(1500); // simulated delay — demonstrates multithreading

        String bookingID = bookingService.bookRoom(custID, roomNum, checkIn, checkOut);
        Booking booking = bookingService.findActiveBookingByRoom(roomNum);

        Platform.runLater(() -> {
            bookingData.setAll(bookingService.getAllBookings());
            roomTab.refresh();
            customerTab.refresh();
            clearBookingForm();
            btnBook.setDisable(false);
            lblProcessing.setText("");
            lblStatus.setStyle("-fx-text-fill: green;");
            lblStatus.setText("Booking confirmed! ID: " + bookingID);
            showAlert(Alert.AlertType.INFORMATION, "Booking Confirmed",
                    "Booking ID: " + bookingID + "\nRoom: " + roomNum +
                    "\nTotal: ₹" + String.format("%.2f", booking != null ? booking.getTotalCost() : 0));
        });

    } catch (Exception ex) {
        Platform.runLater(() -> {
            btnBook.setDisable(false);
            lblProcessing.setText("");
            lblStatus.setStyle("-fx-text-fill: red;");
            lblStatus.setText("Booking failed: " + ex.getMessage());
        });
    }
});
bookingThread.setDaemon(true);
bookingThread.start();

}

    private void handleCheckout() {
        lblStatus.setStyle("-fx-text-fill: red;");
        String roomStr = txtCheckoutRoom.getText().trim();
        if (roomStr.isEmpty()) {
            lblStatus.setText("Enter a room number to checkout.");
            return;
        }

        int roomNum;
        try {
            roomNum = Integer.parseInt(roomStr);
        } catch (NumberFormatException e) {
            lblStatus.setText("Room number must be numeric.");
            return;
        }

        try {
            bookingService.checkout(roomNum);
            roomTab.refresh();
            customerTab.refresh();
            txtCheckoutRoom.clear();
            lblStatus.setStyle("-fx-text-fill: green;");
            lblStatus.setText("Room " + roomNum + " checked out successfully.");
            showAlert(Alert.AlertType.INFORMATION, "Checkout Complete",
                    "Room " + roomNum + " has been released and is now available.");
        } catch (Exception ex) {
            lblStatus.setText(ex.getMessage());
            showAlert(Alert.AlertType.ERROR, "Checkout Failed", ex.getMessage());
        }
    }

    private void clearBookingForm() {
        txtCustID.clear();
        txtRoomNum.clear();
        dpCheckIn.setValue(LocalDate.now());
        dpCheckOut.setValue(LocalDate.now().plusDays(1));
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
