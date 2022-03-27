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

    private Cluster initializeCluster() {
        // Create a Cassandra client.
        Cluster cluster = Cluster.builder()
                .addContactPoint(host)
                .withPort(port)
                .build();
        return cluster;
    }

    public CassandraSessionProvider(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public Cluster getCluster() {
        if (currentCassandraCluster != null && !this.currentCassandraCluster.isClosed()) {
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
            session.execute(createKeyspace);
            System.out.println("Created keyspace relay");

            // Create table 'employee' if it does not exist.
            String createTable = "CREATE TABLE IF NOT EXISTS relay.iot_humidity_data_tab (id_ bigint, cluster_id_ bigint, type_ varchar, name_ varchar, timestamp_ TIMESTAMP, " +
                    "value_ double, initialized_ boolean, PRIMARY KEY (id_, timestamp_));";

            session.execute(createTable);
            System.out.println("Created table iot_humidity_data_tab");


            // Close the client.
            session.close();
            cluster.close();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
