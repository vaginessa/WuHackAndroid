package hackwu.wuhack;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

/**
 * Created by Paul on 29.06.2015.
 */
public class AuthenticatorTest extends Authenticator
{
    String user;
    char[] password;

    public AuthenticatorTest(String user, char[] password) {
        this.user = user;
        this.password = password;
    }

    @Override
    protected PasswordAuthentication getPasswordAuthentication()
    {
        return new PasswordAuthentication(user, password);
    }
}
