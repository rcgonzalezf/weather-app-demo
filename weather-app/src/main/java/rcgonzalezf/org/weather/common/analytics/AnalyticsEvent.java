package rcgonzalezf.org.weather.common.analytics;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class AnalyticsEvent {
  public String name;
  public String additionalValue;

  public AnalyticsEvent(@AnalyticsCatalog @NonNull String name, @Nullable String additionalValue) {
    this.name = name;
    this.additionalValue = additionalValue;
  }
}
