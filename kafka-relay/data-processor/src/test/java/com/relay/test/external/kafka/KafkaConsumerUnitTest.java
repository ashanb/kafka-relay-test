package com.relay.test.external.kafka;

import com.datastax.driver.core.Cluster;
import com.relay.test.internal.CassandraSessionProvider;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.MockConsumer;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;

import static com.relay.test.external.kafka.KafkaConsumerController.*;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class KafkaConsumerUnitTest {

    private static final String TOPIC = "topic";
    private static final int PARTITION = 0;

    private Throwable pollException;

    private MockConsumer<Long, String> consumer;

    @BeforeEach
    void setUp() {
        consumer = new MockConsumer<>(OffsetResetStrategy.EARLIEST);
    }

    @Test
    void whenStartingBySubscribingToTopic() {
        CassandraSessionProvider cassandraSessionProvider =
                new CassandraSessionProvider("host.docker.internal", 9042);

        try (Cluster cluster = cassandraSessionProvider.getCluster()) {
            cluster.connect();
        } catch (Exception exception) {
            System.out.printf("consuming record test ignored::");
            return;
        }

        // GIVEN
        consumer.schedulePollTask(() -> {
            consumer.rebalance(Collections.singletonList(new TopicPartition(TOPIC, 0)));
            consumer.addRecord(record(TOPIC, PARTITION, 1l, "eyJpZCI6MiwidHlwZSI6IkhVTUlESVRZIiwibmFtZSI6IkxpdmluZyBSb29tIEh1bWlkaXR5XzgiLCJjbHVzdGVySWQiOjEsInRpbWVzdGFtcCI6IjIwMjItMDMtMjVUMTg6MDY6MjQuNTkxMzE2WiIsInZhbHVlIjozNi43NTgzNTMxMjMwOTk5NjUsImluaXRpYWxpemVkIjp0cnVlfQ=="));
        });

        consumer.schedulePollTask(() -> stop());

        HashMap<TopicPartition, Long> startOffsets = new HashMap<>();
        TopicPartition tp = new TopicPartition(TOPIC, PARTITION);
        startOffsets.put(tp, 0L);
        consumer.updateBeginningOffsets(startOffsets);
        consumer.subscribe(Collections.singletonList(IKafkaConstants.TOPIC_NAME));

        // WHEN
        setupConsumer(consumer);
        startConsume();
        // THEN
        assertThat(consumer.closed()).isTrue();
        System.out.printf("consuming record test success::");
    }

    @Test
    void whenStartingBySubscribingToTopicAndExceptionOccurs_thenExpectExceptionIsHandledCorrectly() {

        CassandraSessionProvider cassandraSessionProvider =
                new CassandraSessionProvider("host.docker.internal", 9042);

        try (Cluster cluster = cassandraSessionProvider.getCluster()) {
            cluster.connect();
        } catch (Exception exception) {
            System.out.printf("poll exception test ignored::");
            return;
        }

        // GIVEN
        consumer.schedulePollTask(() -> consumer.setPollException(new KafkaException("poll exception")));

        consumer.schedulePollTask(() -> stop());

        HashMap<TopicPartition, Long> startOffsets = new HashMap<>();
        TopicPartition tp = new TopicPartition(TOPIC, 0);
        startOffsets.put(tp, 0L);
        consumer.updateBeginningOffsets(startOffsets);

        consumer.subscribe(Collections.singletonList(IKafkaConstants.TOPIC_NAME));

        // WHEN
        setupConsumer(consumer);

        KafkaException thrown = assertThrows(
                KafkaException.class,
                () -> startConsume(),
                "poll exception"
        );

        // THEN
        assertThat(consumer.closed()).isTrue();

        System.out.printf("poll exception test success::");
    }

    private ConsumerRecord<Long, String> record(String topic, int partition, Long long1, String string1) {
        return new ConsumerRecord<>(topic, partition, 0, long1, string1);
    }
}