package ch.heigvd.amt.jpa.auth;

import io.quarkus.security.jpa.PasswordProvider;
import jakarta.xml.bind.DatatypeConverter;
import org.wildfly.security.password.Password;
import org.wildfly.security.password.interfaces.SimpleDigestPassword;

public class SHA1PasswordProvider implements PasswordProvider {
    @Override
    public Password getPassword(String passwordFromDatabase) {
        byte[] digest = DatatypeConverter.parseHexBinary(passwordFromDatabase);

        // let the runtime know that the password is a SHA-1 hash
        return SimpleDigestPassword.createRaw(SimpleDigestPassword.ALGORITHM_SIMPLE_DIGEST_SHA_1, digest);
    }
}
