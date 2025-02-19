package ch.heig.amt.vineward.business.model;

/**
 * Standard column lengths for the database.
 *
 * @author Loïc Herman
 * @author Massimo Stefani
 * @author Quentin Surdez
 */
public interface ColumnSize {

    int POST_TITLE_LENGTH = 250;

    int USERNAME_LENGTH = 48;
    int EMAIL_LENGTH = 255;
    int PASSWORD_LENGTH = 324;
    int ROLE_LENGTH = 16;

    int CANTON_LENGTH = 32;
    int REGION_LENGTH = 24;

    int WINE_NAME_LENGTH = 100;
    int WINE_VARIETY_LENGTH = 32;
    int WINE_VINTNER_LENGTH = 50;

    int POST_STATUS_LENGTH = 16;
}
