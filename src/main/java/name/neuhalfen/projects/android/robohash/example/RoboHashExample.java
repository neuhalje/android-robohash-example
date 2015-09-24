package name.neuhalfen.projects.android.robohash.example;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import name.neuhalfen.projects.android.robohash.RoboHash;
import name.neuhalfen.projects.android.robohash.handle.Handle;

import java.io.IOException;
import java.util.UUID;

public class RoboHashExample extends Activity {
    private ImageView robot;

    private RoboHash robots;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        robots = new RoboHash(this);

        robot = (ImageView) findViewById(R.id.robot);
        ((Button) findViewById(R.id.new_robot)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    UUID uuid = UUID.randomUUID();
                    Handle handle = robots.calculateHandleFromUUID(uuid);
                    Bitmap bitmap = robots.imageForHandle(handle);
                    robot.setImageBitmap(bitmap);
                } catch (IOException e) {
                    Toast.makeText(RoboHashExample.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_speed_test_asnc) {
            testLoadingInTheBackground();
            return true;
        } else if (item.getItemId() == R.id.menu_speed_test_sync) {
            testLoadingWithRenderingInTheUiThread();
            return true;
        } else
            return super.onOptionsItemSelected(item);
    }

    private void testLoadingInTheBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPostExecute(String result) {
                new AlertDialog.Builder(RoboHashExample.this).setMessage(result).create().show();
            }

            @Override
            protected String doInBackground(Void... params) {

                final int SAMPLES = 500;

                UUID[] seeds = new UUID[SAMPLES];
                for (int i = 0; i < SAMPLES; i++) {
                    seeds[i] = UUID.randomUUID();
                }

                long startTimeCompose = System.currentTimeMillis();

                for (int i = 0; i < SAMPLES; i++) {
                    Handle handle = robots.calculateHandleFromUUID(seeds[i]);
                    try {
                        Bitmap bitmap = robots.imageForHandle(handle);
                    } catch (IOException e) {
                        //
                    }
                }

                long durationCompose = System.currentTimeMillis() - startTimeCompose;
                return "Loading " + SAMPLES + " images took " + durationCompose + " ms total "
                        + " (loading took " + durationCompose / SAMPLES + " ms/robot)";
            }
        }.execute();
    }


    void testLoadingWithRenderingInTheUiThread() {
        final int SAMPLES = 100;

        // How log does writing to the image control take?
        long sumRenderingIntoImage = 0;


        UUID[] seeds = new UUID[SAMPLES];
        for (int i = 0; i < SAMPLES; i++) {
            seeds[i] = UUID.randomUUID();
        }

        long startTimeCompose = System.currentTimeMillis();

        for (int i = 0; i < SAMPLES; i++) {
            Handle handle = robots.calculateHandleFromUUID(seeds[i]);
            try {
                Bitmap bitmap = robots.imageForHandle(handle);

                long startRender = System.currentTimeMillis();
                RoboHashExample.this.robot.setImageBitmap(bitmap);
                sumRenderingIntoImage += (System.currentTimeMillis() - startRender);
            } catch (IOException e) {
                //
            }
        }


        long durationCompose = System.currentTimeMillis() - startTimeCompose;
        String msg = "Loading + rendering " + SAMPLES + " images took " + durationCompose + " ms total "
                + (durationCompose - sumRenderingIntoImage) + " ms for loading and "
                + sumRenderingIntoImage + " ms for rendering " +
                " (loading +  rendering: " + durationCompose / SAMPLES + " ms/robot)";

        new AlertDialog.Builder(this).setMessage(msg).create().show();

    }
}
