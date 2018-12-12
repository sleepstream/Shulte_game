package georgearistov.shulte_game;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.Image;
import android.net.Uri;
import android.os.*;
import android.support.annotation.Nullable;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.*;

import java.util.Locale;
import java.util.Random;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int CHANGE_SETTINGS = 3000;
    public static GridViewEx gridView;
    public Context context;
    public static int mapSize = 4;
    public static CustomGridAdapter customGridAdapter;
    public TextView nextItem;
    public static Chronometer timer;
    public RelativeLayout gameField;
    public RelativeLayout startButtonLayout;
    public RelativeLayout trackField;
    public static RelativeLayout gameOver;
    public static RelativeLayout youWin;
    public static RelativeLayout youLoose;
    public static TextView youWinText1;
    public static  SharedPreferences sharedPreferences;
    public ImageView restartBtn;
    private int counter = 1;
    public NavigationView navigationView;
    public RelativeLayout startButton;
    private static final String LOG_TAG = "MainActivity";
    public String[] gameMode=new String[]{"classic", "red_n_black"};
    public static Integer currentGameMode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);

        this.context = this;
        sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this /* Activity context */);
        String game_size = sharedPreferences.getString("game_size", "");
        if(game_size.length()>0)
            mapSize = Integer.valueOf(game_size);


        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);


        nextItem = findViewById(R.id.nextItem);
        timer = findViewById(R.id.timer);



        // Get gridview object from xml file
        gridView = (GridViewEx) findViewById(R.id.gridView1);
        // Set custom adapter (GridAdapter) to gridview

        customGridAdapter = new CustomGridAdapter( this, mapSize, nextItem);
        gridView.setAdapter(customGridAdapter);
        gridView.setNumColumns(mapSize);

        restartBtn = findViewById(R.id.restartBtn);
        gameField = findViewById(R.id.gameField);
        trackField = findViewById(R.id.trackField);
        gameOver = findViewById(R.id.gameOver);
        youWin = findViewById(R.id.youWin);
        youLoose = findViewById(R.id.youLoose);
        startButton = findViewById(R.id.startButton);
        youWinText1 = findViewById(R.id.youWinText1);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                restartGame();
                /*startButtonLayout.setVisibility(View.GONE);
                gameField.setVisibility(View.VISIBLE);
                trackField.setVisibility(View.VISIBLE);
                gridView.setEnabled(true);
                nextItem.setText(String.valueOf(1));
                timer.start();*/
            }
        });

        restartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                restartGame();
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(LOG_TAG, "click!!!!" + i + " - position " + l +" hz");
                int tmp =  (int)customGridAdapter.getItem(i);

                if(sharedPreferences.getBoolean("vibration_on_click", true)) {
                    vibrate();
                }
                if(tmp == counter)
                {
                    if(tmp < mapSize*mapSize) {
                        counter++;
                        nextItem.setText(String.valueOf(counter));

                        if(sharedPreferences.getBoolean("resSort_on_press", false)) {
                            customGridAdapter.reGenerate(mapSize);
                            customGridAdapter.notifyDataSetChanged();
                        }
                        
                    }
                    else
                    {
                        counter =1;
                        gameOver.setVisibility(View.VISIBLE);
                        youWin.setVisibility(View.VISIBLE);
                        youLoose.setVisibility(View.GONE);
                        timer.stop();
                        int seconds = (int) (SystemClock.elapsedRealtime() - timer.getBase())/1000;
                        int minutes = (int)(seconds/60);
                        seconds = seconds - minutes*60;
                        youWinText1.setText(context.getResources().getString(R.string.yourResult)+
                                minutes +  context.getResources().getString(R.string.minutes) + " " +
                                seconds + context.getResources().getString(R.string.seconds));
                        gridView.setClickable(false);
                        gridView.setEnabled(false);

                    }
                }
                else
                {
                    counter =1;
                    gameOver.setVisibility(View.VISIBLE);
                    youWin.setVisibility(View.GONE);
                    youLoose.setVisibility(View.VISIBLE);
                    gridView.setClickable(false);
                    gridView.setEnabled(false);
                    timer.stop();
                }
            }
        });





        onNavigationItemSelected(navigationView.getMenu().findItem(R.id.classic));

    }

    public Locale getLocale()
    {
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = context.getResources().getConfiguration().getLocales().get(0);
        } else {
            locale = context.getResources().getConfiguration().locale;
        }
        return locale;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_restart)
        {
            restartGame();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        navigationView.setCheckedItem(id);

        if (id == R.id.nav_share) {
            // Handle the camera action
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            final String appPackageName = getPackageName();
            String shareBody = context.getString(R.string.shareBody)+"\n"+context.getString(R.string.linl_to_app) + appPackageName;
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, context.getString(R.string.app_name)+" "+context.getString(R.string.try_this_app));
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, context.getString(R.string.share)));
        }
        else if (id == R.id.nav_rate)
        {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            final String appPackageName = getPackageName();
            intent.setData(Uri.parse("market://details?id="+appPackageName));
            startActivity(intent);
        }
        else if(id == R.id.nav_write_to_dev)
        {
            Intent intent = new Intent(context, ConnectWithDev.class);
            startActivity(intent);
        }
        else if(id == R.id.classic)
        {
            stopGame();
            currentGameMode = 0;
        }
        else if (id == R.id.black_and_red)
        {
            stopGame();
            currentGameMode = 1;
        }
        else if (id == R.id.action_settings)
        {
            Intent intent = new Intent(context, SettingsActivity.class);
            startActivityForResult(intent, CHANGE_SETTINGS);
        }
        else if(id == R.id.nav_about)
        {
            Intent intent = new Intent(context, About.class);
            startActivity(intent);
        }
        else if(id == R.id.nav_donate)
        {
            Intent intent = new Intent(context, DonationsActivity.class);
            startActivity(intent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return super.onOptionsItemSelected(item);
    }

    public void vibrate() {
        if (Build.VERSION.SDK_INT >= 26) {
            ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(VibrationEffect.createOneShot(100,5));
        } else {
            ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(100);
        }
    }

    public void restartGame()
    {
        counter =1;
        nextItem.setText(String.valueOf(counter));
        if(customGridAdapter != null) {
            gridView.setNumColumns(mapSize);
            customGridAdapter.reGenerate(mapSize);
            customGridAdapter.notifyDataSetChanged();

            startButton.setVisibility(View.GONE);
            trackField.setVisibility(View.VISIBLE);
            gameOver.setVisibility(View.GONE);
            gridView.setEnabled(true);
        }
        timer.setBase(SystemClock.elapsedRealtime());
        timer.start();
    }

    public void stopGame()
    {
        gameOver.setVisibility(View.GONE);
        youWin.setVisibility(View.GONE);
        youLoose.setVisibility(View.GONE);
        gridView.setClickable(false);
        gridView.setEnabled(false);
        gridView.setNumColumns(mapSize);
        customGridAdapter.reGenerate(mapSize);
        customGridAdapter.notifyDataSetChanged();
        startButton.setVisibility(View.VISIBLE);
        trackField.setVisibility(View.GONE);
        nextItem.setText(String.valueOf(1));
        timer.stop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode)
        {
            case CHANGE_SETTINGS:
                stopGame();
                break;
        }
    }
}
