# 🩸 Blood Bank Management System (Java + MySQL + Swing)

This is a complete desktop-based Blood Bank Management System built using Java (Swing) and MySQL.

## 📌 Features

- Add & manage donors
- Inventory tracking (blood groups, expiry tracking)
- Blood requests and fulfilment
- Donation tracking
- Admin login
- Java Swing GUI
- SQL views and stored procedures
- PDF report generation

## 🛠 Tech Stack

- Java
- Swing (GUI)
- MySQL (Database)
- iText (PDF generation)
- JDBC

## 🖼 GUI Preview

![Dashboard](screenshots/dashboard.png)

## ⚙ How to Run

1. Clone the repo
2. Import MySQL schema (`database.sql`)
3. Compile and run using:
   ```bash
   javac -cp ".;mysql-connector-j-9.4.0.jar" Main.java
   java -cp ".;mysql-connector-j-9.4.0.jar" Main
