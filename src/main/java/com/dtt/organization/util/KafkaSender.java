package com.dtt.organization.util;

import com.dtt.organization.request.entity.LogModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaSender {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    /** Primary topic */
    @Value("${com.dt.kafka.topic.central}")
    private String topic;

    /** RA topic (second send) */
    @Value("${com.dt.kafka.topic.ra}")
    private String topicRA;

    public void send(LogModel logmodel) {

        kafkaTemplate.send(topic, logmodel);
        kafkaTemplate.send(topicRA, logmodel);
        System.out.println("Kafka messages sent to topics: " + topic + ", " + topic);
        System.out.println("Kafka messages sent to topics: " + topic + ", " + topicRA);
    }
}
