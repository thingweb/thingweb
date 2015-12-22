package de.thingweb.security;

public class TokenRequirementsBuilder {
    private String issuer = null;
    private String audience = null;
    private boolean expirationTimeDefined = false;
    private String verificationKey = null;
    private String client = null;

    public TokenRequirementsBuilder setIssuer(String issuer) {
        this.issuer = issuer;
        return this;
    }

    public TokenRequirementsBuilder setAudience(String audience) {
        this.audience = audience;
        return this;
    }

    public TokenRequirementsBuilder setExpirationTimeDefined(boolean expirationTimeDefined) {
        this.expirationTimeDefined = expirationTimeDefined;
        return this;
    }

    public TokenRequirementsBuilder setVerificationKey(String verificationKey) {
        this.verificationKey = verificationKey;
        return this;
    }

    public TokenRequirementsBuilder setClient(String client) {
        this.client = client;
        return this;
    }

    public TokenRequirements createTokenRequirements() {
        return new TokenRequirements(issuer, audience, expirationTimeDefined, verificationKey, client);
    }
}