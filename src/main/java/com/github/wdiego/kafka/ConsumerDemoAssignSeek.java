package com.github.wdiego.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

/**
 * Consume 5 messages from a specific topic/partition/offset
 */
public class ConsumerDemoAssignSeek {

    public static void main(String[] args) {

        final Logger logger = LoggerFactory.getLogger(ConsumerDemoAssignSeek.class.getName());

        String bootstrapServers = "localhost:9092";
        String topic = "first_topic";

        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); // earliest, latest, none

        // create the consumer
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);

        // assign and seek are mostly used to replay data or fetch a specific message

        // assign
        TopicPartition partitionToReadFrom = new TopicPartition(topic, 0);
        long offsetToReadFrom = 15L;
        consumer.assign(Arrays.asList(partitionToReadFrom));

        // seek
        consumer.seek(partitionToReadFrom, offsetToReadFrom);

        int numberOfMessagesToRead = 5;
        boolean keepOnReading = true;
        int numberOfMessagesReadSoFar = 0;

        // poll for new data
        while(keepOnReading) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));

            for (ConsumerRecord<String, String> record : records) {
                numberOfMessagesReadSoFar += 1;

                logger.info("----------------------------------------");
                logger.info("Key: " + record.key());
                logger.info("Value: " + record.value());
                logger.info("Partition: " + record.partition());
                logger.info("Offset: " + record.offset());

                if (numberOfMessagesReadSoFar >= numberOfMessagesToRead) {
                    keepOnReading = false; // to exit the while loop
                    break; // to exit the for loop
                }
            }
        }

        logger.info("----------------------------------------");
        logger.info("Exiting the application...");
    }
}
