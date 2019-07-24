import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;
// import zookeeper classes

public class zookeeper {
    static Logger LOGGER = Logger.getLogger(zookeeper.class);
    // declare zookeeper instance to access ZooKeeper ensemble
    private ZooKeeper zoo;
    final CountDownLatch connectedSignal = new CountDownLatch(1);

    // Method to connect zookeeper ensemble.
    public ZooKeeper connect(String host) throws IOException,
            InterruptedException {
        zoo = new ZooKeeper(host, 5000, new Watcher() {
            public void process(WatchedEvent we) {
                if (we.getState() == KeeperState.SyncConnected) {
                    connectedSignal.countDown();
                }
            }
        });
        connectedSignal.await();
        return zoo;
    }

    // Method to disconnect from zookeeper server
    public void close() throws InterruptedException {
        zoo.close();
    }

    public static void main(String[] args){
        String log4jConfigFile = System.getProperty("user.dir") + "/src/main/resources/log4j_zookeeper.properties";
        PropertyConfigurator.configure(log4jConfigFile);
        zookeeper zk = new zookeeper();
        try {
            LOGGER.info("before zk.connect");
            zk.connect("172.30.12.14");
            LOGGER.info("after zk.connect");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}