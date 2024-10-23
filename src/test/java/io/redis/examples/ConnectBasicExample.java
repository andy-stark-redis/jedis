// EXAMPLE: connect_basic
// REMOVE_START
package io.redis.examples;
import org.junit.Assert;
import org.junit.Test;
// REMOVE_END
// STEP_START connect_basic
import redis.clients.jedis.UnifiedJedis;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisClientConfig;

public class ConnectBasicExample {
    @Test
    public void run() {
        JedisClientConfig config = DefaultJedisClientConfig.builder()
                .user("default")
                .password("<password>")
                .build();

        UnifiedJedis jedis = new UnifiedJedis(
            new HostAndPort("<host>", <port>),
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