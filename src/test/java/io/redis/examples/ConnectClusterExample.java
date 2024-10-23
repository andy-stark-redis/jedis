// EXAMPLE: connect_cluster
// REMOVE_START
package io.redis.examples;
import org.junit.Assert;
import org.junit.Test;
// REMOVE_END

// STEP_START connect_cluster
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisClientConfig;

import java.util.Set;
import java.util.HashSet;

public class ConnectClusterExample {
    @Test
    public void run() {
        Set<HostAndPort> clusterNodeSet = new HashSet<HostAndPort>();
        
        clusterNodeSet.add(new HostAndPort("<host>", <port>));

        JedisClientConfig config = DefaultJedisClientConfig.builder()
                .user("default")
                .password("<password>")
                .build();

        JedisCluster jedis = new JedisCluster(
            clusterNodeSet,
            config
        );
        // REMOVE_START
        jedis.del("foo");
        // REMOVE_END

        String res1 = jedis.set("foo", "bar");
        System.out.println(res1); // >>> OK

        String res2 = jedis.get("foo");
        System.out.println(res2); // >>> bar

        // REMOVE_START
        Assert.assertEquals("OK", res1);
        Assert.assertEquals("bar", res2);
        // REMOVE_END
        jedis.close();
    }
}
// STEP_END