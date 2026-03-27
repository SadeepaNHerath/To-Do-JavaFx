# TaskTrack - Advanced JavaFX To-Do Application

TaskTrack is a professional, cloud-enabled To-Do application built using **JavaFX** and **Appwrite Cloud**. It features a modern, mobile-inspired user interface with glassmorphism aesthetics and an asynchronous data-sync engine.

![TaskTrack Preview](https://github.com/SadeepaNHerath/To-Do-JavaFx/raw/main/src/main/resources/img/preview.png)

## 🌟 Features

- **Cloud-Native Architecture**: Powered by Appwrite TablesDB for real-time data sync across devices.
- **Auto-Provisioning**: Automatically creates your database tables and columns in the cloud on first run.
- **Premium Glassmorphism UI**: High-fidelity design with vibrant gradients, soft shadows, and a compact 420x720 layout.
- **Asynchronous Execution**: Deeply optimized with thread-safe network calls to ensure a lag-free experience.
- **Full Task Lifecycle**: Advanced Create, List, Mark-as-Done, and Delete functionality.
- **History Tracking**: Dedicated history view with date-based search for tracking your productivity.
- **Secure Configuration**: Cloud credentials managed via `.env` for security and deployment flexibility.

## 🛠️ Technology Stack

- **Java 21 / JavaFX 19**: Core application framework.
- **Appwrite Cloud**: Cloud database (Tables, Rows, Columns).
- **OkHttp 4.12.0**: High-performance HTTP client for REST API interactions.
- **Gson 2.11.0**: JSON parsing and serialization.
- **JFoenix**: Material design components for JavaFX.
- **Dotenv-Java**: Environment variable management.
- **Maven**: Build and dependency management.

## 🚀 Getting Started

### Prerequisites

1.  **JDK 21**: Ensure you have Java 21 installed.
2.  **Appwrite Project**:
    - Sign up at [cloud.appwrite.io](https://cloud.appwrite.io).
    - Create a new project and note your **Project ID**.
    - Create an **API Key** with `databases.write` and `collections.read/write` scopes.
    - Create a **Database** and note its ID.

### Configuration

1.  **Environment Setup**:
    - Duplicate `.env.example` and rename it to `.env`.
    - Fill in your Appwrite credentials:
    ```bash
    APPWRITE_ENDPOINT=https://cloud.appwrite.io/v1
    APPWRITE_PROJECT_ID=your_project_id
    APPWRITE_API_KEY=your_api_key
    APPWRITE_DATABASE_ID=todo_db
    APPWRITE_TABLE_ID=tasks_table
    ```

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
    *The application will automatically create the table and columns in your Appwrite project if they don't exist!*

## 📁 Project Structure

- `edu.iCET.Main`: The application entry point (wrapper).
- `edu.iCET.Starter`: The main JavaFX application class.
- `edu.iCET.controller`: UI logic and event handling.
- `edu.iCET.service`: Cloud data logic and Appwrite API integration.
- `edu.iCET.db`: Centralized Appwrite client and endpoint management.
- `edu.iCET.model`: PoJo data models.
- `edu.iCET.util`: Utility classes (EnvLoader).

## 📄 License

This project is open-source and available under the MIT License.
