package name.neuhalfen.projects.android.robohash.example;

import android.support.test.rule.ActivityTestRule;
import android.test.UiThreadTest;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import android.support.test.runner.AndroidJUnit4;


import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class RoboHashExampleTest  {

    @Rule
    public ActivityTestRule<RoboHashExample> activityRule  = new ActivityTestRule(RoboHashExample.class);

    @Test
    public void startingTheActivity_works() {
        assertThat( activityRule.getActivity(), notNullValue())  ;
    }

    @UiThreadTest
    public void pressingRandomRobot_loadsRandomRobot() {
        RoboHashExample sut =  activityRule.getActivity();

        Button createRandomRobotButton = (Button) sut.findViewById(R.id.new_random_robot);

        ImageView largeRobot = (ImageView) sut.findViewById(R.id.robot_full);
        largeRobot.setImageResource(android.R.color.transparent);

        int pixelInTheMiddleBefore = largeRobot.getDrawingCache().getPixel(150, 150);

        createRandomRobotButton.callOnClick();

        // FIXME: Make this a better test
        assertThat("hopefully the pixel in the middle of the image changed", largeRobot.getDrawingCache().getPixel(150, 150), is(not(equalTo(pixelInTheMiddleBefore))));
    }
}
