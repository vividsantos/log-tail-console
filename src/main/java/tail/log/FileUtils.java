package tail.log;

import tail.log.themes.ColorScheme;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

import static tail.log.themes.ColorUtils.coloredLines;

public class FileUtils {

    public static ColorScheme colorScheme = ColorScheme.DEFAULT;

    public void setColorScheme(ColorScheme colorScheme) {
        FileUtils.colorScheme = colorScheme;
    }

    private static Optional<String> readFile(String filePath, boolean readAll, int wantedLines) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8))) {

            List<String> lines = new ArrayList<>();
            String line;
            int qtdLines = 0;

            if (readAll) {
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
                return Optional.of(String.join(System.lineSeparator(), lines));
            }

            while ((line = reader.readLine()) != null && qtdLines < wantedLines) {
                lines.add(line);
                qtdLines++;
            }

            int startIndex = Math.max(0, lines.size() - wantedLines);
            List<String> lastLines = lines.subList(startIndex, lines.size());

            return Optional.of(String.join(System.lineSeparator(), lastLines));
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

            for (String linha : linhas) {
                String linhaLower = linha.toLowerCase();
                boolean encontrou = false;
                for (String termo : termos) {
                    if (linhaLower.contains(termo.toLowerCase())) {
                        encontrou = true;
                        break;
                    }
                }
                if (encontrou) {
                    System.out.println(linha.trim());
                }
            }
        });

        coloredLines(colorScheme, lines).forEach(System.out::println);
    }

    public static List<String> followFileWithFilter(String filter, List<String> listaLinhas) {
        String[] termos = filter.split("\\|");
        List<String> filtered = new ArrayList<>();

        for (String linha : listaLinhas) {
            String linhaLower = linha.toLowerCase();
            boolean encontrou = false;
            for (String termo : termos) {
                if (linhaLower.contains(termo.toLowerCase())) {
                    encontrou = true;
                    break;
                }
            }
            if (encontrou) {
                filtered.add(linha.trim());
            }
        }
        return filtered;
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

    public static List<String> followFileWithRegex(String regex, List<String> listaLinhas) {
        Pattern pattern = Pattern.compile(regex);
        List<String> filtered = new ArrayList<>();

        for (int i = listaLinhas.size() - 1; i >= 0; i--) {
            if (pattern.matcher(listaLinhas.get(i)).find()) {
                filtered.add(listaLinhas.get(i).trim());
            }
        }
        return filtered;
    }

    public static void showFileWithExclude(String filePath, boolean readAll, int wantedLines, String exclude) {
        List<String> lines = new ArrayList<>(Collections.emptyList());
        readFile(filePath, readAll, wantedLines).ifPresent(resultado -> {
            Pattern pattern = Pattern.compile(exclude, Pattern.CASE_INSENSITIVE);
            String[] linhas = resultado.split("\n");
            for (int i = linhas.length - 1; i >= 0; i--) {
                if (!pattern.matcher(linhas[i]).find()) {
                    lines.add(linhas[i].trim());
                }
            }
        });

        coloredLines(colorScheme, lines).forEach(System.out::println);
    }

    public static List<String> followFileWithExclude(String exclude, List<String> listaLinhas) {
        Pattern pattern = Pattern.compile(exclude, Pattern.CASE_INSENSITIVE);
        List<String> filtered = new ArrayList<>();

        for (int i = listaLinhas.size() - 1; i >= 0; i--) {
            if (!pattern.matcher(listaLinhas.get(i)).find()) {
                filtered.add(listaLinhas.get(i).trim());
            }
        }
        return filtered;
    }

    public static void followingFile(String filePath, boolean readAll, int wantedLines, String filter, String regex, String exclude) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nMonitoring stopped by user");
        }));

        if (filter != null) {
            showFileWithFilter(filePath, readAll, wantedLines, filter);
        } else if (regex != null) {
            showFileWithRegex(filePath, readAll, wantedLines, regex);
        } else if (exclude != null) {
            showFileWithExclude(filePath, readAll, wantedLines, exclude);
        } else {
            showFile(filePath, readAll, wantedLines);
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
                    List<String> filtered = new ArrayList<>();

                    if (filter != null) {
                        filtered = followFileWithFilter(filter, newLines);
                    } else if (regex != null) {
                        filtered = followFileWithRegex(regex, newLines);
                    } else if (exclude != null) {
                        filtered = followFileWithExclude(exclude, newLines);
                    }

                    if (filter == null && regex == null && exclude == null) {
                        filtered = newLines;
                    }

                    for (String line : filtered) {
                        if (!processedLines.contains(line)) {
                            System.out.println(line);
                            processedLines.add(line);
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
}
