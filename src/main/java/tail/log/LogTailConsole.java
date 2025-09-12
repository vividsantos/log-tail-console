package tail.log;

import java.io.*;
import java.nio.charset.StandardCharsets;

import static tail.log.FileUtils.*;

public class LogTailConsole {
    public static void main(String[] args) {
        LogTailArgsParser parser = new LogTailArgsParser(args);

        if (parser.filePath == null) {
            System.out.println("Por favor, forneça o caminho do arquivo de log como argumento.");
            return;
        }

        lerArquivo(parser.filePath, parser.linhasDesejadas, parser.lerTudo);
        lerArquivoFiltro(parser.filePath, parser.linhasDesejadas, parser.filter);
    }
}

