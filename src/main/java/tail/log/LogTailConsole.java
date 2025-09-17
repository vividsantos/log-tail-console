package tail.log;

import static tail.log.FileUtils.*;

public class LogTailConsole {

    enum Mode {
        FOLLOW, FILTER, REGEX, SHOW, INVALID
    }

    public static void main(String[] args) {
        LogTailArgsParser parser = new LogTailArgsParser(args);

        Mode mode = Mode.SHOW;
        if (parser.filePath == null) {
            mode = Mode.INVALID;
        } else if (parser.following) {
            mode = Mode.FOLLOW;
        } else if (parser.filter != null) {
            mode = Mode.FILTER;
        } else if (parser.regex != null) {
            mode = Mode.REGEX;
        }

        switch (mode) {
            case INVALID:
                System.out.println("Log path not provided.");
                break;
            case FOLLOW:
                followingFile(parser.filePath, parser.readAll, parser.wantedLines, parser.filter);
                break;
            case FILTER:
                showFileWithFilter(parser.filePath, parser.readAll, parser.wantedLines, parser.filter);
                break;
            case REGEX:
                showFileWithRegex(parser.filePath, parser.readAll, parser.wantedLines, parser.regex);
                break;
            case SHOW:
                showFile(parser.filePath, parser.readAll, parser.wantedLines);
                break;
        }
    }
}

