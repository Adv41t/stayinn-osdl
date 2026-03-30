package com.hotel;

import com.hotel.service.BookingService;
import com.hotel.service.CustomerService;
import com.hotel.service.RoomService;
import com.hotel.ui.MainUI;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        RoomService roomService = new RoomService();
        CustomerService customerService = new CustomerService();
        BookingService bookingService = new BookingService(roomService, customerService);

        try {
            java.io.File dataFile = new java.io.File("hotel_data.dat");
            if (!dataFile.exists()) {
                // Seed static data
                customerService.addCustomer(new com.hotel.model.Customer("C001", "Alice Smith", "555-0101"));
                customerService.addCustomer(new com.hotel.model.Customer("C002", "Bob Smith", "555-0102"));
                customerService.addCustomer(new com.hotel.model.Customer("C003", "Charlie Brown", "555-0103"));
                
                // Add a sample booking
                bookingService.bookRoom("C001", 101, java.time.LocalDate.now().minusDays(1), java.time.LocalDate.now().plusDays(2));
                
                bookingService.saveAll("hotel_data.dat");
                System.out.println("Generated static hotel_data.dat");
            }
            
            bookingService.loadAll("hotel_data.dat");
            System.out.println("Data loaded from hotel_data.dat");
        } catch (Exception e) {
            System.out.println("Failed to setup static data.");
            e.printStackTrace();
        }

        showLoginScreen(primaryStage, roomService, customerService, bookingService);
    }

    public static void showLoginScreen(Stage primaryStage, RoomService roomService, CustomerService customerService, BookingService bookingService) {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        
        Label title = new Label("StayInn Hotel Management System");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        
        Label subtitle = new Label("Select Login Role");
        subtitle.setStyle("-fx-font-size: 16px; -fx-text-fill: #666666;");
        
        Button adminBtn = new Button("Admin Login");
        adminBtn.setPrefWidth(200);
        adminBtn.setStyle("-fx-font-size: 16px; -fx-padding: 10 20;");
        adminBtn.setOnAction(e -> {
            MainUI mainUI = new MainUI(roomService, customerService, bookingService, "ADMIN");
            mainUI.buildAndShow(primaryStage);
        });
        
        Button customerBtn = new Button("Customer Login");
        customerBtn.setPrefWidth(200);
        customerBtn.setStyle("-fx-font-size: 16px; -fx-padding: 10 20;");
        customerBtn.setOnAction(e -> {
            MainUI mainUI = new MainUI(roomService, customerService, bookingService, "CUSTOMER");
            mainUI.buildAndShow(primaryStage);
        });
        
        root.getChildren().addAll(title, subtitle, adminBtn, customerBtn);
        
        Scene scene = new Scene(root, 500, 400);
        String css = Main.class.getResource("/com/hotel/styles.css").toExternalForm();
        scene.getStylesheets().add(css);
        
        primaryStage.setTitle("Hotel Management System - Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
