Feature: Leitura de arquivo estático
  Como usuário
  Eu quero ler arquivos de log estáticos seguindo o comportamento padrão do tail
  Para ter consistência com ferramentas Unix/Linux conhecidas

  Background:
    Given que o comportamento padrão segue o tail do Linux
    And por padrão são exibidas as últimas 10 linhas
    And a aplicação trabalha com um arquivo por vez

  Scenario: Ler arquivo estático com comportamento padrão (últimas 10 linhas)
    Given um arquivo de log estático com 1000 linhas
    When eu executo "java LogTailConsole /var/log/old.log"
    Then apenas as últimas 10 linhas devem ser exibidas
    And a aplicação deve terminar após exibir as linhas
    And não deve entrar em modo de monitoramento

  Scenario: Especificar número de linhas com flag -n
    Given um arquivo de log com 1000 linhas
    When eu executo "java LogTailConsole -n 50 /var/log/app.log"
    Then apenas as últimas 50 linhas devem ser exibidas
    And a aplicação deve terminar após exibir as linhas

  Scenario: Usar sintaxe alternativa para número de linhas
    Given um arquivo de log com 1000 linhas
    When eu executo "java LogTailConsole -50 /var/log/app.log"
    Then apenas as últimas 50 linhas devem ser exibidas
    And deve ter o mesmo comportamento que "-n 50"

  Scenario: Exibir todas as linhas do arquivo
    Given um arquivo de log com 500 linhas
    When eu executo "java LogTailConsole -n +1 /var/log/app.log"
    Then todas as 500 linhas devem ser exibidas
    And deve começar da primeira linha

  Scenario: Comportamento com arquivo pequeno
    Given um arquivo de log com apenas 5 linhas
    When eu executo "java LogTailConsole /var/log/small.log"
    Then todas as 5 linhas devem ser exibidas
    And não deve tentar exibir linhas inexistentes

  Scenario: Comportamento com arquivo vazio
    Given um arquivo de log vazio
    When eu executo "java LogTailConsole /var/log/empty.log"
    Then nenhuma linha deve ser exibida
    And a aplicação deve terminar imediatamente
    And não deve exibir erro

  Scenario: Combinar leitura estática com filtros
    Given um arquivo de log com 1000 linhas
    And 5 das últimas 10 linhas contêm "ERROR"
    When eu executo "java LogTailConsole -n 10 --filter ERROR /var/log/app.log"
    Then apenas as 5 linhas com "ERROR" das últimas 10 devem ser exibidas

  Scenario: Validar parâmetro -n com valores inválidos
    When eu executo "java LogTailConsole -n 0 /var/log/app.log"
    Then deve exibir erro "Invalid number of lines: 0"
    And deve sugerir "Use positive number or +1 for all lines"
    
    When eu executo "java LogTailConsole -n -5 /var/log/app.log"
    Then deve exibir erro "Invalid number of lines: -5"
    
    When eu executo "java LogTailConsole -n abc /var/log/app.log"
    Then deve exibir erro "Invalid number format: abc"

  Scenario: Rejeitar múltiplos arquivos
    When eu executo "java LogTailConsole /var/log/app1.log /var/log/app2.log"
    Then deve exibir erro "Multiple files not supported in this version"
    And deve sugerir "Process one file at a time"
    And deve retornar código de saída diferente de zero

  Scenario: Performance com arquivos grandes
    Given um arquivo de log de 10GB com milhões de linhas
    When eu executo "java LogTailConsole -n 10 /var/log/huge.log"
    Then deve ler apenas o final do arquivo
    And não deve carregar o arquivo inteiro na memória
    And deve completar em tempo razoável (< 2 segundos)
