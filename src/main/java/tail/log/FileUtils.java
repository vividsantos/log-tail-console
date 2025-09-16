package tail.log;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;

public class FileUtils {

    private static String readFile(String filePath, boolean readAll, int wantedLines) {
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
            for (int i = 0, j = conteudo.length - 1; i < j; i++, j--) {
                byte item = conteudo[i];
                conteudo[i] = conteudo[j];
                conteudo[j] = item;
            }

            String resultado = new String(conteudo, StandardCharsets.UTF_8);
            return resultado.trim();

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

    public static void showFile(String filePath, boolean readAll, int wantedLines) {
        String resultado = readFile(filePath, readAll, wantedLines);
        if (resultado != null) {
            System.out.println(resultado);
            return;
        }
    }

    public static void showFileWithFilter(String filePath, boolean readAll, int wantedLines, String filter) {
        String resultado = readFile(filePath, readAll, wantedLines);
        assert resultado != null;
        String[] linhas = resultado.split("\n");

        int count = 0;
        for (int i = linhas.length - 1; i >= 0; i--) {
            if (linhas[i].toLowerCase().contains(filter.toLowerCase())) {
                System.out.println(linhas[i].trim());
                count++;
                if(count>=wantedLines) break;
            }
        }
    }

    public static void followingFile(String filePath, boolean readAll, int wantedLines, String filter) {
        showFile(filePath, readAll, wantedLines);

        try (RandomAccessFile raf = new RandomAccessFile(filePath, "r")) {
            long fileLength = raf.length();
            long pointer = fileLength;

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
            System.exit(1);
        } catch (Exception e) {
            System.out.println("Error during following file: " + e.getMessage());
            System.exit(1);
        }
    }
}
