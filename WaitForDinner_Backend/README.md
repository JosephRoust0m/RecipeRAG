## Local Database Setup

### 1. Create environment file
From the project root, run:
```bash
cp .env.example .env
```
* This creates a `.env` file in the root directory. Update it with your current database username and password,
and never commit or share your `.env` file in version control.

### 2. Install pgAdmin
Download and install pgAdmin from the official PostgreSQL website.

### 3. Create a New Server
Open pgAdmin and create a new server with the following connection settings:

- Host: localhost
- Port: 5433
- Username: postgres
- Password: 12345678

### 4. Create the Database
Create a new database named: WaitForDinner

### 5. Build the Backend
Open the `backend` folder in your IDE or terminal and run:

./gradlew clean build

### 6. Verify the Tables
Return to pgAdmin, refresh the database, and confirm that 18 tables have been created in the `public` schema.

### Note:
This repository was built as a group project for a course at McGill, contributors include:

Ralph Azrak
Karine Ha
Arthur Huang
Lince Ketchate
Logan Ma
Ayham Nassar
Janelle Tam
Bilguun Tegshbayar
Joseph Roustom
Ismail Sentissi

However, the Recipe Chatbot was built solely by me (Joseph Roustom).