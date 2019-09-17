import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.acl.*;
import org.apache.kafka.common.resource.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
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

    public static String[] ingestFlattenConfigs() throws FileNotFoundException {
        String[] acl_commands = new String[100];
        Gson gson = new Gson();
        ACL[] objects = gson.fromJson(new FileReader(System.getProperty("user.dir") + "/resources/acl.json"), ACL[].class);
        for (ACL obj:objects){
            System.out.println(obj.getUser());
            ACLPerms[] perms = obj.getACLPerms();
            for (ACLPerms perm:perms){
                System.out.println(Arrays.toString(perm.getAllow()));
                System.out.println(Arrays.toString(perm.getHostIP()));
            }

            //ACLPerms[] perms = gson.fromJson(obj.getACLPerms().toString(), ACLPerms[].class);

        }
        /*for (ACL obj:objects){
            ACLPerms[] perms = gson.fromJson(obj.getACLPerms(), ACLPerms[].class);
            for (ACLPerms perm:perms){
                System.out.println(perm.hostIPs());
            }

        }*/
        return acl_commands;
    }

    public static void main(String[] args) throws FileNotFoundException {
        String[] object = ingestFlattenConfigs();

    }
}
