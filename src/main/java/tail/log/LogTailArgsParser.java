package tail.log;

public class LogTailArgsParser {
    public String filePath;
    public int linhasDesejadas = 1;
    public boolean lerTudo = false;
    public String filter = null;

    public LogTailArgsParser(String[] args) {
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];

            if (!arg.startsWith("-") && !arg.startsWith("+")) {
                filePath = arg;
            } else if ("-n".equalsIgnoreCase(arg) && i + 1 < args.length) {
                linhasDesejadas = Integer.parseInt(args[++i]);
            } else if (arg.startsWith("-") && arg.length() > 1 && Character.isDigit(arg.charAt(1))) {
                linhasDesejadas = Integer.parseInt(arg.substring(1));
            } else if (arg.startsWith("+") && arg.length() > 1 && Character.isDigit(arg.charAt(1))) {
                lerTudo = true;
            } else if ("-f".equalsIgnoreCase(arg) && i + 1 < args.length) {
                filter = args[++i];
            }
        }
    }
}
