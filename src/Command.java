public interface Command {
  /** 
   * Запуск команды с указанными параметрами
   * @param args Параметры :)
   * @throws Exception Что угодно
   */
  void execute(String[] args) throws Exception;
}