package tail.log;

public class LogTailArgsValidator {
    public static void validarNumeroDeLinhas(String valor) {
        try {
            int n = Integer.parseInt(valor);
            if (n <= 0) {
                System.err.println("Invalid number of lines: " + n);
                System.err.println("Use positive number or +1 for all lines");
                System.exit(1);
            }
        } catch (NumberFormatException e) {
            System.err.println("Invalid number format: " + valor);
            System.exit(1);
        }
    }
}
