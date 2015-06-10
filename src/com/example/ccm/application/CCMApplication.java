package com.example.ccm.application;

import com.example.ccm.R;
import com.facebook.FacebookSdk;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseUser;

import android.app.Application;
import android.content.Intent;


/**
 * Clase que extiende de Application para inicializar algunos componentes como lo es
 * La Libreria de Parse
 * La SDK de Facebook
 * 
 * IMPORTANTE: Esta clase se invoca en la etiqueta 
 * <application
 *     android:name="com.example.ccm.application.CCMApplication" 
 * />
 * Con el fin de que sea invocada en el arranque de la app
 * @author Santiago Céspedes Zapata - cespedesz07@gmail.com
 *
 */
public class CCMApplication extends Application {
	
	
	//Números asociados con la libreria Parse para su inicializacion
	//estos números están en el apartado: https://www.parse.com/apps/ccm--5/edit#keys
	//o en la Pestaña Settings -> Keys en la pagina de Parse
	private String PARSE_APPLICATION_ID = getResources().getString( R.string.parse_app_id );
	private String PARSE_CLIENT_KEY = getResources().getString( R.string.parse_client_key );
	
	 @Override
	 public void onCreate() {
		 super.onCreate();
		 
	 
		 // Add your initialization code here
		 Parse.initialize( this, PARSE_APPLICATION_ID, PARSE_CLIENT_KEY );
 
		 ParseUser.enableAutomaticUser();
	     ParseACL defaultACL = new ParseACL();
 
	     // If you would like all objects to be private by default, remove this
	     // line.
	     defaultACL.setPublicReadAccess(true);
 
	     ParseACL.setDefaultACL(defaultACL, true);
        
	     
	     //Se inicializa la Facebook SDK version 4 
	     FacebookSdk.sdkInitialize( getApplicationContext() );
	 }

}
