Feature: Monitoramento de logs em tempo real
  Como um desenvolvedor ou administrador de sistema
  Eu quero monitorar um arquivo de log em tempo real
  Para identificar problemas e acompanhar o comportamento da aplicação

  Background:
    Given que a aplicação LogTailConsole está instalada
    And o sistema operacional suporta Java 8 ou superior
    And a aplicação trabalha com um arquivo por vez

  Scenario: Iniciar monitoramento básico de arquivo de log
    Given existe um arquivo de log "/var/log/app.log"
    When eu executo "java LogTailConsole -f /var/log/app.log"
    Then as últimas 10 linhas do arquivo devem ser exibidas inicialmente
    And o cursor deve ficar aguardando novas linhas
    And novas linhas adicionadas ao arquivo devem aparecer automaticamente

  Scenario: Monitoramento com número específico de linhas iniciais
    Given um arquivo de log com 1000 linhas
    When eu executo "java LogTailConsole -n 20 -f /var/log/app.log"
    Then deve exibir últimas 20 linhas inicialmente
    And depois deve continuar monitorando novas linhas

  Scenario: Rejeitar follow com múltiplos arquivos
    When eu executo "java LogTailConsole -f /var/log/app1.log /var/log/app2.log"
    Then deve exibir erro "Follow mode supports only one file"
    And deve sugerir "Use: java LogTailConsole -f <single-file>"
    And deve retornar código de saída diferente de zero

  Scenario: Diferença clara entre modo estático e follow
    Given um arquivo de log sendo escrito
    When eu executo "java LogTailConsole /var/log/app.log"
    Then deve exibir últimas 10 linhas e terminar (modo estático)
    
    When eu executo "java LogTailConsole -f /var/log/app.log"
    Then deve exibir últimas 10 linhas e continuar monitorando
    
    When eu executo "java LogTailConsole --follow /var/log/app.log"
    Then deve ter o mesmo comportamento que "-f"

  Scenario: Monitoramento funciona em diferentes sistemas operacionais
    Given que estou em um sistema <sistema_operacional>
    When eu executo "java LogTailConsole -f <caminho_arquivo>"
    Then o monitoramento deve funcionar corretamente
    
    Examples:
      | sistema_operacional | caminho_arquivo        |
      | Windows            | C:\logs\app.log        |
      | Linux              | /var/log/app.log       |
      | MacOS              | /usr/local/logs/app.log|
