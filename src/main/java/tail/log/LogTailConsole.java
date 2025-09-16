package tail.log;

import static tail.log.FileUtils.*;

public class LogTailConsole {
    public static void main(String[] args) {
        LogTailArgsParser parser = new LogTailArgsParser(args);

         if (parser.filePath == null) {
             System.out.println("Log path not provided.");
             return;
         }

        if (parser.following) {
            followingFile(parser.filePath, parser.readAll, parser.wantedLines, parser.filter);
            return;
        }

        if (parser.filter != null) {
            showFileWithFilter(parser.filePath, parser.readAll, parser.wantedLines, parser.filter);
            return;
        }

        showFile(parser.filePath, parser.readAll, parser.wantedLines);
    }
}

