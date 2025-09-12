package tail.log;

import java.io.*;
import java.nio.charset.StandardCharsets;

import static tail.log.FileUtils.*;

public class LogTailConsole {
    public static void main(String[] args) {
        LogTailArgsParser parser = new LogTailArgsParser(args);

        assert parser.filePath != null : "Caminho do arquivo de log não fornecido.";

        if(parser.filter == null) {
            lerArquivo(parser.filePath, parser.lerTudo, parser.linhasDesejadas);
        } else{
            lerArquivoFiltro(parser.filePath, parser.linhasDesejadas, parser.filter);
        }
    }
}

