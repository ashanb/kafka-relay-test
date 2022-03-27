package com.relay.test.internal;

import com.datastax.driver.core.*;
import com.relay.test.client.operations.OperationType;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.Base64;

import static java.time.ZoneOffset.UTC;

public class DatabaseAccessUtil {
    public static void pushToCassandra(Session session, Long key, String encodedValue) {
        // sanitize the incoming string before the conversion
        if (encodedValue.startsWith("\"")) {
            encodedValue = encodedValue.substring(1, encodedValue.length() - 1);
        }
        // Insert a row.=
        final String decodedMessage = new String(Base64.getDecoder().decode(encodedValue));

        System.out.println(decodedMessage);

//        ObjectMapper m = new ObjectMapper();
//        Set<Product> products = m.readValue(json, new TypeReference<Set<Product>>() {});

        // https://github.com/fabienrenaud/java-json-benchmark
        final JSONObject jsonObject = new JSONObject(decodedMessage);
        final int id = (int) jsonObject.get("id");
        final int clusterId = (int) jsonObject.get("clusterId");

        // assume this is available for all the cases. // take this from in memory collection
        final String type = jsonObject.get("type").toString();
        final String name = jsonObject.get("name").toString();

        final OffsetDateTime offsetDateTime = OffsetDateTime.parse(jsonObject.get("timestamp").toString());
        final Timestamp timestamp = Timestamp.valueOf(offsetDateTime.atZoneSameInstant(UTC).toLocalDateTime());

//            OffsetDateTime offsetDateTime = OffsetDateTime.parse("2022-03-26T20:51:27.798761Z");
//            Timestamp timestamp = Timestamp.valueOf(offsetDateTime.toLocalDateTime());

        final BigDecimal value = (BigDecimal) jsonObject.get("value");
        final Boolean initialized = (Boolean) jsonObject.get("initialized");

        final String insert = "INSERT INTO relay.iot_humidity_data_tab (id_, cluster_id_, type_, name_, timestamp_, value_, initialized_) VALUES (?,?,?,?,?,?,?)";
        final PreparedStatement ps = session.prepare(insert);
        final BoundStatement bound = ps.bind().setLong(0, id).setLong(1, clusterId).setString(2, type).setString(3, name).setTimestamp(4, timestamp).setDouble(5, value.floatValue()).setBool(6, initialized);
        session.execute(bound);

        // injection risk!
//            String insert =
//                    "INSERT INTO relay.iot_humidity_data_tab " +
//                            "(id, cluster_id, type, name, timestamp, value, initialized) VALUES (" +
//                            ""+ id +","+ clusterId +",'"+ type +"','"+ name +"','"+ timestamp +"',"+ value + ","+ initialized +")";
//            System.out.println(insert);
//            ResultSet insertResult = session.execute(insert);

    }

    public static String getAggregateValueFromCassandra(String type, Long clusterId, Session session, String from, String to, OperationType operationType) {
        final String dbSQL = "select ## from relay.iot_humidity_data_tab where timestamp_ > ? and timestamp_ < ?";

        final String aggregate;

        // Mandatory Fields
        System.out.println("From:====" + from);
        final OffsetDateTime offsetDateTime = OffsetDateTime.parse(from);
        System.out.println("OffsetDateTime:====" + offsetDateTime);
        System.out.println("ZoneSimilarLocal====" + offsetDateTime.atZoneSimilarLocal(UTC).toLocalDateTime());
        final Timestamp timestamp = Timestamp.valueOf(offsetDateTime.atZoneSimilarLocal(UTC).toLocalDateTime());
        System.out.println("Timestamp====" + timestamp);
        final OffsetDateTime offsetDateTime2 = OffsetDateTime.parse(to);
        final Timestamp timestamp2 = Timestamp.valueOf(offsetDateTime2.atZoneSimilarLocal(UTC).toLocalDateTime());

        // Optional

        if (operationType == OperationType.MIN) {
            aggregate = "min(value_) as min_";
        } else if (operationType == OperationType.MAX) {
            aggregate = "max(value_) as min_";
        } else if (operationType == OperationType.AVG) {
            aggregate = "avg(value_) as avg_";
        } else {
            throw new IllegalStateException("Operation Not Implemented");
        }

        String readSQL = dbSQL.replace("##", aggregate);

        if (type != null) {
            readSQL = readSQL + " and type_ = :type_";
        }

        if (clusterId != null) {
            readSQL = readSQL + " and cluster_id_ = :cluster_id_";
        }

        final PreparedStatement ps = session.prepare(readSQL);
        final BoundStatement bound = ps.bind().setTimestamp(0, timestamp).setTimestamp(1, timestamp2);

        if (type != null) {
            bound.setString("type_", type);
        }

        if (clusterId != null) {
            bound.setLong("cluster_id_", clusterId);
        }


        final ResultSet insertResult = session.execute(bound);

        final Object out = insertResult.one().getDouble(0);
        System.out.println(out);
        return out.toString();
    }

    public static void createModels(Session session) {
        // Create keyspace 'relay' if it does not exist.
        String createKeyspace = "CREATE KEYSPACE IF NOT EXISTS relay;";
        session.execute(createKeyspace);
        System.out.println("Created keyspace relay");

        // Create table 'iot_humidity_data_tab' if it does not exist.
        String createTable = "CREATE TABLE IF NOT EXISTS relay.iot_humidity_data_tab (id_ bigint, cluster_id_ bigint, type_ varchar, name_ varchar, timestamp_ TIMESTAMP, " +
                "value_ double, initialized_ boolean, PRIMARY KEY (id_, timestamp_));";

        session.execute(createTable);
        System.out.println("Created table iot_humidity_data_tab");
    }


    // TODO: 3/27/2022 remove 
    public static void main(String[] args) {
//        CassandraSessionProvider cassandraSessionProvider;
//        cassandraSessionProvider = new CassandraSessionProvider("localhost", 9042);
//        Cluster cluster = cassandraSessionProvider.getCluster();
//        Session session = cluster.connect();
//        String encodedString = "\"eyJpZCI6MiwidHlwZSI6IkhVTUlESVRZIiwibmFtZSI6IkxpdmluZyBSb29tIEh1bWlkaXR5XzgiLCJjbHVzdGVySWQiOjEsInRpbWVzdGFtcCI6IjIwMjItMDMtMjVUMTg6MDY6MjQuNTkxMzE2WiIsInZhbHVlIjozNi43NTgzNTMxMjMwOTk5NjUsImluaXRpYWxpemVkIjp0cnVlfQ==\"";
//        pushToCassandra(session, 1L, encodedString);


        CassandraSessionProvider cassandraSessionProvider;
        cassandraSessionProvider = new CassandraSessionProvider("localhost", 9042);
        Cluster cluster = cassandraSessionProvider.getCluster();
        Session session = cluster.connect();

        getAggregateValueFromCassandra("TEMPERATURE", 1L, session, "2022-03-27T11:35:38.708094Z", "2022-03-27T11:36:57.818677Z", OperationType.AVG);
    }
}
