package tail.log;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

public class FileUtils {

    public static ColorScheme colorScheme = ColorScheme.DEFAULT;

    public void setColorScheme(ColorScheme colorScheme) {
        FileUtils.colorScheme = colorScheme;
    }

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
            return Optional.empty();
        } catch (Exception e) {
            System.err.println("Error reading file: " + e.getMessage());
            System.exit(1);
            return Optional.empty();
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
        List<String> lines = readFile(filePath, readAll, wantedLines)
                .map(resultado -> Arrays.asList(resultado.split("\n")))
                .orElse(new ArrayList<>());

        coloredLines(colorScheme, lines).forEach(System.out::println);
    }

    public static void showFileWithFilter(String filePath, boolean readAll, int wantedLines, String filter) {
        List<String> lines = new ArrayList<>(Collections.emptyList());
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
                    lines.add(linhas[i].trim());
                    count++;
                    if (count >= wantedLines) break;
                }
            }
        });

        coloredLines(colorScheme, lines).forEach(System.out::println);
    }

    public static void showFileWithRegex(String filePath, boolean readAll, int wantedLines, String regex) {
        List<String> lines = new ArrayList<>(Collections.emptyList());
        readFile(filePath, readAll, wantedLines).ifPresent(resultado -> {
            Pattern pattern = Pattern.compile(regex);
            String[] linhas = resultado.split("\n");
            for (String linha : linhas) {
                if (pattern.matcher(linha).find()) {
                    lines.add(linha.trim());
                }
            }
        });

        coloredLines(colorScheme, lines).forEach(System.out::println);
    }

    public static void showFileWithExclude(String filePath, boolean readAll, int wantedLines, String exclude) {
        List<String> lines = new ArrayList<>(Collections.emptyList());
        readFile(filePath, readAll, wantedLines).ifPresent(resultado -> {
            Pattern pattern = Pattern.compile(exclude, Pattern.CASE_INSENSITIVE);
            String[] linhas = resultado.split("\n");
            int count = 0;
            for (int i = linhas.length - 1; i >= 0; i--) {
                if (!pattern.matcher(linhas[i]).find()) {
                    lines.add(linhas[i].trim());
                }
            }
        });

        coloredLines(colorScheme, lines).forEach(System.out::println);
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

        return coloredLines(colorScheme, newLines);
    }

    private static List<String> coloredLines(ColorScheme colorScheme, List<String> linhas) {
        List<String> coloredLines = new ArrayList<>();
        if (colorScheme == ColorScheme.DEFAULT) {
            for (String line : linhas) {
                if (line.contains("ERROR")) {
                    coloredLines.add(ConsoleColors.RED + line + ConsoleColors.RESET);
                } else if (line.contains("WARN")) {
                    coloredLines.add(ConsoleColors.YELLOW + line + ConsoleColors.RESET);
                } else if (line.contains("INFO")) {
                    coloredLines.add(ConsoleColors.GREEN + line + ConsoleColors.RESET);
                } else if (line.contains("DEBUG")) {
                    coloredLines.add(ConsoleColors.GRAY + line + ConsoleColors.RESET);
                } else {
                    coloredLines.add(line);
                }
            }
        } else if (colorScheme == ColorScheme.DARK) {
            for (String line : linhas) {
                if (line.contains("ERROR")) {
                    coloredLines.add(ConsoleColors.BRIGHT_RED + line + ConsoleColors.RESET);
                } else if (line.contains("WARN")) {
                    coloredLines.add(ConsoleColors.BRIGHT_YELLOW + line + ConsoleColors.RESET);
                } else if (line.contains("INFO")) {
                    coloredLines.add(ConsoleColors.BRIGHT_GREEN + line + ConsoleColors.RESET);
                } else if (line.contains("DEBUG")) {
                    coloredLines.add(ConsoleColors.WHITE + line + ConsoleColors.RESET);
                } else {
                    coloredLines.add(ConsoleColors.BRIGHT_WHITE + line + ConsoleColors.RESET);
                }
            }
        } else if (colorScheme == ColorScheme.LIGHT) {
            for (String line : linhas) {
                if (line.contains("ERROR")) {
                    coloredLines.add(ConsoleColors.DARK_RED + line + ConsoleColors.RESET);
                } else if (line.contains("WARN")) {
                    coloredLines.add(ConsoleColors.BROWN + line + ConsoleColors.RESET);
                } else if (line.contains("INFO")) {
                    coloredLines.add(ConsoleColors.DARK_GREEN + line + ConsoleColors.RESET);
                } else if (line.contains("DEBUG")) {
                    coloredLines.add(ConsoleColors.DARK_BLUE + line + ConsoleColors.RESET);
                } else {
                    coloredLines.add(ConsoleColors.BLACK + line + ConsoleColors.RESET);
                }
            }
        } else if (colorScheme == ColorScheme.HIGH_CONTRAST) {
            for (String line : linhas) {
                if (line.contains("ERROR")) {
                    coloredLines.add(ConsoleColors.BOLD_RED + line + ConsoleColors.RESET);
                } else if (line.contains("WARN")) {
                    coloredLines.add(ConsoleColors.BOLD_YELLOW + line + ConsoleColors.RESET);
                } else if (line.contains("INFO")) {
                    coloredLines.add(ConsoleColors.BOLD_CYAN + line + ConsoleColors.RESET);
                } else if (line.contains("DEBUG")) {
                    coloredLines.add(ConsoleColors.BOLD_MAGENTA + line + ConsoleColors.RESET);
                } else {
                    coloredLines.add(ConsoleColors.BOLD_WHITE + line + ConsoleColors.RESET);
                }
            }
        } else if (colorScheme == ColorScheme.MINIMAL) {
            for (String line : linhas) {
                if (line.contains("ERROR")) {
                    coloredLines.add(ConsoleColors.RED + line + ConsoleColors.RESET);
                } else if (line.contains("WARN")) {
                    coloredLines.add(ConsoleColors.YELLOW + line + ConsoleColors.RESET);
                } else {
                    coloredLines.add(line);
                }
            }
        }

        return coloredLines;
    }
}
