package rcgonzalezf.org.weather;

import android.preference.PreferenceFragment;
import java.util.Arrays;
import java.util.Collection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class SettingsActivityValidFragmentParameterizedTest {

  private SettingsActivity uut;
  private boolean mIsValid;

  @Parameterized.Parameters
  public static Collection<Object[]> data() {
    return Arrays.asList(new Object[][] {
        { true, PreferenceFragment.class.getName() },
        { true, SettingsActivity.GeneralPreferenceFragment.class.getName() },
        { false, "SomeOtherFragmentName" }
    });
  }
  @SuppressWarnings("WeakerAccess") @Parameterized.Parameter public boolean expectingValidFragment;

  @SuppressWarnings("WeakerAccess") @Parameterized.Parameter(value = 1) public String fragmentName;

  @Test public void shouldGetFormattedWindDirectionString() {
    givenSettingsActivity();

    whenCheckingIfFragmentIsValid();

    thenVerifyExpectation();
  }

  private void thenVerifyExpectation() {
    assertEquals(expectingValidFragment, mIsValid);
  }

  private void whenCheckingIfFragmentIsValid() {
    mIsValid = uut.isValidFragment(fragmentName);
  }

  private void givenSettingsActivity() {
    uut = new SettingsActivity();
  }
}
