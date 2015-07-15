package com.specializedti.ccm.restclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.specializedti.ccm.R;
import com.specializedti.ccm.eventos.AreaListActivity;
import com.specializedti.ccm.menuinicio.MenuInicioActivity;
import com.specializedti.ccm.preferences.CCMPreferences;
import com.specializedti.ccm.qrcode.QRCodeActivity;
import com.specializedti.ccm.registro.RegistroActivity;


/**
 * Hilo Secundario que se encarga de procesar la creación de un Usuario al WebService de Yii_CCM_WebService
 * IMPORTANTE: 
 * 
 * AsyncTask <Params, Progress, Result>
 * Params:Tipo de dato de los Parámetros de entrada
 * Progress: Progreso del Hilo
 * Result: Tipo de dato del resultado
 * @author Santiago Céspedes Zapata - cespedesz07@gmail.com
 *
 */

public class LoginRestClientTask extends AsyncTask<String, Integer, Object[]>{
	
	
	
	private static final String URL_UBICACIONES_READ = "http://ccm2015.specializedti.com/index.php/rest/persona-ubicacion/ubicaciones";
	
	
	private static final String URL_PERSONA_EXIST = "http://ccm2015.specializedti.com/index.php/rest/persona/exist";
	//private static final String URL_PERSONA_EXIST = "http://192.168.1.56/Yii_CCM_WebService/web/index.php/rest/persona/exist";
	
	public static final String EXISTE_PERSONA = "existePersona";
	public static final String LOGIN_PERSONA = "loginPersona";
	
	//Nombre de los campos de la tabla Persona para proceder al registro
	//( se usan en RegistroActivity.guardarDatosFormulario() )
	public static final String CAMPO_DOC_PERSONA = "docPersona";
	public static final String CAMPO_NOMBRE_PERSONA = "nombre";
	public static final String CAMPO_APELLIDOS_PERSONA = "apellidos";
	public static final String CAMPO_CORREO_ELECTRONICO_PERSONA = "correo_electronico";
	
	private static final String KEY_PERSONA_DOCPERSONA = "persona_docPersona";
	private static final String CAMPO_UBICACION_IDUBICACION = "ubicacion_idubicacion";
	
	//Valor por defecto de las personas que se se registraron
	public static final String VALOR_ASISTIO_NO = "NO";
	
	private Context context;
	private ProgressDialog progressDialog;
	private AlertDialog.Builder alertDialog;
	private String mensajeError;
	
	private String[] params;
	private String metodo;
	private RegistroActivity registroActivity;
	private String nombrePersona;
	private String apellidosPersona;
	
	
	
	
	public LoginRestClientTask( Context context, RegistroActivity registroActivity ){
		this.context = context;
		this.registroActivity = registroActivity;
		this.mensajeError = "";
		progressDialog = new ProgressDialog( context );
		progressDialog.setCancelable(false);
		progressDialog.setProgressStyle( ProgressDialog.STYLE_SPINNER );
		progressDialog.setMessage( context.getResources().getText( R.string.alert_cargando ) );
		
		alertDialog = new AlertDialog.Builder( context );
		alertDialog.setCancelable( false );
		alertDialog.setPositiveButton( context.getResources().getString(R.string.alert_ok) , null);
	}
	
	
	
	//Antes de ejecutar la consulta, muestra un ProgressDialog con el porcentaje del progreso
	@Override
	protected void onPreExecute(){
		progressDialog.show();		
	}
	
	
	
	//Luego de ejecutar la consulta,
	@Override
	protected void onPostExecute( Object[] result ){
		if ( progressDialog.isShowing() ){
			progressDialog.dismiss();
		}
		boolean existePersona = (Boolean) result[0];
		ArrayList<String> ubicacionesPersona = (ArrayList<String>)result[1];
		if ( existePersona ){
			if ( metodo.equals( EXISTE_PERSONA ) ){
				alertDialog.setMessage( context.getResources().getString(R.string.alert_existe_persona) );
				alertDialog.show();
			}
			else if ( metodo.equals( LOGIN_PERSONA ) ){//
				CCMPreferences preferences = new CCMPreferences( this.context );
				preferences.guardarDocPersona( this.params[0] );
				preferences.guardarNombreApellidosPersona( this.nombrePersona, this.apellidosPersona );
				preferences.guardarEmailPersona( this.params[1] );
				preferences.guardarTipoLogin( this.params[2] );	
				preferences.guardarIdRegistrosUbicacion( new HashSet<String>(ubicacionesPersona) );
				Intent i = new Intent( this.context, MenuInicioActivity.class );
				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				this.context.startActivity( i );
			}
		}
		else{
			if ( mensajeError.length() != 0 ){
				alertDialog.setMessage( mensajeError );
				alertDialog.show();
			}
			else{
				if ( metodo.equals( EXISTE_PERSONA ) ){//
					CCMPreferences preferences = new CCMPreferences( this.context );
					preferences.guardarDocPersona( this.params[0] );
					preferences.guardarNombreApellidosPersona(this.params[1], this.params[2]);
					preferences.guardarEmailPersona( this.params[3] );
					preferences.guardarTipoLogin( this.params[4] );
					this.registroActivity.guardarDatosFormulario();
				}
				if ( metodo.equals( LOGIN_PERSONA ) ){//
					alertDialog.setMessage( context.getResources().getString(R.string.alert_no_existe_persona) );
					alertDialog.show();
				}
			}
		}
	}
	
	
	
	@Override
	protected Object[] doInBackground(String... params){
		Object[] result = new Object[2];
		if ( !hayConexionWebService( URL_PERSONA_EXIST ) ){
			result[0] = false;
			result[1] = new ArrayList<String>();
		}
		else{
			if ( metodo.equals( EXISTE_PERSONA ) ){
				String docPersona = this.params[0];
				result[0] = existePersona( docPersona, null );;
				result[1] = new ArrayList<String>();
			}
			else if ( metodo.equals( LOGIN_PERSONA ) ){
				String docPersona = this.params[0];
				String email = this.params[1];
				result[0] = existePersona( docPersona, email );
				if ( (Boolean)result[0] == true ){
					result[1] = consultarUbicacionesEnBD( docPersona );
				}
				else{
					result[1] = new ArrayList<String>();
				}
			}
		}
		return result;
	}
	
	
	
	//método que actualiza el progreso de la consulta
	@Override
	protected void onProgressUpdate( Integer... progress ){
		super.onProgressUpdate( progress[0] );
		progressDialog.setProgress( progress[0] );
	}
	
	
	public void setParams( String[] params ){
		this.params = params;
	}
	
	public void setMetodo( String metodo ){
		this.metodo = metodo;
	}
	
	
	
	
	
	
	
	
	//---------------------------Métodos para hacer consultas GET Y POST al WebService por medio del protocolo REST------------------------
	
	public boolean existePersona( String numDocumento, String email ){
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost( URL_PERSONA_EXIST );
		
		String textoResultado = "";
		JSONArray jsonArray = null;		
		InputStream inputStream = null;
		
		List<NameValuePair> parametros = new ArrayList<NameValuePair>();
		parametros.add( new BasicNameValuePair(CAMPO_DOC_PERSONA, numDocumento) );
		
		try{
			httpPost.setEntity( new UrlEncodedFormEntity(parametros, "utf-8") );
			HttpResponse response = httpClient.execute( httpPost );
			HttpEntity entity = response.getEntity();
			inputStream = entity.getContent();
			BufferedReader reader = new BufferedReader( new InputStreamReader(inputStream, "UTF-8") );
			String linea = reader.readLine();
			while ( linea != null ){
				textoResultado += String.format( "%s \n", linea );
				linea = reader.readLine();
			}
			jsonArray = new JSONArray( textoResultado );
			if ( jsonArray.length() != 0 ){
				JSONObject jsonObject = jsonArray.getJSONObject(0);
				if ( email != null ){
					if ( jsonObject.getString( CAMPO_DOC_PERSONA ).equals( numDocumento )  &&  
					     jsonObject.getString( CAMPO_CORREO_ELECTRONICO_PERSONA ).equals( email ) ){
						this.nombrePersona = jsonObject.getString( CAMPO_NOMBRE_PERSONA );
						this.apellidosPersona = jsonObject.getString( CAMPO_APELLIDOS_PERSONA );						
						return true;
					}
					else{
						return false;
					}						
				}
				else{
					if ( jsonObject.getString( CAMPO_DOC_PERSONA ).equals( numDocumento ) ){
						return true;
					}
					else{
						return false;
					}
				}				
			}
			else{
				return false;
			}
			
		}
		catch (UnsupportedEncodingException error){
			error.printStackTrace();
			mensajeError = "UnsupportedEncodingException: " + error.getMessage(); 
		}
		catch (ClientProtocolException error){
			error.printStackTrace();
			mensajeError = "ClientProtocolException: " + error.getMessage();
		}
 		catch (IOException error){
 			error.printStackTrace();
 			mensajeError = "IOException: " + error.getMessage();
 		}
		catch (JSONException error){
			error.printStackTrace();
			mensajeError = "JSONException: " + error.getMessage();
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
		return false;
	}
	
	
	
	
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
	
	
	
	
	
	
	public boolean hayConexionWebService( String urlString ){
		try{
    		URL url = new URL( urlString );
    		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    		connection.connect();
    		if ( connection.getResponseCode() != HttpURLConnection.HTTP_OK ){
    			mensajeError = context.getResources().getString(R.string.alert_no_server);
    			return false;
    		} 	  
    		else{
    			return true;
    		}
    	}
    	catch ( IOException error ){
    		error.printStackTrace();
    		return false;
    	}
	}
	
	
	
	
	
	
	
	
	
	

}

