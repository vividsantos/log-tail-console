Feature: Performance e uso de recursos
  Como usuário
  Eu quero que a aplicação seja eficiente
  Para não impactar o sistema

  Scenario: Monitorar arquivo grande
    Given um arquivo de log de 1GB
    When eu executo "java LogTailConsole /var/log/huge.log"
    Then apenas as últimas linhas devem ser carregadas inicialmente
    And uso de memória deve permanecer baixo

  Scenario: Limite de buffer
    Given configuração "buffer.max.lines=1000"
    When monitorando arquivo com muitas linhas
    Then apenas últimas 1000 linhas devem ficar em memória
