/*
 * JAVADRAW
 * paasmoo 2022 (GitHub)
 */
package at.paasmoo.config;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class Config {
    private static Locale locale = Locale.getDefault();
    public static ResourceBundle bundle = ResourceBundle.getBundle("at.paasmoo.config.internationalization.text", locale);
    private static final Preferences prefs = Preferences.userNodeForPackage(Config.class);

    public static String version = "V1.5.0";

    public static Preferences getPrefs() {
        return prefs;
    }

    public static void setNewLanguage(String lang) {
        locale = new Locale(lang);
        bundle = ResourceBundle.getBundle("at.paasmoo.config.internationalization.text", locale);
    }

    public static String getLanguage() {
        return switch (locale.getLanguage()) {
            case "de" -> "Deutsch";
            case "at" -> "Östareichisch";
            case "en" -> "English";
            case "fr" -> "Français";
            case "it" -> "Italiano";
            case "kr" -> "Hangug-in";
            default -> "";
        };
    }

    public static String getCopyright() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return "JavaDraw " + version + "\n© paasmoo " + dtf.format(LocalDateTime.now());
    }
}
