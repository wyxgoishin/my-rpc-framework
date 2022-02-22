package zookeeperTest;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.server.DumbWatcher;
import org.apache.zookeeper.server.NIOServerCnxn;

import java.util.ArrayList;
import java.util.List;

public class zookeeperTest {
    private static final String DEFAULT_ZOOKEEPER_ADDR = "172.25.173.247:2181";

    public static void main(String[] args) throws Exception {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.builder()
                                    .connectString(DEFAULT_ZOOKEEPER_ADDR)
                                    .retryPolicy(retryPolicy)
                                    .build();
        client.start();

        /*
        curator 普遍使用 builder 模式返回可操控对象，下面是其操控 zookeeper 的一些常用接口
         */
        // path 必须以 / 开头
        String path = "/path";
        byte[] value = null;
        Watcher watcher = new DumbWatcher();
        List<ACL> aclList = new ArrayList<>();
        // client 后的一个是一个建造者，会返回能执行对应操作的对象
        // 创建路径无法直接多级创建，比如直接创建 /father/son
        // forPath() 会执行对应操作，但若无法执行则会直接抛出异常，内部参数包括路径和可选的值，不填值默认为 null
        client.create().forPath(path);
        client.create().forPath(path, value);
        // withMode 可以指定创建的 ZNode 的类型
        client.create().withMode(CreateMode.PERSISTENT).forPath(path);
        // 创建 ZNode 节点时在必须时同时创建父节点
        client.create().creatingParentsIfNeeded().forPath(path);
        // 查、改、删除对应节点的数据
        client.getData().forPath(path);
        client.setData().forPath(path, value);
        client.delete().forPath(path);
        // 检查对应节点是否存在，返回一个节点状态 Stat 对象
        client.checkExists().forPath(path);
        // 获取、设置节点的访问权限表
        client.getACL().forPath(path);
        client.setACL().withACL(aclList).forPath(path);
        // 增加、移除节点的监听器
        client.watchers().add().forPath(path);
        client.watchers().remove(watcher).forPath(path);

    }
}
