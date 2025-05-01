## To run

# DB Connection

Create the schema in PostgreSQL
```sql
CREATE TABLE suppliers (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    contact VARCHAR(255)
);
CREATE TABLE products (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price NUMERIC(10, 2) NOT NULL,
    quantity INTEGER NOT NULL,
    supplier_id INTEGER NOT NULL,
    FOREIGN KEY (supplier_id) REFERENCES suppliers(id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
);
```
Create a file database.properties and include your database credentials
```bash
DB_URL=""
DB_USER=""
DB_PASSWORD=""
```
Compile
```bash
javac InventoryApp.java Conn.java
```
Run
```bash
java -cp ".;.\postgresql-42.7.5.jar" InventoryApp
```