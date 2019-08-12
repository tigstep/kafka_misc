import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Headers;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.Logger;

public class AvroProducer {
    static Logger LOGGER = Logger.getLogger(AvroProducer.class);

    private static File getFile(){
        File propFile = new File(System.getProperty("user.dir") + "/resources/producerProps.json");
        return propFile;
    };

    private static Map<String, String> getProperties (File propFile){
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> kafkaProperties = new HashMap<>();
        try {
            kafkaProperties = mapper.readValue(propFile, new TypeReference<Map<String,String>>(){});
        } catch (IOException e) {
            e.printStackTrace();
        }
        return kafkaProperties;
    }

    public static void main(String[] args) {
        String log4jConfigFile = System.getProperty("user.dir") + "/resources/log4j_producer.properties";
        PropertyConfigurator.configure(log4jConfigFile);

        File propFile = getFile();
        Map<String, String> props = getProperties(propFile);
        LOGGER.info("Properties are : " + props.toString());

        if(args.length == 0){
            System.out.println("Enter topic name");
            return;
        }

        String topicName = args[0];

        Properties kafkaProps = new Properties();
        kafkaProps.put("bootstrap.servers", props.get("bootstrap.servers"));
        kafkaProps.put("security.protocol", props.get("security.protocol"));
        kafkaProps.put("ssl.endpoint.identification.algorithm", props.get("ssl.endpoint.identification.algorithm"));
        kafkaProps.put("acks", props.get("acks"));
        kafkaProps.put("retries", props.get("retries"));
        kafkaProps.put("batch.size", props.get("batch.size"));
        kafkaProps.put("linger.ms", props.get("linger.ms"));
        kafkaProps.put("buffer.memory", props.get("buffer.memory"));
        kafkaProps.put("key.serializer", props.get("key.serializer"));
        kafkaProps.put("value.serializer", props.get("value.serializer"));

        //SSL SETTINGS
        kafkaProps.put("ssl.truststore.location", props.get("ssl.truststore.location"));
        kafkaProps.put("ssl.truststore.password", props.get("ssl.truststore.password"));
        kafkaProps.put("ssl.keystore.location", props.get("ssl.keystore.location"));
        kafkaProps.put("ssl.keystore.password", props.get("ssl.keystore.password"));
        kafkaProps.put("ssl.key.password", props.get("ssl.key.password"));

        //SCHEMA REGISTRY
        kafkaProps.put("schema.registry.url", props.get("schema.registry.url"));

        //SASL SETTINGS
        //kafkaProps.put("sasl.mechanism", props.get("sasl.mechanism"));
        //kafkaProps.put("sasl.jaas.config", props.get("sasl.jaas.config"));

        Producer<String, String> producer = new KafkaProducer
                <String, String>(kafkaProps);
        Date date = new Date();

        long timeMilli = -1;
        for(int i = 0; i < 10; i++) {
            LOGGER.info("Producer message is : " + Integer.toString(i));
            ProducerRecord<String, String> record = new ProducerRecord<String, String>(topicName, Integer.toString(i), "kafka test message");
            Headers headers = record.headers();
            timeMilli = date.getTime();
            headers.add("DATE", String.valueOf(timeMilli).getBytes());
            producer.send(record);
        }
        producer.close();
    }
}