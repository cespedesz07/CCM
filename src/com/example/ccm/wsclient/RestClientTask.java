package com.example.ccm.wsclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.example.ccm.R;


/*
 * AsyncTask<Params, Progress, Result>
 * Params:Tipo de dato de los Parámetros de entrada
 * Progress: Progreso del Hilo
 * Result: Tipo de dato del resultado
 */

public class RestClientTask extends AsyncTask<Object, Integer, String>{		
	
	
	private Activity activity;
	private ProgressDialog progressDialog;
	private AlertDialog.Builder alertDialog;
	private String msgFinal;
	
	
	public RestClientTask( Activity activity ){
		this.activity = activity;
		msgFinal = "";
		progressDialog = new ProgressDialog( activity );
		progressDialog.setCancelable(false);
		progressDialog.setProgressStyle( ProgressDialog.STYLE_HORIZONTAL );
		
		alertDialog = new AlertDialog.Builder( activity );
		alertDialog.setCancelable( false );
		alertDialog.setPositiveButton("Ok Entendido!", null);
	}
	
	
	@Override
	protected void onPreExecute(){
		progressDialog.setMessage( "Esperar un poco" );
		progressDialog.show();		
	}
	
	
	@Override
	protected void onPostExecute( String resultado ){		
		if ( progressDialog.isShowing() ){
			progressDialog.dismiss();
		}
		alertDialog.setMessage( msgFinal );
		alertDialog.show();
		
		TextView textViewResultado = (TextView) activity.findViewById( R.id.textView1 );
		textViewResultado.setText( resultado );
	}
	
	
	protected String doInBackground(Object... params){
		String urlWebService = String.valueOf(params[0]);
		return consultarUsuarios( urlWebService );
	}
	
	
	protected void onProgressUpdate( Integer progress ){
		progressDialog.setProgress( progress );
	}
	
	
		
	
	
	
	
	
	
	public String consultarUsuarios(String urlWebService){
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet( urlWebService );				//EL METODO DEL SERVICIO WEB REQUIERE UNA PETICION GET AL SERVICIO WEB
																	//@GET
		httpGet.setHeader( "Content-type", "application/json" );
		
		InputStream inputStream = null;
		String resultado = "";
		
		try{
			HttpResponse response = httpClient.execute( httpGet );	//Se ejecuta la peticion GET
			
			HttpEntity entity = response.getEntity();               			
			inputStream = entity.getContent();						//Se obtiene el contenido de la respuesta del servicio web
			
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
