package com.example.ccm.parseutils;

import com.facebook.FacebookSdk;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseUser;

import android.app.Application;
import android.content.Intent;



public class CCMApplication extends Application {
	
	
	private String APPLICATION_ID = "6iYB10BfMZCaWNOfccmsHEZjn6KZhPVVrNzyv9da";
	private String CLIENT_KEY = "t1LyLjDNlovPqTmhBZJvTjYzsUnYOQl7Qh7yodHs";
	
	 @Override
	 public void onCreate() {
		 super.onCreate();
		 
	 
		 // Add your initialization code here
		 Parse.initialize(this, APPLICATION_ID, CLIENT_KEY);
 
		 ParseUser.enableAutomaticUser();
	     ParseACL defaultACL = new ParseACL();
 
	     // If you would like all objects to be private by default, remove this
	     // line.
	     defaultACL.setPublicReadAccess(true);
 
	     ParseACL.setDefaultACL(defaultACL, true);
        
	     //Se inicializa la Facebook SDK version 4 
	     FacebookSdk.sdkInitialize( getApplicationContext() );
	 }

}
