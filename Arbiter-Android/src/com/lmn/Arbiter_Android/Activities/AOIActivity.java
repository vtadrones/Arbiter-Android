package com.lmn.Arbiter_Android.Activities;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.cordova.Config;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;

import com.lmn.Arbiter_Android.ArbiterProject;
import com.lmn.Arbiter_Android.ArbiterState;
import com.lmn.Arbiter_Android.R;
import com.lmn.Arbiter_Android.BaseClasses.Project;
import com.lmn.Arbiter_Android.CordovaPlugins.ArbiterCordova;
import com.lmn.Arbiter_Android.Map.Map;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;

public class AOIActivity extends FragmentActivity implements CordovaInterface, Map.CordovaMap{
	private static final String TAG = "AOIActivity";
	
	// For CORDOVA
    private CordovaWebView cordovaWebView;
    private final ExecutorService threadPool = Executors.newCachedThreadPool();
    private ArbiterProject arbiterProject;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.choose_aoi_dialog);
		
		Config.init(this);
		
		cordovaWebView = (CordovaWebView) findViewById(R.id.aoiWebView);
		
		Init();
		
        cordovaWebView.loadUrl(ArbiterCordova.aoiUrl, 5000);
	}
	
	private void Init(){
		registerListeners();
		arbiterProject = ArbiterProject.getArbiterProject();
	}
	
	private void resetSavedExtent(){
        arbiterProject.setSavedBounds(null);
        arbiterProject.setSavedZoomLevel(null);
	}
	
	private void registerListeners(){
		View cancel = (View) findViewById(R.id.cancelButton);
		final ArbiterProject arbiterProject = ArbiterProject.getArbiterProject();
        final AOIActivity activity = this;
        final boolean isCreatingProject = ArbiterState.getState().isCreatingProject();
        
        cancel.setOnClickListener(new OnClickListener(){
        	@Override
        	public void onClick(View v){
        		if(isCreatingProject){
        			arbiterProject.doneCreatingProject(
        					activity.getApplicationContext());
        		}
        		
        		activity.finish();
        	}
        });
        
        View ok = (View) findViewById(R.id.okButton);
        
        ok.setOnClickListener(new OnClickListener(){
        	@Override
        	public void onClick(View v){
        		if(isCreatingProject){
        			Log.w("AOIActivity", "AOIActivity: set new projects aoi");
        			Map.getMap().setNewProjectsAOI(cordovaWebView);
        		}else{
        			Map.getMap().setAOI(cordovaWebView);
        		}
        	}
        });
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.aoi, menu);
		return true;
	}

	@Override
    protected void onPause() {
            super.onPause();
            Log.d(TAG, "onPause");
    }
    
    @Override 
    protected void onResume(){
    	super.onResume();
    	Log.d(TAG, "onResume");
    	
    	resetSavedExtent();
    }
    
    @Override
    protected void onDestroy(){
    	super.onDestroy();
    	if(this.cordovaWebView != null){
    		cordovaWebView.handleDestroy();
    	}
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) 
    {
        super.onConfigurationChanged(newConfig);
    }

    /**
     * Map.CordovaMap methods
     */
    
    @Override
    public CordovaWebView getWebView(){
    	return this.cordovaWebView;
    }
    
    /**
     * Cordova methods
     */
	@Override
	public Activity getActivity() {
		return this;
	}

	@Override
	public ExecutorService getThreadPool() {
		return threadPool;
	}

	@Override
	public Object onMessage(String message, Object obj) {
		Log.d(TAG, message);
		if(message.equals("onPageFinished")){
        	if(obj instanceof String){
        		if(((String) obj).equals(ArbiterCordova.aoiUrl)){
        			String savedBounds = arbiterProject.getSavedBounds();
        			String savedZoom = arbiterProject.getSavedZoomLevel();
        			
        			if(savedBounds != null && savedZoom != null){
        				Map.getMap().zoomToExtent(cordovaWebView, 
            					arbiterProject.getSavedBounds(),
            					arbiterProject.getSavedZoomLevel());
        			}else{
        				Map.getMap().zoomToDefault(cordovaWebView);
        			}
        		}else if(((String) obj).equals("about:blank")){
        			this.cordovaWebView.loadUrl(ArbiterCordova.aoiUrl);
        		}
        	}
        }
		
        return null;
	}
	
	@Override
	public void setActivityResultCallback(CordovaPlugin cordovaPlugin) {
		Log.d(TAG, "setActivityResultCallback is unimplemented");
		
	}

	@Override
	public void startActivityForResult(CordovaPlugin cordovaPlugin, Intent intent, int resultCode) {
		Log.d(TAG, "startActivityForResult is unimplemented");
		
	}
}
