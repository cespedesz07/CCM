package com.example.ccm.actionbar;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.ccm.R;
import com.example.ccm.login.LoginActivity;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.api.GoogleApiClient;

public class CCMActionBarActivity extends ActionBarActivity {
	
	
	private GoogleApiClient googleApiClient;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled( true );
		actionBar.setDisplayShowHomeEnabled( true );
		actionBar.setHomeButtonEnabled( true );
		//actionBar.setBackgroundDrawable(  getResources().getDrawable( R.drawable.actionbar_bg )  );
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.ccmaction_bar, menu);
		return true;
	}

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_acerca_de) {
			return true;
		}
		else if ( id == R.id.action_salir ){
			boolean haSalidoExitosamente = salir();
			if ( haSalidoExitosamente ){
				Intent i = new Intent( getApplicationContext(), LoginActivity.class );
				startActivity( i );	
			}
			else{
				new AlertDialog.Builder( this )
				.setMessage( getResources().getString(R.string.alert_sesion_cerrada) )
				.setPositiveButton( getResources().getString(R.string.alert_ok) , null)
				.show();
			}
		}
		return super.onOptionsItemSelected(item);
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
		else if ( googleApiClient.isConnected() ){
			googleApiClient.disconnect();
			return true;
		}
		return false;
	}
	
	
}
