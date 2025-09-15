package tail.log;

import static tail.log.FileUtils.*;

public class LogTailConsole {
    public static void main(String[] args) {
        LogTailArgsParser parser = new LogTailArgsParser(args);

         if (parser.filePath == null) {
             System.out.println("Log path not provided.");
             return;
         }

        if(parser.filter == null) {
            exibirArquivo(parser.filePath, parser.lerTudo, parser.linhasDesejadas);
        } else{
            exibirArquivoFiltro(parser.filePath, parser.lerTudo, parser.linhasDesejadas, parser.filter);
        }
    }
}

