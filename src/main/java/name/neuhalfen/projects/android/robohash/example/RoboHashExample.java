package name.neuhalfen.projects.android.robohash.example;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import name.neuhalfen.projects.android.robohash.RoboHash;
import name.neuhalfen.projects.android.robohash.handle.Handle;
import name.neuhalfen.projects.android.robohash.handle.HandleFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class RoboHashExample extends Activity {
    private ImageView robot16dp, robot24dp, robot48dp, robot96dp, robotFull;

    private EditText handleView;
    private RoboHash robots;

    private Bitmap robot;

    private Handle rootHandle;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        robots = new RoboHash(this);

        robot16dp = (ImageView) findViewById(R.id.robot_16dp);
        robot24dp = (ImageView) findViewById(R.id.robot_24dp);
        robot48dp = (ImageView) findViewById(R.id.robot_48dp);
        robot96dp = (ImageView) findViewById(R.id.robot_96dp);
        robotFull = (ImageView) findViewById(R.id.robot_full);
        handleView = (EditText) findViewById(R.id.new_robot_id);

        ((Button) findViewById(R.id.new_random_robot)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    UUID uuid = UUID.randomUUID();
                    rootHandle = robots.calculateHandleFromUUID(uuid);
                    robot = robots.imageForHandle(rootHandle);
                    handleView.setText("" + rootHandle.pack());
                    displayRobot(robot);

                } catch (IOException e) {
                    Toast.makeText(RoboHashExample.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        ((Button) findViewById(R.id.new_robot)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    rootHandle = new HandleFactory().unpack(Long.valueOf(handleView.getText().toString()));
                    robot = robots.imageForHandle(rootHandle);
                    displayRobot(robot);
                } catch (IOException e) {
                    Toast.makeText(RoboHashExample.this, e.getMessage(), Toast.LENGTH_LONG).show();
                } catch (NumberFormatException e) {
                    // ignore
                }
            }
        });


        ((Button) findViewById(R.id.new_random_robot)).callOnClick();
    }

    private void displayRobot(Bitmap bitmap) {
        robot16dp.setImageBitmap(bitmap);
        robot24dp.setImageBitmap(bitmap);
        robot48dp.setImageBitmap(bitmap);
        robot96dp.setImageBitmap(bitmap);
        robotFull.setImageBitmap(bitmap);
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
        } else if (item.getItemId() == R.id.menu_save) {
            saveRobot();
            return true;
        } else
            return super.onOptionsItemSelected(item);
    }

    private void saveRobot() {
        Handle handle = new HandleFactory().unpack(Long.valueOf(handleView.getText().toString()));

        final String robotPath = savebitmap(robot, "" + rootHandle.pack()).getAbsolutePath();

        new AlertDialog.Builder(RoboHashExample.this).setMessage("Robot saved as " + robotPath).create().show();
        Log.i("RoboHash", "SAVED ROBOT #" + handle.pack() + ": " + robotPath);
    }

    private File savebitmap(Bitmap bitmap, String filenameWithoutExt) {
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        OutputStream outStream = null;

        File file = new File(extStorageDirectory, filenameWithoutExt + ".png");
        if (file.exists()) {
            file.delete();
        }
        try {
            outStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;

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
                displayRobot(bitmap);
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
