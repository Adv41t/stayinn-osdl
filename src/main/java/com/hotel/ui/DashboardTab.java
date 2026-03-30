package com.hotel.ui;

import com.hotel.model.Booking;
import com.hotel.model.Room;
import com.hotel.model.RoomType;
import com.hotel.service.BookingService;
import com.hotel.service.CustomerService;
import com.hotel.service.RoomService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.List;

public class DashboardTab {

    private final RoomService roomService;
    private final CustomerService customerService;
    private final BookingService bookingService;

    public DashboardTab(RoomService roomService, CustomerService customerService, BookingService bookingService) {
        this.roomService = roomService;
        this.customerService = customerService;
        this.bookingService = bookingService;
    }

    private Tab tab;

    public Tab buildTab() {
        tab = new Tab("Dashboard");
        tab.setClosable(false);
        tab.setOnSelectionChanged(e -> {
            if (tab.isSelected()) {
                refresh();
            }
        });
        refresh();
        return tab;
    }

    public void refresh() {
        if (tab == null) return;

        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("scroll-pane");

        VBox content = new VBox(20);
        content.setPadding(new Insets(20));

        // ── Stat Cards ──
        HBox statCards = buildStatCards();

        // ── Charts row ──
        HBox chartsRow = new HBox(16);
        chartsRow.getChildren().addAll(buildRoomTypeChart(), buildOccupancyChart());
        HBox.setHgrow(chartsRow.getChildren().get(0), Priority.ALWAYS);
        HBox.setHgrow(chartsRow.getChildren().get(1), Priority.ALWAYS);

        // ── Recent Bookings ──
        VBox recentBox = buildRecentBookings();

        content.getChildren().addAll(
                sectionLabel("Overview"),
                statCards,
                sectionLabel("Room Analytics"),
                chartsRow,
                sectionLabel("Recent Bookings"),
                recentBox
        );

        scroll.setContent(content);
        tab.setContent(scroll);
    }

    private HBox buildStatCards() {
        int totalRooms = roomService.getAllRooms().size();
        int availableRooms = roomService.getAvailableRooms().size();
        int occupiedRooms = totalRooms - availableRooms;
        int totalCustomers = customerService.getAllCustomers().size();
        int totalBookings = bookingService.getAllBookings().size();
        double totalRevenue = bookingService.getAllBookings().stream().mapToDouble(Booking::getTotalCost).sum();

        HBox row = new HBox(14);
        row.getChildren().addAll(
                statCard("Total Rooms", String.valueOf(totalRooms), "stat-card-blue"),
                statCard("Available", String.valueOf(availableRooms), "stat-card-green"),
                statCard("Occupied", String.valueOf(occupiedRooms), "stat-card-orange"),
                statCard("Customers", String.valueOf(totalCustomers), "stat-card-red"),
                statCard("Bookings", String.valueOf(totalBookings), "stat-card-blue"),
                statCard("Revenue", String.format("₹%.0f", totalRevenue), "stat-card-green")
        );
        row.getChildren().forEach(n -> HBox.setHgrow(n, Priority.ALWAYS));
        return row;
    }

    private VBox statCard(String labelText, String valueText, String colorStyle) {
        VBox card = new VBox(6);
        card.getStyleClass().addAll("stat-card", colorStyle);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setMinWidth(100);

        Label value = new Label(valueText);
        value.getStyleClass().add("stat-value");

        Label label = new Label(labelText);
        label.getStyleClass().add("stat-label");

        card.getChildren().addAll(value, label);
        return card;
    }

    private VBox buildRoomTypeChart() {
        VBox box = new VBox(10);
        box.getStyleClass().add("section-card");

        Label title = new Label("Rooms by Type");
        title.getStyleClass().add("label-header");

        PieChart chart = new PieChart();
        chart.setLegendVisible(true);
        chart.setLabelsVisible(true);
        chart.setPrefHeight(260);

        long standard = roomService.getAllRooms().stream().filter(r -> r.getRoomType() == RoomType.STANDARD).count();
        long deluxe = roomService.getAllRooms().stream().filter(r -> r.getRoomType() == RoomType.DELUXE).count();
        long suite = roomService.getAllRooms().stream().filter(r -> r.getRoomType() == RoomType.SUITE).count();

        if (standard > 0) chart.getData().add(new PieChart.Data("Standard (" + standard + ")", standard));
        if (deluxe > 0)   chart.getData().add(new PieChart.Data("Deluxe (" + deluxe + ")", deluxe));
        if (suite > 0)    chart.getData().add(new PieChart.Data("Suite (" + suite + ")", suite));

        box.getChildren().addAll(title, chart);
        VBox.setVgrow(chart, Priority.ALWAYS);
        return box;
    }

    private VBox buildOccupancyChart() {
        VBox box = new VBox(10);
        box.getStyleClass().add("section-card");

        Label title = new Label("Occupancy Status");
        title.getStyleClass().add("label-header");

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Rooms");

        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setLegendVisible(false);
        chart.setPrefHeight(260);
        chart.setBarGap(4);
        chart.setCategoryGap(30);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (RoomType type : RoomType.values()) {
            List<Room> ofType = roomService.getAllRooms().stream()
                    .filter(r -> r.getRoomType() == type).toList();
            long occupied = ofType.stream().filter(r -> !r.isAvailable()).count();
            long available = ofType.stream().filter(Room::isAvailable).count();
            series.getData().add(new XYChart.Data<>(type.name(), ofType.size()));
        }
        chart.getData().add(series);

        box.getChildren().addAll(title, chart);
        VBox.setVgrow(chart, Priority.ALWAYS);
        return box;
    }

    private VBox buildRecentBookings() {
        VBox box = new VBox(10);
        box.getStyleClass().add("section-card");

        List<Booking> bookings = bookingService.getAllBookings();

        if (bookings.isEmpty()) {
            Label none = new Label("No bookings yet.");
            none.getStyleClass().add("label-muted");
            box.getChildren().add(none);
            return box;
        }

        // Header row
        HBox header = tableRow("Booking ID", "Customer", "Room", "Check-in", "Check-out", "Total", true);
        box.getChildren().add(header);
        box.getChildren().add(new Separator());

        int start = Math.max(0, bookings.size() - 5);
        for (int i = bookings.size() - 1; i >= start; i--) {
            Booking b = bookings.get(i);
            HBox row = tableRow(
                    b.getBookingID(),
                    b.getCustomerName(),
                    String.valueOf(b.getRoomNumber()),
                    b.getCheckInDate().toString(),
                    b.getCheckOutDate().toString(),
                    String.format("₹%.2f", b.getTotalCost()),
                    false
            );
            box.getChildren().add(row);
        }
        return box;
    }

    private HBox tableRow(String... cols) {
        return tableRow(cols[0], cols[1], cols[2], cols[3], cols[4], cols[5], false);
    }

    private HBox tableRow(String c1, String c2, String c3, String c4, String c5, String c6, boolean isHeader) {
        HBox row = new HBox();
        row.setPadding(new Insets(6, 0, 6, 0));
        String[] vals = {c1, c2, c3, c4, c5, c6};
        double[] widths = {150, 140, 60, 110, 110, 90};
        for (int i = 0; i < vals.length; i++) {
            Label lbl = new Label(vals[i]);
            lbl.setMinWidth(widths[i]);
            lbl.setPrefWidth(widths[i]);
            if (isHeader) {
                lbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #1a237e;");
            } else {
                lbl.setStyle("-fx-text-fill: #37474f;");
            }
            row.getChildren().add(lbl);
        }
        return row;
    }

    private Label sectionLabel(String text) {
        Label lbl = new Label(text);
        lbl.getStyleClass().add("label-header");
        lbl.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #1a237e;");
        return lbl;
    }


}
