package com.relay.test.internal;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

import java.util.List;

public class CassandraSessionProvider {
    private Cluster currentCassandraCluster;
    private String host;
    private int port;

    private Cluster initializeCluster()  {
        // Create a Cassandra client.
        Cluster cluster = Cluster.builder()
                .addContactPoint(host)
                .withPort(port)
                .build();
        return cluster;
    }

    public CassandraSessionProvider(String host, int port){
        this.host = host;
        this.port = port;
    }

    public Cluster getCluster() {
        if (currentCassandraCluster != null  && !this.currentCassandraCluster.isClosed()) {
            return this.currentCassandraCluster;
        }
        return this.currentCassandraCluster = initializeCluster();
    }

    // todo: remove
    public static void main(String[] args) {
        try {
            CassandraSessionProvider cassandraSessionProvider = new CassandraSessionProvider("localhost", 9042);
            Cluster cluster = cassandraSessionProvider.getCluster();
            Session session = cluster.connect();

            // Create keyspace 'ybdemo' if it does not exist.
            String createKeyspace = "CREATE KEYSPACE IF NOT EXISTS relay;";
            ResultSet createKeyspaceResult = session.execute(createKeyspace);
            System.out.println("Created keyspace relay");

//            // Create table 'employee' if it does not exist.
//            String createTable = "CREATE TABLE IF NOT EXISTS relay.iot_data_tab (key int PRIMARY KEY, " +
//                    "value varchar);";
//            ResultSet createResult = session.execute(createTable);
//            System.out.println("Created table iot_data_tab");


            // Create table 'employee' if it does not exist.
            String createTable = "CREATE TABLE IF NOT EXISTS relay.iot_humidity_data_tab (id bigint, cluster_id bigint, type varchar, name varchar, timestamp TIMESTAMP, " +
                    "value decimal, initialized boolean, PRIMARY KEY (id, timestamp));";

            ResultSet createResult = session.execute(createTable);
            System.out.println("Created table iot_humidity_data_tab");


//            // Insert a row.
//            String insert = "INSERT INTO relay.iot_data_tab (key, value)" +
//                    " VALUES (1, 'John');";
//            ResultSet insertResult = session.execute(insert);
//            System.out.println("Inserted data: " + insert);
//
//            // Query the row and print out the result.
//            String select = "SELECT key, value FROM relay.iot_data_tab WHERE key = 1;";
//            ResultSet selectResult = session.execute(select);
//            List<Row> rows = selectResult.all();
//            int key = rows.get(0).getInt(0);
//            String value = rows.get(0).getString(1);
//            System.out.println("Query returned " + rows.size() + " row: " +
//                    "key=" + key + ", value=" + value );

            // Close the client.
            session.close();
            cluster.close();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
