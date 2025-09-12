package tail.log;

import java.io.ByteArrayOutputStream;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;

public class FileUtils {

    private static String leituraArquivo(String filePath, boolean lerTudo, int linhasDesejadas) {
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

    public static void lerArquivo(String filePath, int linhasDesejadas, boolean lerTudo) {

        String resultado = leituraArquivo(filePath, lerTudo, linhasDesejadas);
        assert resultado != null;
        System.out.println(resultado.trim());

    }

    public static void lerArquivoFiltro(String filePath, int linhasDesejadas, String filter) {
        String resultado = leituraArquivo(filePath, true, 4); //por ler tudo ser true não importa o numero de linhas
        assert resultado != null;
        String[] linhas = resultado.split("\n");
        System.out.println("---------------filtro---------------");
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
