package tail.log;

import static tail.log.FileUtils.*;

public class LogTailConsole {
    public static void main(String[] args) {
        LogTailArgsParser parser = new LogTailArgsParser(args);

         if (parser.filePath == null) {
             System.err.println("Missing file argument.");
             System.err.println("Usage: java LogTailConsole [OPTIONS] FILE");
             return;
         }

        if(parser.filter != null) {
            showFileWithFilter(parser.filePath, parser.readAll, parser.wantedLines, parser.filter);
        }

        if (parser.following) {
            followingFile(parser.filePath, parser.readAll, parser.wantedLines, parser.filter);
        }

        showFile(parser.filePath, parser.readAll, parser.wantedLines);
    }
}

