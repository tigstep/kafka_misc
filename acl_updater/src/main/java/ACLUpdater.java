import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.acl.*;
import org.apache.kafka.common.resource.*;
import org.apache.log4j.Level;
import org.apache.log4j.PropertyConfigurator;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.ExecutionException;

import com.google.gson.*;
import org.apache.log4j.Logger;


public class ACLUpdater {
    static Logger LOGGER = Logger.getLogger(ACLUpdater.class);

    public static AdminClient initClient(String bootstrapServerLst) throws ExecutionException, InterruptedException {
        Properties props = new Properties();
        props.put("client.id", "z_kafka");
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServerLst); //need to be argument driven
        props.put("security.protocol","SSL");
        props.put("ssl.truststore.location", "<location>" );
        props.put("ssl.truststore.password","<pass>");
        props.put("ssl.keystore.location", "<location>" );
        props.put("ssl.keystore.password","<pass>");
        props.put("ssl.endpoint.identification.algorithm", "");

        AdminClient adminClient = AdminClient.create(props);
        return adminClient;
    }

    public static void configureACL(AdminClient adminClient, ACL[] aclObjects) throws ExecutionException, InterruptedException {
        ArrayList<AclBinding> aclBindings = new ArrayList<>();
        LOGGER.info("Configure AdminClient is : " + adminClient.describeCluster().nodes().get());
        LOGGER.info(adminClient.describeCluster().clusterId().get());

        for (ACL aclObject:aclObjects) {
            System.out.println(aclObject.getUser());
            for(ACLPerms param:aclObject.getACLPerms()){
                ResourcePattern resourcePattern = new ResourcePattern(ResourceType.TOPIC, param.getTopic(), PatternType.LITERAL);
                AccessControlEntry accessControlEntry = new AccessControlEntry(aclObject.getUser()
                        , String.join(","
                        , param.getHostIP())
                        , AclOperation.ALL
                        , AclPermissionType.ALLOW);
                AclBinding aclBinding = new AclBinding(resourcePattern, accessControlEntry);
                aclBindings.add(aclBinding);
                //System.out.println(param.getTopic());
                //System.out.println(String.join(",", param.getAllow()));
                //System.out.println(String.join(",", param.getHostIP()));
            }
        }
        try {
            LOGGER.info("aclBindings are : " + (aclBindings.toString()));
            LOGGER.info("adminClient.createAcls(aclBindings).getClass() : " + adminClient.createAcls(aclBindings).all().get());

        }
        catch(Exception e) {
            LOGGER.info(e.fillInStackTrace());
        }

    }

    public static ACL[] ingestFlattenConfigs() throws FileNotFoundException {
        Gson gson = new Gson();
        ACL[] aclObjects = gson.fromJson(new FileReader(System.getProperty("user.dir") + "/resources/acl.json"), ACL[].class);
        /*for (ACL obj:objects){
            System.out.println(obj.getUser());
            ACLPerms[] perms = obj.getACLPerms();
            for (ACLPerms perm:perms){
                System.out.println(String.join(",", perm.getAllow()));
                System.out.println(String.join(",", perm.getHostIP()));
                System.out.println(perm.getTopic());
            }
        }*/
        return aclObjects;
    }

    public static void main(String[] args) throws FileNotFoundException, ExecutionException, InterruptedException {
        LOGGER.setLevel(Level.DEBUG);
        String log4jConfigFile = System.getProperty("user.dir") + "/resources/log4j_consumer.properties";
        PropertyConfigurator.configure(log4jConfigFile);
        AdminClient adminClient = initClient(args[0]);
        ACL[] aclObjects = ingestFlattenConfigs();
        configureACL(adminClient, aclObjects);

    }
}
