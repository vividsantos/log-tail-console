package tail.log;

public class LogTailConsole {

    enum Mode {
        FOLLOW, FILTER, REGEX, SHOW, INVALID, EXCLUDE
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
        } else if (parser.exclude != null) {
            mode = Mode.EXCLUDE;
        }

        switch (mode) {
            case INVALID:
                System.out.println("Log path not provided.");
                break;
            case FOLLOW:
                FileService.followingFile(parser.filePath, parser.readAll, parser.wantedLines, parser.filter);
                break;
            case FILTER:
                FileService.showFileWithFilter(parser.filePath, parser.readAll, parser.wantedLines, parser.filter);
                break;
            case REGEX:
                FileService.showFileWithRegex(parser.filePath, parser.readAll, parser.wantedLines, parser.regex);
                break;
            case EXCLUDE:
                FileService.showFileWithExclude(parser.filePath, parser.readAll, parser.wantedLines, parser.exclude);
                break;
            case SHOW:
                FileService.showFile(parser.filePath, parser.readAll, parser.wantedLines);
                break;
        }
    }
}

