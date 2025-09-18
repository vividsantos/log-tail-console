package tail.log;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public class FileReaderUtils {

    private static void reverseArray(byte[] array) {
        for (int i = 0, j = array.length - 1; i < j; i++, j--) {
            byte temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }

    public static Optional<String> readFile(String filePath, boolean readAll, int wantedLines) {
        try (RandomAccessFile raf = new RandomAccessFile(filePath, "r")) {
            long fileLength = raf.length();
            long pointer = fileLength - 1;
            int foundLines = 0;

            if (readAll) {
                wantedLines = (int) (raf.length() - 1);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            while (pointer >= 0 && foundLines <= wantedLines) {
                raf.seek(pointer);
                int qtByte = raf.readByte();
                baos.write(qtByte);

                if (qtByte == '\n') {
                    foundLines++;
                }
                pointer--;
            }

            byte[] conteudo = baos.toByteArray();
            reverseArray(conteudo);

            return Optional.of(new String(conteudo, StandardCharsets.UTF_8).trim());

        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + filePath);
        } catch (Exception e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        return Optional.empty();
    }

    public static List<String> filterLines(String filePath, boolean readAll, int wantedLines, String filter) {
        List<String> result = new ArrayList<>();
        readFile(filePath, readAll, wantedLines).ifPresent(conteudo -> {
            String[] termos = filter.split("\\|");
            String[] linhas = conteudo.split("\n");
            for (String linha : linhas) {
                String linhaLower = linha.toLowerCase();
                for (String termo : termos) {
                    if (linhaLower.contains(termo.toLowerCase())) {
                        result.add(linha);
                        break;
                    }
                }
            }
        });
        return result;
    }

    public static List<String> filterLinesWithRegex(String filePath, boolean readAll, int wantedLines, String regex) {
        List<String> result = new ArrayList<>();
        readFile(filePath, readAll, wantedLines).ifPresent(conteudo -> {
            Pattern pattern = Pattern.compile(regex);
            String[] linhas = conteudo.split("\n");
            for (String linha : linhas) {
                if (pattern.matcher(linha).find()) {
                    result.add(linha);
                }
            }
        });
        return result;
    }

    public static List<String> filterLinesWithExclude(String filePath, boolean readAll, int wantedLines, String exclude) {
        List<String> result = new ArrayList<>();
        readFile(filePath, readAll, wantedLines).ifPresent(conteudo -> {
            Pattern pattern = Pattern.compile(exclude, Pattern.CASE_INSENSITIVE);
            String[] linhas = conteudo.split("\n");
            for (String linha : linhas) {
                if (!pattern.matcher(linha).find()) {
                    result.add(linha);
                }
            }
        });
        return result;
    }
}
