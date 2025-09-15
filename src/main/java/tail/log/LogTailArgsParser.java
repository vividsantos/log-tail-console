package tail.log;

public class LogTailArgsParser {
    public String filePath;
    public int linhasDesejadas = 10;
    public boolean lerTudo = false;
    public String filter = null;

    public LogTailArgsParser(String[] args) {
        int arquivosEncontrados = 0;

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];

            if (!arg.startsWith("-") && !arg.startsWith("+") && arg.contains(".")) {
                arquivosEncontrados++;
                if (arquivosEncontrados > 1) {
                    System.err.println("Multiple files not supported in this version");
                    System.err.println("Process one file at a time");
                    System.exit(2);
                }
                filePath = arg;
            } else if (arg.equalsIgnoreCase("-n")) {
                if (args.length >= i + 1  && Character.isDigit(args[i + 1].charAt(0))) {
                    String valor = args[i + 1];
                    LogTailArgsValidator.validarNumeroDeLinhas(valor);
                    linhasDesejadas = Integer.parseInt(valor);
                } else if (args[i + 1].startsWith("+") && args[i + 1].length() > 1 && Character.isDigit(args[i + 1].charAt(1))) {
                    lerTudo = true;
                } else if (i + 1 >= args.length || args[i + 1].startsWith("-")) {
                    System.err.println("Invalid number of lines: " + null);
                    System.err.println("Use positive number or +1 for all lines");
                    System.exit(1);
                }
            } else if (arg.startsWith("-") && arg.length() > 1 && Character.isDigit(arg.charAt(1))) {
                linhasDesejadas = Integer.parseInt(arg.substring(1));
            } else if ("--filter".equalsIgnoreCase(arg) && i + 1 < args.length) {
                filter = args[i + 1];
            }
        }
    }
}
