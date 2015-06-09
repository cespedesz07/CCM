package com.example.ccm;

import java.util.Arrays;
import java.util.Collections;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

public class Login extends Activity {
	
	
	
	private static final String[] FACEBOOK_PERMISSIONS = { "user_friends", "public_profile", "email" };
	
	private CallbackManager callbackManager;
	private LoginButton facebookLoginBtn;
	
	
	

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        
        callbackManager = CallbackManager.Factory.create();       
    }
    
    
    
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ){
    	View view = inflater.inflate(R.layout.login, container, false);
    	
    	facebookLoginBtn = (LoginButton) view.findViewById( R.id.facebook_login_button );
    	facebookLoginBtn.setReadPermissions( Arrays.asList(FACEBOOK_PERMISSIONS) );
    	
    	// Callback registration
        facebookLoginBtn.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
            	Toast.makeText(getApplicationContext(), loginResult.getAccessToken().getUserId(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                // App code
            }

			@Override
			public void onError(FacebookException error) {
				Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();				
			}
        }); 
    	return view;
    }
    
    
    
    @Override
    protected void onActivityResult( int requestCode, int resultCode, Intent data ){
    	super.onActivityResult(requestCode, resultCode, data);
    	callbackManager.onActivityResult(requestCode, resultCode, data);
    }
    
}
