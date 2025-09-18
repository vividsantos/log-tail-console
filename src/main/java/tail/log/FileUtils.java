package tail.log;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {
    private static BasicFileAttributes fileAttributes;
    private static FileTime creationTime;

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
        if (filter == null) {
            showFile(filePath, readAll, wantedLines);
        } else {
            showFileWithFilter(filePath, readAll, wantedLines, filter);
        }

        try {
            File file = new File(filePath);
            long lastPosition = file.length();
            fileAttributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
            creationTime = fileAttributes.creationTime();

            while (true) {
                if (!file.exists()) {
                    System.out.println("File not found, waiting for creation...");
                    while (!file.exists()) {
                        Thread.sleep(1000);
                        file = new File(filePath);
                    }
                    System.out.println("File created, resuming monitoring...");
                    lastPosition = 0;
                    continue;
                }

                long currentLength = file.length();
                long currentModified = file.lastModified();
                FileTime currentCreationTime = Files.readAttributes(file.toPath(), BasicFileAttributes.class).creationTime();

                if (!currentCreationTime.equals(creationTime)) {
                    System.out.println("Log rotated, switching to new file");
                    creationTime = currentCreationTime;
                    lastPosition = 0;
                } // Detecta rotação (arquivo zerado e modificado recentemente)
                else if (currentLength < lastPosition) {
                    System.out.println("Log file truncated, restarting from beginning");
                    lastPosition = 0;
                }

                // Detecta rotação: arquivo menor que a última posição ou data modificação muito recente
//                if (currentLength < lastPosition || (currentLength == 0 && currentModified > lastModified)) {
//                    System.out.println("Log rotated, switching to new file");
//                    lastPosition = 0;
//                    lastModified = currentModified;
//                }

                // Lê novas linhas se o arquivo cresceu
                else if (currentLength > lastPosition) {
                    List<String> newLines = readNewLines(filePath, lastPosition);
                    for (String line : newLines) {
                        if (filter == null || line.toLowerCase().contains(filter.toLowerCase())) {
                            System.out.println(line);
                        }
                    }
                    lastPosition = currentLength;
                }

                Thread.sleep(1000);
            }
        } catch (Exception e) {
            System.err.println("Error following file: " + e.getMessage());
        }

//        try {
//            File file = new File(filePath);
//            long fileLength = file.length();
//            long pointer = fileLength;
//
//            BasicFileAttributes bfattr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
//            long creationTime = bfattr.creationTime().toInstant().toEpochMilli();
//
//
//            while (true) {
//                if (!file.exists()) {
//                    System.out.println("File deleted, waiting for it to be recreated...");
//                    while (!file.exists()) {
//                        Thread.sleep(1000);
//                    }
//                    System.out.println("File recreated, resuming...");
//                    followingFile(filePath, false, 0, filter);
//                    return;
//                }
//
//
//                try (RandomAccessFile raf = new RandomAccessFile(filePath, "r")) {
//                    long currentLength = file.length();
//                    BasicFileAttributes currentAttrs = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
//                    long currentCreationTime = currentAttrs.creationTime().toInstant().toEpochMilli();
//
//                    if (currentCreationTime != creationTime) {
//                        System.out.println("Log file rotated, starting from beginning");
//                        pointer = 0;
//                        followingFile(filePath, false, 0, filter);
//                        return;
//                    } else if (currentLength < pointer) {
//                        System.out.println("Log file truncated, restarting from beginning");
//                        pointer = 0;
//                        raf.seek(0);
//                    } else if (currentLength > pointer) {
//                        raf.seek(pointer);
//                        String line;
//                        while ((line = raf.readLine()) != null) {
//                            String decodedLine = new String(line.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
//                            if (filter == null || decodedLine.toLowerCase().contains(filter.toLowerCase())) {
//                                System.out.println(decodedLine);
//                            }
//                        }
//                        pointer = raf.getFilePointer();
//                    }
//
//                    creationTime = currentCreationTime;
//                    currentLength = fileLength;
//                    Thread.sleep(1000);
//                }
//            }
//        } catch (FileNotFoundException e) {
//            System.err.println("File not found: " + filePath);
//            System.exit(1);
//        } catch (Exception e) {
//            System.out.println("Error during following file: " + e.getMessage());
//            System.exit(1);
//        }
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

    private static long getFileInode(File file) {
        try {
            BasicFileAttributes attrs = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
            Object fileKey = attrs.fileKey();
            return fileKey != null ? fileKey.hashCode() : file.lastModified();
        } catch (Exception e) {
            return file.lastModified();
        }
    }
}
