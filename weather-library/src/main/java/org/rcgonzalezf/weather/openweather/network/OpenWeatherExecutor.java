package org.rcgonzalezf.weather.openweather.network;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.rcgonzalezf.weather.common.models.WeatherData;
import org.rcgonzalezf.weather.common.models.converter.ModelConverter;
import org.rcgonzalezf.weather.common.network.ApiCallback;
import org.rcgonzalezf.weather.openweather.models.OpenWeatherApiRawData;

public class OpenWeatherExecutor {

  private final Executor mExecutor;
  private ApiCallback<OpenWeatherApiResponse, OpenWeatherApiError> mApiCallback;
  private InputStream mInputStream;
  private ModelConverter<Void, OpenWeatherApiRawData> mConverter;

  public OpenWeatherExecutor(ApiCallback apiCallback) {
    this(apiCallback, Executors.newSingleThreadExecutor());
  }

  public OpenWeatherExecutor(ApiCallback apiCallback, Executor executor) {
    mApiCallback = apiCallback;
    mExecutor = executor;
  }

  public void performNetworkCall(final String stringUrl) {

    mExecutor.execute(new Runnable() {
      @Override public void run() {
        try {
          final URL url = new URL(stringUrl);
          HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
          mInputStream = urlConnection.getInputStream();
          InputStream inputStream = null;

          try {
            inputStream = new BufferedInputStream(getInputStream());
            convertToModel(inputStream);
          } finally {
            urlConnection.disconnect();
            if (inputStream != null) {
              inputStream.close();
            }
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    });
  }

  private void convertToModel(InputStream inputStream) throws IOException {
    mConverter.fromInputStream(inputStream);
    List<OpenWeatherApiRawData> rawModelList = mConverter.generateRawModel();
    List<WeatherData> weatherData = new ArrayList<>(rawModelList.size());

    if (rawModelList != null && rawModelList.size() > 0) {

      for(OpenWeatherApiRawData rawData : rawModelList) {


        if(rawData.getCod() == HttpURLConnection.HTTP_OK) {

          weatherData.add(new WeatherData(rawData.getName(),
              rawData.getWind().getSpeed(),
              rawData.getWind().getDeg(),
              rawData.getMain().getTemp(),
              rawData.getMain().getHumidity(),
              rawData.getSys().getSunrise(),
              rawData.getSys().getSunset()
          ));
        }
      }
      OpenWeatherApiResponse response = new OpenWeatherApiResponse();
      response.setData(weatherData);
      mApiCallback.onSuccess(response);

    } else {
      final OpenWeatherApiError error = new OpenWeatherApiError();
      error.setMessage("Empty result");
      error.setCode(ErrorCode.EMPTY);
      mApiCallback.onError(error);
    }

  }

  public InputStream getInputStream() {
    return mInputStream;
  }

  public void setModelConverter(ModelConverter<Void, OpenWeatherApiRawData> converter) {
    mConverter = converter;
  }
}
