package com.relay.test.internal;

import com.datastax.driver.core.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Date;

import static java.nio.charset.StandardCharsets.UTF_8;

public class DatabaseAccessUtil {
    public static void pushToCassandra(Session session, Long key, String encodedValue) {
        try {
            // sanitize the incoming string before conversion
            if (encodedValue.startsWith("\""))  {
                encodedValue = encodedValue.substring(1, encodedValue.length()-1);
            }

            // Insert a row.=
            final String decodedMessage = new String(Base64.getDecoder().decode(encodedValue));
//            ObjectMapper m = new ObjectMapper();
//            Set<Product> products = m.readValue(json, new TypeReference<Set<Product>>() {});

            // https://github.com/fabienrenaud/java-json-benchmark
            JSONObject jsonObject = new JSONObject(decodedMessage);
            int id = (int) jsonObject.get("id");
            int clusterId = (int) jsonObject.get("clusterId");

            // assume this is available for all the cases. // take this from in memory collection
            String type = jsonObject.get("type").toString();
            String name = jsonObject.get("name").toString();

            OffsetDateTime offsetDateTime = OffsetDateTime.parse(jsonObject.get("timestamp").toString());
            Timestamp timestamp = Timestamp.valueOf(offsetDateTime.atZoneSameInstant(ZoneOffset.UTC).toLocalDateTime());

            BigDecimal value = (BigDecimal) jsonObject.get("value");
            Boolean initialized = (Boolean) jsonObject.get("initialized");

            String insert = "INSERT INTO relay.iot_humidity_data_tab (id, cluster_id, type, name, timestamp, value, initialized) VALUES (?,?,?,?,?,?,?)";
            PreparedStatement ps = session.prepare(insert);
            BoundStatement bound = ps.bind().setLong(0, id).setLong(1, clusterId).setString(2, type).setString(3, name).setTimestamp(4, timestamp).setDecimal(5, value).setBool(6, initialized);
            session.execute(bound);

//            String insert =
//                    "INSERT INTO relay.iot_humidity_data_tab " +
//                            "(id, cluster_id, type, name, timestamp, value, initialized) VALUES (" +
//                            ""+ id +","+ clusterId +",'"+ type +"','"+ name +"','"+ timestamp +"',"+ value + ","+ initialized +")";
//            System.out.println(insert);
//            ResultSet insertResult = session.execute(insert);

        } finally {
        }
    }

    public static void main(String[] args) {
        CassandraSessionProvider cassandraSessionProvider;
        cassandraSessionProvider = new CassandraSessionProvider("host.docker.internal", 9042);
        Cluster cluster = cassandraSessionProvider.getCluster();
        Session session = cluster.connect();
        String encodedString = "\"eyJpZCI6MiwidHlwZSI6IkhVTUlESVRZIiwibmFtZSI6IkxpdmluZyBSb29tIEh1bWlkaXR5XzgiLCJjbHVzdGVySWQiOjEsInRpbWVzdGFtcCI6IjIwMjItMDMtMjVUMTg6MDY6MjQuNTkxMzE2WiIsInZhbHVlIjozNi43NTgzNTMxMjMwOTk5NjUsImluaXRpYWxpemVkIjp0cnVlfQ==\"";
        pushToCassandra(session, 1L, encodedString);
    }
}
