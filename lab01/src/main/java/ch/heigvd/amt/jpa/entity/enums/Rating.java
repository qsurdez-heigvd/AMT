package ch.heigvd.amt.jpa.entity.enums;

import java.util.stream.Stream;

public enum Rating {

    GENERAL_AUDIENCES("G"),
    PARENTAL_GUIDANCE_SUGGESTED("PG"),
    PARENTAL_GUIDANCE_STRONGLY_SUGGESTED("PG-13"),
    RESTRICTED("R"),
    NO_ONE_17_AND_UNDER_ADMITTED("NC-17");

    private final String code;

    private Rating(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static Rating fromCode(String code) {
        return Stream.of(Rating.values())
                .filter(c -> c.getCode().equals(code))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

}
