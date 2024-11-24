# PDF Merge

## Overview

PDF Merge is a simple, efficient tool to merge multiple PDF files into a single PDF. It's designed to be user-friendly
and quick, making the process of combining PDFs straightforward and hassle-free.

## Table of Contents

- [Features](#features)
- [Installation](#installation)
- [License](#license)

## Features

- Merge multiple PDF files into one
- Cross-platform compatibility

## Installation

### Prerequisites

Make sure you have the following tools and libraries installed:

- Java (JDK 23 or later)
- Maven

### Steps

1. Clone the repository:
    ```bash
    git clone https://github.com/ahjaworski/pdf-merge.git
    cd pdf-merge
    ```

2. Build the project using Maven:
    ```bash
    mvn clean install
    ```

## Running with Docker Compose

To run this project using Docker Compose, follow these steps after ensuring you have completed the installation steps:

1. Build the project using Maven:

   ```bash
   mvn clean install
   ```

2. Start the application using Docker Compose:

   ```bash
   docker-compose up -d
   ```

3. Access the application by navigating to [http://localhost:8080](http://localhost:8080) in your web browser.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
