package com.example.ccm;

import java.util.Arrays;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.ccm.callbacks.FacebookCallbackCCM;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;


/**
 * Actividad que corresponde al inicio de sesion en diferentes redes sociales
 * (Facebook, Twitter, Google+, o Login Navito en la plataforma)
 * @author Santiago Céspedes Zapata
 *
 */
public class LoginActivity extends Activity implements OnClickListener, ConnectionCallbacks, OnConnectionFailedListener {
	
	
	//Permisos que se van a solicitar cuando se inicie sesion con Facebook
	private static final String[] FACEBOOK_PERMISSIONS = {	"user_friends", "public_profile", "email", 
														   	"user_birthday", "user_hometown",  };
	
	
	private static final int DIALOG_GET_GOOGLE_PLAY_SERVICES = 1;
    private static final int REQUEST_CODE_SIGN_IN = 0;
    private static final int REQUEST_CODE_GET_GOOGLE_PLAY_SERVICES = 2;
	
	
	private CallbackManager callbackManager;
	
	private GoogleApiClient googleApiClient;
	private ConnectionResult connectionResult;
	private boolean intentInProgress;
	private boolean signedInUser;
	
	private LoginButton facebookLoginBtn;
	private SignInButton googleLoginBtn;
	
	
	

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
    	
    	facebookLoginBtn = (LoginButton) findViewById( R.id.facebook_login_button );    	
    	//Se asignan los permisos de lectura al inicio de sesion en facebook
    	facebookLoginBtn.setReadPermissions( Arrays.asList(FACEBOOK_PERMISSIONS) );  
    	//Registro del Callback de facebook que se encarga de procesar el inicio de sesion exitoso, cancelado o con errores
    	/*
        facebookLoginBtn.registerCallback( callbackManager, new FacebookCallback<LoginResult>() {
			
			@Override
			public void onSuccess(LoginResult result) {
				Toast.makeText(getApplicationContext(), "Facebook User Id: " + result.getAccessToken().getUserId(), Toast.LENGTH_LONG ).show();
				
			}
			
			@Override
			public void onError(FacebookException error) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onCancel() {
				// TODO Auto-generated method stub
				
			}
		} );
		*/
        facebookLoginBtn.setOnClickListener( this );
        
        
        googleLoginBtn = (SignInButton) findViewById( R.id.google_login_button );
        googleLoginBtn.setOnClickListener( this );
        
        
        //Se crea un CallbackManager para gestionar el inicio de sesion de usuarios con Facebook
        //Las respuestas del CallBack las recibe la clase LoginManager de Facebook
        callbackManager = CallbackManager.Factory.create();
        
        //Se crea un cliente PlusClient para gestionar el inicio de sesion por Google+
	    googleApiClient = new GoogleApiClient.Builder(this)
	    .addConnectionCallbacks(this)
	    .addOnConnectionFailedListener(this)
	    .addApi(Plus.API, Plus.PlusOptions.builder().build())
	    .addScope(Plus.SCOPE_PLUS_LOGIN)
	    .build();
    }
    
    
    
    protected void onStart(){
    	super.onStart();
    	googleApiClient.connect();
    } 
    
    
    
    protected void onStop(){
    	super.onStop();
    	if ( googleApiClient.isConnected() ){
    		googleApiClient.disconnect();
    	}
    }
    
    
    
    private void resolveSignInError() {
        if (connectionResult.hasResolution()) {
            try {
                intentInProgress = true;
                connectionResult.startResolutionForResult(this, REQUEST_CODE_SIGN_IN);
            } 
            catch (IntentSender.SendIntentException e) {
                // The intent was canceled before it was sent.  Return to the default state and
                // attempt to connect to get an updated ConnectionResult.
                intentInProgress = false;
                googleApiClient.connect();
            }
        }
    }
    
    
    
    @Override
    protected void onActivityResult( int requestCode, int resultCode, Intent data ){
    	super.onActivityResult(requestCode, resultCode, data);
    	
    	//El siguiente código permite abrir el dialogo de inicio de sesion en facebook
    	//o capturar las credenciales en caso de que se tenga la App nativa de Facebook
    	callbackManager.onActivityResult(requestCode, resultCode, data);
    	
    	
    	 if (requestCode == REQUEST_CODE_SIGN_IN ){    		 
             if (resultCode == RESULT_OK) {
            	 signedInUser = false;
             }             
             intentInProgress = false;             
             if ( !googleApiClient.isConnecting() ){
            	 googleApiClient.connect();
             }
         }
    }   
    
    

    
    
    
	@Override
	public void onClick(View view){
		switch ( view.getId() ){
		case R.id.facebook_login_button:
			LoginManager.getInstance().logInWithReadPermissions( this, Arrays.asList(FACEBOOK_PERMISSIONS) );
			break;
		
		case R.id.google_login_button:
			inicioSesionGoogle();
			break;			
		
		default:
			break;
		}		
	}
	
	
	
	public void inicioSesionGoogle(){    	
		if ( !googleApiClient.isConnecting() ){
			signedInUser = true;
			resolveSignInError();
		}
	}
	
	
	
	
	
	
	
	
	
	//---------------------CALLBACKS---------------------------------
	//GOOGLE CALLBACK
	@Override
	public void onConnected(Bundle arg0) {
		signedInUser = false;
		Toast.makeText( this, "User is connected!", Toast.LENGTH_LONG).show();
		 
	    // Get user's information
	    if ( Plus.PeopleApi.getCurrentPerson(googleApiClient) != null ){
	    	Person currentPerson = Plus.PeopleApi.getCurrentPerson(googleApiClient);
	    	String info = String.format(
	    			  "Display Name: %s \n"
	    			+ "Gender: %d \n"
	    			+ "Name: %s \n"
	    			+ "Birthday: %s"
	    			+ "Email: %s", currentPerson.getDisplayName(), currentPerson.getGender(),
	    			currentPerson.getName(), currentPerson.getBirthday(), Plus.AccountApi.getAccountName(googleApiClient) );
	    	Toast.makeText(this, info, Toast.LENGTH_LONG).show();
	    }
	    
	}
	
	
	@Override
	public void onConnectionSuspended(int arg0) {
		googleApiClient.connect();		
	}
	
	
	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if ( !result.hasResolution() ){
			GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this, 0).show();
			return;
		}
		
		if ( !intentInProgress ){
			//se guarda la conexion actual
			connectionResult = result;
			
			if ( signedInUser ){
				resolveSignInError();
			}
		}
				
	}
	
	
	
	
	
	//FACEBOOK CALLBACK
    
}
