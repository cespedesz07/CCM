package com.example.ccm.callbacks;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;


/**
 * Callback de Facebook SDK v4 que se encarga de procesar el inicio de sesion exitoso, cancelado o con errores
 * @author Santiago Céspedes Zapata - cespedesz07@gmail.com
 *
 */
public class FacebookCallbackCCM implements FacebookCallback<LoginResult> {
	
	
	private Context context;
	
	
	public FacebookCallbackCCM( Context context ){
		this.context = context;
	}
	

	
	@Override
	public void onSuccess(LoginResult result) {
		Toast.makeText(context, "Success: " + result.getAccessToken().getUserId(), Toast.LENGTH_SHORT ).show();
		Log.i( "fb_success", "Success!: " + result.getAccessToken().getUserId() );
	}
	

	@Override
	public void onCancel() {
		Log.i( "fb_cancel", "Canceled by user" );		
	}
	

	@Override
	public void onError(FacebookException error) {
		Log.i( "fb_error", "error: " + error.getMessage() );		
	}

}
