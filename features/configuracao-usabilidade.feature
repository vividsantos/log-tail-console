Feature: Configuração e usabilidade
  Como usuário
  Eu quero configurar a aplicação facilmente
  Para adaptar às minhas necessidades

  Scenario: Exibir ajuda
    When eu executo "java LogTailConsole --help"
    Then deve exibir todas as opções disponíveis
    And deve mostrar exemplos de uso

  Scenario: Validar argumentos
    When eu executo "java LogTailConsole arquivo_inexistente.log"
    Then deve exibir erro "File not found: arquivo_inexistente.log"
    And deve retornar código de saída diferente de zero

  Scenario: Interrupção graceful
    Given estou monitorando um arquivo em tempo real
    When eu pressiono Ctrl+C
    Then a aplicação deve terminar graciosamente
    And deve exibir "Monitoring stopped by user"
