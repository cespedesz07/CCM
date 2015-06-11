package com.example.ccm;

import java.util.Arrays;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
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
public class LoginActivity extends Activity implements OnClickListener, ConnectionCallbacks, OnConnectionFailedListener, FacebookCallback<LoginResult> {
	
	
	//Permisos que se van a solicitar cuando se inicie sesion con Facebook
	private static final String[] FACEBOOK_PERMISSIONS = {	"user_friends", "public_profile", "email", 
														   	"user_birthday", "user_hometown" };
	
	
	//Código de solicitud para el inicio de sesion en Google
    private static final int REQUEST_CODE_SIGN_IN = 0;
	
	//Callback Manager para procesar el inicio de sesion en Facebook
	private CallbackManager callbackManager;	
	
	//Atributos de la Api de Google
	private GoogleApiClient googleApiClient;	
	
    //Guarda los resultados de la conexion del callback onCOnnectionFaield
	//de manera que se puedan resolver cuando el usuario hace click en el boton de inicio de sesion    
	private ConnectionResult connectionResult;
	
	//Evalua si un Intent está en progreso, y previene un inicio de mas Intents
	private boolean intentInProgress;
	
	//Variable para verificar si se ha logueado el usario, de manera que se pueda resolver cualquier
	//problema previniendo un logueo sin esperar
	private boolean signedInUser;
	
	private LoginButton facebookLoginBtn;
	private SignInButton googleLoginBtn;
	
	
	
	
	

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        
        
        //CONFIGURACION PARA EL BOTON DE LOGIN DE FACEBOOK
        //Se crea un CallbackManager para gestionar el inicio de sesion de usuarios con Facebook
        //Las respuestas del CallBack las recibe la clase LoginManager de Facebook
        callbackManager = CallbackManager.Factory.create();
    	
    	facebookLoginBtn = (LoginButton) findViewById( R.id.facebook_login_button );    	
    	//Se asignan los permisos de lectura al inicio de sesion en facebook
    	facebookLoginBtn.setReadPermissions( Arrays.asList(FACEBOOK_PERMISSIONS) );  
    	//Registro del Callback de facebook que se encarga de procesar el inicio de sesion exitoso, cancelado o con errores
    	
        facebookLoginBtn.registerCallback( callbackManager, this ); 
        facebookLoginBtn.setOnClickListener( this );
        
        
        
        //CONFIGUTACION PARA EL BOTON DE LOGIN DE GOOGLE
        googleLoginBtn = (SignInButton) findViewById( R.id.google_login_button );
        googleLoginBtn.setOnClickListener( this );
        
        
        
        //Se crea un cliente PlusClient para gestionar el inicio de sesion por Google+
	    googleApiClient = new GoogleApiClient.Builder(this)
	    .addConnectionCallbacks(this)
	    .addOnConnectionFailedListener(this)
	    .addApi(Plus.API, Plus.PlusOptions.builder().build())
	    .addScope(Plus.SCOPE_PLUS_LOGIN)
	    .build();
    }
    
    
    
    
    
    //Método que se ejecuta luego de que la actividad ha sido creada al llamar onCreate()
    protected void onStart(){
    	super.onStart();
    	//Luego de que la actividad es Created, se conecta el usuario ya logueado a Google+
    	googleApiClient.connect();
    }
    
    
    
    
    //Método que se llama luego de que la actividad es Pausada all lamar a onPause()
    //Este método oculta la actividad.
    protected void onStop(){
    	super.onStop();
    	//Si el usuario está conectado, al cerrar la actividad lo desconecta
    	if ( googleApiClient.isConnected() ){
    		googleApiClient.disconnect();
    	}
    }
    
    
    
    
    //Método que captura los eventos de los botones cuando se les hace click 
    @Override
	public void onClick(View view){
		switch ( view.getId() ){
		case R.id.facebook_login_button:
			//LoginManager.getInstance().logInWithReadPermissions( this, Arrays.asList(FACEBOOK_PERMISSIONS) );
			break;
			
		case R.id.google_login_button:
			inicioSesionGoogle();
			break;	
		
		default:
			break;
		}		
	}
    
    
    
    
    public void inicioSesionGoogle(){
    	//Si el usuario está conectandose a la app, se tienen que resolver  
		if ( !googleApiClient.isConnecting() ){
			signedInUser = true;
			resolveSignInError();
		}
	}
	
    
	
    
	//Método que se invoca para resolver errores de inicio de sesion
	private void resolveSignInError() {
		//Si es posible establecer una conexion,
		//cambia la variable intentInProgress a true
        if (connectionResult.hasResolution()) {
            try {
                intentInProgress = true;
              //IMPORTANTE: el metodo startResolutionForResult() invoca al método onActivityResult(), y envia el código de inicio de sesion
                connectionResult.startResolutionForResult(this, REQUEST_CODE_SIGN_IN);		
            } 
            catch (IntentSender.SendIntentException error) {
                // The intent was canceled before it was sent.  Return to the default state and
                // attempt to connect to get an updated ConnectionResult.
                intentInProgress = false;
                googleApiClient.connect();
                Toast.makeText(this, "ResolveSingInError: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
    
	
    
    
    
    @Override
    protected void onActivityResult( int requestCode, int resultCode, Intent data ){
    	super.onActivityResult(requestCode, resultCode, data);
    	
    	//El siguiente código permite abrir el dialogo de inicio de sesion en facebook
    	//o capturar las credenciales en caso de que se tenga la App nativa de Facebook
    	callbackManager.onActivityResult(requestCode, resultCode, data);
    	
    	
    	//PARA LA FUNCIONALIDAD DE GOOGLE: eL método startResolutionForResult() envia datos a onActivityResult()
    	//(en especial el REQUEST_CODE_SIGN_IN) para 
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
    
    

	
	
	
	
	//=====================================================CALLBACKS=============================================
    
	//--------------------------------------------------GOOGLE CALLBACK---------------------------------------
	@Override
	public void onConnected(Bundle arg0) {
		signedInUser = false;
		Toast.makeText( this, "User is connected!", Toast.LENGTH_LONG).show();
		
		
	    if ( Plus.PeopleApi.getCurrentPerson(googleApiClient) != null ){
	    	Person currentPerson = Plus.PeopleApi.getCurrentPerson(googleApiClient);
	    	
	    	String info = String.format(
	    			  "Display Name: %s \n"
	    			+ "Gender: %d \n"
	    			+ "Name: %s \n"
	    			+ "Birthday: %s"
	    			+ "Email: %s", currentPerson.getDisplayName(), currentPerson.getGender(),
	    			currentPerson.getName(), currentPerson.getBirthday(), Plus.AccountApi.getAccountName(googleApiClient) );    	
	    	
	    }
	    
	    
	}
	
	
	@Override
	public void onConnectionSuspended(int arg0) {
		googleApiClient.connect();		
	}
	
	
	
	//Método que se llama cuando falla la conexion con Google
	@Override
	public void onConnectionFailed(ConnectionResult result) {
		Log.i( "onCOnnectionFailed", String.valueOf(result.getErrorCode()) );
		//Si definitivamente la conexión no se puede establecer, 
		//se muestra el Dialog del error y se detiene el método con return
		if ( !result.hasResolution() ){
			GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this, 0).show();
			return;
		}
		//Si hay un Intent en progreso, se guarda la conexion actual en la variable connectionResult 
		if ( !intentInProgress ){
			connectionResult = result;
			//Si el 
			if ( signedInUser ){
				resolveSignInError();
			}
		}
				
	}
	
	
	
	
	
	
	//----------------------------------FACEBOOK CALLBACK--------------------------------------
	@Override
	public void onSuccess(LoginResult result) {
		Profile currentProfile = Profile.getCurrentProfile();
		Toast.makeText(this, currentProfile.getName(), Toast.LENGTH_LONG ).show();
		
		//Se captura la información
		GraphRequest request = GraphRequest.newMeRequest( result.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
			@Override
			public void onCompleted(JSONObject object, GraphResponse response) {
				Log.v("LoginActivity", response.toString());				
			}
			
		});
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,gender,birthday" );
        request.setParameters(parameters);
        request.executeAsync();
	}


	//Método que se invoca cuando el usuario cierra el dialogo de inicio de sesion
	@Override
	public void onCancel() {
		Toast.makeText(this, "Facebook Canceled", Toast.LENGTH_SHORT).show();	
	}


	//Método que se llama cuando ocurre un error al momento de iniciar la sesion
	//este método es llamado frecuentemente cuando por ejemplo no hay conexion a internet
	@Override
	public void onError(FacebookException error) {
		//Toast.makeText(this, "Facebook Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();		 
		Toast.makeText(this, "No hay conexión a Internet. Por favor verifica la configuración Wifi", Toast.LENGTH_LONG).show();
	}
    
}
