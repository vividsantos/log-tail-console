package tail.log;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class CustomConfig {
    private String errorColor = "RED";
    private String warnColor = "YELLOW";
    private String infoColor = "GREEN";
    private String debugColor = "GRAY";

    public static CustomConfig loadFromFile(String configPath) throws IOException {
        CustomConfig config = new CustomConfig();
        Properties props = new Properties();

        try (FileInputStream fis = new FileInputStream(configPath)) {
            props.load(fis);
        }

        config.errorColor = props.getProperty("error.color", "RED");
        config.warnColor = props.getProperty("warn.color", "YELLOW");
        config.infoColor = props.getProperty("info.color", "GREEN");
        config.debugColor = props.getProperty("debug.color", "GRAY");

        return config;
    }

    public String getColorCode(String colorName) {
        String colorCode = getColorByName(colorName);
        return colorCode;
    }

    private String getColorByName(String colorName) {
        switch (colorName.toUpperCase()) {
            case "DARK_RED": return ConsoleColors.DARK_RED;
            case "RED": return ConsoleColors.RED;
            case "BRIGHT_RED": return ConsoleColors.BRIGHT_RED;
            case "BOLD_RED": return ConsoleColors.BOLD_RED;
            case "BROWN": return ConsoleColors.BROWN;
            case "YELLOW": return ConsoleColors.YELLOW;
            case "BRIGHT_YELLOW": return ConsoleColors.BRIGHT_YELLOW;
            case "BOLD_YELLOW": return ConsoleColors.BOLD_YELLOW;
            case "DARK_GREEN": return ConsoleColors.DARK_GREEN;
            case "GREEN": return ConsoleColors.GREEN;
            case "BRIGHT_GREEN": return ConsoleColors.BRIGHT_GREEN;
            case "DARK_BLUE": return ConsoleColors.DARK_BLUE;
            case "BLUE": return ConsoleColors.BLUE;
            case "DARK_GRAY": return ConsoleColors.DARK_GRAY;
            case "GRAY": return ConsoleColors.GRAY;
            case "WHITE": return ConsoleColors.WHITE;
            case "BOLD_WHITE": return ConsoleColors.BOLD_WHITE;
            case "BRIGHT_WHITE": return ConsoleColors.BRIGHT_WHITE;
            case "BLACK": return ConsoleColors.BLACK;
            case "BOLD_CYAN": return ConsoleColors.BOLD_CYAN;
            case "BOLD_MAGENTA": return ConsoleColors.BOLD_MAGENTA;
            default: return "";
        }
    }

    public String getErrorColor() { return errorColor; }
    public String getWarnColor() { return warnColor; }
    public String getInfoColor() { return infoColor; }
    public String getDebugColor() { return debugColor; }

}
