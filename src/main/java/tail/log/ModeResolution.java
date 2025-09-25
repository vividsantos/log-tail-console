package tail.log;

public class ModeResolution {
    LineFilter filterFunc;
    String filterParam;
    boolean isFollow;

    public ModeResolution(LogTailArgsParser parser) {
        filterFunc = (fp, ra, wl, f) -> FileReaderUtils.readFile(fp, ra, wl);
        filterParam = null;
//        if (parser.following) {
//            isFollow = true;
//            filterFunc = (fp, ra, wl, f) -> FileService.showFile(fp, ra, wl);
//            return;
//        }
        if (parser.filter != null) {
            filterFunc = FileReaderUtils::filterLines;
            filterParam = parser.filter;
            return;
        }
        if (parser.regex != null) {
            filterFunc = FileReaderUtils::filterLinesWithRegex;
            filterParam = parser.regex;
            return;
        }
        if (parser.exclude != null) {
            filterFunc = FileReaderUtils::filterLinesWithExclude;
            filterParam = parser.exclude;
        }
    }
}
