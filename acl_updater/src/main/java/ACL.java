import com.google.gson.JsonObject;

public class ACL {
    private String user;
    private ACLPerms[] acls;

    public String getUser() {
        return user;
    }
    public void setUser(String user) {
        this.user = user;
    }

    public ACLPerms[] getACLPerms(){
        return acls;
    }
    public void setACLPerms(ACLPerms[] acls){
        this.acls = acls;
    }
}
