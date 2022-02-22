package rpc_common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import rpc_common.enumeration.RpcExceptionBean;
import rpc_common.exception.RpcException;
import rpc_common.factory.SingletonFactory;
import rpc_core.annotation.PropertySource;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ZkUtil {
    private static final int DEFAULT_BASE_SLEEP_TIME = 1000;
    private static final int DEFAULT_MAX_RETRY = 3;
    private static final String DEFAULT_REGISTER_ROOT_PATH = "/my-rpc";
    private static final String DEFAULT_SERVER_ADDRESS = "127.0.0.1:2181";
    private static CuratorFramework zkClient;
    private static final Map<String, List<String>> SERVICE_ADDRESS_MAP = new ConcurrentHashMap<>();
    private static final Set<String> REGISTERED_PATH_SET = ConcurrentHashMap.newKeySet();
    private static InetSocketAddress address;

    public static CuratorFramework getZkClient(String serverAddress){
        if(zkClient != null && zkClient.getState() == CuratorFrameworkState.STARTED){
            return zkClient;
        }
        serverAddress = serverAddress == null ? DEFAULT_SERVER_ADDRESS : serverAddress;
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(DEFAULT_BASE_SLEEP_TIME, DEFAULT_MAX_RETRY);
        zkClient = CuratorFrameworkFactory.builder()
                .connectString(serverAddress)
                .retryPolicy(retryPolicy)
                .build();
        zkClient.start();
        try{
            if(!zkClient.blockUntilConnected(30, TimeUnit.SECONDS)){
                throw new RuntimeException("Zookeeper 启动超时");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return zkClient;
    }

    public static void registerService(CuratorFramework zkClient, String serviceName, InetSocketAddress inetSocketAddress) {
        address = inetSocketAddress;
        String servicePath = DEFAULT_REGISTER_ROOT_PATH + "/" + serviceName + "/" + inetSocketAddress.getHostString() + ":" + inetSocketAddress.getPort();
        try {
            if(REGISTERED_PATH_SET.contains(servicePath) || zkClient.checkExists().forPath(servicePath) != null){
                log.info("该服务已创建");
            }else{
                zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(servicePath);
                log.info("成功为服务{}创建节点", serviceName);
            }
            REGISTERED_PATH_SET.add(servicePath);
        } catch (Exception e) {
            log.error("为服务{}创建节点失败", serviceName, e);
        }
    }

    public static void clearRegistry(){
        REGISTERED_PATH_SET.stream().parallel().forEach(p -> {
            try {
                if (p.endsWith(address.toString())) {
                    zkClient.delete().forPath(p);
                }
            } catch (Exception e) {
                log.error("clear registry for path [{}] fail", p);
            }
        });
        log.info("All registered services on the server are cleared:[{}]", REGISTERED_PATH_SET);
    }

    public static List<String> getChildNodes(CuratorFramework zkClient, String serviceName) {
        if(SERVICE_ADDRESS_MAP.containsKey(serviceName)){
            return SERVICE_ADDRESS_MAP.get(serviceName);
        }
        List<String> result = null;
        String servicePath = DEFAULT_REGISTER_ROOT_PATH + "/" + serviceName;
        try {
            Stat stat = zkClient.checkExists().forPath(servicePath);
            if(stat == null){
                throw new RpcException(RpcExceptionBean.SERVICE_NOT_FOUND);
            }
            result = zkClient.getChildren().forPath(servicePath);
            SERVICE_ADDRESS_MAP.put(serviceName, result);
            registerWatcher(serviceName, servicePath, zkClient);
        } catch (Exception e) {
            log.error("获取 {} 的子节点失败", servicePath, e);
        }
        return result;
    }

    private static void registerWatcher(String serviceName, String servicePath, CuratorFramework zkClient) throws Exception {
        PathChildrenCache pathChildrenCache = new PathChildrenCache(zkClient, servicePath, true);
        PathChildrenCacheListener pathChildrenCacheListener = (curatorFramework, pathChildrenCacheEvent) -> {
            List<String> serviceAddresses = curatorFramework.getChildren().forPath(servicePath);
            SERVICE_ADDRESS_MAP.put(serviceName, serviceAddresses);
        };
        pathChildrenCache.getListenable().addListener(pathChildrenCacheListener);
        pathChildrenCache.start();
    }
}
