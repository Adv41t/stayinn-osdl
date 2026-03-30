package com.hotel.ui;

import com.hotel.model.Booking;
import com.hotel.model.RoomType;
import com.hotel.service.BookingService;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

public class BillingTab {

    private final BookingService bookingService;

    public BillingTab(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    private Tab tab;

    public Tab buildTab() {
        tab = new Tab("Billing");
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

        VBox content = buildContent();
        content.setPadding(new Insets(20));
        content.setSpacing(20);

        scroll.setContent(content);
        tab.setContent(scroll);
    }

    private VBox buildContent() {
        VBox root = new VBox(20);

        // ── Revenue Summary Cards ──
        root.getChildren().add(sectionLabel("Revenue Summary"));
        root.getChildren().add(buildSummaryCards());

        // ── Revenue by Room Type ──
        root.getChildren().add(sectionLabel("Revenue by Room Type"));
        root.getChildren().add(buildRevenueByTypeSection());

        // ── Monthly Revenue Chart ──
        root.getChildren().add(sectionLabel("Monthly Revenue"));
        root.getChildren().add(buildMonthlyChart());

        // ── Booking Revenue Table ──
        root.getChildren().add(sectionLabel("All Bookings Revenue Breakdown"));
        root.getChildren().add(buildRevenueTable());

        return root;
    }

    // ── Summary Cards ────────────────────────────────────────────────────────

    private HBox buildSummaryCards() {
        List<Booking> all = bookingService.getAllBookings();

        double totalRevenue = all.stream().mapToDouble(Booking::getTotalCost).sum();

        double todayRevenue = all.stream()
                .filter(b -> b.getCheckInDate().equals(LocalDate.now()))
                .mapToDouble(Booking::getTotalCost).sum();

        double monthRevenue = all.stream()
                .filter(b -> b.getCheckInDate().getMonth() == LocalDate.now().getMonth()
                        && b.getCheckInDate().getYear() == LocalDate.now().getYear())
                .mapToDouble(Booking::getTotalCost).sum();

        long totalBookings = all.size();

        double avgPerBooking = totalBookings == 0 ? 0 : totalRevenue / totalBookings;

        HBox row = new HBox(14);
        row.getChildren().addAll(
                statCard("Total Revenue",    String.format("₹%.2f", totalRevenue),    "stat-card-blue"),
                statCard("This Month",       String.format("₹%.2f", monthRevenue),    "stat-card-green"),
                statCard("Today",            String.format("₹%.2f", todayRevenue),    "stat-card-orange"),
                statCard("Total Bookings",   String.valueOf(totalBookings),            "stat-card-red"),
                statCard("Avg per Booking",  String.format("₹%.2f", avgPerBooking),   "stat-card-blue")
        );
        row.getChildren().forEach(n -> HBox.setHgrow(n, Priority.ALWAYS));
        return row;
    }

    // ── Revenue by Room Type ─────────────────────────────────────────────────

    private HBox buildRevenueByTypeSection() {
        List<Booking> all = bookingService.getAllBookings();

        Map<RoomType, Double> revenueByType = new LinkedHashMap<>();
        for (RoomType rt : RoomType.values()) revenueByType.put(rt, 0.0);
        for (Booking b : all) {
            revenueByType.merge(b.getRoomType(), b.getTotalCost(), Double::sum);
        }

        double total = revenueByType.values().stream().mapToDouble(Double::doubleValue).sum();

        // Left: breakdown cards
        VBox cards = new VBox(12);
        cards.setPrefWidth(320);

        String[] colors = {"#3949ab", "#43a047", "#fb8c00"};
        int i = 0;
        for (Map.Entry<RoomType, Double> entry : revenueByType.entrySet()) {
            cards.getChildren().add(buildTypeCard(entry.getKey(), entry.getValue(), total, colors[i % colors.length]));
            i++;
        }

        // Right: horizontal bar chart
        VBox chartBox = new VBox(10);
        chartBox.getStyleClass().add("section-card");
        chartBox.setPadding(new Insets(16));

        Label chartTitle = new Label("Revenue Share by Room Type");
        chartTitle.getStyleClass().add("label-header");

        CategoryAxis yAxis = new CategoryAxis();
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Revenue (₹)");

        BarChart<Number, String> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setLegendVisible(false);
        barChart.setPrefHeight(180);
        barChart.setBarGap(4);
        barChart.setCategoryGap(20);

        XYChart.Series<Number, String> series = new XYChart.Series<>();
        for (Map.Entry<RoomType, Double> entry : revenueByType.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getValue(), entry.getKey().name()));
        }
        barChart.getData().add(series);
        VBox.setVgrow(barChart, Priority.ALWAYS);

        chartBox.getChildren().addAll(chartTitle, barChart);
        HBox.setHgrow(chartBox, Priority.ALWAYS);

        HBox row = new HBox(16, cards, chartBox);
        return row;
    }

    private HBox buildTypeCard(RoomType type, double revenue, double total, String color) {
        HBox card = new HBox(14);
        card.getStyleClass().add("section-card");
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(14));

        // Color bar on left
        VBox colorBar = new VBox();
        colorBar.setMinWidth(6);
        colorBar.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 3px;");

        VBox info = new VBox(4);
        Label nameLabel = new Label(type.name());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #1a237e;");

        Label revenueLabel = new Label(String.format("₹%.2f", revenue));
        revenueLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");

        double pct = total == 0 ? 0 : (revenue / total) * 100;
        Label pctLabel = new Label(String.format("%.1f%% of total revenue", pct));
        pctLabel.getStyleClass().add("label-muted");

        // Progress bar
        ProgressBar bar = new ProgressBar(total == 0 ? 0 : revenue / total);
        bar.setPrefWidth(200);
        bar.setStyle("-fx-accent: " + color + ";");

        info.getChildren().addAll(nameLabel, revenueLabel, pctLabel, bar);
        card.getChildren().addAll(colorBar, info);
        HBox.setHgrow(info, Priority.ALWAYS);
        return card;
    }

    // ── Monthly Revenue Chart ────────────────────────────────────────────────

    private VBox buildMonthlyChart() {
        VBox box = new VBox(10);
        box.getStyleClass().add("section-card");

        // Aggregate by month
        Map<String, Double> monthlyRevenue = new LinkedHashMap<>();
        for (Month m : Month.values()) {
            monthlyRevenue.put(m.getDisplayName(TextStyle.SHORT, Locale.ENGLISH), 0.0);
        }

        for (Booking b : bookingService.getAllBookings()) {
            String month = b.getCheckInDate().getMonth()
                    .getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            monthlyRevenue.merge(month, b.getTotalCost(), Double::sum);
        }

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setCategories(FXCollections.observableArrayList(monthlyRevenue.keySet()));
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Revenue (₹)");

        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setLegendVisible(false);
        chart.setPrefHeight(260);
        chart.setBarGap(2);
        chart.setCategoryGap(12);
        chart.setTitle(null);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Revenue");
        for (Map.Entry<String, Double> entry : monthlyRevenue.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }
        chart.getData().add(series);

        box.getChildren().add(chart);
        return box;
    }

    // ── Daily Revenue Chart ──────────────────────────────────────────────────

    private VBox buildDailyChart() {
        VBox box = new VBox(10);
        box.getStyleClass().add("section-card");

        // Last 14 days
        Map<String, Double> dailyRevenue = new LinkedHashMap<>();
        LocalDate today = LocalDate.now();
        for (int i = 13; i >= 0; i--) {
            LocalDate d = today.minusDays(i);
            dailyRevenue.put(d.toString(), 0.0);
        }

        for (Booking b : bookingService.getAllBookings()) {
            String key = b.getCheckInDate().toString();
            if (dailyRevenue.containsKey(key)) {
                dailyRevenue.merge(key, b.getTotalCost(), Double::sum);
            }
        }

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setCategories(FXCollections.observableArrayList(dailyRevenue.keySet()));
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Revenue (₹)");

        LineChart<String, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setLegendVisible(false);
        chart.setPrefHeight(240);
        chart.setCreateSymbols(true);
        chart.setAnimated(false);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Daily Revenue");
        for (Map.Entry<String, Double> entry : dailyRevenue.entrySet()) {
            // Shorten date label to MM-dd
            String shortDate = entry.getKey().substring(5);
            series.getData().add(new XYChart.Data<>(shortDate, entry.getValue()));
        }
        chart.getData().add(series);

        box.getChildren().add(chart);
        return box;
    }

    // ── Revenue Table ────────────────────────────────────────────────────────

    private VBox buildRevenueTable() {
        VBox box = new VBox(8);
        box.getStyleClass().add("section-card");

        TableView<Booking> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(260);
        table.setItems(FXCollections.observableArrayList(bookingService.getAllBookings()));

        TableColumn<Booking, String> colID = col("Booking ID", "bookingID");
        TableColumn<Booking, String> colCust = col("Customer", "customerName");
        TableColumn<Booking, Integer> colRoom = col("Room #", "roomNumber");
        TableColumn<Booking, String> colType = col("Type", "roomType");
        TableColumn<Booking, String> colIn = col("Check-in", "checkInDate");
        TableColumn<Booking, String> colOut = col("Check-out", "checkOutDate");

        TableColumn<Booking, Double> colNights = new TableColumn<>("Nights");
        colNights.setCellValueFactory(c ->
                new javafx.beans.property.SimpleObjectProperty<>((double) c.getValue().getNights()));
        colNights.setCellFactory(tc -> new TableCell<>() {
            @Override protected void updateItem(Double v, boolean empty) {
                super.updateItem(v, empty);
                setText(empty || v == null ? null : String.valueOf(v.intValue()));
            }
        });

        TableColumn<Booking, Double> colCost = new TableColumn<>("Total Cost");
        colCost.setCellValueFactory(c ->
                new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getTotalCost()));
        colCost.setCellFactory(tc -> new TableCell<>() {
            @Override protected void updateItem(Double v, boolean empty) {
                super.updateItem(v, empty);
                if (empty || v == null) { setText(null); setStyle(""); return; }
                setText(String.format("₹%.2f", v));
                setStyle("-fx-font-weight: bold; -fx-text-fill: #2e7d32;");
            }
        });

        table.getColumns().addAll(colID, colCust, colRoom, colType, colIn, colOut, colNights, colCost);

        // Totals footer
        double grandTotal = bookingService.getAllBookings().stream()
                .mapToDouble(Booking::getTotalCost).sum();
        Label footer = new Label(String.format("Grand Total:  ₹%.2f   |   %d bookings",
                grandTotal, bookingService.getAllBookings().size()));
        footer.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #1a237e;");
        footer.setPadding(new Insets(8, 0, 0, 4));

        box.getChildren().addAll(table, footer);
        return box;
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    private <S, T> TableColumn<S, T> col(String title, String property) {
        TableColumn<S, T> col = new TableColumn<>(title);
        col.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>(property));
        return col;
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

    private Label sectionLabel(String text) {
        Label lbl = new Label(text);
        lbl.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #1a237e;");
        return lbl;
    }
}
