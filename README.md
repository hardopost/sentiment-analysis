
**System Preconditions for Running the Full Application (Backend + Frontend)**

This project consists of two main parts:

1. A backend built with **Java Spring Boot**
2. A frontend built with **Vue 3 + Vite + TypeScript**
3. A PostgreSQL 16.4 database with **PGVector** extension running in Docker

The following tools must be installed on the machine **before running the application**.

---

### General Requirements (One-time setup)

* **Docker Desktop**
  Required to run PostgreSQL with PGVector inside a container.
  Download: [https://www.docker.com/products/docker-desktop/](https://www.docker.com/products/docker-desktop/)

* **Git**
  Required to clone the GitHub repository.
  Download: [https://git-scm.com/](https://git-scm.com/)

* **Clone the Repository**
  Once Git is installed, run the following command in your terminal or PowerShell:

  ```
  git clone https://github.com/hardopost/sentiment-analysis.git
  ```

This will download the full project to your local machine.

---

### Backend Requirements (Spring Boot Application)

1. **Java Development Kit (JDK) 21 or newer**

   * Required to run the backend
   * Verify: `java -version`
   * Download: [https://adoptium.net/](https://adoptium.net/)

2. **Maven 3.8+** (or use included wrapper `mvnw`)

   * Builds and runs the backend
   * Verify: `mvn -version`
   * Download: [https://maven.apache.org/download.cgi](https://maven.apache.org/download.cgi)

3. **Google Cloud CLI (`gcloud`)**

   * Required if using Vertex AI (Gemini) for LLM features
   * Install: [https://cloud.google.com/sdk/docs/install](https://cloud.google.com/sdk/docs/install)
   * Authenticate and set project:

     ```
     gcloud auth login
     gcloud config set project [YOUR_PROJECT_ID]
     ```

4. **Chrome Browser + ChromeDriver**

   * Used for Selenium scraping functionality
   * Chrome: [https://www.google.com/chrome/](https://www.google.com/chrome/)
   * ChromeDriver is auto-managed via WebDriverManager

---

### Frontend Requirements (Vue 3 + Vite App)

1. **Node.js (version 18+ recommended)**

   * Required for running and building the frontend
   * Verify: `node -v`
   * Download: [https://nodejs.org/](https://nodejs.org/)

2. **npm** (comes with Node.js)

   * Used to install frontend packages
   * Verify: `npm -v`

---

### Optional Tools

* **pgAdmin** ([https://www.pgadmin.org/](https://www.pgadmin.org/)) – optional GUI to browse PostgreSQL
* **IntelliJ IDEA / VS Code** – recommended IDE for development

---

**PostgreSQL + PGVector Setup (Reusing Existing Data Volume)**

This guide explains how to run PostgreSQL 16.4 with the PGVector extension using Docker, reusing an existing data volume.

Download the prebuilt database volume and extract the zip file:
https://drive.google.com/file/d/1s_UrYJYiwgK5BZNktKFXomXTlCL0kP00/view?usp=sharing

It assumes you have **Docker Desktop installed** and access to a folder like:

```
C:\Users\youruser\postgres-volume
```

This folder must contain previously initialized PostgreSQL data.

---

### 1. Download the PGVector-Enabled PostgreSQL Image

Open PowerShell or terminal and run:

```
docker pull pgvector/pgvector:pg16
```

This pulls the Docker image with PostgreSQL 16 and PGVector pre-installed.

---

### 2. Ensure the `postgres-volume` Folder Exists

Make sure the following folder is available on your machine:

```
C:\Users\youruser\postgres-volume
```

This folder should contain the actual database files from your previous environment (created via volume binding).

---

### 3. Start the Docker Container Using the Volume

Run the following (adjust the folder path if needed):

```
docker run --name postgres `
  -e POSTGRES_USER=user `
  -e POSTGRES_PASSWORD=password `
  -p 5432:5432 `
  -v C:\Users\youruser\postgres-volume:/var/lib/postgresql/data `
  -d pgvector/pgvector:pg16
```

This mounts your local database volume and starts the container.

---

### 4. Verify the Container Is Running

Check the container status:

```
docker ps
```

You should see a running container named `postgres`.

---

### 5. Check the PostgreSQL Version

Run the following:

```
docker exec -it postgres psql -U user -d postgres -c "SELECT version();"
```

You should see output like:

```
PostgreSQL 16.4 ...
```

---

### 6. Verify PGVector Is Installed

Run this:

```
docker exec -it postgres psql -U user -d postgres -c "SELECT * FROM pg_extension WHERE extname = 'vector';"
```

Expected output:

```
 extname | extversion 
---------+------------
 vector  | 0.8.0
```

If it returns **(0 rows)**, the extension is not yet enabled.

---

### 7. Enable PGVector (Only If Needed)

Run the following to activate the extension (only once per database):

```
docker exec -it postgres psql -U user -d postgres -c "CREATE EXTENSION IF NOT EXISTS vector;"
```

---

### 8. Backend Application DB Connection Settings

Your Spring Boot application can now connect to the PostgreSQL instance using the following:

```
JDBC URL:     jdbc:postgresql://localhost:5432/postgres
Username:     user
Password:     password
```

> If you're using a different database name than `postgres`, update the URL accordingly.


---


**Backend System – Preconditions for Running the Java Spring Boot Application**

This project uses Spring Boot 3.4.5, Java 21, and Maven. The following tools must be installed before running the backend.

---

### Required Installations (Outside the Project)

1. **Java Development Kit (JDK) 21 or higher**

   * Required for compiling and running the Spring Boot application.
   * Verify with: `java -version`
   * Download: [https://adoptium.net/en-GB/temurin/releases/](https://adoptium.net/en-GB/temurin/releases/)

2. **Maven 3.8+ (if not using the wrapper)**

   * Required to build the project and resolve dependencies.
   * Verify with: `mvn -version`
   * Download: [https://maven.apache.org/download.cgi](https://maven.apache.org/download.cgi)
   * *Optional if using `./mvnw` or `mvnw.cmd` provided in the project.*

3. **Google Cloud CLI (gcloud)**

   * Required if using Vertex AI (Gemini) for embeddings or LLM chat.
   * Install: [https://cloud.google.com/sdk/docs/install](https://cloud.google.com/sdk/docs/install)
   * After install, run: `gcloud auth login` and `gcloud config set project [YOUR_PROJECT_ID]`

4. **Chrome Browser + ChromeDriver (for Selenium)**

   * Used for scraping PDF links via Selenium automation.
   * Chrome should be installed: [https://www.google.com/chrome/](https://www.google.com/chrome/)
   * WebDriverManager (in the code) will auto-download the correct ChromeDriver version.

5. **Docker Desktop**

   * Used to run PostgreSQL 16.4 with PGVector extension locally.
   * Download: [https://www.docker.com/products/docker-desktop/](https://www.docker.com/products/docker-desktop/)

6. **PostgreSQL + PGVector Docker Image**

   * Pull using: `docker pull pgvector/pgvector:pg16`
   * Make sure the `postgres-volume` folder is available with the data.
   * Start the container with volume mount before running the backend.

7. **Environment Variables or Configuration**

   * Make sure the `application.yml` or `.env` file is set to use:

     * DB URL: `jdbc:postgresql://localhost:5432/postgres`
     * DB Username: `user`
     * DB Password: `password`
   * If using Gemini via Vertex AI, set up necessary Google credentials and region settings.

---

### Optional but Recommended

* **IDE**: IntelliJ IDEA or Visual Studio Code with Java plugin.
* **PostgreSQL GUI**: [pgAdmin](https://www.pgadmin.org/) to inspect database if needed.

---

**Frontend System – Preconditions for Running the Vue 3 + Vite Application**

This project uses Vue 3, Vite, TypeScript, Tailwind CSS, and ECharts. The following tools must be installed before running the frontend locally.

---

### Required Installations (Outside the Project)

1. **Node.js (LTS version recommended, e.g., 20.x or newer)**

   * Required to run the frontend and install dependencies.
   * Verify with: `node -v`
   * Download: [https://nodejs.org/](https://nodejs.org/)

2. **npm (comes with Node.js) or Yarn (optional)**

   * Used to install and manage frontend dependencies.
   * Verify with: `npm -v` or `yarn -v`

3. **Git**

   * Required to clone the repository.
   * Download: [https://github.com/hardopost/sentiment-analysis.git](https://github.com/hardopost/sentiment-analysis.git)

---

### How to Set Up and Run the Frontend

1. Open terminal and navigate to the `frontend/` directory.

2. Install dependencies:

   ```
   npm install
   ```

3. Start the development server:

   ```
   npm run dev
   ```

   The app will be available at:
   [http://localhost:5173](http://localhost:5173)

---

### Development Tools Used

* **Vue 3** – reactive UI framework
* **Vite** – fast development build tool
* **TypeScript** – type-safe JavaScript
* **Tailwind CSS** – utility-first styling
* **Vue Router** – for routing between pages
* **Axios** – HTTP client for API requests
* **ECharts (via vue-echarts)** – for sentiment visualizations
* **DOMPurify + Marked** – for safe Markdown rendering in chat

---

### Backend Dependency

Make sure the Spring Boot backend is running and accessible at:

```
http://localhost:8080
```

The frontend fetches data and sends chat prompts to this backend via REST API.

If needed, adjust proxy settings in `vite.config.ts` or `api.ts` for local or remote API access.



