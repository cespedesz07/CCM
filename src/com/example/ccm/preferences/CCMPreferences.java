package com.example.ccm.preferences;

import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.example.ccm.restclient.RegistroRestClientTask;

public class CCMPreferences {
	
	
	private static final String NOMBRE_PREFERENCES = "com.example.ccm_preferences";
	private static final String CAMPO_DOC_PERSONA = RegistroRestClientTask.CAMPO_DOC_PERSONA;
	private static final String CAMPO_NOMBRE_PERSONA = RegistroRestClientTask.CAMPO_NOMBRE_PERSONA;
	private static final String CAMPO_APELLIDOS_PERSONA = RegistroRestClientTask.CAMPO_APELLIDOS_PERSONA;
	private static final String CAMPO_CORREO_ELECTRONICO_PERSONA = RegistroRestClientTask.CAMPO_CORREO_ELECTRONICO_PERSONA;
	public static final String CAMPO_LOGIN_TYPE = "login_type";
	
	
	private Context context;
	private SharedPreferences pref;
	
	public CCMPreferences( Context context ){
		this.context = context;
		this.pref = this.context.getSharedPreferences( NOMBRE_PREFERENCES, Context.MODE_PRIVATE);
	}
	
	
	public void guardarDocPersona( String docPersona ){		
		Editor editor = pref.edit();
		editor.putString( CAMPO_DOC_PERSONA, docPersona );
		editor.commit();
	}
	
	
	public void guardarNombreApellidosPersona( String nombrePersona, String apellidosPersona ){		
		Editor editor = pref.edit();
		editor.putString( CAMPO_NOMBRE_PERSONA, nombrePersona );
		editor.putString( CAMPO_APELLIDOS_PERSONA, apellidosPersona );
		editor.commit();
	}
	
	
	public void guardarEmailPersona( String emailPersona ){		
		Editor editor = pref.edit();
		editor.putString( CAMPO_CORREO_ELECTRONICO_PERSONA, emailPersona );
		editor.commit();
	}
	
	
	
	public String obtenerDocPersona(){
		return pref.getString( CAMPO_DOC_PERSONA, "" );		
	}
	
	
	
	public void guardarTipoResponse( String responseType ){
		Editor editor = pref.edit();
		editor.putString( CAMPO_LOGIN_TYPE, responseType );
		editor.commit();
	}
	
	
	
	public String obtenerTipoResponse(){
		return pref.getString( CAMPO_LOGIN_TYPE, "" );		
	}
	
	
	public String obtenerPreferencias(){
		String resultado = "";
		Map<String, ?> keys = this.pref.getAll();
		for( Map.Entry<String, ?> entry : keys.entrySet() ){
			resultado += entry.getKey() + ": " + entry.getValue().toString() + "\n";
		}
		return resultado;
	}
	
	public void vaciar(){
		Editor editor = pref.edit();
		editor.clear();
	}


}
