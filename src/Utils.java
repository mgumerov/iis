public abstract class Utils {
  /** 
   * Возвращает входную string, или null, если она равна ""
   * @param string Строка, nullable
   * @return Входная string, если она не равна ""; null в противном случае
   */
  public static String nullIfEmpty(final String string) { return "".equals(string) ? null : string; }
}