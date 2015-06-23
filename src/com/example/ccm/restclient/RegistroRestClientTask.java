package com.example.ccm.restclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
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
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.ccm.R;


/*
 * Hilo Secundario que se encarga de procesar loas request y responses al WebService de Yii_CCM_WebService
 * IMPORTANTE: 
 * 
 * AsyncTask <Params, Progress, Result>
 * Params:Tipo de dato de los Parámetros de entrada
 * Progress: Progreso del Hilo
 * Result: Tipo de dato del resultado
 */

public class RegistroRestClientTask extends AsyncTask<Object, Integer, Object>{		
	
	
	
	//URLs correspondientes a las opciones que publica el WebService para su consumo
	private static final String URL_PERSONA_CREATE = "http://192.168.173.1/Yii_CCM_WebService/web/index.php/rest/persona/create";
	
	
	private Context context;
	private ProgressDialog progressDialog;
	private AlertDialog.Builder alertDialog;
	private String msgFinal;
	
	
	
	
	public RegistroRestClientTask( Context context ){
		this.context = context;
		msgFinal = "";
		progressDialog = new ProgressDialog( context );
		progressDialog.setCancelable(false);
		progressDialog.setProgressStyle( ProgressDialog.STYLE_HORIZONTAL );
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
	protected void onPostExecute( Object result ){
		if ( progressDialog.isShowing() ){
			progressDialog.dismiss();
		}
		alertDialog.setMessage( msgFinal );
		alertDialog.show();
	}
	
	
	
	//Consulta que se hace de fondo
	//La estructura de llamado de este método es doInBackground( String nombre_metodo, Object params )
	@Override
	protected Object doInBackground(Object... params){
		List<NameValuePair> parametros = (List<NameValuePair>)params[0];
		return ingresarPersona( parametros );
	}
	
	
	
	//método que actualiza el progreso de la consulta
	@Override
	protected void onProgressUpdate( Integer... progress ){
		super.onProgressUpdate( progress[0] );
		progressDialog.setProgress( progress[0] );
	}
	
	
	
	
	
	
	
	
	//---------------------------Métodos para hacer consultas GET Y POST al WebService por medio del protocolo REST------------------------
	
	//Método para ingresar un usuario nuevo usando POST
	public Object ingresarPersona( List<NameValuePair> parametros ){
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost( URL_PERSONA_CREATE );					
		try {
			httpPost.setEntity( new UrlEncodedFormEntity( parametros ) );
			HttpResponse response = httpClient.execute( httpPost );
			msgFinal = String.valueOf( response.getStatusLine().getStatusCode() );
			Log.i("response", String.valueOf( response.getStatusLine().getStatusCode() ) );
			return true;
		} 
		catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} 
		catch (ClientProtocolException e2) {
			e2.printStackTrace();
		} 
		catch (IOException e3) {
			e3.printStackTrace();
		}
		catch (ParseException e) {
			e.printStackTrace();
		}
		return false;
		
	}
	
	
	
	
	//Método que hace una peticion GET al servicio web
	public String consultarUsuarios(String urlWebService){
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet( urlWebService );				//EL METODO DEL SERVICIO WEB REQUIERE UNA PETICION GET AL SERVICIO WEB
																	//EN EL SERVICIO WEB, EL MÉTODO WEBTIENE LA SIGUIENTE SIGNATURE
																	/*
																	 * @GET
																	 * @Path("{from}/{to}")
																	 * @Produces({"application/xml", "application/json"})
																	 */
																	
		httpGet.setHeader( "Content-type", "application/json" );	//Se establece la captura del contenido del servicio web en formato JSON
		
		InputStream inputStream = null;
		String resultado = "";
		
		try{
			HttpResponse response = httpClient.execute( httpGet );	//Se ejecuta la peticion GET
			
			HttpEntity entity = response.getEntity();               			
			inputStream = entity.getContent();						//Se obtiene el contenido de la respuesta del servicio web
			
			//Se hace una lectura del contenido de la peticion get al Web Service
			BufferedReader reader = new BufferedReader( new InputStreamReader(inputStream, "UTF-8") );
			String linea = reader.readLine();
			while ( linea != null ){
				resultado += linea;
				linea = reader.readLine();
			}						
			JSONObject jsonObject = new JSONObject( resultado );
			msgFinal = "Datos Capturados OK";
			return jsonObject.toString();			
		}
		catch (Exception error){
			error.printStackTrace();
			msgFinal = error.getMessage().toString();
			return resultado;
		}		
		finally{
			if ( inputStream != null ){
				try {
					inputStream.close();
				} catch (IOException e) {
					msgFinal = e.getMessage().toString();
					Log.i( "IOException: ", e.getMessage().toString() );
					
				}
			}
		}
		
	}
	
	

}

