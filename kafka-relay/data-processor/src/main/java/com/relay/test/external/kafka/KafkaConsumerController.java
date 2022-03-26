package com.relay.test.external.kafka;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.relay.test.internal.CassandraSessionProvider;
import com.relay.test.internal.DatabaseAccessUtil;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import org.apache.kafka.common.errors.WakeupException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// require thread safe operations!
public class KafkaConsumerController {

    private static final Logger LOG = LogManager.getLogger(KafkaConsumerController.class);
    static boolean consumerSwitch = false;
    private static Consumer<Long, String> consumer;
    private static boolean continueOnError = false;

    public static void setupConsumer() {
        if (consumer == null) {
            System.out.println("###### Consumer Created ######");
            consumer = ConsumerCreator.createConsumer();
        }
    }

    public static void setupConsumer(
            final Consumer<Long, String> consumer1) {
        consumer = consumer1;
    }

    public static void startConsume() {
        if (consumerSwitch) {
            System.out.println("###### Consumer already started ######");
            return;
        } else {
            consumerSwitch = true;
            if (consumer == null) {
                setupConsumer();
            }
            System.out.println("###### Listening to Topic ######");
        }

        CassandraSessionProvider cassandraSessionProvider = new CassandraSessionProvider("host.docker.internal", 9042);
        Cluster cluster = cassandraSessionProvider.getCluster();

//        int noMessageToFetch = 0;
        try (final Session session = cluster.connect()) {
            while (true) {
                final ConsumerRecords<Long, String> consumerRecords = consumer.poll(100);
                if (consumerRecords.count() == 0) {
//                    noMessageToFetch++;
//                    if (noMessageToFetch > IKafkaConstants.MAX_NO_MESSAGE_FOUND_COUNT)
//                        break;
//                    else
                    continue;
                }

                consumerRecords.forEach(record -> {
//				LOG.info(record.key());
                    System.out.println("Record Key " + record.key());
                    System.out.println("Record value " + record.value());
                    System.out.println("Record partition " + record.partition());
                    System.out.println("Record offset " + record.offset());
                    DatabaseAccessUtil.pushToCassandra(session, record.offset(), record.value());
                });
                consumer.commitAsync();
            }
        } catch (WakeupException e) {
            System.out.println("Wake up Signal Received.");
        } catch (RuntimeException ex) {
            System.out.println(ex.getMessage());
            throw ex;
        } finally {
            consumerSwitch = false;
            consumer.close();
            consumer = null;
            if (cluster != null) {
                cluster.close();
            }
            System.out.println("Consumer Stopped.");
        }
    }

    public static String stop() {
        if (consumer != null) {
            consumer.wakeup();
            return "Consumer Stopped";
        } else {
            System.out.println("###### Consumer Already Stopped ######");
            return "Consumer Already Stopped";
        }
    }
}
