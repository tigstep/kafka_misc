import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.kafka.clients.consumer.*;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.Logger;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class AvroConsumer {
    static Logger LOGGER = Logger.getLogger(AvroConsumer.class);
    private static File getFile() {
        File propFile = new File(System.getProperty("user.dir") + "/resources/consumerProps.json");
        return propFile;
    }

    private static Map<String, String> getProperties(File propFile) {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> kafkaProperties = new HashMap<>();
        try {
            kafkaProperties = mapper.readValue(propFile, new TypeReference<Map<String, String>>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return kafkaProperties;
    }

    public static void main(String[] args) {
        String log4jConfigFile = System.getProperty("user.dir") + "/resources/log4j_consumer.properties";
        PropertyConfigurator.configure(log4jConfigFile);

        File propFile = getFile();
        Map<String, String> props = getProperties(propFile);
        LOGGER.info("Properties are : " + props.toString());
        if (args.length == 0) {
            System.out.println("Enter topic name");
            return;
        }

        String topicName = args[0];
        Properties kafkaProps = new Properties();

        kafkaProps.put("bootstrap.servers", props.get("bootstrap.servers"));
        kafkaProps.put("group.id", props.get("group.id"));
        kafkaProps.put("key.deserializer", props.get("key.deserializer"));
        kafkaProps.put("value.deserializer", props.get("value.deserializer"));
        kafkaProps.put("schema.registry.url", props.get("schema.registry.url"));
        kafkaProps.put("security.protocol", props.get("security.protocol"));
        kafkaProps.put("ssl.endpoint.identification.algorithm", props.get("ssl.endpoint.identification.algorithm"));
        kafkaProps.put("sasl.mechanism", props.get("sasl.mechanism"));
        kafkaProps.put("sasl.jaas.config", props.get("sasl.jaas.config"));

        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(kafkaProps);

        consumer.subscribe(Collections.singletonList(topicName));

        final int giveUp = 100;
        int noRecordsCount = 0;
        while (true) {
            final ConsumerRecords<String, String> consumerRecords =
                    consumer.poll(1000);
            if (consumerRecords.count() == 0) {
                noRecordsCount++;
                if (noRecordsCount > giveUp) break;
                else continue;
            }
            consumerRecords.forEach(record -> {
                LOGGER.info(String.format("Consumer Record:(%s, %s, %d, %d, %s)\n",
                        record.key(),
                        record.value(),
                        record.partition(),
                        record.offset(),
                        record.headers()
                ));
            });

            consumer.commitAsync();
        }
        consumer.close();
        System.out.println("DONE");
    }
}