package rcgonzalezf.org.weather;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import org.rcgonzalezf.weather.common.network.ApiError;
import org.rcgonzalezf.weather.common.network.ApiResponse;
import rcgonzalezf.org.weather.adapters.ModelAdapter;
import rcgonzalezf.org.weather.common.BaseActivity;
import rcgonzalezf.org.weather.models.Forecast;
import rcgonzalezf.org.weather.models.ForecastMapper;
import rcgonzalezf.org.weather.models.WeatherViewModel;

public class WeatherActivity extends BaseActivity implements ModelAdapter.OnItemClickListener {

  private RecyclerView mRecyclerView;
  private ModelAdapter<Forecast> mAdapter;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setupRecyclerView();
  }

  @Override public void onEnterAnimationComplete() {
    super.onEnterAnimationComplete();
    mRecyclerView.scheduleLayoutAnimation();
  }

  @Override public void onSuccess(ApiResponse apiResponse) {
    final List<Forecast> forecastList = new ForecastMapper().withData(apiResponse.getData())
        .map();
    runOnUiThread(new Runnable() {
      @Override public void run() {
        mAdapter.setItems(forecastList);
        mAdapter.notifyDataSetChanged();
      }
    });
  }

  @Override public void onError(ApiError apiError) {
    apiError.getError();
  }

  private void setupRecyclerView() {
    mAdapter = new ModelAdapter<>(new ArrayList<Forecast>(), this);
    mAdapter.setOnItemClickListener(this);

    mRecyclerView = (RecyclerView) findViewById(R.id.main_recycler_view);
    mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    mRecyclerView.setAdapter(mAdapter);
  }

  @Override public void onItemClick(View view, WeatherViewModel viewModel) {
    Toast.makeText(this, "clicked:" + viewModel.getId(), Toast.LENGTH_SHORT).show();
  }
}
