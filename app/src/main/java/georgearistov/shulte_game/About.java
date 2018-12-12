package georgearistov.shulte_game;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import java.io.InputStream;

public class About extends AppCompatActivity {

    public WebView webViewAbout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        webViewAbout = findViewById(R.id.webView_about);
        WebSettings webSettings = webViewAbout.getSettings();
        webSettings.setDefaultTextEncodingName("utf-8");

        webViewAbout.loadUrl("file:///android_asset/about_ru.html");

    }

    private String readAssetFileAsString(String sourceHtmlLocation)
    {
        InputStream is;
        try
        {
            is = this.getAssets().open(sourceHtmlLocation);
            int size = is.available();

            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            return new String(buffer, "UTF-8");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return "";
    }

}
