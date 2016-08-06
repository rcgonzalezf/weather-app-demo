package rcgonzalezf.org.weather.location;

public interface LocationRetrieverListener {
  void checkForPermissions();

  void onEmptyLocation();

  void onLocationFound(double lat, double lon);
}
