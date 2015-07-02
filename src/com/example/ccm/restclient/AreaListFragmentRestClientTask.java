package com.example.ccm.restclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.ccm.R;
import com.example.ccm.registro.SpinnerArrayAdapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;




/**
 * Hilo utilizado para extraer los datos de completitud de tipo_area, 
 * cargarlos en AreaListFragment.java y mostrarlos en el patron Master/Detail
 * @author Santiago Céspedes Zapata - cespedesz07@gmail.com
 *
 */
public class AreaListFragmentRestClientTask extends AsyncTask<String, Integer, ArrayList<String>>{
	
	
	//URL que conecta a los datos de completitud de tipo_area
	private static final String URL_TIPO_AREA_READ = "http://192.168.173.1/Yii_CCM_WebService/web/index.php/rest/tipo-area";
	
	
	//Nombre del campo asociado al nombre del tipo de área
	public static final String CAMPO_TIPO_AREA = "tipo_area";

	
	private Context context;
	private ProgressDialog progressDialog;
	private AlertDialog.Builder alertDialog;
	private String mensajeError;
	
	public AreaListFragmentAsyncResponse delegate = null;
	
	
	public AreaListFragmentRestClientTask( Context context ) {
		this.context = context;
		
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
	@Override
	protected void onPostExecute( ArrayList<String> result ){
		if ( progressDialog.isShowing() ){
			progressDialog.dismiss();
		}
		/*
		if ( result != null ){
			
		}
		else{
			alertDialog.setMessage( mensajeError );
			alertDialog.show();
		}
		*/
		delegate.processFinish(result);
	}	
	
	
	
	
	//Durante la ejecucion del hilo, ejecuta la consulta de datos de completitud
	//enviando como parámetro la tabla de consulta del WebService
	@Override
	protected ArrayList<String> doInBackground(String... params) {
		return consultarAreas();
	}
	
	
	
	
	//----------------------------------------------------------------------------------------------------
	//Metodo usado para consultar ls tipos de áreas necesarios para llenar el AreaListFragment
	//del patrón Master/Detail
	public ArrayList<String> consultarAreas(){
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = null;
		String textoResultado = "";
		JSONArray jsonArray = null;
		
		httpPost.setHeader( "Content-type", "application/json" );
		InputStream inputStream = null;
		try{
			HttpResponse response = httpClient.execute( httpPost );
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
		return procesarJSONArray( jsonArray );
	}
	
	
	
	private ArrayList<String> procesarJSONArray( JSONArray jsonArray ){
		ArrayList<String> lista = new ArrayList<String>();
		int numJSONObjects = jsonArray.length();								//Se obtiene el numero de JSONObjects en el JSONArray
		for ( int i=0; i<numJSONObjects; i++ ){
			try{
				JSONObject jsonObjectElement = jsonArray.getJSONObject( i ); 		//Se captura el elemento JSONObject: 
																					//{ "idtipo_area":1, 
																					//  "tipo_area":"Álgebra, ..." }				
				
				String nombreTipoArea = jsonObjectElement.getString( CAMPO_TIPO_AREA );
				lista.add( nombreTipoArea );
			}
			catch ( JSONException error ){
				error.printStackTrace();
			}
		}
		return lista;		
	}

}
