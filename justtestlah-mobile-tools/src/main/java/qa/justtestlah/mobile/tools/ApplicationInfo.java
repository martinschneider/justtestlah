package qa.justtestlah.mobile.tools;

/** DTO to hold application meta information. */
public class ApplicationInfo {

  /** Return the full application information. */
  @Override
  public String toString() {
    return applicationName + " " + versionName + "_" + versionCode;
  }

  private String applicationName;
  private String versionName;
  private String versionCode;

  public String getApplicationName() {
    return applicationName;
  }

  public void setApplicationName(String applicationName) {
    this.applicationName = applicationName;
  }

  public String getVersionName() {
    return versionName;
  }

  public void setVersionName(String versionName) {
    this.versionName = versionName;
  }

  public String getVersionCode() {
    return versionCode;
  }

  public void setVersionCode(String versionCode) {
    this.versionCode = versionCode;
  }

  public ApplicationInfo(String applicationName, String versionName, String versionCode) {
    super();
    this.applicationName = applicationName;
    this.versionName = versionName;
    this.versionCode = versionCode;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((applicationName == null) ? 0 : applicationName.hashCode());
    result = prime * result + ((versionCode == null) ? 0 : versionCode.hashCode());
    result = prime * result + ((versionName == null) ? 0 : versionName.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ApplicationInfo other = (ApplicationInfo) obj;
    if (applicationName == null) {
      if (other.applicationName != null)
        return false;
    } else if (!applicationName.equals(other.applicationName))
      return false;
    if (versionCode == null) {
      if (other.versionCode != null)
        return false;
    } else if (!versionCode.equals(other.versionCode))
      return false;
    if (versionName == null) {
      if (other.versionName != null)
        return false;
    } else if (!versionName.equals(other.versionName))
      return false;
    return true;
  }


}
