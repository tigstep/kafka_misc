public class ACLPerms {
    private String topic;
    private String[] allow;
    private String[] hostIPs;

    public String getTopic() {
        return topic;
    }
    public void setTopic(String topic) {
        this.topic = topic;
    }
    public String[] getAllow() {
        return allow;
    }
    public void setAllow(String[] allow) {
        this.allow = allow;
    }
    public String[] getHostIP() {
        return hostIPs;
    }
    public void setHostIP(String[] hostIPs) {
        this.hostIPs = hostIPs;
    }
    /*public String[] getAction() {
        return action;
    }
    public void setAction(String[] action) {
        this.action = action;
    }
    public String getActionType() {
        return actionType;
    }
    public void setActionType(String actionType) {
        this.actionType = actionType;
    }
    public String[] hostIPs() {
        return hostIPs;
    }
    public void setHostIPs(String[] hostIPs) {
        this.hostIPs = hostIPs;
    }*/

}