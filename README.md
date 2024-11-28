# Automotive Task Management System

This is a task management system for an automotive factory, developed with **Spring Boot** for the backend and **HTML/CSS/JS** for the frontend. The system supports three roles: **Admin**, **Staff**, and **Team Leader**.

## Prerequisites

Before you begin, make sure you have the following software installed:

1. **[JDK 11 or higher](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html)**
2. **[Maven](https://maven.apache.org/download.cgi)** (for building and managing dependencies)
3. **[MySQL](https://dev.mysql.com/downloads/)** or another relational database (PostgreSQL, etc.)
4. **[IntelliJ IDEA](https://www.jetbrains.com/idea/download/)** or any Java IDE for development
5. **[Git](https://git-scm.com/downloads)** for version control

## Getting Started

Follow these steps to set up and run the project on your local machine.

### 1. Install JDK (Java Development Kit)

To run a Java-based application, you'll need to install JDK. For this project, **JDK 11** or higher is recommended.

- **Download JDK 11** from [Oracle JDK](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html) or use an open-source variant like **OpenJDK**.
- After installation, verify that Java is installed by running the following command in your terminal/command prompt:

```bash
java -version
```

### 2. Install Maven

Maven is used for building and managing dependencies in Java projects.

- **Download Maven** from [Maven's official website](https://maven.apache.org/download.cgi).
- After downloading, follow the installation instructions based on your operating system from the [Maven Installation Guide](https://maven.apache.org/install.html).
- To verify that Maven is installed, run the following command in your terminal/command prompt:

```bash
mvn -version
```
### 3. Install MySQL

MySQL is the relational database used for this project. You can also use other databases like PostgreSQL.

- **Download MySQL** from [MySQL's official website](https://dev.mysql.com/downloads/).
- Follow the installation instructions for your operating system.
- After installation, log into MySQL and create a database for the project:

```sql
CREATE DATABASE automotive_task_management;
```

### 4. Install IntelliJ IDEA

IntelliJ IDEA is a popular IDE for Java development.

- Download and install the **Community** or **Ultimate** edition from [IntelliJ's website](https://www.jetbrains.com/idea/download/).
- After installation, open IntelliJ IDEA and set up your development environment by importing your project or creating a new one.


### 5. Install Git

Git is used for version control.

- **Download Git** from [Git's official website](https://git-scm.com/downloads).
- Follow the installation instructions for your operating system.
- After installation, verify that Git is installed by running the following command in your terminal/command prompt:

```bash
git --version
```

### 6. Clone the Repository

Start by cloning the repository to your local machine:

```bash
git clone https://github.com/trannhutdong15/Task-Mangement-Web.git
```

### 7. Set up the Backend (Spring Boot)

#### a. Configure Database

Ensure you have a running instance of MySQL (or your preferred database like PostgreSQL).

- Create a new database for the application (e.g., `automotive_task_management`).
- Update the database configuration in the `src/main/resources/application.properties` or `application.yml` file:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/automotive_task_management
spring.datasource.username=your-username
spring.datasource.password=your-password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```
### Enjoy Using the Web!

I hope you enjoy using the **Automotive Task Management System**.This is my first Spring Project and i create my own this is my own work so feel free to tell me does the instruction good or not if you have any questions or run into any issues, feel free to reach out via email at:

[nhutdong9a3@gmail.com](mailto:nhutdong9a3@gmail.com)



