package com.iven.feedreader.mainact;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.iven.feedreader.R;
import com.iven.feedreader.domparser.DOMParser;
import com.iven.feedreader.domparser.RSSFeed;
import com.iven.feedreader.utils.Preferences;
import com.iven.feedreader.utils.saveUtils;

public class SplashActivity extends AppCompatActivity {

    //the default feed
    public static String default_feed_value;

    //the items
    RSSFeed lfflfeed;

    //Connectivity manager
    ConnectivityManager connectivityManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        default_feed_value = saveUtils.getFeedUrl(SplashActivity.this);

        //set the navbar tint if the preference is enabled
        Preferences.applyNavTint(this);

        //set LightStatusBar
        Preferences.applyLightIcons(this);

        // Detect if there's a connection issue or not
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        // If there's a connection problem
        if (connectivityManager.getActiveNetworkInfo() == null) {

            // Show alert splash
            setContentView(R.layout.splash_no_internet);
            new Handler().postDelayed(new Runnable() {
                public void run() {

                    // and finish the splash activity
                    SplashActivity.this.finish();

                }
            }, 2000);

        } else {

            //else :P, start the default splash screen and parse the RSSFeed and save the object
            setContentView(R.layout.splash);
            new AsyncLoadXMLFeed().execute();

        }
    }

    //using intents we send the lfflfeed (the parsed xml to populate the listview)
    // from the async task to listactivity
    private void startListActivity(RSSFeed lfflfeed) {

        Bundle bundle = new Bundle();
        bundle.putSerializable("feed", lfflfeed);
        Intent i = new Intent(SplashActivity.this, ListActivity.class);
        i.putExtras(bundle);
        startActivity(i);
        finish();
    }

    //parse the xml in an async task (background thread)
    private class AsyncLoadXMLFeed extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            DOMParser Do = new DOMParser();
            lfflfeed = Do.parseXml(default_feed_value);

            return null;

        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            startListActivity(lfflfeed);
        }

    }
}
