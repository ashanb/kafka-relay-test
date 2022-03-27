package com.relay.test.client.operations;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.relay.test.ApiRequest;
import com.relay.test.internal.CassandraSessionProvider;
import com.relay.test.internal.DatabaseAccessUtil;

public class ClientOperationsUtil {
    public static Object getAggregate(String type, Long clusterId, String from, String to, OperationType operationType){
        final CassandraSessionProvider cassandraSessionProvider =
                new CassandraSessionProvider(ApiRequest.getCassandraHost(), ApiRequest.getCassandraPort());
        try (Cluster cluster = cassandraSessionProvider.getCluster(); Session session = cluster.connect()) {

            if (operationType == OperationType.MIN) {
                return DatabaseAccessUtil.getAggregateValueFromCassandra(type, clusterId, session, from, to, operationType);
            } else if (operationType == OperationType.MAX){
                return DatabaseAccessUtil.getAggregateValueFromCassandra(type, clusterId, session, from, to, operationType);
            } else if (operationType == OperationType.AVG){
                return DatabaseAccessUtil.getAggregateValueFromCassandra(type, clusterId, session, from, to, operationType);
            }

            throw new IllegalStateException("Not Implemented Operation Type.");
        }
    }
}
