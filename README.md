# Java Project and Task Management System

A web-based Project and Task Management System developed using Java Servlets, JDBC, MySQL, HTML, CSS, and JavaScript. The application helps users organize projects, manage tasks, and track task completion.

## Features

- Create, view, update, and delete projects.
- Create, view, update, and delete tasks.
- Organize tasks by project.
- Manage task status and priority.
- Store project and task information in a MySQL database.
- Display project progress and completion percentage.
- Use a web-based interface to manage project information.

## Technologies Used

**Backend**
- Java
- Java Servlets
- JDBC
- DAO and Service layers

**Frontend**
- HTML5
- CSS3
- JavaScript

**Database**
- MySQL

**Tools and Server**
- Apache Maven
- Apache Tomcat 11
- Eclipse IDE
- Git and GitHub

## Project Structure

```text
taskmanager/
├── src/
│   └── main/
│       ├── java/com/taskmanager/
│       │   ├── controller/
│       │   ├── dao/
│       │   ├── model/
│       │   ├── service/
│       │   └── util/
│       └── webapp/
│           ├── css/
│           ├── js/
│           └── index.html
├── pom.xml
└── README.md
```

## Prerequisites

Install the following software before running the application:

- JDK compatible with your project configuration
- Apache Maven
- Apache Tomcat 11
- MySQL Server
- Eclipse IDE or another Java IDE

## Setup Instructions

1. Clone or download this repository.
2. Create a MySQL database named `taskmanager_db`.
3. Configure your database connection in `DBConnection.java` or use environment variables if configured.
4. Ensure the MySQL JDBC driver and other required dependencies are configured in `pom.xml`.
5. Import the project into Eclipse as an existing Maven project.
6. Build the project using Maven.
7. Deploy the application to Apache Tomcat 11.
8. Start Tomcat and open the application in your browser.

Example local URL:

```text
http://localhost:8080/taskmanager/
```

## Learning Outcomes

This project demonstrates practical experience with Java web development, database connectivity using JDBC, CRUD operations, layered application architecture, and frontend-to-backend integration.

## Author

Dhilip Kumar

## License

This project is available for learning and portfolio demonstration purposes.
