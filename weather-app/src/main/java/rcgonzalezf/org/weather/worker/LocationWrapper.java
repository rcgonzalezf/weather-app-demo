package rcgonzalezf.org.weather.worker;

public class LocationWrapper {

  private double mLatitude;
  private double mLongitude;

  public double getLatitude() {
    return mLatitude;
  }

  public void setLatitude(double latitude) {
    mLatitude = latitude;
  }

  public double getLongitude() {
    return mLongitude;
  }

  public void setLongitude(double longitude) {
    mLongitude = longitude;
  }
}
