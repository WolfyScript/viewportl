package me.wolfyscript.utilities.api.console;

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.language.LanguageAPI;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Console {

    private final WolfyUtilities wolfyUtilities;
    private final LanguageAPI languageAPI;
    private final Plugin plugin;
    private final Logger logger;

    public Console(WolfyUtilities wolfyUtilities) {
        this.wolfyUtilities = wolfyUtilities;
        this.languageAPI = wolfyUtilities.getLanguageAPI();
        this.plugin = wolfyUtilities.getPlugin();
        this.logger = plugin.getLogger();
    }

    public void log(Level level, String message) {
        logger.log(level, () -> languageAPI.replaceColoredKeys(message));
    }

    public void log(Level level, String message, String... replacements) {
        message = languageAPI.replaceKeys(message);
        List<String> keys = new ArrayList<>();
        Pattern pattern = Pattern.compile("%([A-Z]*?)(_*?)%");
        Matcher matcher = pattern.matcher(message);
        while (matcher.find()) {
            keys.add(matcher.group(0));
        }
        for (int i = 0; i < keys.size(); i++) {
            message = message.replace(keys.get(i), replacements[i]);
        }
        log(level, message);
    }

    public void log(Level level, String message, String[]... replacements) {
        if (replacements != null) {
            message = languageAPI.replaceKeys(message);
            for (String[] replace : replacements) {
                if (replace.length > 1) {
                    message = message.replaceAll(replace[0], replace[1]);
                }
            }
        }
        log(level, message);
    }

    public void info(String message) {
        log(Level.INFO, message);
    }

    public void fine(String message) {
        log(Level.FINE, message);
    }

    public void finer(String message) {
        log(Level.FINER, message);
    }

    public void finest(String message) {
        log(Level.FINEST, message);
    }

    public void warn(String message) {
        log(Level.WARNING, message);
    }

    public void severe(String message) {
        log(Level.SEVERE, message);
    }

    public void debug(String message) {
        if (wolfyUtilities.hasDebuggingMode()) {
            info(message);
        }
    }

    public Logger getLogger() {
        return logger;
    }

}
