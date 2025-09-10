Feature: Filtragem de conteúdo
  Como usuário
  Eu quero filtrar logs por padrões específicos
  Para focar apenas em informações relevantes

  Scenario: Filtrar por string simples
    Given um arquivo de log com várias linhas
    When eu executo "java LogTailConsole --filter 'ERROR' /var/log/app.log"
    Then apenas linhas contendo "ERROR" devem ser exibidas

  Scenario: Filtrar por expressão regular
    Given um arquivo de log com timestamps
    When eu executo "java LogTailConsole --regex '2024-01-.*ERROR.*' /var/log/app.log"
    Then apenas linhas que correspondem ao padrão devem ser exibidas

  Scenario: Filtrar múltiplos padrões (OR)
    Given um arquivo de log variado
    When eu executo "java LogTailConsole --filter 'ERROR|WARN' /var/log/app.log"
    Then linhas contendo "ERROR" ou "WARN" devem ser exibidas

  Scenario: Excluir padrões específicos
    Given um arquivo de log com debug
    When eu executo "java LogTailConsole --exclude 'DEBUG' /var/log/app.log"
    Then linhas contendo "DEBUG" não devem ser exibidas
