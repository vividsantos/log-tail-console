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

            String resultado = new String(conteudo, StandardCharsets.UTF_8);
            return resultado.trim();

        } catch (Exception e) {
            System.out.println("Erro ao ler o arquivo: " + e.getMessage());
            return null;
        }
    }

    public static void exibirArquivo(String filePath, boolean lerTudo, int linhasDesejadas) {
        String resultado = leituraArquivo(filePath, lerTudo, linhasDesejadas);
        if (resultado != null) {
            System.out.println(resultado);
            return;
        }
    }

    public static void exibirArquivoFiltro(String filePath, boolean lerTudo, int linhasDesejadas, String filter) {
        String resultado = leituraArquivo(filePath, lerTudo, linhasDesejadas);
        assert resultado != null;
        String[] linhas = resultado.split("\n");

        int count = 0;
        for (int i = linhas.length - 1; i >= 0; i--) {
            if (linhas[i].toLowerCase().contains(filter.toLowerCase())) {
                System.out.println(linhas[i].trim());
                count++;
                if(count>=linhasDesejadas) break;
            }
        }
    }
}
