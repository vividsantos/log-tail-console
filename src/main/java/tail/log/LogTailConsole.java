package tail.log;

import java.io.IOException;

import static tail.log.ColorUtils.setCustomConfig;
import static tail.log.FileUtils.*;

public class LogTailConsole {

    enum Mode {
        FOLLOW, FILTER, REGEX, SHOW, INVALID, EXCLUDE
    }

    public static void main(String[] args) {
        LogTailArgsParser parser = new LogTailArgsParser(args);
        CustomConfig customConfig = null;
        FileUtils fileUtils = new FileUtils();
        fileUtils.setColorScheme(parser.colorScheme);

        if (parser.colorConfigPath != null) {
            try {
                customConfig = CustomConfig.loadFromFile(parser.colorConfigPath);
                parser.colorScheme = ColorScheme.CUSTOM;
                fileUtils.setColorScheme(parser.colorScheme);
                setCustomConfig(customConfig);
            } catch (IOException e) {
                System.err.println("Error loading color config: " + e.getMessage());
                System.exit(1);
            }
        }

        Mode mode = Mode.SHOW;
        if (parser.filePath == null) {
            mode = Mode.INVALID;
        } else if (parser.following) {
            mode = Mode.FOLLOW;
        } else if (parser.filter != null) {
            mode = Mode.FILTER;
        } else if (parser.regex != null) {
            mode = Mode.REGEX;
        } else if (parser.exclude != null) {
            mode = Mode.EXCLUDE;
        }

        switch (mode) {
            case INVALID:
                System.err.println("Missing file argument.");
                System.err.println("Usage: java LogTailConsole [OPTIONS] FILE");
                break;
            case FOLLOW:
                followingFile(parser.filePath, parser.readAll, parser.wantedLines, parser.filter, parser.regex, parser.exclude);
                break;
            case FILTER:
                showFileWithFilter(parser.filePath, parser.readAll, parser.wantedLines, parser.filter);
                break;
            case REGEX:
                showFileWithRegex(parser.filePath, parser.readAll, parser.wantedLines, parser.regex);
                break;
            case EXCLUDE:
                showFileWithExclude(parser.filePath, parser.readAll, parser.wantedLines, parser.exclude);
                break;
            case SHOW:
                showFile(parser.filePath, parser.readAll, parser.wantedLines);
                break;
        }
    }



}

