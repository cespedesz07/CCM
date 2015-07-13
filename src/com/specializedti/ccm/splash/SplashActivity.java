package com.specializedti.ccm.splash;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.specializedti.ccm.R;
import com.specializedti.ccm.login.LoginActivity;


/**
 * Splash Screen que muestra el logo del CCM, el logo de la Universidad Nacional
 * y el logo de la sociedad colombiana de matemáticas
 * @author Santiago Céspedes Zapata - cespedesz07@gmail.com
 *
 */
public class SplashActivity extends Activity {
	
	
	//Duracion del Splash Screen
	private static final long DURACION_SPLASH = 2000;
	
	
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
				//Cuando termine el Splash, se redirige a la actividad LoginActivity
				Intent loginIntent = new Intent( SplashActivity.this, LoginActivity.class );				
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
