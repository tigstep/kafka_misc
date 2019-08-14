
public class ACLUpdater {

/*
    1) Iterates over a json file in a form of

    {
        "topicName":
        {
            "principal": "principalName",
            "allow: [list of allowed actions].
            "deny": [list of denied actions],
            "hosts": [list of hosts that action can run from]
        }
    }
    2) Creates the topic if does not exist
    3) Constructs the ACL command from the subjson and applies if does not exist
*/
}
