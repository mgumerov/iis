class NatKey {
  private final String depCode;
  private final String depJob;

  public NatKey(final String depCode, final String depJob) {
    if (depCode == null || depJob == null) throw new NullPointerException("Natural key cannot contain null values");
    this.depCode = depCode;
    this.depJob = depJob;
  }

  public String getDepCode() { return depCode; }
  public String getDepJob() { return depJob; }

  @Override
  public boolean equals(final Object other) {
    if (other == this) return true;
    if (other != null && this.getClass().equals(other.getClass())) {
      final NatKey _other = (NatKey) other;
      return depCode.equals(_other.depCode) && depJob.equals(_other.depJob);
    }
    return false;
  }

  @Override
  public int hashCode() { return depCode.hashCode() ^ depJob.hashCode(); }

  @Override
  public String toString() { return new StringBuilder(depCode).append("/").append(depJob).toString(); }
}
