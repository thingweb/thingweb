package de.webthing.binding.auth;

/**
 * Created by Johannes on 05.10.2015.
 */
public class GrantAllTokenVerifier implements TokenVerifier {
    @Override
    public boolean isAuthorized(String jwt) {
        return true;
    }
}
