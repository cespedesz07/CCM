package com.specializedti.ccm.actionbar;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.specializedti.ccm.R;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.api.GoogleApiClient;
import com.specializedti.ccm.login.LoginActivity;
import com.specializedti.ccm.preferences.CCMPreferences;

public class CCMActionBarActivity extends ActionBarActivity {
	
	
	private static final String URL_STRING = "http://ccm2015.specializedti.co";
	
	private ActionBar actionBar;
	private GoogleApiClient googleApiClient;
	private CCMPreferences preferences;
	private boolean salirEnabled;
	private boolean ayudaTipoEventoEnabled;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled( true );
		actionBar.setDisplayShowHomeEnabled( true );
		actionBar.setHomeButtonEnabled( true );
		actionBar.setBackgroundDrawable(  getResources().getDrawable( R.drawable.actionbar_background )  );
		this.salirEnabled = true;
		this.ayudaTipoEventoEnabled = false;
		this.preferences = new CCMPreferences( getApplicationContext() );
	}
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.ccmaction_bar, menu);
		menu.findItem( R.id.action_salir ).setEnabled( this.salirEnabled );
		menu.findItem( R.id.action_ayuda_tipo_evento ).setEnabled( this.ayudaTipoEventoEnabled );
		return true;
	}
	
	
	public void setSalirEnabled( boolean enabled ){
		this.salirEnabled = enabled;
	}
	
	
	public void setAyudaTipoEnabled( boolean enabled ){
		this.ayudaTipoEventoEnabled = enabled;
	}	
	
	
	public void disableHome(){
		this.actionBar.setDisplayHomeAsUpEnabled( false );
	}
	
	
	private void mostrarAlertDialog( String mensaje, String positiveBtn, String negativeBtn, 
									 DialogInterface.OnClickListener listener ){
		new AlertDialog.Builder( this )
		.setMessage( mensaje )
		.setPositiveButton( positiveBtn , listener)
		.setNegativeButton( negativeBtn , null)
		.setCancelable(false)
		.show();
	}
	
	
	
	private void mostrarAyudaTipoEvento() {
	}
	

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch( item.getItemId() ){		
			case R.id.action_acerca_de:
				Toast.makeText( this, new CCMPreferences( this ).obtenerPreferencias(), Toast.LENGTH_LONG ).show(); 
				return true;
			case R.id.action_salir:
				mostrarAlertDialog( 
						getResources().getString(R.string.alert_cerrar_sesion),
						getResources().getString(R.string.alert_aceptar),
						getResources().getString(R.string.alert_cancelar),
						new DialogInterface.OnClickListener() {
								
							@Override
							public void onClick(DialogInterface dialog, int which) {
								boolean haSalidoExitosamente = salir();
								if ( haSalidoExitosamente ){
									preferences.vaciar();
									Intent i = new Intent( CCMActionBarActivity.this, LoginActivity.class );
									finish();
									startActivity( i );	
								}							
							}
						}
				);				
				return true;
			
			case R.id.action_ayuda_tipo_evento:
				mostrarAyudaTipoEvento();
				return true;
				
			//Caso en el cual se presiona el la flecha del actionBar para regresar a la actividad anterior
			// IMPORTANTE: La definicion del orden de la pila de Actividades va en el manifest
			// agregando el <meta-data> a cada actividad registrada
			case android.R.id.home:
				NavUtils.navigateUpFromSameTask(this);
				return true;
				
			default:				
				return super.onOptionsItemSelected(item);
		}		
	}
	
	
	
	//Método para capturar la variable googleApiClient, enviada por la actividad hijo LoginActivity.java
	public void setGoogleApiClient( GoogleApiClient googleApiClient ){
		this.googleApiClient = googleApiClient;
	}
	
	
	
	
	//Métodos del ActionBar
	//Método para cerrar sesion en la cuenta que esté abierta
	public boolean salir(){		
		//Para cerrar sesion en Facebook		
		if ( AccessToken.getCurrentAccessToken() != null ){
			LoginManager.getInstance().logOut();
			return true;
		}
		//Para cerra sesion en Google+
		//IMPORTANTE: la variable googleApiClient está en la Actividad hijo LoginActivity.java
		else if ( googleApiClient != null ){			
			if ( googleApiClient.isConnected() ){
				googleApiClient.disconnect();
				return true;
			}
			else{
				return false;
			}
		}		
		return true;
	}
	
	
	
    //Métdodo para verificar el estado de Internet
    //IMPORTANTE: Para verificar la conexion a Internet es necerario agregar el siguiente permiso en el Manifest:
    // <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    public boolean hayInternet(){
    	boolean respuesta = true;
    	ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService( Context.CONNECTIVITY_SERVICE );
 	    NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
 	    if ( netInfo == null ){
 	    	respuesta = false;
 	    }
 	    else if ( !netInfo.isConnected() ){
 	    	respuesta = false;
 	    }
 	    else if ( !netInfo.isAvailable() ){
 	    	respuesta = false;
 	    }
 	    else{
 	    	respuesta = true;
 	    }
 	    
 	    if ( !respuesta ){
 	    	new AlertDialog.Builder( this )
			.setMessage( getResources().getString(R.string.alert_no_internet) )
			.setPositiveButton( getResources().getString(R.string.alert_ok) , null)
			.show();
 	    }
 	    return respuesta;
    }
	
	
}
