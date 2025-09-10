Feature: Validação de argumentos
  Como usuário
  Eu quero receber mensagens claras sobre uso incorreto
  Para usar a aplicação corretamente

  Scenario: Validar número correto de arquivos
    When eu executo "java LogTailConsole"
    Then deve exibir erro "Missing file argument"
    And deve mostrar uso básico "Usage: java LogTailConsole [OPTIONS] FILE"

  Scenario: Validar combinações inválidas de flags
    When eu executo "java LogTailConsole -f --no-follow /var/log/app.log"
    Then deve exibir erro "Cannot use --follow and --no-follow together"
    And deve sugerir usar apenas uma opção

  Scenario: Sugerir funcionalidades futuras
    When eu executo "java LogTailConsole /var/log/app1.log /var/log/app2.log"
    Then deve exibir:
      """
      Multiple files not supported in this version.
      This feature is planned for a future release.
      
      Current usage: java LogTailConsole [OPTIONS] FILE
      """

  Scenario: Validar arquivo existe antes de iniciar follow
    When eu executo "java LogTailConsole -f /path/nonexistent.log"
    Then deve exibir erro "File not found: /path/nonexistent.log"
    And não deve entrar em modo de monitoramento
    And deve retornar código de saída 1
