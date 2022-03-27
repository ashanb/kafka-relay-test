package com.relay.test.external.kafka;

/**
 * @author AshanB
 */
public interface IKafkaConstants {
   String KAFKA_BROKERS = "host.docker.internal:9092,host.docker.internal:9094,host.docker.internal:9095,localhost:9092,localhost:9094,localhost:9095";
   Integer MESSAGE_COUNT=10_000_000;
   String CLIENT_ID="client1";
   String TOPIC_NAME="iot-data";
   String GROUP_ID_CONFIG="consumerGroup1";
   Integer MAX_NO_MESSAGE_FOUND_COUNT=1000;
   String OFFSET_RESET_LATEST="latest";
   String OFFSET_RESET_EARLIER="earliest";
   Integer MAX_POLL_RECORDS=1;
}
