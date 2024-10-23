// EXAMPLE: connect_basic_tls_client_auth

// REMOVE_START
package io.redis.examples;
import org.junit.Assert;
import org.junit.Test;
// REMOVE_END

// STEP_START connect_basic_tls_client_auth
import redis.clients.jedis.*;
import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

public class ConnectBasicTLSClientAuthExample {
    @Test
    public void run() throws GeneralSecurityException, IOException {
        HostAndPort address = new HostAndPort(
            "redis-14669.c338.eu-west-2-1.ec2.redns.redis-cloud.com",
            14669
        );

        SSLSocketFactory sslFactory = createSslSocketFactory(
                "/Users/andrew.stark/Documents/Repos/forks/jedis/src/test/java/io/redis/examples/truststore.jks",
                "secret", // use the password you specified for keytool command
                "/Users/andrew.stark/Documents/Repos/forks/jedis/src/test/java/io/redis/examples/redis-user-keystore.p12",
                "secret" // use the password you specified for openssl command
        );

        JedisClientConfig config = DefaultJedisClientConfig.builder()
                .ssl(true).sslSocketFactory(sslFactory)
                .user("default") // use your Redis user. More info https://redis.io/docs/latest/operate/oss_and_stack/management/security/acl/
                .password("jj7hRGi1K22vop5IDFvAf8oyeeF98s4h") // use your Redis password
                .build();

        UnifiedJedis jedis = new UnifiedJedis(address, config);

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