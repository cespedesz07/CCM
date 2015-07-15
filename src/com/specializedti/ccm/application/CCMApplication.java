package com.specializedti.ccm.application;

import android.app.Application;

import com.specializedti.ccm.R;
import com.facebook.FacebookSdk;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.plus.Plus;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseUser;


/**
 * Clase que extiende de Application para inicializar algunos componentes como lo es
 * La Libreria de Parse
 * La SDK de Facebook
 * 
 * IMPORTANTE: Esta clase se invoca en la etiqueta 
 * <application
 *     android:name="com.specializedti.ccm.application.CCMApplication" 
 * />
 * Con el fin de que sea invocada en el arranque de la app
 * @author Santiago Céspedes Zapata - cespedesz07@gmail.com
 *
 */
public class CCMApplication extends Application {
	
	
	//Números asociados con la libreria Parse para su inicializacion
	//estos números están en el apartado: https://www.parse.com/apps/ccm--5/edit#keys
	//o en la Pestaña Settings -> Keys en la pagina de Parse
	
	 @Override
	 public void onCreate() {
		 super.onCreate();
		 
	 
		 // Add your initialization code here
		 /*
		 Parse.initialize( this, 
				 getResources().getString( R.string.parse_app_id ), 
				 getResources().getString( R.string.parse_client_key ) );
 
		 ParseUser.enableAutomaticUser();
	     ParseACL defaultACL = new ParseACL();
 
	     // If you would like all objects to be private by default, remove this line.
	     defaultACL.setPublicReadAccess(true); 
	     ParseACL.setDefaultACL(defaultACL, true);
        */
	     
	     
	     //Se inicializa la Facebook SDK version 4 
	     FacebookSdk.sdkInitialize( getApplicationContext() );
	     
	     
	     //Se inicializa el PlusClient de Google+ API
	 }

}
