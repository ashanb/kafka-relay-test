package com.relay.test;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.TestMethodOrder;
import org.microshed.testing.SharedContainerConfig;
import org.microshed.testing.jaxrs.RESTClient;
import org.microshed.testing.jupiter.MicroShedTest;


@MicroShedTest
@SharedContainerConfig(AppContainerConfig.class)
@TestMethodOrder(OrderAnnotation.class)
public class MicroShedTests {

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
