package com.example.ccm.login;

import java.util.Arrays;

import org.json.JSONObject;

import android.content.Intent;
import android.content.IntentSender;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.Toast;

import com.example.ccm.R;
import com.example.ccm.registro.RegistroActivity;
import com.facebook.AccessToken;
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
 * @author Santiago C�spedes Zapata
 *
 */
public class LoginActivity extends ActionBarActivity implements OnClickListener, OnTouchListener, ConnectionCallbacks, OnConnectionFailedListener, FacebookCallback<LoginResult> {
	
	
	//Permisos que se van a solicitar cuando se inicie sesion con Facebook
	private static final String[] FACEBOOK_PERMISSIONS = {	"user_friends", "public_profile", "email", 
														   	"user_birthday", "user_hometown" };
	
	
	//C�digo de solicitud para el inicio de sesion en Google
    private static final int REQUEST_CODE_SIGN_IN = 0;
	
	//Callback Manager para procesar el inicio de sesion en Facebook
	private CallbackManager callbackManager;	
	
	//Atributos de la Api de Google
	private GoogleApiClient googleApiClient;	
	
    //Guarda los resultados de la conexion del callback onCOnnectionFaield
	//de manera que se puedan resolver cuando el usuario hace click en el boton de inicio de sesion    
	private ConnectionResult connectionResult;
	
	//Evalua si un Intent est� en progreso, y previene un inicio de mas Intents
	private boolean intentInProgress;
	
	//Variable para verificar si se ha logueado el usario, de manera que se pueda resolver cualquier
	//problema previniendo un logueo sin esperar
	private boolean signedInUser;
	
	//Variable para verificar si el usuario ya ha iniciado sesion en facebook
	//(usada principalmente luego de cargar el splash screen y evitar que se 
	//inicie el login principal, es decir LoginActivity.java )
	private boolean facebookLoggedIn;
	
	private LoginButton facebookLoginBtn;
	private SignInButton googleLoginBtn;
	private Button btnRegistro;
	
	
	
	
	

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
        
        
        
        //Configuracion del Bot�n de Registro nativo
        btnRegistro = (Button) findViewById( R.id.btn_registro_login_activity );
        btnRegistro.setOnClickListener(this);
        btnRegistro.setOnTouchListener(this);
        
        
        //Se crea un cliente PlusClient para gestionar el inicio de sesion por Google+
	    googleApiClient = new GoogleApiClient.Builder(this)
	    .addConnectionCallbacks(this)
	    .addOnConnectionFailedListener(this)
	    .addApi(Plus.API, Plus.PlusOptions.builder().build())
	    .addScope(Plus.SCOPE_PLUS_LOGIN)
	    .build();
	    
	    
	    //Se verifica si ha iniciado sesion con la cuenta de Facebook
	    if ( AccessToken.getCurrentAccessToken() != null ){
    		facebookLoggedIn = true;
    	}
    	else{
    		facebookLoggedIn = false;
    	}
	    
	    
    }
    
    
    
    
    
    //M�todo que se ejecuta luego de que la actividad ha sido creada al llamar onCreate()
    protected void onStart(){
    	super.onStart();
    	//Luego de que la actividad es Created, se conecta el usuario ya logueado a Google+
    	
    	if ( facebookLoggedIn ){
    		inicioRegistro();
    	}
    	
    	googleApiClient.connect();
    }
    
    
    
    
    //M�todo que se llama luego de que la actividad es Pausada all lamar a onPause()
    //Este m�todo oculta la actividad.
    protected void onStop(){
    	super.onStop();
    	//Si el usuario est� conectado, al cerrar la actividad lo desconecta
    	if ( googleApiClient.isConnected() ){
    		googleApiClient.disconnect();
    	}
    }
    
    
    //M�todo que se llama cuando la actividad es detruida
    @Override
    protected void onDestroy(){
    	super.onDestroy();
    }
    
    
    
    
    
    //M�todo que captura los eventos de los botones cuando se les hace click 
    @Override
	public void onClick(View view){
		switch ( view.getId() ){
		case R.id.facebook_login_button:
			LoginManager.getInstance().logInWithReadPermissions( this, Arrays.asList(FACEBOOK_PERMISSIONS) );
			break;
			
		case R.id.google_login_button:
			inicioSesionGoogle();
			break;	
		
		case R.id.btn_registro_login_activity:
			inicioRegistro();
		default:
			break;
		}		
	}
    
    
    
    
    public void inicioSesionGoogle(){
    	//Si el usuario est� conectandose a la app, se tienen que resolver  
		if ( !googleApiClient.isConnecting() ){
			signedInUser = true;
			resolveSignInError();
		}
	}
	
    
	
    
	//M�todo que se invoca para resolver errores de inicio de sesion de google
	private void resolveSignInError() {
		//Si es posible establecer una conexion,
		//cambia la variable intentInProgress a true
        if (connectionResult.hasResolution()) {
            try {
                intentInProgress = true;
              //IMPORTANTE: el metodo startResolutionForResult() invoca al m�todo onActivityResult(), y envia el c�digo de inicio de sesion
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
    	
    	//El siguiente c�digo permite abrir el dialogo de inicio de sesion en facebook
    	//o capturar las credenciales en caso de que se tenga la App nativa de Facebook
    	callbackManager.onActivityResult(requestCode, resultCode, data);
    	
    	
    	//PARA LA FUNCIONALIDAD DE GOOGLE: eL m�todo startResolutionForResult() envia datos a onActivityResult()
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
    
    
    
    
    private void inicioRegistro(){
    	//Se inician las animaciones de: 
    	//enterAnim (R.animator.first_close_translate): trasladar de derecha al centro la actividad a abrir (RegistroActivity.class)
    	//exitAnim: (R.animator.first_close_scale): Escalar o disminuir tama�o de la actividad saliente (LoginActivity.class)
    	overridePendingTransition( R.animator.first_close_translate, R.animator.second_close_scale);
    	Intent i = new Intent( LoginActivity.this, RegistroActivity.class );    	
    	startActivity( i );
    }
    
    
    
    //M�todo para capturar el presionado del bot�n y cabiar el fondo del mismo
  	//esto es para crear el efecto de presionado de los botones
    @Override
	public boolean onTouch(View v, MotionEvent event) {
		if ( event.getAction() == MotionEvent.ACTION_DOWN ){	//Si se presiona la pantalla...
			Drawable d = v.getBackground();						// se captura el background la vista presionada
			//d.mutate();
			d.setAlpha(100);									// se agrega transparencia al background
			v.setBackgroundDrawable(d);							// A la vista presionada se agrega el nuevo background transparente
		}
		else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL){
			Drawable d = v.getBackground();
			d.setAlpha(255);
			v.setBackgroundDrawable(d);
		}
		return false;
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
	
	
	
	//M�todo que se llama cuando falla la conexion con Google
	@Override
	public void onConnectionFailed(ConnectionResult result) {
		Log.i( "onConnectionFailed", String.valueOf(result.getErrorCode()) );
		//Si definitivamente la conexi�n no se puede establecer, 
		//se muestra el Dialog del error y se detiene el m�todo con return
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
		
		//Se crea una peticion a Facebook, y que envie datos en formato JSON
		GraphRequest request = GraphRequest.newMeRequest( result.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
			@Override
			//Cuando se complete el request a Facebook
			public void onCompleted( JSONObject object, GraphResponse response ) {
				JSONObject jsonResponse = response.getJSONObject();
				Log.v( "LoginActivity", response.toString() );
				inicioRegistro();				
			}			
		});
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,gender,birthday" );	//Se establecen los parametros a extraer del request
        request.setParameters(parameters);									//(Estos par�metros se pueden consultar en: https://developers.facebook.com/docs/graph-api/reference/user)
        request.executeAsync();												//Se ejecuta el request y queda a la espera de la ejecucion del m�todo onCompleted()
	}


	
	//M�todo que se invoca cuando el usuario cierra el dialogo de inicio de sesion
	@Override
	public void onCancel() {
		//Toast.makeText(this, "Facebook Canceled", Toast.LENGTH_SHORT).show();	
	}


	//M�todo que se llama cuando ocurre un error al momento de iniciar la sesion
	//este m�todo es llamado frecuentemente cuando por ejemplo no hay conexion a internet
	@Override
	public void onError(FacebookException error) {
		Log.i("Facebook Callback onError(): ", error.getMessage() );		 
		Toast.makeText(this, "No hay conexi�n a Internet. Por favor verifica la configuraci�n Wifi", Toast.LENGTH_LONG).show();
	}
    
}