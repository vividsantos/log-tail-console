package tail.log;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class LogTailConsole {
    public static void main(String[] args) {
//        if (args.length == 0) {
//            System.out.println("Por favor, forneça o caminho do arquivo de log como argumento.");
//            return;
//        }

        String filePath = "testelog.txt";
        int linhasDesejadas = 1;
        boolean lerTudo = true;

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];

            if (!arg.startsWith("-") && !arg.startsWith("+")) {
                filePath = arg;
            } else if ("-n".equalsIgnoreCase(arg) && i + 1 < args.length) {
                linhasDesejadas = args[++i] != null ? Integer.parseInt(args[i]) : 10;
            } else if (arg.startsWith("-") && arg.length() > 1 && Character.isDigit(arg.charAt(1))) { // usado pra identificar argumentos com traço seguido de numero sem espaço
                linhasDesejadas = Integer.parseInt(arg.substring(1));
            } else if (arg.startsWith("+") && arg.length() > 1 && Character.isDigit(arg.charAt(1))) {
                lerTudo = true;
            }
        }

        lerArquivo(filePath, linhasDesejadas, lerTudo);
        lerArquivoFiltro(filePath,2, linhasDesejadas, "ERRO");
    }

    private static void lerArquivo(String filePath, int linhasDesejadas, boolean lerTudo) {

        String resultado = leituraArquivo(filePath, linhasDesejadas, lerTudo);
        assert resultado != null;
        System.out.println(resultado.trim());

    }

    private static void lerArquivoFiltro(String filePath, int n, int linhasDesejadas, String filter) {
        String resultado = leituraArquivo(filePath,  linhasDesejadas, true);
        assert resultado != null;
        String[] linhas = resultado.split("\n");
        System.out.println("---------------filtro---------------");
        int count = 0;
        for (int i = linhas.length - 1; i >= 0; i--) {
            if (linhas[i].contains(filter)) {
                System.out.println(linhas[i].trim());
                count++;
                if(count>=n) break;
            }
        }
    }

    private static String leituraArquivo(String filePath, int linhasDesejadas, boolean lerTudo) {
        try (RandomAccessFile raf = new RandomAccessFile(filePath, "r")) {
            long fileLength = raf.length(); // tamanho em bytes
            long pointer = fileLength - 1; // aponta para o último byte do arquivo
            int linhasEncontradas = 0;
            if (lerTudo) {
                linhasDesejadas = (int) (raf.length() - 1);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            while (pointer >= 0 && linhasEncontradas <= linhasDesejadas) {
                raf.seek(pointer);
                int qtByte = raf.readByte();
                baos.write(qtByte);

                if (qtByte == '\n') {
                    linhasEncontradas++;
                }
                pointer--;
            }

            byte[] conteudo = baos.toByteArray();
            for (int i = 0, j = conteudo.length - 1; i < j; i++, j--) {
                byte item = conteudo[i];
                conteudo[i] = conteudo[j];
                conteudo[j] = item;
            }

            return new String(conteudo, StandardCharsets.UTF_8).trim();

        } catch (Exception e) {
            System.out.println("Erro ao ler o arquivo: " + e.getMessage());
            return null;
        }
    }
}

