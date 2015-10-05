package de.webthing.binding;

/**
 * Created by Johannes on 05.10.2015.
 */
public interface TokenVerifier {
    boolean isAuthorized(String jwt);
}
