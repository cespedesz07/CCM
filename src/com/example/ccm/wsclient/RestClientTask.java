package com.example.ccm.wsclient;

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
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ccm.R;


/*
 * Hilo Secundario que se encarga de procesar loas request y responses al WebService de Yii_CCM_WebService
 * IMPORTANTE: 
 * AsyncTask<Params, Progress, Result>
 * Params:Tipo de dato de los Parámetros de entrada
 * Progress: Progreso del Hilo
 * Result: Tipo de dato del resultado
 */

public class RestClientTask extends AsyncTask<Object, Integer, Object>{		
	
	
	private static final String URL_WEBSERVICE = "http://192.168.173.1/Yii_CCM_WebService/web/index.php/persona/create";
	
	private Activity activity;
	private ProgressDialog progressDialog;
	private AlertDialog.Builder alertDialog;
	private String msgFinal;
	
	
	public RestClientTask( Activity activity ){
		this.activity = activity;
		msgFinal = "";
		progressDialog = new ProgressDialog( activity );
		progressDialog.setCancelable(false);
		progressDialog.setProgressStyle( ProgressDialog.STYLE_SPINNER );
		
		alertDialog = new AlertDialog.Builder( activity );
		alertDialog.setCancelable( false );
		alertDialog.setPositiveButton("Ok Entendido!", null);
	}
	
	
	//Antes de ejecutar la consulta, muestra un ProgressDialog con el porcentaje del progreso
	@Override
	protected void onPreExecute(){
		progressDialog.setMessage( activity.getResources().getText( R.string.alert_guardando_usuario ) );
		progressDialog.show();		
	}
	
	
	//Luego de ejecutar la consulta, 
	protected void onPostExecute(){		
		if ( progressDialog.isShowing() ){
			progressDialog.dismiss();
		}
		alertDialog.setMessage( msgFinal );
		alertDialog.show();
	}
	
	
	//Consulta que se hace de fondo
	protected Object doInBackground(Object... params){
		//return consultarUsuarios( URL_WEBSERVICE );
		List<NameValuePair> parametros = (List<NameValuePair>)params[0];
		return ingresarPersona( parametros );
	}
	
	
	
	//método que actualiza el progreso de la consulta
	protected void onProgressUpdate( Integer progress ){
		progressDialog.setProgress( progress );
	}
	
	
	
	//Método para ingresar un usuario nuevo usando POST
	public Object ingresarPersona( List<NameValuePair> parametros ){
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost( URL_WEBSERVICE );		
			
		try {
			List<NameValuePair> paresNombreValor = new ArrayList<NameValuePair>();
			httpPost.setEntity( new UrlEncodedFormEntity( parametros ) );
			HttpResponse response = httpClient.execute( httpPost );
			//msgFinal = EntityUtils.toString( response.getEntity() );
			msgFinal = "ok";
			//Log.i("response", EntityUtils.toString( response.getAllHeaders()[0]. ) );
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
		return true;
		
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
				resultado += String.format( "%s \n", linea );
				linea = reader.readLine();
			}			
			
			JSONObject jsonObject = new JSONObject( resultado );
			msgFinal = "Usuario Capturado OK";
			return jsonObject.toString();			
		}
		catch (Exception error){
			error.printStackTrace();
			msgFinal = error.getMessage().toString();
			resultado = "(Error)";
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
