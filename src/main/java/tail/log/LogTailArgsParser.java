package tail.log;

import java.util.Objects;

public class LogTailArgsParser {
    public String filePath;
    public int linhasDesejadas = 10;
    public boolean lerTudo = false;
    public String filter = null;

    public LogTailArgsParser(String[] args) {
        int arquivosEncontrados = 0;

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];

            if (!arg.startsWith("-") && !arg.startsWith("+") && filter == null) {
                arquivosEncontrados++;
                if (arquivosEncontrados > 1) {
                    System.err.println("Multiple files not supported in this version");
                    System.err.println("Process one file at a time");
                    System.exit(2);
                }
                filePath = arg;
            }else if ("-n".equalsIgnoreCase(arg)) {
                String valor = args[++i];
                if("+1".equals(valor)) {
                    lerTudo = true;
                }
                LogTailArgsValidator.validarNumeroDeLinhas(valor);
                linhasDesejadas = Integer.parseInt(valor);

//            } else if (arg.startsWith("-") && arg.length() > 1 && Character.isDigit(arg.charAt(1))) {
//                linhasDesejadas = Integer.parseInt(arg.substring(1));
            }  else if ("--filter".equalsIgnoreCase(arg) && i + 1 < args.length) {
                filter = args[++i];
            }
        }
    }
}
