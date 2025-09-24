package tail.log;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class FileService {

    private static final FileReaderUtils fileReader = new FileReaderUtils();

    public static void exportLinesToFile(List<String> linhas, String outputPath) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputPath), StandardCharsets.UTF_8)) {
            for (String linha : linhas) {
                writer.write(linha);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Erro ao exportar linhas: " + e.getMessage());
        }
    }


    public static List<String> showFile(String filePath, boolean readAll, int wantedLines) {
        List<String> linhas = FileReaderUtils.readFile(filePath, readAll, wantedLines);
        linhas.forEach(System.out::println);
        return linhas;
    }

    public static List<String> showFileWithFilter(String filePath, boolean readAll, int wantedLines, String filter) {
        List<String> linhas = FileReaderUtils.filterLines(filePath, readAll, wantedLines, filter);
        linhas.forEach(System.out::println);
        return linhas;
    }

    public static List<String> showFileWithRegex(String filePath, boolean readAll, int wantedLines, String regex) {
        List<String> linhas = FileReaderUtils.filterLinesWithRegex(filePath, readAll, wantedLines, regex);
        linhas.forEach(System.out::println);
        return linhas;
    }

    public static List<String> showFileWithExclude(String filePath, boolean readAll, int wantedLines, String exclude) {
        List<String> linhas = FileReaderUtils.filterLinesWithExclude(filePath, readAll, wantedLines, exclude);
        linhas.forEach(System.out::println);
        return linhas;
    }

    public static void followingFile(String filePath, boolean readAll, int wantedLines, String filter) {
        showFile(filePath, true, 0);

        try (RandomAccessFile raf = new RandomAccessFile(filePath, "r")) {
            long pointer = raf.length();

            while (true) {
                long currentLength = raf.length();
                if (currentLength > pointer) {
                    raf.seek(pointer);
                    String line;
                    while ((line = raf.readLine()) != null) {
                        String decodedLine = new String(line.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
                        if (filter == null || decodedLine.toLowerCase().contains(filter.toLowerCase())) {
                            System.out.println(decodedLine);
                        }
                    }
                    pointer = raf.getFilePointer();
                }
                Thread.sleep(1000);
            }
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + filePath);
        } catch (Exception e) {
            System.out.println("Error during following file: " + e.getMessage());
        }
    }
}