package tail.log;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class FileReaderUtils {

    private static void reverseArray(byte[] array) {
        for (int i = 0, j = array.length - 1; i < j; i++, j--) {
            byte temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }

    public static List<String> readFile(String filePath, boolean readAll, int wantedLines) {
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
            String[] linhas = new String(conteudo, StandardCharsets.UTF_8).split("\n");
            return Arrays.asList(linhas);

        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + filePath);
        } catch (Exception e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    public static List<String> filterLines(String filePath, boolean readAll, int wantedLines, String filter) {
        List<String> result = new ArrayList<>();
        List<String> linhas = readFile(filePath, readAll, wantedLines);
        String[] termos = filter.split("\\|");
        for (String linha : linhas) {
            String linhaLower = linha.toLowerCase();
            for (String termo : termos) {
                if (linhaLower.contains(termo.toLowerCase())) {
                    result.add(linha);
                    break;
                }
            }
        }
        return result;
    }

    public static List<String> filterLinesWithRegex(String filePath, boolean readAll, int wantedLines, String regex) {
        List<String> result = new ArrayList<>();
        List<String> linhas = readFile(filePath, readAll, wantedLines);
        Pattern pattern = Pattern.compile(regex);
        for (String linha : linhas) {
            if (pattern.matcher(linha).find()) {
                result.add(linha);
            }
        }
        return result;
    }

    public static List<String> filterLinesWithExclude(String filePath, boolean readAll, int wantedLines, String exclude) {
        List<String> result = new ArrayList<>();
        List<String> linhas = readFile(filePath, readAll, wantedLines);
        Pattern pattern = Pattern.compile(exclude, Pattern.CASE_INSENSITIVE);
        for (String linha : linhas) {
            if (!pattern.matcher(linha).find()) {
                result.add(linha);
            }
        }
        return result;
    }
}
