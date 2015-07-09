package com.example.ccm.restclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
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

import com.example.ccm.R;
import com.example.ccm.eventos.AreaListActivity;
import com.example.ccm.preferences.CCMPreferences;
import com.example.ccm.qrcode.QRCodeActivity;
import com.example.ccm.registro.RegistroActivity;


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

public class LoginRestClientTask extends AsyncTask<String, Integer, Boolean>{		
	
	
	//private static final String URL_PERSONA_EXIST = "http://ccm2015.specializedti.com/index.php/rest/persona/exist";
	private static final String URL_PERSONA_EXIST = "http://192.168.1.56/Yii_CCM_WebService/web/index.php/rest/persona/exist";
	
	public static final String EXISTE_PERSONA = "existePersona";
	public static final String LOGIN_PERSONA = "loginPersona";
	
	//Nombre de los campos de la tabla Persona para proceder al registro
	//( se usan en RegistroActivity.guardarDatosFormulario() )
	public static final String CAMPO_DOC_PERSONA = "docPersona";
	public static final String CAMPO_CORREO_ELECTRONICO_PERSONA = "correo_electronico";
	
	//Valor por defecto de las personas que se se registraron
	public static final String VALOR_ASISTIO_NO = "NO";
	
	private Context context;
	private ProgressDialog progressDialog;
	private AlertDialog.Builder alertDialog;
	private String mensajeError;
	
	private String[] params;
	private String metodo;
	private RegistroActivity registroActivity;
	
	
	
	
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
	protected void onPostExecute( Boolean existePersona ){
		if ( progressDialog.isShowing() ){
			progressDialog.dismiss();
		}
		
		if ( existePersona ){
			if ( metodo.equals( EXISTE_PERSONA ) ){
				alertDialog.setMessage( context.getResources().getString(R.string.alert_existe_persona) );
				alertDialog.show();
			}
			else if ( metodo.equals( LOGIN_PERSONA ) ){
				CCMPreferences preferences = new CCMPreferences( this.context );
				preferences.guardarDocPersona( this.params[0] );
				preferences.guardarEmailPersona( this.params[1] );
				preferences.guardarTipoResponse( this.params[2] );
				this.context.startActivity( new Intent( this.context, AreaListActivity.class ) );
			}
		}
		else{
			if ( metodo.equals( EXISTE_PERSONA ) ){
				CCMPreferences preferences = new CCMPreferences( this.context );
				preferences.guardarDocPersona( this.params[0] );
				preferences.guardarEmailPersona( this.params[1] );
				preferences.guardarTipoResponse( this.params[2] );
				this.registroActivity.guardarDatosFormulario();
			}
			if ( metodo.equals( LOGIN_PERSONA ) ){
				alertDialog.setMessage( context.getResources().getString(R.string.alert_no_existe_persona) );
				alertDialog.show();
			}
		}
	}
	
	
	
	//Consulta que se hace de fondo
	//La estructura de llamado de este método es doInBackground( String nombre_metodo, Object params )
	@Override
	protected Boolean doInBackground(String... params){
		if ( metodo.equals( EXISTE_PERSONA ) ){
			String docPersona = this.params[0];
			return existePersona( docPersona, null );
		}
		else if ( metodo.equals( LOGIN_PERSONA ) ){
			String docPersona = this.params[0];
			String email = this.params[1];
			return existePersona( docPersona, email );
		}
		return false;
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
			httpPost.setEntity( new UrlEncodedFormEntity(parametros) );
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
	
	

}

