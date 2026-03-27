# TaskTrack - JavaFX To-Do Application

TaskTrack is a professional, desktop-based To-Do application built using JavaFX and MySQL. It features a clean user interface powered by the JFoenix material design library and implements a robust service-oriented architecture.

![TaskTrack Logo](src/main/resources/img/logo.png)

## Features

- **Custom Window Controls**: Functional Close and Minimize buttons on a custom title bar.
- **Premium Dark Theme**: Modern aesthetics with a deep charcoal background and vibrant blue accents.
- **Task Management**: Add new tasks with ease.
- **Status Tracking**: Move tasks from "To-Do" to "Done" with a single click.
- **History View**: Review all completed tasks in a detailed table.
- **Advanced Search**: Filter completed tasks by date to find specific history.
- **Secure Configuration**: Database credentials are managed via environment variables (`.env`).
- **Clean Architecture**: Separation of concerns between UI (Controllers) and Logic (Services).

## Technology Stack

- **Java 11+**
- **JavaFX 19** (Controls & FXML)
- **JFoenix** (Modern UI components)
- **MySQL** (Database)
- **Lombok** (Boilerplate reduction)
- **Dotenv-Java** (Environment variable management)
- **Maven** (Project management and build)

## Getting Started

### Prerequisites

1.  **MySQL Database**: Ensure a MySQL server is running on your machine.
2.  **Database Setup**:
    - You can use the provided [schema.sql](file:///e:/Projects/To-Do-JavaFx/schema.sql) file to set up your database.
    - Alternatively, run the following SQL:
    ```sql
    CREATE DATABASE tasktrack;
    USE tasktrack;

    CREATE TABLE Tasks (
        task_id INT AUTO_INCREMENT PRIMARY KEY,
        task_title VARCHAR(255) NOT NULL,
        task_status VARCHAR(50) NOT NULL,
        completion_date DATE
    );
    ```

### Configuration

1.  Duplicate the `.env.example` file in the root directory and rename it to `.env`.
2.  Update the `DB_USER` and `DB_PASSWORD` in the `.env` file with your local MySQL credentials.

### Installation & Execution

1.  Clone the repository:
    ```bash
    git clone https://github.com/SadeepaNHerath/To-Do-JavaFx.git
    cd To-Do-JavaFx
    ```
2.  Build and Run:
    ```bash
    mvn javafx:run
    ```
    *Alternatively, you can run the `edu.iCET.Main` class from your preferred IDE.*

## Project Structure

- `edu.iCET.Main`: The application entry point (wrapper).
- `edu.iCET.Starter`: The main JavaFX application class.
- `edu.iCET.controller`: Contains UI logic for different forms.
- `edu.iCET.service`: Encapsulates business logic and database interactions.
- `edu.iCET.db`: Manages the database connection.
- `edu.iCET.model`: Contains data models like `Task`.
- `edu.iCET.util`: Utility classes like `EnvLoader`.

## License

This project is open-source and available under the MIT License.
