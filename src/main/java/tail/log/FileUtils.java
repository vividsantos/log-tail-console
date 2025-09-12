package tail.log;

import java.io.ByteArrayOutputStream;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;

public class FileUtils {

    private static String leituraArquivo(String filePath, boolean lerTudo, int linhasDesejadas) {
        try (RandomAccessFile raf = new RandomAccessFile(filePath, "r")) {
            long fileLength = raf.length();
            long pointer = fileLength - 1;
            int linhasEncontradas = 0;
            StringBuilder sb = new StringBuilder();

            if (lerTudo) {
                raf.seek(0);
                byte[] conteudo = new byte[(int) fileLength];
                raf.readFully(conteudo);
                return new String(conteudo, StandardCharsets.UTF_8).trim();
            }

            while (pointer >= 0 && linhasEncontradas < linhasDesejadas) {
                raf.seek(pointer);
                int b = raf.readByte();
                sb.append((char) b);
                if (b == '\n') {
                    linhasEncontradas++;
                }
                pointer--;
            }

            // Inverte o resultado para ordem correta
            sb.reverse();
            return sb.toString().trim();

        } catch (Exception e) {
            System.out.println("Erro ao ler o arquivo: " + e.getMessage());
            return null;
        }
    }

    public static void lerArquivo(String filePath, boolean lerTudo, int linhasDesejadas) {

        String resultado = leituraArquivo(filePath, lerTudo, linhasDesejadas);
        assert resultado != null;
        System.out.println(resultado.trim());

    }

    public static void lerArquivoFiltro(String filePath, int linhasDesejadas, String filter) {
        String resultado = leituraArquivo(filePath, true, 4); //por ler tudo ser true não importa o numero de linhas
        assert resultado != null;
        String[] linhas = resultado.split("\n");

        int count = 0;
        for (int i = linhas.length - 1; i >= 0; i--) {
            if (linhas[i].contains(filter)) {
                System.out.println(linhas[i].trim());
                count++;
                if(count>=linhasDesejadas) break;
            }
        }
    }
}
