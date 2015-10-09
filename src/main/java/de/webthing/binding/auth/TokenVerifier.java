package de.webthing.binding.auth;



/**
 * Created by Johannes on 05.10.2015.
 *
 * Interface to pass a Token to a checking entity
 *
 */
public interface TokenVerifier {

    /**
     * Check a Token for authorization
     *
     * @param jwt token as string
     * @return allowance as boolean
     */
    boolean isAuthorized(String jwt);
}
