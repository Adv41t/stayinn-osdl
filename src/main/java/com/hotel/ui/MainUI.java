package com.hotel.ui;

import com.hotel.service.BookingService;
import com.hotel.service.CustomerService;
import com.hotel.service.RoomService;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MainUI {

    private final RoomService roomService;
    private final CustomerService customerService;
    private final BookingService bookingService;
    private final String role;

    private Label statusLabel;
    private Label clockLabel;

    public MainUI(RoomService roomService, CustomerService customerService, BookingService bookingService, String role) {
        this.roomService = roomService;
        this.customerService = customerService;
        this.bookingService = bookingService;
        this.role = role;
    }

    public void buildAndShow(Stage primaryStage) {
        RoomTab roomTab = new RoomTab(roomService);
        CustomerTab customerTab = new CustomerTab(customerService);
        BookingTab bookingTab = new BookingTab(bookingService, roomService, customerService, roomTab, customerTab);
        DashboardTab dashboardTab = new DashboardTab(roomService, customerService, bookingService);
        BillingTab billingTab = new BillingTab(bookingService);

        TabPane tabPane = new TabPane();
        if ("ADMIN".equals(role)) {
            tabPane.getTabs().addAll(
                    dashboardTab.buildTab(),
                    roomTab.buildTab(),
                    customerTab.buildTab(),
                    bookingTab.buildTab(),
                    billingTab.buildTab()
            );
        } else {
            tabPane.getTabs().addAll(
                    customerTab.buildTab(),
                    bookingTab.buildTab()
            );
        }
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        MenuBar menuBar = buildMenuBar(primaryStage, roomTab, customerTab, dashboardTab, billingTab);
        HBox statusBar = buildStatusBar();

        VBox root = new VBox(menuBar, tabPane, statusBar);
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        Scene scene = new Scene(root, 980, 700);
        String css = getClass().getResource("/com/hotel/styles.css").toExternalForm();
        scene.getStylesheets().add(css);

        primaryStage.setTitle("Hotel Management System");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        // Auto-save removed to keep static data
        primaryStage.show();

        startClock();
    }

    private MenuBar buildMenuBar(Stage stage, RoomTab roomTab, CustomerTab customerTab, DashboardTab dashboardTab, BillingTab billingTab) {
        MenuBar menuBar = new MenuBar();

        Menu fileMenu = new Menu("File");
        MenuItem saveItem = new MenuItem("Save Data");
        MenuItem loadItem = new MenuItem("Load Data");
        MenuItem switchRoleItem = new MenuItem("Switch Role");
        MenuItem exitItem = new MenuItem("Exit");
        
        saveItem.setOnAction(e -> {
            try {
                bookingService.saveAll("hotel_data.dat");
                setStatus("Data saved to hotel_data.dat.");
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("All data saved to hotel_data.dat.");
                alert.showAndWait();
            } catch (Exception ex) {
                setStatus("Save failed: " + ex.getMessage());
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Failed to save data: " + ex.getMessage());
                alert.showAndWait();
            }
        });

        loadItem.setOnAction(e -> {
            try {
                bookingService.loadAll("hotel_data.dat");
                roomTab.refresh();
                customerTab.refresh();
                dashboardTab.refresh();
                billingTab.refresh();
                setStatus("Data loaded from hotel_data.dat.");
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("All data loaded from hotel_data.dat.");
                alert.showAndWait();
            } catch (Exception ex) {
                setStatus("Load failed: " + ex.getMessage());
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Failed to load data: " + ex.getMessage());
                alert.showAndWait();
            }
        });

        switchRoleItem.setOnAction(e -> {
            com.hotel.Main.showLoginScreen(stage, roomService, customerService, bookingService);
        });
        exitItem.setOnAction(e -> stage.close());
        fileMenu.getItems().addAll(saveItem, loadItem, new SeparatorMenuItem(), switchRoleItem, new SeparatorMenuItem(), exitItem);

        Menu viewMenu = new Menu("View");
        MenuItem refreshItem = new MenuItem("Refresh All");
        refreshItem.setOnAction(e -> { 
            roomTab.refresh(); 
            customerTab.refresh(); 
            dashboardTab.refresh();
            billingTab.refresh();
            setStatus("All data refreshed."); 
        });
        viewMenu.getItems().add(refreshItem);

        Menu helpMenu = new Menu("Help");
        MenuItem aboutItem = new MenuItem("About");
        aboutItem.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("About");
            alert.setHeaderText("Hotel Management System");
            alert.setContentText("Version 1.0\nBuilt with JavaFX 23 + Maven\n\nFeatures:\n- Room Management\n- Customer Management\n- Booking & Checkout\n- Billing\n- Dashboard Analytics");
            alert.showAndWait();
        });
        helpMenu.getItems().add(aboutItem);

        menuBar.getMenus().addAll(fileMenu, viewMenu, helpMenu);
        return menuBar;
    }

    private HBox buildStatusBar() {
        statusLabel = new Label("Ready  |  Hotel Management System v1.0");
        clockLabel = new Label();
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button themeButton = new Button("Dark Theme");
        themeButton.getStyleClass().add("button-theme-toggle");
        themeButton.setOnAction(e -> {
            boolean isDark = themeButton.getText().equals("Light Theme");
            if (isDark) {
                themeButton.getScene().getRoot().getStyleClass().remove("dark-theme");
                themeButton.setText("Dark Theme");
            } else {
                themeButton.getScene().getRoot().getStyleClass().add("dark-theme");
                themeButton.setText("Light Theme");
            }
        });

        HBox bar = new HBox(10, statusLabel, spacer, themeButton, clockLabel);
        bar.getStyleClass().add("status-bar");
        bar.setPadding(new Insets(5, 14, 5, 14));
        return bar;
    }

    private void startClock() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMM yyyy  HH:mm:ss");
        javafx.animation.Timeline clock = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(javafx.util.Duration.seconds(1),
                        e -> clockLabel.setText(LocalDateTime.now().format(fmt)))
        );
        clock.setCycleCount(javafx.animation.Animation.INDEFINITE);
        clock.play();
    }

    public void setStatus(String message) {
        if (statusLabel != null) statusLabel.setText(message);
    }
}
