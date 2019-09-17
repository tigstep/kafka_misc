import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.acl.*;
import org.apache.kafka.common.resource.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import com.google.gson.*;


public class ACLUpdater {
    Map props = new HashMap();

    public AdminClient initClient(){
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092"); //need to be argument driven
        AdminClient adminClient = AdminClient.create(props);
        return adminClient;
    };

    public void configureACL(){
        ResourcePattern resourcePattern = new ResourcePattern(ResourceType.TOPIC, "topic", PatternType.MATCH);
        AccessControlEntry accessControlEntry = new AccessControlEntry("user", "0.0.0.0", AclOperation.ALL, AclPermissionType.ALLOW);
        AclBinding aclBinding = new AclBinding(resourcePattern, accessControlEntry);
        ArrayList<AclBinding> aclBindings = new ArrayList<>();
        aclBindings.add(aclBinding);
        AdminClient adminClient = initClient();
        adminClient.createAcls(aclBindings);
    }

    public static Map<String, String> ingestConfigs() throws FileNotFoundException {
        Gson gson = new Gson();
        Map<String, String> configMap = new HashMap<>();
        Object object = gson.fromJson(new FileReader("../resources/acl.json"), Object.class);

        return configMap;
    }

    public static void main(String[] args) throws FileNotFoundException {
        Map<String, String> configMap = ingestConfigs();
        System.out.println(configMap);
    }
}
