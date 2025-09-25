package tail.log;

import java.util.List;

@FunctionalInterface
public interface LineFilter {
    List<String> apply(String filePath, boolean readAll, int wantedLines, String filter);
}
