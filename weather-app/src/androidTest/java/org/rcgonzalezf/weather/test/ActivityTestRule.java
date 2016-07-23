package org.rcgonzalezf.weather.test;

import android.app.Activity;
import android.content.Intent;

public class ActivityTestRule<T extends Activity> {

  public T getActivity() {
    return null;
  }

  protected Intent getActivityIntent() {
    return null;
  }

  protected void beforeActivityLaunched() {
  }

  protected void afterActivityFinished() {
  }
}
