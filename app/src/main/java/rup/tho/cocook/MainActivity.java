package rup.tho.cocook;

import android.app.Activity;
import android.content.Intent;
import android.os.SystemClock;
import android.os.Bundle;
import android.view.View;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import rup.tho.cocook.letscook.recipe.SuggestedRecipesActivity;
import rup.tho.cocook.dosdonts.DosDontsActivity;
import rup.tho.cocook.fridge.FridgeActivity;
import rup.tho.cocook.util.db.CoCookDatabaseHelper;

public class MainActivity extends Activity {

    private long mTimeLastClick = 0;
    CoCookDatabaseHelper dbHelper;
    private static boolean first = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onMenuButtonClick(View v) {
        Intent intent = null;

        // disallow clicking two buttons at the same time by checking last click time
        if (SystemClock.elapsedRealtime() - mTimeLastClick < 1000) {
            return;
        }
        //     AudioPlayer.getInstance().playMenuClick(this);
        mTimeLastClick = SystemClock.elapsedRealtime();

        // Handle button clicks
        switch (v.getId()) {
            case R.id.btLetsCook:
                 intent = new Intent(this, SuggestedRecipesActivity.class);
                break;
            case R.id.btYourFridge:
                intent = new Intent(this, FridgeActivity.class);
                break;
            case R.id.btDosDonts:
                intent = new Intent(this, DosDontsActivity.class);
                break;
            default:
                /*Log.i(MainActivity.class.getName(), "Attempt to create DB");
                EatItDbCreator creator = new EatItDbCreator(getHelper(), this);
                creator.createNewDatabase();*/
                break;
        }

        if (intent != null) {
            startActivity(intent);
        }
    }


}
