package com.example.ccm;

import java.util.Arrays;
import java.util.Collections;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.ccm.facebookLogin.FacebookCallbackCCM;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;


/**
 * Actividad que corresponde al inicio de sesion en diferentes redes sociales
 * (Facebook, Twitter, Google+, o Login Navito en la plataforma)
 * @author Santiago Céspedes Zapata
 *
 */
public class LoginActivity extends Activity {
	
	
	//Permisos que se van a solicitar cuando se inicie sesion con Facebook
	private static final String[] FACEBOOK_PERMISSIONS = { "user_friends", "public_profile", "email" };
	
	private CallbackManager callbackManager;
	private LoginButton facebookLoginBtn;
	
	
	

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        
        //Se crea un CallbackManager para gestionar el inicio de sesion de usuarios con Facebook
        //Las respuestas del CallBack las recibe la clase LoginManager de Facebook
        callbackManager = CallbackManager.Factory.create();       
    }
    
    
    
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ){
    	View view = inflater.inflate(R.layout.login, container, false);
    	
    	facebookLoginBtn = (LoginButton) view.findViewById( R.id.facebook_login_button );
    	
    	//Se asignan los permisos de lectura al inicio de sesion en facebook
    	facebookLoginBtn.setReadPermissions( Arrays.asList(FACEBOOK_PERMISSIONS) );
    	
    	//Registro del Callback de facebook que se encarga de procesar el inicio de sesion exitoso, cancelado o con errores
        facebookLoginBtn.registerCallback( callbackManager, new FacebookCallbackCCM() );
        
        
    	return view;
    }
    
    
    
    @Override
    protected void onActivityResult( int requestCode, int resultCode, Intent data ){
    	super.onActivityResult(requestCode, resultCode, data);
    	
    	//El siguiente código permite abrir el dialogo de inicio de sesion en facebook
    	//o capturar las credenciales en caso de que se tenga la App nativa de Facebook
    	callbackManager.onActivityResult(requestCode, resultCode, data);
    }
    
}
