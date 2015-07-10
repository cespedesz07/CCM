package com.example.ccm.restclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.ccm.R;
import com.example.ccm.eventos.AreaListActivity;
import com.example.ccm.preferences.CCMPreferences;

/**
 * Hilo usado para procesar la consulta de aquellos 
 * EVENTOS QUE PERTENECEN A UN ÁREA Y DÍA EN PARTICULAR.
 * 
 * Este hilo es llamado especialmente cuando ocurren cambios de Tabs
 * y cuando se selecciona un área en particular del patron Master/Detail
 * @author Santiago Céspedes Zapata - cespedesz07@gmail.com
 *
 */
public class CargaUbicacionesPersonaRestClientTask extends AsyncTask< String, Integer, ArrayList<String> > {
	
	
	//URL que conecta a los datos de eventos y ubicaciones
	private static final String URL_UBICACIONES_READ = "http://ccm2015.specializedti.com/index.php/rest/evento/sql";
	//private static final String URL_UBICACIONES_READ = "http://192.168.56.1/Yii_CCM_WebService/web/index.php/rest/persona-ubicacion/ubicaciones";
	
	
	//Llaves o campos asociados a los parametros POST enviados al WebService
	private static final String KEY_PERSONA_DOCPERSONA = "persona_docPersona";
	
	//Campos de los valores que devuelve el servidor
	private static final String CAMPO_UBICACION_IDUBICACION = "ubicacion_idubicacion";

	
	private Context context;
	private ProgressDialog progressDialog;
	private AlertDialog.Builder alertDialog;
	private String mensajeError;
	
	
	
	public CargaUbicacionesPersonaRestClientTask( Context context ) {
		this.context = context;
		progressDialog = new ProgressDialog( context );
		progressDialog.setCancelable(false);
		progressDialog.setProgressStyle( ProgressDialog.STYLE_SPINNER );
		progressDialog.setMessage( context.getResources().getText( R.string.alert_cargando ) );
		
		mensajeError = "";
		alertDialog = new AlertDialog.Builder( context );
		alertDialog.setMessage( mensajeError );
		alertDialog.setPositiveButton( context.getResources().getString(R.string.alert_ok) , null);
	}
	
	
	
	//Antes de ejecutar la consulta, muestra un ProgressDialog con el porcentaje del progreso
	@Override
	protected void onPreExecute(){
		progressDialog.show();
	}
	
	
	
	//Luego de ejecutar la consulta en el metodo doInBackground()
	@SuppressLint("NewApi")
	@Override
	protected void onPostExecute( ArrayList<String> result ){
		if ( progressDialog.isShowing() ){
			progressDialog.dismiss();
		}
		if ( !result.isEmpty() ){
			SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this.context);
			Editor editor = pref.edit();
			editor.remove( CCMPreferences.CAMPO_UBICACION );
			editor.apply();
			editor.putStringSet( CCMPreferences.CAMPO_UBICACION, toSet(result) );
			editor.commit();
			this.context.startActivity( new Intent(this.context, AreaListActivity.class) );
		}
		else{
			if ( mensajeError.length() != 0 ){
				alertDialog.setMessage( mensajeError );
				alertDialog.show();
			}
			else{
				new AlertDialog.Builder(this.context)
				.setMessage( this.context.getResources().getString( R.string.ubicaciones_vacio ) )
				.setPositiveButton( this.context.getResources().getString(R.string.alert_ok), null)
				.setCancelable( false )
				.show();
			}			
		}
	}	
	
	private Set<String> toSet( ArrayList<String> arrayList ){
		Set<String> result = new HashSet<String>();
		for ( String string : arrayList ){
			result.add(string);
		}
		return result;
	}
	
	

	@Override
	protected ArrayList<String> doInBackground(String... params) {
		String docPersona = params[0];
		return consultarUbicacionesEnBD( docPersona );
	}
	
	
	
	
	
	//-------------------------------------------------------------------------------------------------
	public ArrayList<String> consultarUbicacionesEnBD( String docPersona ){
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost( URL_UBICACIONES_READ );
		
		String textoResultado = "";
		JSONArray jsonArray = null;		
		InputStream inputStream = null;
		
		List<NameValuePair> parametros = new ArrayList<NameValuePair>();
		parametros.add( new BasicNameValuePair(KEY_PERSONA_DOCPERSONA, docPersona) );
		
		try{
			httpPost.setEntity( new UrlEncodedFormEntity(parametros) );
			HttpResponse response = httpClient.execute( httpPost );
			Log.i("executed", "executed" );
			HttpEntity entity = response.getEntity();
			inputStream = entity.getContent();
			BufferedReader reader = new BufferedReader( new InputStreamReader(inputStream) );
			String linea = reader.readLine();
			while ( linea != null ){
				textoResultado += String.format( "%s \n", linea );
				linea = reader.readLine();
			}
			jsonArray = new JSONArray( textoResultado );
		}
		catch (Exception error){
			error.printStackTrace();
			this.progressDialog.dismiss();
		}
		finally{
			if ( inputStream != null ){
				try {
					inputStream.close();
				} 
				catch (IOException error) {
					Log.i( "IOException finally: ", error.getMessage() );
					mensajeError = error.getMessage();
				}
			}
		}
		ArrayList<String> resultado = procesarJSONArray( jsonArray );
		return resultado;
	}
	
	
	
	public ArrayList<String> procesarJSONArray( JSONArray jsonArray ){
		ArrayList<String> ubicacionesEnBD = new ArrayList<String>(); 
		int numJSONObjects = jsonArray.length();
		for ( int i=0; i<numJSONObjects; i++ ){
			try{
				JSONObject jsonObjectElement = jsonArray.getJSONObject(i);
				ubicacionesEnBD.add(  jsonObjectElement.getString( CAMPO_UBICACION_IDUBICACION )  );				
			}
			catch ( JSONException error ){
				mensajeError = error.getMessage();
				error.printStackTrace();
			}
		}
		return ubicacionesEnBD;
	}

}
