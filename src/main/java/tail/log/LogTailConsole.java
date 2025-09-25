package tail.log;

public class LogTailConsole {

    public static void main(String[] args) {

        LogTailArgsParser parser = new LogTailArgsParser(args);
        ModeResolution mode = new ModeResolution(parser);

        if(mode.isFollow){
            FileService.followingFile(parser.filePath, parser.readAll, parser.wantedLines, mode.filterParam);
        } else{
            FileService.showFileGeneric(
                    parser.filePath,
                    parser.readAll,
                    parser.wantedLines,
                    mode.filterParam,
                    mode.filterFunc
            );
        }

        if (parser.export != null) {
            FileService.exportLinesToFile(
                    parser.filePath,
                    parser.readAll,
                    parser.wantedLines,
                    mode.filterParam,
                    mode.filterFunc,
                    parser.export
            );
        }
//        List<String> linhas = new ArrayList<>();
//        switch (mode){
//            case INVALID:
//                System.out.println("Log path not provided.");
//                break;
//            case FOLLOW:
//                FileService.followingFile(parser.filePath, parser.readAll, parser.wantedLines, parser.filter);
//                break;
//            case FILTER:
//                linhas = FileService.showFileWithFilter(parser.filePath, parser.readAll, parser.wantedLines, parser.filter);
//                break;
//            case REGEX:
//                linhas = FileService.showFileWithRegex(parser.filePath, parser.readAll, parser.wantedLines, parser.regex);
//                break;
//            case EXCLUDE:
//                linhas = FileService.showFileWithExclude(parser.filePath, parser.readAll, parser.wantedLines, parser.exclude);
//                break;
//            case SHOW:
//                linhas = FileService.showFile(parser.filePath, parser.readAll, parser.wantedLines);
//                break;
//            default:
//                System.out.println("Nenhuma opção foi identificada");
//                break;
//        }
//
//        if(parser.export != null){
//            System.out.println("Log path excluded.");
//            FileService.exportLinesToFile(linhas, parser.export);
//        }
    }
}

