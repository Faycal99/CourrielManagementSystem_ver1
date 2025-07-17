package dgb.Mp.config;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomDaoAuthenticationProvider extends DaoAuthenticationProvider implements CustomDaoAuthenticationProviderInterface {
    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        // Log the password provided by the user in the request
        System.out.println("Provided password: " + authentication.getCredentials());
        System.out.println("Stored password: " + userDetails.getPassword());

        // Call the parent method to perform the actual password check
        super.additionalAuthenticationChecks(userDetails, authentication);
    }
}
