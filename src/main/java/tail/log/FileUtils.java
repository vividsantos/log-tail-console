package tail.log;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
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
        } catch (Exception e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        return Optional.empty();
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
            String[] linhas = resultado.split("\n");
            int count = 0;
            for (int i = linhas.length - 1; i >= 0; i--) {
                if (linhas[i].toLowerCase().contains(filter.toLowerCase())) {
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

    public static void followingFile(String filePath, boolean readAll, int wantedLines, String filter) {
        showFile(filePath, readAll, wantedLines);

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
