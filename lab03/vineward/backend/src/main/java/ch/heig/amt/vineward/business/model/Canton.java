package ch.heig.amt.vineward.business.model;

import java.util.EnumSet;
import java.util.Set;
import lombok.Getter;

/**
 * Enumeration of Swiss cantons.
 *
 * @author Loïc Herman
 * @author Massimo Stefani
 * @author Quentin Surdez
 */
@Getter
public enum Canton {
    AARGAU("AG", "Aargau", Region.GERMAN_SWITZERLAND),
    APPENZELL_INNERRHODEN("AI", "Appenzell Innerrhoden", Region.GERMAN_SWITZERLAND),
    APPENZELL_AUSSERRHODEN("AR", "Appenzell Ausserrhoden", Region.GERMAN_SWITZERLAND),
    BERN("BE", "Bern", Region.GERMAN_SWITZERLAND),
    BASEL_LAND("BL", "Basel-Landschaft", Region.GERMAN_SWITZERLAND),
    BASEL_STADT("BS", "Basel-Stadt", Region.GERMAN_SWITZERLAND),
    FRIBOURG("FR", "Fribourg", Region.VAUD, Region.THREE_LAKES, Region.GERMAN_SWITZERLAND),
    GENEVE("GE", "Genève", Region.GENEVA),
    GLARUS("GL", "Glarus", Region.GERMAN_SWITZERLAND),
    GRAUBUNDEN("GR", "Graubünden", Region.GERMAN_SWITZERLAND),
    JURA("JU", "Jura", Region.THREE_LAKES),
    LUZERN("LU", "Luzern", Region.GERMAN_SWITZERLAND),
    NEUCHATEL("NE", "Neuchâtel", Region.THREE_LAKES),
    NIDWALDEN("NW", "Nidwalden", Region.GERMAN_SWITZERLAND),
    OBWALDEN("OW", "Obwalden", Region.GERMAN_SWITZERLAND),
    SCHAFFHAUSEN("SH", "Schaffhausen", Region.GERMAN_SWITZERLAND),
    SCHWYZ("SZ", "Schwyz", Region.GERMAN_SWITZERLAND),
    SOLOTHURN("SO", "Solothurn", Region.GERMAN_SWITZERLAND),
    ST_GALLEN("SG", "St. Gallen", Region.GERMAN_SWITZERLAND),
    THURGAU("TG", "Thurgau", Region.GERMAN_SWITZERLAND),
    TICINO("TI", "Ticino", Region.TICINO),
    URI("UR", "Uri", Region.GERMAN_SWITZERLAND),
    WALLIS("VS", "Vallais / Wallis", Region.VALAIS),
    VAUD("VD", "Vaud", Region.VAUD, Region.THREE_LAKES),
    ZUG("ZG", "Zug", Region.GERMAN_SWITZERLAND),
    ZURICH("ZH", "Zürich", Region.GERMAN_SWITZERLAND);

    private final String iso2;
    private final String displayName;
    private final Set<Region> allowableRegions;

    Canton(String iso2, String displayName, Region allowableRegion, Region... rest) {
        this.iso2 = iso2;
        this.displayName = displayName;
        this.allowableRegions = EnumSet.of(allowableRegion, rest);
    }
}
