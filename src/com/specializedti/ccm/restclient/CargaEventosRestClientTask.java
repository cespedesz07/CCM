package com.specializedti.ccm.restclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.specializedti.ccm.R;
import com.specializedti.ccm.eventos.ItemEventoAdapter;
import com.specializedti.ccm.eventos.model.Evento;
import com.specializedti.ccm.eventos.model.Ubicacion;

/**
 * Hilo usado para procesar la consulta de aquellos 
 * EVENTOS QUE PERTENECEN A UN ÁREA Y DÍA EN PARTICULAR.
 * 
 * Este hilo es llamado especialmente cuando ocurren cambios de Tabs
 * y cuando se selecciona un área en particular del patron Master/Detail
 * @author Santiago Céspedes Zapata - cespedesz07@gmail.com
 *
 */
public class CargaEventosRestClientTask extends AsyncTask< String, Integer, ArrayList<Object[]> > {
	
	
	//URL que conecta a los datos de eventos y ubicaciones
	private static final String URL_EVENTOS_UBICACION_READ = "http://ccm2015.specializedti.com/index.php/rest/evento/sql";
	//private static final String URL_EVENTOS_UBICACION_READ = "http://192.168.1.56/Yii_CCM_WebService/web/index.php/rest/evento/sql";
	
	
	//Llaves o campos asociados a los parametros POST enviados al WebService
	private static final String KEY_IDTIPO_AREA = "idtipo_area";
	private static final String KEY_DIA = "dia";
	
	
	//Nombre de los campos asociados a la consulta al WebService
	private static final String CAMPO_IDEVENTO_EVENTO = "idevento";
	private static final String CAMPO_NOMBRE_EVENTO = "nombre";
	private static final String CAMPO_DESCRIPCION_EVENTO = "descripcion";
	private static final String CAMPO_IDUBICACION_UBICACION = "idubicacion";
	private static final String CAMPO_HORA_INICIO_UBICACION = "hora_inicio";
	private static final String CAMPO_HORA_FIN_UBICACION = "hora_fin";
	private static final String CAMPO_LUGAR_UBICACION = "lugar";
	private static final String CAMPO_FECHA_UBICACION = "fecha";
	private static final String CAMPO_CUPOS_DISPONIBLES_UBICACION = "cupos_disponibles";
	private static final String CAMPO_TIPO_EVENTO_TIPOEVENTO = "tipo_evento";
	
	
	//Nombre de los Tipo de Eventos necesarios para colorear cada uno de los Items de Eventos y Ubicaciones 
	//desplegados en ItemEventoAdapter.java
	//IMPORTANTE: EL valor de estas constantes debe ser el mismo que los datos de Tipo de Eventos del WebService
	public static final String TIPO_EVENTO_PLENARIA = "Plenaria";
	public static final String TIPO_EVENTO_SEMIPLENARIA = "Semiplenaria";
	public static final String TIPO_EVENTO_CURSILLO = "Cursillo";
	public static final String TIPO_EVENTO_PRES_POSTERS = "Presentación de Posters";
	public static final String TIPO_EVENTO_CONFERENCIA = "Conferencia";
	public static final String TIPO_EVENTO_PONENCIA = "Ponencia";

	
	private Context context;
	private ListView listaEventos;
	private ProgressDialog progressDialog;
	private AlertDialog.Builder alertDialog;
	private String mensajeError;
	
	
	
	public CargaEventosRestClientTask( Context context, ListView listaEventos ) {
		this.context = context;
		this.listaEventos = listaEventos;
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
	@Override
	protected void onPostExecute( ArrayList<Object[]> result ){
		if ( progressDialog != null ){
			if ( progressDialog.isShowing() ){
				progressDialog.dismiss();
			}
			if ( !result.isEmpty() ){
				ItemEventoAdapter itemEventoAdapter = new ItemEventoAdapter( context, result );
				this.listaEventos.setAdapter( itemEventoAdapter );
			}
			else{
				if ( mensajeError.length() == 0 ){
					mensajeError = "Resultado Vacio";
				}
				alertDialog.setMessage( mensajeError );
				alertDialog.show();
			}
		}
	}	
	
	

	@Override
	protected ArrayList<Object[]> doInBackground(String... params) {
		String idTipoArea = String.valueOf( params[0] );
		String dia = String.valueOf( params[1] );
		return consultarEventosUbicaciones( idTipoArea, dia );
	}
	
	
	
	
	
	//-------------------------------------------------------------------------------------------------
	public ArrayList<Object[]> consultarEventosUbicaciones( String idTipoArea, String dia ){
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost( URL_EVENTOS_UBICACION_READ );
		//httpPost.setHeader( "Content-type", "application/json" );      //AL ENVIAR Content-type AL WEB SERVICE, ESTE DEVUELVE UN ERROR DICIENDO QUE SE DESCONOCEN LOS PARAMETROS
		                                                                 //POST: idtipo_area y dia, UNA ALTERNATIVA SERIA COLOCAR Accept: application/json
		
		String textoResultado = "";
		JSONArray jsonArray = null;		
		InputStream inputStream = null;
		
		//Log.v( "parametros: ", idTipoArea + ", " + dia );
		List<NameValuePair> parametros = new ArrayList<NameValuePair>();
		parametros.add( new BasicNameValuePair(KEY_IDTIPO_AREA, idTipoArea) );
		parametros.add( new BasicNameValuePair(KEY_DIA, dia) );
		
		try{
			httpPost.setEntity( new UrlEncodedFormEntity(parametros) );
			HttpResponse response = httpClient.execute( httpPost );
			HttpEntity entity = response.getEntity();
			inputStream = entity.getContent();
			BufferedReader reader = new BufferedReader( new InputStreamReader(inputStream) );
			String linea = reader.readLine();
			while ( linea != null ){
				textoResultado += String.format( "%s \n", linea );
				linea = reader.readLine();
			}
			//Log.i( "EventosUbicacionJSON", textoResultado );
			jsonArray = new JSONArray( textoResultado );
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
		ArrayList<Object[]> resultado = procesarJSONArray( jsonArray );
		return resultado;
	}
	
	
	
	public ArrayList<Object[]> procesarJSONArray( JSONArray jsonArray ){
		ArrayList<Object[]> eventosUbicaciones = new ArrayList<Object[]>();
		int numJSONObjects = jsonArray.length();
		for ( int i=0; i<numJSONObjects; i++ ){
			try{
				JSONObject jsonObjectElement = jsonArray.getJSONObject(i);
				String idEvento = jsonObjectElement.getString( CAMPO_IDEVENTO_EVENTO );
				String nombreEvento = jsonObjectElement.getString( CAMPO_NOMBRE_EVENTO );
				String descripcionEvento = jsonObjectElement.getString( CAMPO_DESCRIPCION_EVENTO );
				String idUbicacion = jsonObjectElement.getString( CAMPO_IDUBICACION_UBICACION );
				String horaInicio = jsonObjectElement.getString( CAMPO_HORA_INICIO_UBICACION );
				String lugar = jsonObjectElement.getString( CAMPO_LUGAR_UBICACION );
				String fecha = jsonObjectElement.getString( CAMPO_FECHA_UBICACION );
				String cuposDisponibles = jsonObjectElement.getString( CAMPO_CUPOS_DISPONIBLES_UBICACION );
				String tipoEvento = jsonObjectElement.getString( CAMPO_TIPO_EVENTO_TIPOEVENTO );
				
				Evento evento = new Evento(idEvento, nombreEvento, descripcionEvento, tipoEvento);
				Ubicacion ubicacion = new Ubicacion(idUbicacion, horaInicio, lugar, fecha, cuposDisponibles);
				
				if ( !eventosUbicaciones.isEmpty() ){
					Object[] eventoUbicacionRef = eventosUbicaciones.get( eventosUbicaciones.size() - 1 );
					Evento eventoRef = (Evento) eventoUbicacionRef[0];
					if ( evento.idEvento.equals( eventoRef.idEvento ) ){
						ArrayList<Ubicacion> ubicacionesRef = (ArrayList<Ubicacion>) eventoUbicacionRef[1];
						ubicacionesRef.add( ubicacion );
					}
					else{
						ArrayList<Ubicacion> ubicaciones = new ArrayList<Ubicacion>();
						ubicaciones.add( ubicacion );
						Object[] eventoUbicacion = {evento, ubicaciones};
						eventosUbicaciones.add( eventoUbicacion );
					}
				}
				else{				
					ArrayList<Ubicacion> ubicaciones = new ArrayList<Ubicacion>();
					ubicaciones.add( ubicacion );
					Object[] eventoUbicacion = {evento, ubicaciones};
					eventosUbicaciones.add( eventoUbicacion );				
				}
				
			}
			catch ( JSONException error ){
				mensajeError = error.getMessage();
				error.printStackTrace();
			}
		}
		imprimirEstructura( eventosUbicaciones );
		return eventosUbicaciones;
	}
	
	
	private void imprimirEstructura( ArrayList<Object[]> eventosUbicaciones ){
		String resultado = "";
		for ( Object[] eventoUbicacion : eventosUbicaciones ){
			resultado += ((Evento) eventoUbicacion[0]).toString() + "\n";
			ArrayList<Ubicacion> ubicaciones = (ArrayList<Ubicacion>) eventoUbicacion[1];
			for ( Ubicacion ubicacion : ubicaciones ){
				resultado += "    - " + ubicacion.toString() + "\n"; 
			}
		}
		Log.i( "estructura: " , resultado);
	}

}
