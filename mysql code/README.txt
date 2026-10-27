Flight Seat Booking - MySQL files

Contents:
- sql/01_create_all_tables.sql : creates the 8 MySQL tables.
- sql/02_insert_airlines.sql : inserts the 5 airline rows using IDs 1-5 as requested.
- sql/03_insert_airports.sql : inserts airport data extracted from the uploaded Oracle SQL*Plus output.
- java/DBConnection.java : MySQL JDBC connection example. Replace CHANGE_ME with the MySQL password on the target machine.

Important:
- The Oracle output showed 165 AIRPORTS rows. Parsed airport rows in this bundle: 165.
- Only AIRLINES (5 rows) and AIRPORTS (165 rows) were confirmed as migrated in this conversation.
- USERS and the other tables are created but old records were intentionally not inserted because you said new users/passengers should be created in the new database.
- FLIGHTS, SEATS, BOOKINGS and PAYMENTS currently have no old data in these scripts.
- Do not distribute a real database password inside a public installer or public Git repository.
- Run 01_create_all_tables.sql first only if tables do not already exist. 02/03 INSERT scripts will error on duplicate primary keys if run again after data has already been inserted.
