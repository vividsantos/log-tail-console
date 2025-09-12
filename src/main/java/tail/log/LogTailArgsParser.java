package tail.log;

public class LogTailArgsParser {
    public String filePath;
    public int linhasDesejadas = 1;
    public boolean lerTudo = false;
    public String filter = null;

    public LogTailArgsParser(String[] args) {
        int arquivosEncontrados = 0;

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];

            if (!arg.startsWith("-") && !arg.startsWith("+")) {
                arquivosEncontrados++;
                if (arquivosEncontrados > 1) {
                    System.err.println("Multiple files not supported in this version");
                    System.err.println("Process one file at a time");
                    System.exit(2);
                }
                filePath = arg;
            } else if ("-n".equalsIgnoreCase(arg)) {
                if (i + 1 >= args.length || args[i + 1].startsWith("-")) {
                    System.err.println("Insira o número de linhas desejadas");
                    System.exit(1);
                }
                String valor = args[++i];
                LogTailArgsValidator.validarNumeroDeLinhas(valor);
                linhasDesejadas = Integer.parseInt(valor);
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
