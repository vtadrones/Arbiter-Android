package com.lmn.Arbiter_Android;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

public class ModifiedFeaturesActivity extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_modified);
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_modified, menu);
        return true;
    }
}
