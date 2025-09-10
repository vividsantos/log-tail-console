Feature: Tratamento de rotação de logs
  Como usuário monitorando logs
  Eu quero continuar vendo logs mesmo quando há rotação
  Para não perder informações importantes

  Scenario: Detectar rotação de log por renomeação
    Given estou monitorando "/var/log/app.log"
    And o arquivo está sendo escrito continuamente
    When o arquivo é renomeado para "/var/log/app.log.1"
    And um novo arquivo "/var/log/app.log" é criado
    Then devo ver uma mensagem "Log rotated, switching to new file"
    And o monitoramento deve continuar no novo arquivo

  Scenario: Detectar rotação por truncamento
    Given estou monitorando um arquivo de 1000 linhas
    When o arquivo é truncado para 0 bytes
    Then devo ver uma mensagem "Log file truncated, restarting from beginning"
    And o monitoramento deve continuar normalmente

  Scenario: Configurar comportamento na rotação
    When eu executo "java LogTailConsole --follow /var/log/app.log"
    Then rotações devem ser detectadas automaticamente
    When eu executo "java LogTailConsole --no-follow /var/log/app.log"
    Then rotações devem ser ignoradas
