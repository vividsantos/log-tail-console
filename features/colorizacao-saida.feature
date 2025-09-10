Feature: Colorização de saída de logs
  Como usuário da aplicação
  Eu quero ver logs coloridos automaticamente
  Para identificar rapidamente diferentes tipos de mensagens

  Background:
    Given que a aplicação possui esquemas de cores pré-definidos
    And colorização está habilitada por padrão

  Scenario: Aplicar colorização padrão automaticamente
    Given um arquivo de log com diferentes níveis
    When eu executo "java LogTailConsole /var/log/app.log"
    Then linhas contendo "ERROR" devem aparecer em vermelho automaticamente
    And linhas contendo "WARN" devem aparecer em amarelo automaticamente
    And linhas contendo "INFO" devem aparecer em verde automaticamente
    And linhas contendo "DEBUG" devem aparecer em cinza automaticamente

  Scenario: Usar esquema de cores pré-definido alternativo
    Given existem esquemas "default", "dark", "light", "high-contrast"
    When eu executo "java LogTailConsole --color-scheme dark /var/log/app.log"
    Then o esquema "dark" deve ser aplicado
    And cores devem ser otimizadas para terminais com fundo escuro

  Scenario: Listar esquemas de cores disponíveis
    When eu executo "java LogTailConsole --list-color-schemes"
    Then deve exibir:
      """
      Available color schemes:
      - default: Standard colors for most terminals
      - dark: Optimized for dark backgrounds
      - light: Optimized for light backgrounds  
      - high-contrast: High contrast for accessibility
      - minimal: Only errors and warnings colored
      """

  Scenario: Visualizar preview de esquema de cores
    When eu executo "java LogTailConsole --preview-colors dark"
    Then deve exibir exemplos de cada tipo de log colorido:
      """
      [ERROR] Sample error message
      [WARN]  Sample warning message
      [INFO]  Sample info message
      [DEBUG] Sample debug message
      """

  Scenario: Criar configuração personalizada completa
    Given um arquivo de configuração personalizada
    When eu crio "custom-colors.properties" com:
      """
      # Esquema personalizado
      error.color=BRIGHT_RED
      error.background=BLACK
      warn.color=YELLOW
      info.color=BRIGHT_GREEN
      debug.color=DARK_GRAY
      """
    And eu executo "java LogTailConsole --color-config custom-colors.properties /var/log/app.log"
    Then cores customizadas devem ser aplicadas

  Scenario: Colorização em tempo real vs arquivo estático
    Given um arquivo sendo monitorado em tempo real
    When novas linhas são adicionadas
    Then colorização deve ser aplicada instantaneamente
    
