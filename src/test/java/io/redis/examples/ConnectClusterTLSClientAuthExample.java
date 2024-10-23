// EXAMPLE: connect_cluster_tls_client_auth

// REMOVE_START
package io.redis.examples;
import org.junit.Assert;
import org.junit.Test;
// REMOVE_END

// STEP_START connect_cluster_tls_client_auth
import redis.clients.jedis.*;
import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.util.HashSet;
import java.util.Set;

public class ConnectClusterTLSClientAuthExample {
    @Test
    public void run() throws GeneralSecurityException, IOException {
        Set<HostAndPort> clusterNodes = new HashSet<HostAndPort>();
        
        clusterNodes.add(new HostAndPort("redis-15313.c34461.eu-west-2-mz.ec2.cloud.rlrcp.com", 15313));

        SSLSocketFactory sslFactory = createSslSocketFactory(
                "/Users/andrew.stark/Documents/Repos/forks/jedis/src/test/java/io/redis/examples/truststore.jks",
                "secret", // use the password you specified for keytool command
                "/Users/andrew.stark/Documents/Repos/forks/jedis/src/test/java/io/redis/examples/keystore.p12",
                "secret" // use the password you specified for openssl command
        );

        JedisClientConfig config = DefaultJedisClientConfig.builder()
                .ssl(true).sslSocketFactory(sslFactory)
                .user("default")
                .password("MrlnkBuSZqO0s0vicIkLnqJXetbSTCan")
                .build();

        JedisCluster jedis = new JedisCluster(clusterNodes, config);
        
        jedis.set("foo", "bar");
        String result = jedis.get("foo");
        System.out.println(result); // >>> bar

        // REMOVE_START
        Assert.assertEquals("bar", result);
        // REMOVE_END
    }

    private static SSLSocketFactory createSslSocketFactory(
            String caCertPath, String caCertPassword, String userCertPath, String userCertPassword)
            throws IOException, GeneralSecurityException {

        KeyStore keyStore = KeyStore.getInstance("pkcs12");
        keyStore.load(new FileInputStream(userCertPath), userCertPassword.toCharArray());

        KeyStore trustStore = KeyStore.getInstance("jks");
        trustStore.load(new FileInputStream(caCertPath), caCertPassword.toCharArray());

        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("X509");
        trustManagerFactory.init(trustStore);

        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("PKIX");
        keyManagerFactory.init(keyStore, userCertPassword.toCharArray());

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);

        return sslContext.getSocketFactory();
    }
}
// STEP_END