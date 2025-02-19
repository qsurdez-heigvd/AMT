package ch.heig.amt.vineward.business.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enumeration of wine varieties.
 *
 * @author Loïc Herman
 * @author Massimo Stefani
 * @author Quentin Surdez
 */
@Getter
@RequiredArgsConstructor
public enum WineVariety {
    ALIGOTE("Aligoté", GrapeType.WHITE),
    AMIGNE("Amigne", GrapeType.WHITE),
    ANCELLOTTA("Ancellotta", GrapeType.RED),
    BARBERA("Barbera", GrapeType.RED),
    BONDOLA("Bondola", GrapeType.RED),
    CABERNET_BLANC("Cabernet Blanc", GrapeType.WHITE),
    CABERNET_FRANC("Cabernet Franc", GrapeType.RED),
    CABERNET_JURA("Cabernet Jura", GrapeType.RED),
    CABERNET_SAUVIGNON("Cabernet Sauvignon", GrapeType.RED),
    CARMINOIR("Carminoir", GrapeType.RED),
    CHARDONNAY("Chardonnay", GrapeType.WHITE),
    CHASSELAS("Chasselas", GrapeType.WHITE),
    COMPLETER("Completer", GrapeType.WHITE),
    CORNALIN("Cornalin", GrapeType.RED),
    DIOLINOIR("Diolinoir", GrapeType.RED),
    DIVICO("Divico", GrapeType.RED),
    DIVONA("Divona", GrapeType.WHITE),
    DORAL("Doral", GrapeType.WHITE),
    DUNKELFELDER("Dunkelfelder", GrapeType.RED),
    GALOTTA("Galotta", GrapeType.RED),
    GAMARET("Gamaret", GrapeType.RED),
    GAMAY("Gamay", GrapeType.RED),
    GARANOIR("Garanoir", GrapeType.RED),
    GEWURZTRAMINER("Gewürztraminer", GrapeType.RED),
    HEIDA("Heida", GrapeType.WHITE),
    HUMAGNE_BLANCHE("Humagne Blanche", GrapeType.WHITE),
    HUMAGNE_ROUGE("Humagne Rouge", GrapeType.RED),
    JOHANNISBERG("Johannisberg", GrapeType.WHITE),
    MARSANNE_BLANCHE("Marsanne Blanche", GrapeType.WHITE),
    MERLOT("Merlot", GrapeType.RED),
    MULLER_THURGAU("Müller-Thurgau", GrapeType.WHITE),
    MUSCAT_BLANC("Muscat Blanc", GrapeType.WHITE),
    NEBBIOLO("Nebbiolo", GrapeType.RED),
    PETITE_ARVINE("Petite Arvine", GrapeType.WHITE),
    PINOT_GRIS("Pinot Gris", GrapeType.WHITE),
    PINOT_NOIR("Pinot Noir", GrapeType.RED),
    RAUSCHLING("Räuschling", GrapeType.WHITE),
    RIESLING("Riesling", GrapeType.WHITE),
    SAUVIGNON_BLANC("Sauvignon blanc", GrapeType.WHITE),
    SAVAGNIN_BLANC("Savagnin Blanc", GrapeType.WHITE),
    SEMILLON("Sémillon", GrapeType.WHITE),
    SILVANER("Silvaner", GrapeType.WHITE),
    SYRAH("Syrah", GrapeType.RED),
    VIOGNIER("Viognier", GrapeType.WHITE);

    private final String displayName;
    private final GrapeType type;
}
