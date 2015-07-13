package com.specializedti.ccm.preferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.specializedti.ccm.restclient.RegistroRestClientTask;

@SuppressLint("NewApi")
public class CCMPreferences {
	
	
	private static final String NOMBRE_PREFERENCES = "com.example.ccm_preferences";
	public static final String CREAR_PERSONA_UBICACION = "crearPersonaEvento";
	public static final String BORRAR_PERSONA_UBICACION = "borrarPersonaEvento";
	
	private static final String CAMPO_DOC_PERSONA = RegistroRestClientTask.CAMPO_DOC_PERSONA;
	private static final String CAMPO_NOMBRE_PERSONA = RegistroRestClientTask.CAMPO_NOMBRE_PERSONA;
	private static final String CAMPO_APELLIDOS_PERSONA = RegistroRestClientTask.CAMPO_APELLIDOS_PERSONA;
	private static final String CAMPO_CORREO_ELECTRONICO_PERSONA = RegistroRestClientTask.CAMPO_CORREO_ELECTRONICO_PERSONA;
	public static final String CAMPO_UBICACION = "registros_ubicacion";
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
	
	public List<String> obtenerIdRegistrosUbicacion(){
		Set<String> idRegistrosUbicacion = pref.getStringSet( CAMPO_UBICACION, new HashSet<String>() );
		List<String> registrosList = new ArrayList<String>( idRegistrosUbicacion.size() );
		Iterator<String> i = idRegistrosUbicacion.iterator();
		while ( i.hasNext() ){
			registrosList.add( i.next() );
		}
		Log.v( "idRegistrosUbicacionesGuardados" , Arrays.toString( registrosList.toArray() ));
		return registrosList;
	}
	
	
	public void guardarIdRegistrosUbicacion( HashSet<String> registrosPersonaUbicacion ){
		Editor editor = pref.edit();
		editor.remove( CAMPO_UBICACION );
		editor.apply();
		editor.putStringSet( CAMPO_UBICACION, registrosPersonaUbicacion);
		editor.commit();
	}
	
	
	public void sincronizarRegistroUbicacion( String idUbicacion, String metodo ){
		Set<String> registrosPersonaUbicacion = pref.getStringSet( CAMPO_UBICACION, new HashSet<String>() );
		if ( metodo.equals( CREAR_PERSONA_UBICACION ) ){
			registrosPersonaUbicacion.add( idUbicacion );
		}
		if ( metodo.equals( BORRAR_PERSONA_UBICACION ) ){
			registrosPersonaUbicacion.remove( idUbicacion );
		}
		Editor editor = pref.edit();
		editor.remove( CAMPO_UBICACION );
		editor.apply();
		editor.putStringSet( CAMPO_UBICACION, registrosPersonaUbicacion);
		editor.commit();
	} 
	
	
	
	public String obtenerDocPersona(){
		return pref.getString( CAMPO_DOC_PERSONA, "" );		
	}
	
	
	public String obtenerNombrePersona(){
		return pref.getString( CAMPO_NOMBRE_PERSONA, "" );
	}
	
	
	public String obtenerApellidosPersona(){
		return pref.getString( CAMPO_APELLIDOS_PERSONA, "" );
	}
	
	
	
	public void guardarTipoLogin( String responseType ){
		Editor editor = pref.edit();
		editor.putString( CAMPO_LOGIN_TYPE, responseType );
		editor.commit();
	}
	
	
	
	public String obtenerTipoLogin(){
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
		editor.commit();
	}


}
