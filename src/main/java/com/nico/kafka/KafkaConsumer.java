package com.nico.kafka;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.message.MessageAndMetadata;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class KafkaConsumer {
        private ConsumerConnector consumerConnector = null;
        private final String topic = "topic_master";



    private void initialize() {
            Properties props = new Properties();
            props.put("zookeeper.connect", "localhost:2181");
            props.put("group.id", "nico");
            props.put("zookeeper.session.timeout.ms", "400");
            props.put("zookeeper.consumer.path", "/home/nico/tweets");
            props.put("zookeeper.sync.time.ms", "300");
            props.put("auto.commit.interval.ms", "100");
            ConsumerConfig conConfig = new ConsumerConfig(props);
            consumerConnector = Consumer.createJavaConsumerConnector(conConfig);
        }

        private void consume() {
            //Key = topic name, Value = No. of threads for topic
            Map<String, Integer> topicCount = new HashMap<String, Integer>();
            topicCount.put(topic, 1);

            //ConsumerConnector creates the message stream for each topic
            Map<String, List<KafkaStream<byte[], byte[]>>> consumerStreams =
                    consumerConnector.createMessageStreams(topicCount);

            System.out.println("Message consumed from topic");

            // Get Kafka stream for topic 'topic_master'
            List<KafkaStream<byte[], byte[]>> kStreamList =
                    consumerStreams.get(topic);
            // Iterate stream using ConsumerIterator
            for (final KafkaStream<byte[], byte[]> kStreams : kStreamList) {

                for (MessageAndMetadata<byte[], byte[]> kStream : kStreams)
                    System.out.println("Message consumed from topic[" + topic + "] : " +
                            new String(kStream.message()));

            }
            //Shutdown the consumer connector
            if (consumerConnector != null)   consumerConnector.shutdown();
        }

        public static void main(String[] args) throws InterruptedException {
            KafkaConsumer kafkaConsumer = new KafkaConsumer();
            // Configure Kafka consumer
            kafkaConsumer.initialize();
            // Start consumption
            kafkaConsumer.consume();
        }
    }

