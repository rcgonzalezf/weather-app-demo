package org.rcgonzalezf.weather.openweather.network;

import android.net.Uri;
import java.util.HashMap;
import java.util.Map;
import org.rcgonzalezf.weather.common.network.RequestParameters;

public class OpenWeatherApiRequestParameters implements RequestParameters {

  String mQueryString;

  @Override public String getQueryString() {
    return mQueryString;
  }

  @Override public Map<String, String> getKeyValueParameters() {
    throw new UnsupportedOperationException("This service doesn't provides this method");
  }

  public static class OpenWeatherApiRequestBuilder {

    public static final String LON = "lon";
    public static final String LAT = "lat";
    public static final String CITY_ID = "id";
    public static final String CITY_NAME = "q";
    public static final String TYPE = "type";
    private static final String LIKE = "like";
    public static final String UNITS = "units";

    private Map<String, String> mQueryParametersMap;

    public OpenWeatherApiRequestBuilder() {
      mQueryParametersMap = new HashMap<>();
    }

    public OpenWeatherApiRequestBuilder withCityId(Integer cityId) {
      mQueryParametersMap.put(CITY_ID, String.valueOf(cityId));
      return this;
    }

    public OpenWeatherApiRequestParameters build() {
      if (mQueryParametersMap.isEmpty()) {
        throw new IllegalStateException("Can't prepare empty parameters");
      }
      OpenWeatherApiRequestParameters
          openWeatherApiRequestParameters = new OpenWeatherApiRequestParameters();

      Uri.Builder queryBuilder = new Uri.Builder();
      for (String key : mQueryParametersMap.keySet()) {
        queryBuilder.appendQueryParameter(key,mQueryParametersMap.get(key));
      }

      openWeatherApiRequestParameters.mQueryString = queryBuilder.build().getQuery();
      return openWeatherApiRequestParameters;
    }

    private OpenWeatherApiRequestBuilder withLat(Double lat) {
      mQueryParametersMap.put(LAT, String.valueOf(lat));
      return this;
    }

    private OpenWeatherApiRequestBuilder withLon(Double lon) {
      mQueryParametersMap.put(LON, String.valueOf(lon));
      return this;
    }

    public OpenWeatherApiRequestBuilder withLatLon(Double lat, Double lon) {
      return withLat(lat).withLon(lon);
    }

    public OpenWeatherApiRequestBuilder withCityName(String cityName) {
      mQueryParametersMap.put(CITY_NAME, cityName);
      mQueryParametersMap.put(TYPE, LIKE);
      return this;
    }

    public OpenWeatherApiRequestBuilder withUnits(Units units) {
      mQueryParametersMap.put(UNITS, units.getUnitName());
      return this;
    }
  }
}
