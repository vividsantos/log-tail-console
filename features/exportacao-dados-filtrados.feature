Feature: Exportação de dados filtrados
  Como usuário
  Eu quero exportar logs filtrados de um único arquivo
  Para análise posterior ou compartilhamento

  Scenario: Exportar logs filtrados para novo arquivo (modo estático)
    Given um arquivo de log "/var/log/app.log"
    When eu executo "java LogTailConsole --filter 'ERROR' --export /tmp/errors.log /var/log/app.log"
    Then um novo arquivo "/tmp/errors.log" deve ser criado
    And deve conter apenas linhas com "ERROR" 
    And deve manter a formatação original
    And aplicação deve terminar após exportação

  Scenario: Exportar com número específico de linhas
    Given um arquivo de log com muitas linhas
    When eu executo "java LogTailConsole -n 100 --filter 'WARN' --export /tmp/warnings.log /var/log/app.log"
    Then deve processar as últimas 100 linhas
    And exportar apenas as que contêm "WARN"

  Scenario: Exportar em tempo real (modo follow)
    Given estou monitorando logs com filtro
    When eu executo "java LogTailConsole -f --filter 'ERROR' --export /tmp/live-errors.log /var/log/app.log"
    Then logs filtrados devem ser salvos em tempo real no arquivo de exportação
    And também devem aparecer no console
    And deve continuar até interrupção manual

  Scenario: Rejeitar exportação com múltiplos arquivos
    When eu executo "java LogTailConsole --export /tmp/out.log file1.log file2.log"
    Then deve exibir erro "Export supports only one input file"
    And não deve criar arquivo de exportação
