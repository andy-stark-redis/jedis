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
    //  Use the keytool command to convert the PEM file to JKS format:
    //    keytool -importcert -keystore truststore.jks \ 
    //      -storepass REPLACE_WITH_YOUR_PASSWORD \
    //      -file redis_ca.pem
    // Use the openssl command to convert the certificate and private key files to P12 format:
    //    openssl pkcs12 -export -in ./redis_user.crt -inkey ./redis_user_private.key -out redis-user-keystore.p12 -name "redis"
    @Test
    public void run() throws GeneralSecurityException, IOException {
        HostAndPort address = new HostAndPort(
            "<host>",
            <port>
        );

        SSLSocketFactory sslFactory = createSslSocketFactory(
                "<path_to_truststore.jks_file>",
                "<password_for_truststore.jks_file>",
                "<path_to_keystore.p12_file>",
                "<password_for_keystore.p12_file>"
        );

        JedisClientConfig config = DefaultJedisClientConfig.builder()
                .ssl(true).sslSocketFactory(sslFactory)
                .user("default") // use your Redis user. More info https://redis.io/docs/latest/operate/oss_and_stack/management/security/acl/
                .password("<password>") // use your Redis password
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