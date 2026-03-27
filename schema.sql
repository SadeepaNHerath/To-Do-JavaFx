-- Database Schema for TaskTrack

CREATE DATABASE IF NOT EXISTS tasktrack;

USE tasktrack;

CREATE TABLE IF NOT EXISTS Tasks (
    task_id INT AUTO_INCREMENT PRIMARY KEY,
    task_title VARCHAR(255) NOT NULL,
    task_status VARCHAR(50) DEFAULT 'Active',
    completion_date DATE
);
