package tail.log.themes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ColorUtils {
    private static CustomConfig customConfig = null;

    public static void setCustomConfig(CustomConfig config) {
        customConfig = config;
    }

    public static List<String> previewColors(String scheme) {
        if (
            scheme.equalsIgnoreCase("default") ||
            scheme.equalsIgnoreCase("dark") ||
            scheme.equalsIgnoreCase("light") ||
            scheme.equalsIgnoreCase("high-contrast") ||
            scheme.equalsIgnoreCase("minimal")
        ) {
            ColorScheme colorPreview = ColorScheme.valueOf(scheme.toUpperCase().replace("-", "_"));

            List<String> preview = Arrays.asList("[ERROR] Sample error message", "[WARN]  Sample warning message", "[INFO]  Sample info message", "[DEBUG] Sample debug message");

            return coloredLines(colorPreview, preview);
        } else {
            return null;
        }
    }

    public static void previewCustomColors(CustomConfig config) {
        String errorColor = config.getColorCode(config.getErrorColor());
        System.out.println(errorColor + "[ERROR] This is an error message" + ConsoleColors.RESET);

        String warnColor = config.getColorCode(config.getWarnColor());
        System.out.println(warnColor + "[WARN]  This is a warning message" + ConsoleColors.RESET);

        String infoColor = config.getColorCode(config.getInfoColor());
        System.out.println(infoColor + "[INFO]  This is an info message" + ConsoleColors.RESET);

        String debugColor = config.getColorCode(config.getDebugColor());
        System.out.println(debugColor + "[DEBUG] This is a debug message" + ConsoleColors.RESET);
    }

    public static ColorScheme defineColorScheme(String scheme) {
        if (
            scheme.equalsIgnoreCase("default") ||
            scheme.equalsIgnoreCase("dark") ||
            scheme.equalsIgnoreCase("light") ||
            scheme.equalsIgnoreCase("high-contrast") ||
            scheme.equalsIgnoreCase("minimal") ||
            scheme.equalsIgnoreCase("custom")
        ) {
            return ColorScheme.valueOf(scheme.toUpperCase().replace("-", "_"));
        } else {
            return null;
        }
    }

    public static List<String> coloredLines(ColorScheme colorScheme, List<String> linhas) {
        List<String> coloredLines = new ArrayList<>();
        if (colorScheme == ColorScheme.CUSTOM && customConfig != null) {
            for (String line : linhas) {
                if (line.contains("ERROR")) {
                    String color = customConfig.getColorCode(customConfig.getErrorColor());
                    coloredLines.add(color + line + ConsoleColors.RESET);
                } else if (line.contains("WARN")) {
                    String color = customConfig.getColorCode(customConfig.getWarnColor());
                    coloredLines.add(color + line + ConsoleColors.RESET);
                } else if (line.contains("INFO")) {
                    String color = customConfig.getColorCode(customConfig.getInfoColor());
                    coloredLines.add(color + line + ConsoleColors.RESET);
                } else if (line.contains("DEBUG")) {
                    String color = customConfig.getColorCode(customConfig.getDebugColor());
                    coloredLines.add(color + line + ConsoleColors.RESET);
                } else {
                    coloredLines.add(line);
                }
            }
        } else if (colorScheme == ColorScheme.DEFAULT) {
            for (String line : linhas) {
                if (line.contains("ERROR")) {
                    coloredLines.add(ConsoleColors.RED + line + ConsoleColors.RESET);
                } else if (line.contains("WARN")) {
                    coloredLines.add(ConsoleColors.YELLOW + line + ConsoleColors.RESET);
                } else if (line.contains("INFO")) {
                    coloredLines.add(ConsoleColors.GREEN + line + ConsoleColors.RESET);
                } else if (line.contains("DEBUG")) {
                    coloredLines.add(ConsoleColors.GRAY + line + ConsoleColors.RESET);
                } else {
                    coloredLines.add(line);
                }
            }
        } else if (colorScheme == ColorScheme.DARK) {
            for (String line : linhas) {
                if (line.contains("ERROR")) {
                    coloredLines.add(ConsoleColors.BRIGHT_RED + line + ConsoleColors.RESET);
                } else if (line.contains("WARN")) {
                    coloredLines.add(ConsoleColors.BRIGHT_YELLOW + line + ConsoleColors.RESET);
                } else if (line.contains("INFO")) {
                    coloredLines.add(ConsoleColors.BRIGHT_GREEN + line + ConsoleColors.RESET);
                } else if (line.contains("DEBUG")) {
                    coloredLines.add(ConsoleColors.WHITE + line + ConsoleColors.RESET);
                } else {
                    coloredLines.add(ConsoleColors.BRIGHT_WHITE + line + ConsoleColors.RESET);
                }
            }
        } else if (colorScheme == ColorScheme.LIGHT) {
            for (String line : linhas) {
                if (line.contains("ERROR")) {
                    coloredLines.add(ConsoleColors.DARK_RED + line + ConsoleColors.RESET);
                } else if (line.contains("WARN")) {
                    coloredLines.add(ConsoleColors.BROWN + line + ConsoleColors.RESET);
                } else if (line.contains("INFO")) {
                    coloredLines.add(ConsoleColors.DARK_GREEN + line + ConsoleColors.RESET);
                } else if (line.contains("DEBUG")) {
                    coloredLines.add(ConsoleColors.DARK_BLUE + line + ConsoleColors.RESET);
                } else {
                    coloredLines.add(ConsoleColors.BLACK + line + ConsoleColors.RESET);
                }
            }
        } else if (colorScheme == ColorScheme.HIGH_CONTRAST) {
            for (String line : linhas) {
                if (line.contains("ERROR")) {
                    coloredLines.add(ConsoleColors.BOLD_RED + line + ConsoleColors.RESET);
                } else if (line.contains("WARN")) {
                    coloredLines.add(ConsoleColors.BOLD_YELLOW + line + ConsoleColors.RESET);
                } else if (line.contains("INFO")) {
                    coloredLines.add(ConsoleColors.BOLD_CYAN + line + ConsoleColors.RESET);
                } else if (line.contains("DEBUG")) {
                    coloredLines.add(ConsoleColors.BOLD_MAGENTA + line + ConsoleColors.RESET);
                } else {
                    coloredLines.add(ConsoleColors.BOLD_WHITE + line + ConsoleColors.RESET);
                }
            }
        } else if (colorScheme == ColorScheme.MINIMAL) {
            for (String line : linhas) {
                if (line.contains("ERROR")) {
                    coloredLines.add(ConsoleColors.RED + line + ConsoleColors.RESET);
                } else if (line.contains("WARN")) {
                    coloredLines.add(ConsoleColors.YELLOW + line + ConsoleColors.RESET);
                } else {
                    coloredLines.add(line);
                }
            }
        }

        return coloredLines;
    }
}
