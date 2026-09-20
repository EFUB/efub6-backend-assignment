package efub.assignment.community.global.security.jwt;

public enum JwtTokenType {
    ACCESS("access"),
    REFRESH("refresh");

    private final String claimValue;

    JwtTokenType(String claimValue) {
        this.claimValue = claimValue;
    }

    public String claimValue() {
        return claimValue;
    }
}
