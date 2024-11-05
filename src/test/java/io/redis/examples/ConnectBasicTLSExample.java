// EXAMPLE: connect_basic_tls
// REMOVE_START
package io.redis.examples;
import org.junit.Assert;
import org.junit.Test;
// REMOVE_END
// STEP_START connect_basic_tls
import redis.clients.jedis.UnifiedJedis;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisClientConfig;
import javax.net.ssl.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

public class ConnectBasicTLSExample {

    private static SSLSocketFactory createSslSocketFactory(
            String caCertPath, String caCertPassword)
            throws IOException, GeneralSecurityException {

        KeyStore trustStore = KeyStore.getInstance("jks");
        trustStore.load(new FileInputStream(caCertPath), caCertPassword.toCharArray());

        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("X509");
        trustManagerFactory.init(trustStore);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustManagerFactory.getTrustManagers(), null);

        return sslContext.getSocketFactory();
    }

    //  Use the keytool command to convert the PEM file to JKS format:
    //    keytool -importcert -keystore truststore.jks \ 
    //      -storepass REPLACE_WITH_YOUR_PASSWORD \
    //      -file redis_ca.pem
    @Test
    public void run() {
        try {
            SSLSocketFactory sslFactory = createSslSocketFactory(
                "<path_to_truststore.jks_file>",
                "<password_for_truststore.jks_file>"
            );

            JedisClientConfig config = DefaultJedisClientConfig.builder()
                .user("default")
                .password("<password>")
                .ssl(true)
                .sslSocketFactory(sslFactory)
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
        } catch (GeneralSecurityException g) {
            // Error handling code.
        } catch (IOException i) {
            // Error handling code.
        }
    }
}
// STEP_END