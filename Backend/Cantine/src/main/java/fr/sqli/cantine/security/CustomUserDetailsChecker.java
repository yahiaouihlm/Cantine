package fr.sqli.cantine.security;

import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserDetailsChecker extends AccountStatusUserDetailsChecker {


    @Override
    public void check(UserDetails user) {
        if (!user.isEnabled()) {
            throw new DisabledException(super.messages.getMessage("AccountStatusUserDetailsChecker.disabled", "User is disabled"));
        }
        // make  the  other checks
        super.check(user);
    }

}
