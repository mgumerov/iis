public interface Command {
  /** 
   * ������ ������� � ���������� �����������
   * @param args ��������� :)
   * @throws Exception ��� ������
   */
  void execute(String[] args) throws Exception;
}