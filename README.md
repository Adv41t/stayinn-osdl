# StayInn Hotel Management System

StayInn is a comprehensive JavaFX-based desktop application designed to streamline hotel operations. Built with Java 21 and Maven as the final project for Object Oriented and Software Development Learning Lab (IV SEM), it offers an intuitive interface for both administrators and customers to manage rooms, bookings, and billing efficiently.

## 🚀 Features

* **Role-based Access Control**: Distinct login portals for Administrators and Customers, providing tailored views and permissions.
* **Dashboard Analytics**: Real-time overview of hotel status, including occupied rooms, available rooms, total revenue, and recent activities.
* **Room Management**: 
  * View, add, edit, and delete rooms.
  * Support for multiple room types: Standard, Deluxe, and Suite.
  * Real-time filtering by room type and availability status.
* **Customer Management**: Maintain a registry of hotel guests with their contact details.
* **Booking System**: 
  * Easy reservation process linking customers to available rooms.
  * Date picker integration for check-in and check-out dates.
* **Billing System**: Automated invoice generation for bookings, including room charges and support for Indian Rupee (₹) currency formatting.
* **Data Persistence**: Robust local storage utilizing serialized data files (`hotel_data.dat`), ensuring no data loss between sessions.
* **Theming**: Dynamic Light and Dark mode options for personalized user experience.

## 📂 Project Structure

The project follows a standard Maven directory layout:

```text
hotel/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/hotel/
│       │       ├── Main.java              # Application entry point & Role Selection
│       │       ├── model/                 # Data entities (Room, Customer, Booking, etc.)
│       │       ├── service/               # Business logic & Data persistence
│       │       └── ui/                    # JavaFX UI Components (Tabs, Dashboard)
│       └── resources/
│           └── com/hotel/
│               └── styles.css             # Application styling and themes
├── pom.xml                                # Maven build configuration
└── hotel_data.dat                         # Serialized application data
```

## 🛠️ Technologies Used

* **Language**: Java 21
* **UI Framework**: JavaFX 23
* **Build Tool**: Apache Maven
* **Data Storage**: Java Object Serialization (Binary Data Persistence)

## 🏃‍♂️ Getting Started

### Prerequisites
* Java Development Kit (JDK) 21 or higher
* Apache Maven installed

### Running the Application

1. Clone or download the repository.
2. Navigate to the project root directory (`hotel/`).
3. Build and run the project using the JavaFX Maven Plugin:
   
   ```bash
   mvn clean javafx:run
   ```

*(Alternatively, you can run the `com.hotel.Main` class directly from your preferred Java IDE after importing the project as a Maven project).*

## 📖 Usage

Upon launching the application, you will be greeted by the Role Selection screen:
1. **Admin Login**: Access to the full suite of management tools (Dashboard, Rooms, Customers, Bookings, Billing).
2. **Customer Login**: Restricted view tailored for guests.

*(Note: Test data is automatically seeded on the first run if no existing data file is found).*
