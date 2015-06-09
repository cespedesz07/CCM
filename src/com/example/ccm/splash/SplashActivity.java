package com.example.ccm.splash;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.ccm.Login;
import com.example.ccm.LoginEmailActivity;
import com.example.ccm.R;
import com.facebook.FacebookSdk;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class SplashActivity extends Activity {
	
	
	//CONSTANTES
	private static final long DURACION_SPLASH = 1000;
	
	
	//ATRIBUTOS
	private ImageView logoSplashScreen; 
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		
		//Se posiciona la pantalla de forma vertical
		//setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT );
		
		TimerTask task = new TimerTask(){
			@Override
			public void run() {
				Intent loginIntent = new Intent( SplashActivity.this, Login.class );
				startActivity( loginIntent );
				finish();
			}			
		};
		
		//Se crea un temporizador para ejecutar el splash screen mientras se carga la actividad de Login
		//en el intent de TimerTask
		Timer timer = new Timer();
		timer.schedule( task, DURACION_SPLASH );
	}
	
	
	

	
}
