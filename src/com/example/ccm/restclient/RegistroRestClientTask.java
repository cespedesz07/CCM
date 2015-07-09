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
import com.example.ccm.qrcode.QRCodeActivity;


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

public class RegistroRestClientTask extends AsyncTask<Object, Integer, Boolean>{		
	
	
	//URLs correspondientes a las opciones que publica el WebService para su consumo
	//private static final String URL_PERSONA_CREATE = "http://ccm2015.specializedti.com/index.php/rest/persona/create";
	private static final String URL_PERSONA_CREATE = "http://192.168.1.56/Yii_CCM_WebService/web/index.php/rest/persona/create";
	
	//private static final String URL_PERSONA_EXIST = "http://ccm2015.specializedti.com/index.php/rest/persona/exist";
	private static final String URL_PERSONA_EXIST = "http://192.168.1.56/Yii_CCM_WebService/web/index.php/rest/persona/exist";
	
	//Nombre de los campos de la tabla Persona para proceder al registro
	//( se usan en RegistroActivity.guardarDatosFormulario() )
	public static final String CAMPO_DOC_PERSONA = "docPersona";
	public static final String CAMPO_NOMBRE_PERSONA = "nombre";
	public static final String CAMPO_APELLIDOS_PERSONA = "apellidos";
	public static final String CAMPO_GENERO_PERSONA = "genero";
	public static final String CAMPO_CORREO_ELECTRONICO_PERSONA = "correo_electronico";
	public static final String CAMPO_TELEFONO_PERSONA = "telefono";
	public static final String CAMPO_CODIGO_QR_PERSONA = "codigo_qr";
	public static final String CAMPO_FECHA_NACIMIENTO_PERSONA = "fecha_nacimiento";
	public static final String CAMPO_ASISTIO_PERSONA = "asistio";
	public static final String CAMPO_TIPO_DOC_IDTIPO_DOC_PERSONA = "tipo_doc_idtipo_doc";
	public static final String CAMPO_PAIS_PROCEDENCIA_IDPAIS_PROCEDENCIA_PERSONA = "pais_procedencia_idpais_procedencia";
	public static final String CAMPO_INSTITUCION_IDINSTITUCION_PERSONA = "institucion_idinstitucion";
	public static final String CAMPO_TIPO_PERSONA_IDTIPO_PERSONA_PERSONA = "tipo_persona_idtipo_persona";
	
	//Valor por defecto de las personas que se se registraron
	public static final String VALOR_ASISTIO_NO = "NO";
	
	private Context context;
	private String documentoPersona; 			//Se almacena el documento de la persona
												//para enviar como parametros bundle a QRCodeActivity.java
												//y generar el código QR
	private ProgressDialog progressDialog;
	private AlertDialog.Builder alertDialog;
	private String mensajeError;
	
	
	
	
	public RegistroRestClientTask( Context context ){
		this.context = context;
		this.documentoPersona = "";
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
	protected void onPostExecute( Boolean haCreadoUsuario ){
		if ( progressDialog.isShowing() ){
			progressDialog.dismiss();
		}
		if ( !haCreadoUsuario ){
			alertDialog.setMessage( mensajeError );
			alertDialog.show();
		}
		else{
			Bundle bundleParams = new Bundle();
			bundleParams.putString( RegistroRestClientTask.CAMPO_DOC_PERSONA, this.documentoPersona );
			Intent i = new Intent( context, QRCodeActivity.class );
			i.putExtras( bundleParams );
			context.startActivity( i );			
		}
	}
	
	
	
	//Consulta que se hace de fondo
	//La estructura de llamado de este método es doInBackground( String nombre_metodo, Object params )
	@Override
	protected Boolean doInBackground(Object... params){
		List<NameValuePair> parametros = (List<NameValuePair>)params[0];
		this.documentoPersona = parametros.get(0).getValue();
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
	public Boolean ingresarPersona( List<NameValuePair> parametros ){
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost( URL_PERSONA_CREATE );					
		try {
			httpPost.setEntity( new UrlEncodedFormEntity( parametros ) );
			HttpResponse response = httpClient.execute( httpPost );
			int statusCode = response.getStatusLine().getStatusCode();
			if ( statusCode == 200  ||  statusCode == 201 ){
				return true;
			}
			else{
				mensajeError = String.valueOf( statusCode );
				return false;
			}
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

