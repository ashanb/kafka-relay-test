package com.relay.test.internal;

import java.sql.*;

public class PostgresConnectionProvider {
    private Connection currentJdbcConnection;

    private Connection initializeConnection() throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5433/yugabyte",
                "yugabyte", "yugabyte");
        return conn;
    }

    public Connection getConnection() throws SQLException, ClassNotFoundException {
        if (currentJdbcConnection != null  && !this.currentJdbcConnection.isClosed()) {
            return this.currentJdbcConnection;
        }
        return this.currentJdbcConnection = initializeConnection();
    }

    // todo: remove
    public static void main(String[] args) {
        try {
            PostgresConnectionProvider postgresUtil = new PostgresConnectionProvider();

            Statement stmt = postgresUtil.getConnection().createStatement();

            System.out.println("Connected to the PostgreSQL server successfully.");
            stmt.execute("DROP TABLE IF EXISTS employee");
            stmt.execute("CREATE TABLE IF NOT EXISTS employee" +
                    "  (id int primary key, name varchar, age int, language text)");
            System.out.println("Created table employee");

            String insertStr = "INSERT INTO employee VALUES (1, 'John', 35, 'Java')";
            stmt.execute(insertStr);
            System.out.println("EXEC: " + insertStr);

            ResultSet rs = stmt.executeQuery("select * from employee");
            while (rs.next()) {
                System.out.println(String.format("Query returned: name = %s, age = %s, language = %s",
                        rs.getString(2), rs.getString(3), rs.getString(4)));
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
