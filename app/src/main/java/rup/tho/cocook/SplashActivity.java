package rup.tho.cocook;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

import android.widget.TextView;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import rup.tho.cocook.util.db.CoCookDatabaseHelper;

public class SplashActivity extends Activity {
    private TextView tvLoad = null;
    private int mCount = 0;
    private CoCookDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        tvLoad = (TextView) findViewById(R.id.tvLoad);
        new BackgroundSplashTask().execute();
    }

    private class BackgroundSplashTask extends AsyncTask {
        private List<String> listLoadMessages;
        private Timer timer;
        // this handler will obtain messages from the timer which we initialise in preExecute
        // on every update (3 seconds) it will set a new loading message to the tvLoad
        private Handler mHandler = new Handler() {
            private Random r = new Random();

            public void handleMessage(Message msg) {
                mCount++;

                int randMsgIndex = r.nextInt(listLoadMessages.size());
                tvLoad.setText(listLoadMessages.get(randMsgIndex));

                listLoadMessages.remove(randMsgIndex);
            }
        };

        @Override
        protected Object doInBackground(Object[] params) {
            getHelper().createNewDatabase();

            while (mCount < 3) {

            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            initMessageList();

            //start timer
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                public void run() {
                    mHandler.obtainMessage(1).sendToTarget();
                }
            }, 0, 3000);
        }

        private void initMessageList() {
            listLoadMessages = new ArrayList<String>(Arrays.asList(
                    SplashActivity.this.getResources().getStringArray(R.array.load_msg_array)));
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);
            timer.cancel();
            Intent i = new Intent(SplashActivity.this,
                    MainActivity.class);
            startActivity(i);
            finish();
        }
    }

    protected CoCookDatabaseHelper getHelper() {
        if (dbHelper == null) {
            dbHelper = OpenHelperManager.getHelper(this, CoCookDatabaseHelper.class);
        }
        return dbHelper;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (dbHelper != null) {
            OpenHelperManager.releaseHelper();
            dbHelper = null;
        }
    }
}
