package ch.heig.amt.vineward.business.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enumeration of wine regions.
 *
 * @author Loïc Herman
 * @author Massimo Stefani
 * @author Quentin Surdez
 */
@Getter
@RequiredArgsConstructor
public enum Region {
    GENEVA("Geneva"),
    TICINO("Ticino"),
    THREE_LAKES("Three lakes"),
    GERMAN_SWITZERLAND("German Switzerland"),
    VALAIS("Valais"),
    VAUD("Vaud");

    private final String displayName;
}
