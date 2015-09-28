package rcgonzalezf.org.weather;

import android.os.Bundle;
import org.rcgonzalezf.weather.common.network.ApiError;
import org.rcgonzalezf.weather.common.network.ApiResponse;
import rcgonzalezf.org.weather.common.BaseActivity;

public class WeatherActivity extends BaseActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.weather);
  }

  @Override public void onSuccess(ApiResponse apiResponse) {
    apiResponse.getData();
    apiResponse.toString();
  }

  @Override public void onError(ApiError apiError) {
    apiError.getError();
  }
}
