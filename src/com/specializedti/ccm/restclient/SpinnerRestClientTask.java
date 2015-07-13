package com.specializedti.ccm.restclient;

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

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.specializedti.ccm.R;
import com.specializedti.ccm.registro.SpinnerArrayAdapter;

/**
 * Hilo Secundario que se encarga de consultar los datos de completitud y cargarlos
 * a los spinner de RegistroActivity.java, y a la Lista AreaListFragment.java del patrón 
 * Master/Detail de selección de eventos
 * @author Santiago Céspedes Zapata - cespedesz07@gmail.com
 *
 */
public class SpinnerRestClientTask extends AsyncTask<String, Integer, ArrayList<String>> {
	
	
	//URLs correspondientes a las opciones que publica el WebService para su consumo
	private static final String URL_TIPO_DOC_READ = "http://ccm2015.specializedti.com/index.php/rest/tipo-doc";
	private static final String URL_PAIS_PROCEDENCIA_READ = "http://ccm2015.specializedti.com/index.php/rest/pais-procedencia";
	private static final String URL_INSTITUCION_READ = "http://ccm2015.specializedti.com/index.php/rest/institucion";
	
	/*
	private static final String URL_TIPO_DOC_READ = "http://192.168.1.56/Yii_CCM_WebService/web/index.php/rest/tipo-doc";
	private static final String URL_PAIS_PROCEDENCIA_READ = "http://192.168.1.56/Yii_CCM_WebService/web/index.php/rest/pais-procedencia";
	private static final String URL_INSTITUCION_READ = "http://192.168.1.56/Yii_CCM_WebService/web/index.php/rest/institucion";
	*/
	
	
	//Nombres de las Tablas de COmpletitud (USADAS EN RegistroActivity.java y en la presente actividad: SpinnerRestClient.java)
	public static final String TABLA_TIPO_DOC = "tipo_doc";
	public static final String TABLA_PAIS_PROCEDENCIA = "pais_procedencia";
	public static final String TABLA_INSTITUCION = "institucion";
	
	
	//Nombres de los campos de las tablas de completitud para mostrar en los spinners
	private static final String CAMPO_ID_TIPO_DOC = "idtipo_doc";
	private static final String CAMPO_NOMBRE_TIPO_DOC = "tipo_documento";	
	private static final String CAMPO_ID_PAIS_PROCEDENCIA = "idpais_procedencia";
	private static final String CAMPO_NOMBRE_PAIS_PROCEDENCIA = "nombre";	
	private static final String CAMPO_ID_INSTITUCION = "idinstitucion";
	private static final String CAMPO_NOMBRE_INSTITUCION = "nombre";
	
	

	
	
	
	private Context context;
	private SpinnerArrayAdapter spinnerArrayAdapter;
	private String nombreTablaCompletitud;			//Nombre de la tabla que se va a consultar
	private ProgressDialog progressDialog;
	private AlertDialog.Builder alertDialog;
	private String mensajeError;
	
	private String idKey;							//Llaves o keys a utilizar para extraer los values asociados a estas keys del JSONObject (Método consultarDatosCompletitud())
	private String nameKey;
	
	
	
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
		
		idKey = "";
		nameKey = "";
	}
	
	
	
	//Antes de ejecutar la consulta, muestra un ProgressDialog con el porcentaje del progreso
	@Override
	protected void onPreExecute(){
		progressDialog.show();
	}
	
	
	
	//Luego de ejecutar la consulta en el metodo doInBackground()
	//1) Se cierra la ventana de progreso actual
	//
	//2) Se verifica si el resultado no es nulo. Si no es nulo se agrega el arreglo 
	//   al SpinnerArrayAdapter respectivo (enviado en el contructor)
	//a) - IMPORTANTE: Para mostrar el item seleccionado del Spinner y que NO APAREZCA EN BLANCO LA SELECCION
	//     ES NECESARIO NOTIFICAR DEL CAMBIO DEL DATA SET USADO EN EL SPINNER CON spinnerArrayAdapter.notifyDataSetChanged();
	//
	//3) Si el resultado es nulo, es porque hubo un error en la consulta, entonces se muestra
	//   un mensaje de error.
	@Override
	protected void onPostExecute( ArrayList<String> result ){
		if ( progressDialog.isShowing() ){	                	 //1)
			progressDialog.dismiss();
		}
		if ( result != null ){									//2)
			this.spinnerArrayAdapter.addAll( result );
			this.spinnerArrayAdapter.notifyDataSetChanged();	//a)
		}
		else{
			alertDialog.setMessage( mensajeError );				//3)
			alertDialog.show();
		}
	}	
	
	
	//Durante la ejecucion del hilo, ejecuta la consulta de datos de completitud
	//enviando como parámetro la tabla de consulta del WebService
	@Override
	protected ArrayList<String> doInBackground(String... params) {
		return consultarDatosCompletitud( this.nombreTablaCompletitud );
	}
	
	
	
	
	
	
	
	//----------------------------------------------------------------------------------------------------
	//Metodo usado para consultar datos asociados a las tablas de completitud
	//Retorna el objeto JSON que será usado para configurar el Spinner en RegistroActivity.java
	public ArrayList<String> consultarDatosCompletitud( String nombreTablaCompletitud ){
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = null;
		String textoResultado = "";
		JSONArray jsonArray = null;
		
		//Se selecciona la url asociada con la tabla de completitud a consultar 
		if ( nombreTablaCompletitud.equals( TABLA_TIPO_DOC ) ){
			httpGet = new HttpGet( URL_TIPO_DOC_READ );
			idKey = CAMPO_ID_TIPO_DOC;
			nameKey = CAMPO_NOMBRE_TIPO_DOC;
		}
		else if ( nombreTablaCompletitud.equals( TABLA_PAIS_PROCEDENCIA ) ){
			httpGet = new HttpGet( URL_PAIS_PROCEDENCIA_READ );
			idKey = CAMPO_ID_PAIS_PROCEDENCIA;
			nameKey = CAMPO_NOMBRE_PAIS_PROCEDENCIA;
		}
		else if ( nombreTablaCompletitud.equals( TABLA_INSTITUCION ) ){
			httpGet = new HttpGet( URL_INSTITUCION_READ );
			idKey = CAMPO_ID_INSTITUCION;
			nameKey = CAMPO_NOMBRE_INSTITUCION;
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
			//Log.i( "textoResultado", textoResultado );
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
	
	
	
	
	
	//Método que procesa un JSONArray que contiene JSONObjects
	//e inserta los pares { key: value } del JSONObject en un ArrayList
	//IMPORTANTE: El JSONArray contiene JSONObjects, y cada JSONOBject tiene la estructura {key: value}
	//Ejemplo:
	/*
	[
		{
			"idtipo_doc":1,
			"tipo_documento":"Cédula de Ciudadanía"
		},
		{
			"idtipo_doc":2,
			"tipo_documento":"Cédula de Extranjería"
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
																					//  "tipo_documento":"Cédula de Ciudadanía" }
				
				
				String idValue = jsonObjectElement.getString( idKey );	       	//Se obtiene el valor de la primer llave
				String nameValue = jsonObjectElement.getString( nameKey );    	//Se obtiene el valor de la segunda llave
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
