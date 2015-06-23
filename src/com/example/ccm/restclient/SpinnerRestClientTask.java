package com.example.ccm.restclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Spinner;

import com.example.ccm.R;
import com.example.ccm.registro.SpinnerArrayAdapter;
import com.google.android.gms.appdatasearch.GetRecentContextCall;

/**
 * Hilo Secundario que se encarga de consultar los datos de completitud y cargarlos
 * a los spinner de RegistroActivity.java
 * @author Santiago C�spedes Zapata - cespedesz07@gmail.com
 *
 */
public class SpinnerRestClientTask extends AsyncTask<String, Integer, ArrayList<String>> {
	
	
	//URLs correspondientes a las opciones que publica el WebService para su consumo
	private static final String URL_TIPO_DOC_READ = "http://192.168.173.1/Yii_CCM_WebService/web/index.php/tipo-doc";
	private static final String URL_PAIS_PROCEDENCIA_READ = "http://192.168.173.1/Yii_CCM_WebService/web/index.php/pais-procedencia";
	private static final String URL_INSTITUCION_READ = "http://192.168.173.1/Yii_CCM_WebService/web/index.php/institucion";
	
	
	public static final String TABLA_TIPO_DOC = "tipo_doc";
	public static final String TABLA_PAIS_PROCEDENCIA = "pais_procedencia";
	public static final String TABLA_INSTITUCION = "institucion";
	

	
	
	
	private Context context;
	private SpinnerArrayAdapter spinnerArrayAdapter;
	private String nombreTablaCompletitud;				//Nombre de la tabla que se va a consultar
	private ProgressDialog progressDialog;
	private AlertDialog.Builder alertDialog;
	private String mensajeError;
	
	
	
	
	public SpinnerRestClientTask( Context context, SpinnerArrayAdapter spinnerArrayAdapter, String nombreTablaCompletitud ) {
		this.context = context;
		this.spinnerArrayAdapter = spinnerArrayAdapter;
		this.nombreTablaCompletitud = nombreTablaCompletitud;
		
		progressDialog = new ProgressDialog( context );
		progressDialog.setCancelable(false);
		progressDialog.setProgressStyle( ProgressDialog.STYLE_SPINNER );
		progressDialog.setMessage( context.getResources().getText( R.string.alert_cargando ) );
		
		
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
	//1) Se cierra la ventana de progreso actual
	//2) Se verifica si el resultado no es nulo. Si no es nulo se agrega el arreglo 
	//   al SpinnerArrayAdapter respectivo (enviado en el contructor)
	//3) Si el resultado es nulo, es porque hubo un error en la consulta, entonces se muestra
	//   un mensaje de error.
	@Override
	protected void onPostExecute( ArrayList<String> result ){
		if ( progressDialog.isShowing() ){
			progressDialog.dismiss();
		}
		if ( result != null ){
			this.spinnerArrayAdapter.addAll( result );
			this.spinnerArrayAdapter.notifyDataSetInvalidated();
		}
		else{
			alertDialog.setMessage( mensajeError );
			alertDialog.show();
		}
	}	
	
	
	//Durante la ejecucion del hilo, ejecuta la consulta de datos de completitud
	//enviando como par�metro la tabla de consulta del WebService
	@Override
	protected ArrayList<String> doInBackground(String... params) {
		return consultarDatosCompletitud( this.nombreTablaCompletitud );
	}
	
	
	
	
	
	
	
	//----------------------------------------------------------------------------------------------------
	//Metodo usado para consultar datos asociados a las tablas de completitud
	//Retorna el objeto JSON que ser� usado para configurar el Spinner en RegistroActivity.java
	public ArrayList<String> consultarDatosCompletitud( String nombreTablaCompletitud ){
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = null;
		String textoResultado = "";
		JSONArray jsonArray = null;
		
		//Se selecciona la url asociada con la tabla de completitud a consultar 
		if ( nombreTablaCompletitud.equals( TABLA_TIPO_DOC ) ){
			httpGet = new HttpGet( URL_TIPO_DOC_READ );
		}
		else if ( nombreTablaCompletitud.equals( TABLA_PAIS_PROCEDENCIA ) ){
			httpGet = new HttpGet( URL_PAIS_PROCEDENCIA_READ );
		}
		else if ( nombreTablaCompletitud.equals( TABLA_INSTITUCION ) ){
			httpGet = new HttpGet( URL_INSTITUCION_READ );
		}
		
		httpGet.setHeader( "Content-type", "application/json" );
		InputStream inputStream = null;
		try{
			HttpResponse response = httpClient.execute( httpGet );
			HttpEntity entity = response.getEntity();
			inputStream = entity.getContent();
			BufferedReader reader = new BufferedReader( new InputStreamReader(inputStream, "UTF-8") );
			String linea = reader.readLine();
			while ( linea != null ){
				textoResultado += String.format( "%s \n", linea );
				linea = reader.readLine();
			}	
			//Log.v( "textoResultado", textoResultado );
			jsonArray = new JSONArray( textoResultado );
		}
		catch (ClientProtocolException error){
			mensajeError = "ClientProtocolException: " + error.getMessage();
		}
 		catch (IOException error){
 			mensajeError = "IOException: " + error.getMessage();
 		}
		catch (JSONException error){
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
		ArrayList<String> listaUnida = procesarJSONArray( jsonArray );
		return listaUnida;
	}
	
	
	
	
	
	//M�todo que procesa un JSONArray que contiene JSONObjects
	//e inserta los pares { key: value } del JSONObject en un ArrayList
	//IMPORTANTE: El JSONArray contiene JSONObjects, y cada JSONOBject tiene la estructura {key: value}
	//Ejemplo:
	/*
	[
		{
			"idtipo_doc":1,
			"tipo_documento":"C�dula de Ciudadan�a"
		},
		{
			"idtipo_doc":2,
			"tipo_documento":"C�dula de Extranjer�a"
		},
		{
			"idtipo_doc":3,
			"tipo_documento":"Tarjeta de Identidad"
		},
		{
			"idtipo_doc":4,
			"tipo_documento":"Pasaporte"
		}
	]
	 * EN este caso: 
	 * keys: ["idtipo_doc", "tipo_documento"]
	 * values: [1, 'Cedula de Ciudadania', 2, 'Tarjeta de Identidad']                
	 */
	private ArrayList<String> procesarJSONArray( JSONArray jsonArray ){
		ArrayList<String> lista = new ArrayList<String>();
		String itemArrayList = "";
		int numJSONObjects = jsonArray.length();								//Se obtiene el numero de JSONObjects en el JSONArray
		for ( int i=0; i<numJSONObjects; i++ ){
			try{
				JSONObject jsonObjectElement = jsonArray.getJSONObject( i ); 		//Se captura el elemento JSONObject: 
																					//{ "idtipo_doc":1, 
																					//  "tipo_documento":"C�dula de Ciudadan�a" }
				JSONArray keys = jsonObjectElement.names();							//Se obtienen las llaves: ["idtipo_doc", "tipo_documento"]
				String idValue = jsonObjectElement.getString( keys.getString(0) );	//Se obtiene el valor de la primer llave
				String nameValue = jsonObjectElement.getString( keys.getString(1) );//Se obtiene el valor de la segunda llave
				itemArrayList = String.format( "%s. %s", idValue, nameValue );
				lista.add( itemArrayList );
			}
			catch ( JSONException error ){
				error.printStackTrace();
			}
		}
		return lista;
		
	}
}
