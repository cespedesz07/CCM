package com.specializedti.ccm.login;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.Toast;

import com.specializedti.ccm.R;
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
import com.specializedti.ccm.actionbar.CCMActionBarActivity;
import com.specializedti.ccm.eventos.AreaListActivity;
import com.specializedti.ccm.menuinicio.MenuInicioActivity;
import com.specializedti.ccm.preferences.CCMPreferences;
import com.specializedti.ccm.qrcode.QRCodeActivity;
import com.specializedti.ccm.registro.RegistroActivity;
import com.specializedti.ccm.restclient.RegistroRestClientTask;


/**
 * Actividad que corresponde al inicio de sesion en diferentes redes sociales
 * (Facebook, Twitter, Google+, o Login Navito en la plataforma)
 * @author Santiago C�spedes Zapata
 *
 */
public class LoginActivity extends CCMActionBarActivity implements OnClickListener, OnTouchListener, ConnectionCallbacks, OnConnectionFailedListener, FacebookCallback<LoginResult> {
	
	
	//Constantes que representan el tipo de response, utilizadas en el m�todo inicioRegistro()
	//De acuerdo al tipo de response, se insertan en un Bundle los par�metros que retorne la API
	//respectiva, y se envian a la actividad RegistroActivity.java
	public static final String FACEBOOK_LOGIN = "facebookLogin";
	public static final String GOOGLE_LOGIN = "googleLogin";
	public static final String TWITTER_LOGIN = "twitterLogin";
	public static final String NATIVE_LOGIN = "nativeLogin";
	
	
	//Variables que se van a extraer de la peciticion a Facebook con GraphApi
	public static final String FB_NOMBRE = "first_name";
	public static final String FB_APELLIDOS = "last_name";
	public static final String FB_GENERO = "gender";
	public static final String FB_FECHA_NACIMIENTO = "birthday";
	public static final String FB_EMAIL = "email";
	
	
	
	//Permisos que se van a solicitar cuando se inicie sesion con Facebook
	private static final String[] FACEBOOK_PERMISSIONS = {	"user_friends", "public_profile", "email", 
														   	"user_birthday", "user_hometown" };
	
	
	//C�digo de solicitud para el inicio de sesion en Google
    private static final int REQUEST_CODE_SIGN_IN = 0;
	
	//Callback Manager para procesar el inicio de sesion en Facebook
	private CallbackManager callbackManager;	
	
	//Atributos de la Api de Google
	private GoogleApiClient googleApiClient;	
	
    //Guarda los resultados de la conexion del callback onCOnnectionFailed
	//de manera que se puedan resolver cuando el usuario hace click en el boton de inicio de sesion    
	private ConnectionResult connectionResult;
	
	//Evalua si un Intent est� en progreso, y previene un inicio de mas Intents
	private boolean intentInProgress;
	
	//Variable para verificar si se ha logueado el usario, de manera que se pueda resolver cualquier
	//problema previniendo un logueo sin esperar
	private boolean signedInUser;
	
	
	private LoginButton facebookLoginBtn;
	private SignInButton googleLoginBtn;
	private Button btnRegistro;
	private Button btnLogin;
	
	
	
	
	

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
        
        
        
        //CONFIGURACION PARA EL BOTON DE LOGIN DE GOOGLE
        /*
        googleLoginBtn = (SignInButton) findViewById( R.id.google_login_button );
        googleLoginBtn.setOnClickListener( this );
        */
        
        
        
        //Configuracion del Bot�n de Registro nativo
        btnRegistro = (Button) findViewById( R.id.btn_registro_login_activity );
        btnRegistro.setOnClickListener(this);
        btnRegistro.setOnTouchListener(this);
        
        btnLogin = (Button) findViewById( R.id.btn_login_login_activity );
        btnLogin.setOnClickListener(this);
        btnLogin.setOnTouchListener(this);
        
        
        //Se crea un cliente PlusClient para gestionar el inicio de sesion por Google+
	    googleApiClient = new GoogleApiClient.Builder(this)
	    .addConnectionCallbacks(this)
	    .addOnConnectionFailedListener(this)
	    .addApi(Plus.API, Plus.PlusOptions.builder().build())
	    .addScope(Plus.SCOPE_PLUS_LOGIN)
	    .build();
	    
	    //Se envia la variable googleApiClient a la clase padre CCMActionBarActivity.java
	    setGoogleApiClient(googleApiClient);
	    disableHome();
	    setSalirEnabled(false);
    }
    
    
    
    
    
    
    //M�todo que se ejecuta luego de que la actividad ha sido creada al llamar onCreate()
    protected void onStart(){
    	super.onStart();   	
    	//googleApiClient.connect();
    	//Luego de que la actividad es Created, se conecta el usuario ya logueado
    	//segun la red social utilizada para hacer el login
    	String loginType = new CCMPreferences( this ).obtenerTipoLogin();
    	Log.v( "LoginType inicio: ", loginType );
    	if ( !loginType.equals("") ){
    		Intent i = new Intent( this, MenuInicioActivity.class );
    		startActivity( i );
    	}
    	/*
    	if ( loginType.equals( FACEBOOK_LOGIN ) ){
    	}
    	else if ( loginType.equals( GOOGLE_LOGIN ) ){
    		googleApiClient.connect();
    	}
    	else if ( loginType.equals( TWITTER_LOGIN ) ){
    	}
    	else if ( loginType.equals( NATIVE_LOGIN ) ){
    		Intent i = new Intent( this, MenuInicioActivity.class );
    		startActivity( i );
    	}
    	*/    	
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
    
    
    
    public void inicioSesionGoogle(){
    	//Si el usuario est� conectandose a la app, se tienen que resolver
    	/*
		if ( !googleApiClient.isConnecting() ){
			signedInUser = true;
			resolveSignInError();
		}
		*/
    	if ( !googleApiClient.isConnected()  &&  connectionResult != null ){
    		try {
    			//redirige a onActivityResult() 
				connectionResult.startResolutionForResult(this, REQUEST_CODE_SIGN_IN);
			} 
    		catch (SendIntentException e) {
				connectionResult = null;
				googleApiClient.connect();	
				e.printStackTrace();
			}
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
    
	
    
    
    
    protected void onActivityResult( int requestCode, int resultCode, Intent data ){
    	super.onActivityResult(requestCode, resultCode, data);
    	
    	//El siguiente c�digo permite abrir el dialogo de inicio de sesion en facebook
    	//o capturar las credenciales en caso de que se tenga la App nativa de Facebook
    	callbackManager.onActivityResult(requestCode, resultCode, data);    	
    	
    	//PARA LA FUNCIONALIDAD DE GOOGLE: eL m�todo startResolutionForResult() envia datos a onActivityResult()
    	//(en especial el REQUEST_CODE_SIGN_IN) para
    	/*
		if (requestCode == REQUEST_CODE_SIGN_IN ){
			if (resultCode == RESULT_OK) {
				signedInUser = false;
	        }             
	        intentInProgress = false;             
	        if ( !googleApiClient.isConnecting() ){
	        	googleApiClient.connect();
	        }
		}
		*/
    	if (requestCode == REQUEST_CODE_SIGN_IN  &&  resultCode == RESULT_OK) {
    		connectionResult = null;
    		googleApiClient.connect();
    	}
    }   
     
    
    //M�todo que captura los datos del usuario del inicio de sesion en redes sociales
    //y los envia a la actividad RegistroActivity.java
    private void inicioRegistro( JSONObject jsonResponse, String loginType ){
    	//Se inician las animaciones de: 
    	//enterAnim (R.animator.first_close_translate): trasladar de derecha al centro la actividad a abrir (RegistroActivity.class)
    	//exitAnim: (R.animator.first_close_scale): Escalar o disminuir tama�o de la actividad saliente (LoginActivity.class)
    	//overridePendingTransition( R.animator.first_close_translate, R.animator.second_close_scale);
    	//Log.v( "json response", jsonResponse.toString() );
    	Bundle bundleParams = new Bundle();
    	if ( loginType.equals( FACEBOOK_LOGIN ) ){
    		try{
    			bundleParams.putString( FB_NOMBRE, jsonResponse.getString( FB_NOMBRE ) );
    			bundleParams.putString( FB_APELLIDOS, jsonResponse.getString( FB_APELLIDOS ) );
    			bundleParams.putString( FB_GENERO,  procesarGenero( jsonResponse.getString(FB_GENERO) )   );
    			bundleParams.putStringArray( FB_FECHA_NACIMIENTO, procesarFecha( jsonResponse.getString(FB_FECHA_NACIMIENTO) ) );
    			bundleParams.putString( FB_EMAIL, jsonResponse.getString( FB_EMAIL ) );
    		}
    		catch ( JSONException error ){
    			Log.i( "JSONException", error.getMessage() );
    		}
    	}
    	else if ( loginType.equals( GOOGLE_LOGIN )  ){
    		
    	}
    	else if ( loginType.equals( TWITTER_LOGIN ) ){
    		
    	}
    	else if ( loginType.equals( NATIVE_LOGIN ) ){
    		//SI es un inicio de sesion nativo, no se coloca ningun parametro en el Bundle,
    		//por lo tanto este queda vacio
    	}
    	//new CCMPreferences( this ).guardarTipoLogin( loginType );
    	
    	bundleParams.putString( CCMPreferences.CAMPO_LOGIN_TYPE, loginType );    	
    	Intent i = new Intent( this, RegistroActivity.class );
    	i.putExtras( bundleParams );
    	startActivity( i );
    }
    
    
    private String procesarGenero( String generoEntrada ){
    	if ( generoEntrada.toLowerCase().equals("male") ){
    		return getResources().getString( R.string.genero_masculino );
    	}
    	else if ( generoEntrada.toLowerCase().equals("female") ){
    		return getResources().getString( R.string.genero_femenino );
    	}
    	else{
    		return generoEntrada.toLowerCase();
    	}    	
    }
    
    
    private String[] procesarFecha( String fechaEntrada ){
    	//Se parte la fecha de nacimiento, ya que el formato de entrada es 02\/15\/1994
    	String[] fechaEntradaPartida = fechaEntrada.split( "\\/" );
    	return fechaEntradaPartida;
		/*
		Calendar calendar = Calendar.getInstance();
    	calendar.set( Integer.valueOf(fechaEntradaPartida[2]), Integer.valueOf(fechaEntradaPartida[0]), Integer.valueOf(fechaEntradaPartida[1]) );
		SimpleDateFormat dateFormat = new SimpleDateFormat( "dd/M/yyyy" );
		return dateFormat.format( calendar.getTime() );
		*/
    }
    
    
    
    //M�todo que captura los eventos de los botones cuando se les hace click 
    @Override
	public void onClick(View view){
    	if ( hayInternet() ){
    		switch ( view.getId() ){
    		case R.id.facebook_login_button:
    			//Se procede a iniciar sesion por Facebook
    			//VER EN LA PARTE INFERIOR, EL LA SECCI�N Facebook Callback
    			//onSuccess(), aqui se procesa el Click del Boton de Facebook
    			//llamando al metodo inicioRegistro)(
    			//LoginManager.getInstance().logInWithReadPermissions( this, Arrays.asList(FACEBOOK_PERMISSIONS) );
    			break;
    		/*	
    		case R.id.google_login_button:
    			inicioSesionGoogle();
    			break;
    		*/	
    		
    		case R.id.btn_registro_login_activity:
    			inicioRegistro( null, NATIVE_LOGIN );
    			break;
    			
    		case R.id.btn_login_login_activity:
    			startActivity( new Intent( this, LoginNativoActivity.class ) );
    			break;
    			
    		default:
    			break;
    		}
    	}		
	}
    
    
    
    //M�todo para capturar el presionado del bot�n y cabiar el fondo del mismo
  	//esto es para crear el efecto de presionado de los botones
    @Override
	public boolean onTouch(View v, MotionEvent event) {
		if ( event.getAction() == MotionEvent.ACTION_DOWN ){	//Si se presiona la pantalla...
			Drawable d = v.getBackground();						// se captura el background la vista presionada
			//d.mutate();
			d.setAlpha(50);									// se agrega transparencia al background
			v.setBackgroundDrawable(d);							// A la vista presionada se agrega el nuevo background transparente
		}
		else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL){
			Drawable d = v.getBackground();
			d.setAlpha(255);
			v.setBackgroundDrawable(d);
		}
		return false;
	}

	
	
	
    
    
    
    
    
    
    
    
    
	
    
    //===========================================================================================================
	//=====================================================CALLBACKS=============================================
    //===========================================================================================================
    
	//--------------------------------------------------GOOGLE CALLBACK------------------------------------------
    //M�todo que se llama luego de conectar a la Api de Google
    //es decir, luego de invocar googleApiClient.connect()
	@Override
	public void onConnected(Bundle arg0) {
		signedInUser = false;
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
		Person currentPerson = Plus.PeopleApi.getCurrentPerson(googleApiClient);
		Log.v( "current Person: ", currentPerson.getBirthday() + currentPerson.getName() );
	}
	
	
	@Override
	public void onConnectionSuspended(int arg0) {
		googleApiClient.connect();		
	}
	
	
	
	//M�todo que se llama cuando falla la conexion con Google
	@Override
	public void onConnectionFailed(ConnectionResult result) {
		/*
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
		*/
		connectionResult = result;
				
	}
	
	
	
	
	
	
	
	//--------------------------------------------------FACEBOOK CALLBACK--------------------------------------
	@Override
	public void onSuccess(LoginResult result) {
		//Profile currentProfile = Profile.getCurrentProfile();
		//Se crea una peticion a Facebook, y que envie datos en formato JSON
		GraphRequest request = GraphRequest.newMeRequest( result.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
			@Override
			//Cuando se complete el request a Facebook se procesa
			//la respuesta que entrega la API en formato JSON que es asi
			public void onCompleted( JSONObject object, GraphResponse response ) {
				JSONObject jsonResponse = response.getJSONObject();
				Log.v( "Facebook onSuccess", response.toString() );
				inicioRegistro( jsonResponse, FACEBOOK_LOGIN );				
			}			
		});
        Bundle parameters = new Bundle();
        String graphApiFields = String.format( "%s,%s,%s,%s,%s", FB_NOMBRE, FB_APELLIDOS, FB_GENERO, FB_FECHA_NACIMIENTO, FB_EMAIL );
        parameters.putString("fields", graphApiFields );					//Se establecen los parametros a extraer del request
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
		Toast.makeText(this, "Facebook Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
	}
    
}
