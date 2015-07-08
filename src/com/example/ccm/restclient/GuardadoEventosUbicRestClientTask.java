package com.example.ccm.restclient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.ccm.R;

/**
 * Hilo usado para procesar la consulta de aquellos 
 * EVENTOS QUE PERTENECEN A UN �REA Y D�A EN PARTICULAR.
 * 
 * Este hilo es llamado especialmente cuando ocurren cambios de Tabs
 * y cuando se selecciona un �rea en particular del patron Master/Detail
 * @author Santiago C�spedes Zapata - cespedesz07@gmail.com
 *
 */
public class GuardadoEventosUbicRestClientTask extends AsyncTask< String, Integer, Boolean > {
	
	
	//URL que conecta a los datos de eventos y ubicaciones
	//private static final String URL_PERSONA_UBICACION_CREATE = "http://ccm2015.specializedti.com/index.php/rest/persona-ubicacion/create";
	private static final String URL_PERSONA_UBICACION_CREATE = "http://192.168.1.56/Yii_CCM_WebService/web/index.php/rest/persona-ubicacion/create";
	
	//private static final String URL_PERSONA_UBICACION_DELETE = "http://ccm2015.specializedti.com/index.php/rest/persona-ubicacion/create";
	private static final String URL_PERSONA_UBICACION_DELETE = "http://192.168.1.56/Yii_CCM_WebService/web/index.php/rest/persona-ubicacion/delete";
	
	
	//Llaves o campos asociados a los parametros POST enviados al WebService 
	//para crear un registro en la tabla 'per_ubi' 
	private static final String KEY_IDTIPO_AREA = "idtipo_area";
	private static final String KEY_DIA = "dia";
	
	
	//Nombre de los campos asociados a la consulta al WebService
	private static final String CAMPO_PERSONA_DOCPERSONA = "persona_docPersona";
	private static final String CAMPO_UBICACION_IDUBICACION = "ubicacion_idubicacion";
	private static final String CAMPO_TIPO_PERSONA_IDTIPO_PERSONA = "tipo_persona_idtipo_persona";
	
	//Nombre de los m�todos a ejecutar de creacion o eliminaci�n de registros de PersonaEvento;
	public static final String CREAR_PERSONA_UBICACION = "crearPersonaUbicacion";
	public static final String BORRAR_PERSONA_UBICACION = "borrarPersonaUbicacion";

	private Context context;
	private ProgressDialog progressDialog;
	private AlertDialog.Builder alertDialog;
	private String mensajeError;
	private ArrayList<String[]> registrosPersonaUbicacion;
	
	
	
	public GuardadoEventosUbicRestClientTask( Context context ) {
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
	@Override
	protected void onPostExecute( Boolean result ){
		if ( progressDialog.isShowing() ){
			progressDialog.dismiss();
		}
		if ( result ){
			Toast.makeText( this.context, "Ubicaciones Guardadas Exitosamente", Toast.LENGTH_SHORT).show();
		}
		else{
			if ( mensajeError.length() == 0 ){
				mensajeError = "Resultado Vacio";
			}
			alertDialog.setMessage( mensajeError );
			alertDialog.show();
		}
	}	
	
	

	@Override
	protected Boolean doInBackground( String... params ) {
		String metodoEjecutar = params[0];
		if ( metodoEjecutar.equals( CREAR_PERSONA_UBICACION ) ){
			return crearRegistrosPersonaUbicacion(registrosPersonaUbicacion);
		}
		else if ( metodoEjecutar.equals( BORRAR_PERSONA_UBICACION ) ){
			return borrarRegistrosPersonaUbicacion(registrosPersonaUbicacion);
		}		
		return false;
	}
	
	
	
	
	public void agregarRegistrosPersonaUbicacion( ArrayList<String[]> registrosPersonaUbicacion ){
		this.registrosPersonaUbicacion = registrosPersonaUbicacion;
	}
	
	
	
	
	
	
	//-------------------------------------------------------------------------------------------------
	public boolean crearRegistrosPersonaUbicacion( ArrayList<String[]> registrosPersonaUbicacion ){
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost( URL_PERSONA_UBICACION_CREATE );					
		try {
			List<NameValuePair> parametros;
			for ( String[] registro : registrosPersonaUbicacion ){
				 parametros = new ArrayList<NameValuePair>();
				 parametros.add(  new BasicNameValuePair( CAMPO_UBICACION_IDUBICACION, registro[0] )  );
				 parametros.add(  new BasicNameValuePair( CAMPO_PERSONA_DOCPERSONA, registro[1] )  );
				 parametros.add(  new BasicNameValuePair( CAMPO_TIPO_PERSONA_IDTIPO_PERSONA, registro[2] )  );
				 httpPost.setEntity( new UrlEncodedFormEntity( parametros ) );
				 HttpResponse response = httpClient.execute( httpPost );
				 Log.v("Guardado Eventos response", String.valueOf( response.getStatusLine().getStatusCode() ) );
			}			
			return true;
		} 
		catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			mensajeError = e1.getMessage();
		} 
		catch (ClientProtocolException e2) {
			e2.printStackTrace();
			mensajeError = e2.getMessage();
		} 
		catch (IOException e3) {
			e3.printStackTrace();
			mensajeError = e3.getMessage();
		}
		catch (ParseException e) {
			e.printStackTrace();
			mensajeError = e.getMessage();
		}
		return false;	
	}
	
	
	public boolean borrarRegistrosPersonaUbicacion( ArrayList<String[]> registrosPersonaUbicacion ){
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost( URL_PERSONA_UBICACION_CREATE );					
		try {
			List<NameValuePair> parametros;
			for ( String[] registro : registrosPersonaUbicacion ){
				 parametros = new ArrayList<NameValuePair>();
				 parametros.add( new BasicNameValuePair(CAMPO_PERSONA_DOCPERSONA, registro[0] ) );
				 parametros.add( new BasicNameValuePair(CAMPO_PERSONA_DOCPERSONA, registro[1] ) );
				 parametros.add( new BasicNameValuePair(CAMPO_PERSONA_DOCPERSONA, registro[2] ) );
				 httpPost.setEntity( new UrlEncodedFormEntity( parametros ) );
				 HttpResponse response = httpClient.execute( httpPost );
				 Log.v("Guardado Eventos response", String.valueOf( response.getStatusLine().getStatusCode() ) );
			}			
			return true;
		} 
		catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			mensajeError = e1.getMessage();
		} 
		catch (ClientProtocolException e2) {
			e2.printStackTrace();
			mensajeError = e2.getMessage();
		} 
		catch (IOException e3) {
			e3.printStackTrace();
			mensajeError = e3.getMessage();
		}
		catch (ParseException e) {
			e.printStackTrace();
			mensajeError = e.getMessage();
		}
		return false;	
	}

}