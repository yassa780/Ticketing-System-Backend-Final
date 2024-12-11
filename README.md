#Real Time Ticketing Event System 

OVERVIEW
The Real-Time Event Ticketing System is a Spring Boot-based backend application designed to simulate a concurrent ticketing system. 
It uses a multi-threaded approach to manage the addition of tickets by vendors and the purchase of tickets by customers, 
interacting with a ticket pool. The system is exposed via RESTful APIs for configuration, control, and log management.

KEY FEATURES
Dynamic System Configuration:
 - Configure ticket pool capacity, number of vendors, and customers dynamically.
   
Multi-Threaded Backend:
 - Vendors and customers operate concurrently in the backend using Java threads.

RESTful API Endpoints:
 - Manage the system configuration.
 - Start and stop the system.
 - Retrieve and clear logs.

In-Memory Database:
 - Uses H2 for lightweight and fast database operations during development.


Backend: Spring Boot
Threading: Java threads for vendor and customer operations

GETTING STARTED
Java 17 or higher
Maven
H2 Database

TECHNOLOGIES USED
Backend: Spring Boot
Threading: Java threads for vendor and customer operations
Database: H2 (in-memory)

INSTALLATION




