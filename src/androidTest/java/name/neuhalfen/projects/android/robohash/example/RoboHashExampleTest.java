package name.neuhalfen.projects.android.robohash.example;

import android.test.ActivityInstrumentationTestCase2;

/**
 * This is a simple framework for a test of an Application.  See
 * {@link android.test.ApplicationTestCase ApplicationTestCase} for more information on
 * how to write and extend Application tests.
 * <p/>
 * To run this test, you can type:
 * adb shell am instrument -w \
 * -e class name.neuhalfen.projects.android.robohash.example.RoboHashExampleTest \
 * name.neuhalfen.projects.android.robohash.example.tests/android.test.InstrumentationTestRunner
 */
public class RoboHashExampleTest extends ActivityInstrumentationTestCase2<RoboHashExample> {

    public RoboHashExampleTest() {
        super("name.neuhalfen.projects.android.robohash.example", RoboHashExample.class);
    }

}
