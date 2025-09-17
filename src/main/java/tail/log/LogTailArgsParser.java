package tail.log;

public class LogTailArgsParser {
    public String filePath;
    public int wantedLines = 10;
    public boolean readAll = false;
    public String filter = null;
    public boolean following = false;

    public LogTailArgsParser(String[] args) {
        int arquivosEncontrados = 0;

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];

            if (!arg.startsWith("-") && !arg.startsWith("+") && arg.contains(".")) {
                arquivosEncontrados++;
                if (arquivosEncontrados > 1) {
                    System.err.println("Multiple files not supported in this version");
                    System.err.println("This feature is planned for a future release.");
                    System.err.println("Current usage: java LogTailConsole [OPTIONS] FILE");
                    System.exit(2);
                }
                filePath = arg;
            } else if (arg.equalsIgnoreCase("-n")) {
                if (i + 1 >= args.length) {
                    System.err.println("Missing number of lines after -n");
                    System.exit(1);
                }
                if (args.length >= i + 1  && Character.isDigit(args[i + 1].charAt(0))) {
                    String valor = args[i + 1];
//                    LogTailArgsValidator.validarNumeroDeLinhas(valor);
                    wantedLines = Integer.parseInt(valor);
                } else if (args[i + 1].startsWith("+") && args[i + 1].length() == 2 && args[i + 1].charAt(1) == '1') {
                    readAll = true;
                } else if (args[i + 1].startsWith("+") && Character.isDigit(args[i + 1].charAt(1))) {
                    System.err.println("Option +N is not supported in this version.");
                    System.exit(1);
                } else {
                    System.err.println("Invalid number of lines: " + args[i + 1]);
                    System.err.println("Use positive number or +1 for all lines");
                    System.exit(1);
                }
                i++;
            } else if (arg.startsWith("-") && arg.length() > 1 && Character.isDigit(arg.charAt(1))) {
                if (Integer.parseInt(arg.substring(1)) <= 0) {
                    System.err.println("Invalid number of lines: " + arg.substring(1));
                    System.err.println("Use positive number or +1 for all lines");
                    System.exit(1);
                }

                wantedLines = Integer.parseInt(arg.substring(1));
            } else if (arg.startsWith("+") && arg.length() == 2 && arg.charAt(1) == '1') {
                readAll = true;
            } else if (arg.equalsIgnoreCase("--filter") && i + 1 < args.length) {
                filter = args[i + 1];
                i++;
            } else if (arg.equalsIgnoreCase("-f") || arg.equalsIgnoreCase("--follow")) {
                following = true;
            } else {
                System.err.println("Unknown argument: " + arg);
                System.exit(1);
            }

        }
    }
}
