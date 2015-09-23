package name.neuhalfen.projects.android.robohash.example;

import android.app.Activity;
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
        if (item.getItemId() == R.id.menu_speed_test) {
            new AsyncTask<Void, Void, String>() {
                @Override
                protected void onPostExecute(String result) {

                    Toast.makeText(RoboHashExample.this, result, Toast.LENGTH_LONG).show();
                }

                @Override
                protected String doInBackground(Void... params) {
                    long startTime = System.currentTimeMillis();

                    final int SAMPLES = 500;
                    for (int i = 0; i < SAMPLES; i++) {
                        UUID uuid = UUID.randomUUID();
                        Handle handle = robots.calculateHandleFromUUID(uuid);
                        try {
                            Bitmap bitmap = robots.imageForHandle(handle);
                        } catch (IOException e) {
                            //
                        }
                    }

                    long endTime = System.currentTimeMillis();

                    long duration = endTime - startTime;
                    return "Loading " + SAMPLES + " images took " + duration + " ms (" + duration / SAMPLES + " ms/robot)";
                }
            }.execute();
            return true;
        } else
            return super.onOptionsItemSelected(item);
    }
}
