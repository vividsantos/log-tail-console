package tail.log;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Optional;
import java.util.regex.Pattern;

public class FileUtils {

    private static Optional<String> readFile(String filePath, boolean readAll, int wantedLines) {
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
            System.exit(1);
            return null;
        } catch (Exception e) {
            System.err.println("Error reading file: " + e.getMessage());
            System.exit(1);
            return null;
        }
    }

    private static void reverseArray(byte[] array) {
        for (int i = 0, j = array.length - 1; i < j; i++, j--) {
            byte temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }


    public static void showFile(String filePath, boolean readAll, int wantedLines) {
        readFile(filePath, readAll, wantedLines)
                .ifPresent(System.out::println);
    }

    public static void showFileWithFilter(String filePath, boolean readAll, int wantedLines, String filter) {
        readFile(filePath, readAll, wantedLines).ifPresent(resultado -> {
            String[] termos = filter.split("\\|");
            String[] linhas = resultado.split("\n");
            int count = 0;
            for (int i = linhas.length - 1; i >= 0; i--) {
                String linhaLower = linhas[i].toLowerCase();
                boolean encontrou = false;
                for (String termo : termos) {
                    if (linhaLower.contains(termo.toLowerCase())) {
                        encontrou = true;
                        break;
                    }
                }
                if (encontrou) {
                    System.out.println(linhas[i].trim());
                    count++;
                    if (count >= wantedLines) break;
                }
            }
        });
    }

    public static void showFileWithRegex(String filePath, boolean readAll, int wantedLines, String regex) {
        readFile(filePath, readAll, wantedLines).ifPresent(resultado -> {
            Pattern pattern = Pattern.compile(regex);
            String[] linhas = resultado.split("\n");
            for (String linha : linhas) {
                if (pattern.matcher(linha).find()) {
                    System.out.println(linha.trim());
                }
            }
        });
    }

    public static void showFileWithExclude(String filePath, boolean readAll, int wantedLines, String exclude) {
        readFile(filePath, readAll, wantedLines).ifPresent(resultado -> {
            Pattern pattern = Pattern.compile(exclude, Pattern.CASE_INSENSITIVE);
            String[] linhas = resultado.split("\n");
            int count = 0;
            for (int i = linhas.length - 1; i >= 0; i--) {
                if (!pattern.matcher(linhas[i]).find()) {
                    System.out.println(linhas[i].trim());
                }
            }
        });
    }

    public static void followingFile(String filePath, boolean readAll, int wantedLines, String filter) {
        if (filter == null) {
            showFile(filePath, readAll, wantedLines);
        } else {
            showFileWithFilter(filePath, readAll, wantedLines, filter);
        }

        try {
            File file = new File(filePath);
            long lastPosition = file.length();
            Set<String> processedLines = new HashSet<>();

            while (true) {
                if (!file.exists()) {
                    System.out.println("File not found, waiting for creation...");
                    while (!file.exists()) {
//                        Thread.sleep(1000);
                        file = new File(filePath);
                    }
                    System.out.println("File created, resuming monitoring...");
                    processedLines.clear();
                    continue;
                }

                long currentLength = file.length();

                if (currentLength < lastPosition) {
                    System.out.println("Log rotated, switched to new file");
                    lastPosition = 0;
                    processedLines.clear();
                } else if (currentLength > lastPosition) {
                    List<String> newLines = readNewLines(filePath, lastPosition);
                    for (String line : newLines) {
                        if (filter == null || line.toLowerCase().contains(filter.toLowerCase())) {
                            if (!processedLines.contains(line)) {
                                System.out.println(line);
                                processedLines.add(line);
                            }
                        }
                    }
                    lastPosition = currentLength;
                }
            }
        } catch (Exception e) {
            System.err.println("Error following file: " + e.getMessage());
        }
    }

    private static List<String> readNewLines(String filePath, long fromPosition) throws IOException {
        List<String> newLines = new ArrayList<>();

        try (RandomAccessFile raf = new RandomAccessFile(filePath, "r")) {
            raf.seek(fromPosition);
            String line;
            while ((line = raf.readLine()) != null) {
                newLines.add(new String(line.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8));
            }
        }

        return newLines;
    }
}
