package com.relay.test;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.microshed.testing.SharedContainerConfig;
import org.microshed.testing.jaxrs.RESTClient;
import org.microshed.testing.jupiter.MicroShedTest;
import org.microshed.testing.kafka.KafkaProducerClient;


@MicroShedTest
@SharedContainerConfig(AppContainerConfig.class)
@TestMethodOrder(OrderAnnotation.class)
public class MicroShedTestTests {

    @RESTClient
    public static ConsumerResource consumerResource;

//    @KafkaProducerClient(valueSerializer = SystemLoad.SystemLoadSerializer.class)
//    public static KafkaProducer<String, SystemLoad> producer;

//    @Test
//    public void testCpuUsage() throws InterruptedException {
//        SystemLoad sl = new SystemLoad("localhost", 1.1);
//        producer.send(new ProducerRecord<String, SystemLoad>("system.load", sl));
//        Thread.sleep(1000);
//
//        consumerResource.startConsumer();
//        consumerResource.stopConsumer();
//    }
}
