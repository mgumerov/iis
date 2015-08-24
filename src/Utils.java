public abstract class Utils {
  /** 
   * ���������� ������� string, ��� null, ���� ��� ����� ""
   * @param string ������, nullable
   * @return ������� string, ���� ��� �� ����� ""; null � ��������� ������
   */
  public static String nullIfEmpty(final String string) { return "".equals(string) ? null : string; }
}