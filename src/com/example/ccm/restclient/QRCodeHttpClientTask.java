package com.example.ccm.restclient;

import java.io.IOException;
import java.io.InputStream;
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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.ccm.R;

public class QRCodeHttpClientTask extends AsyncTask<String, Integer, Bitmap> {
	
	
	
	//Url de la API de generación de codigos QR
	private static final String URL_CREAR_CODIGO_QR = "https://api.qrserver.com/v1/create-qr-code/";
	
	
	//Parámetros para enviar por POST a la Url de creación del codigo QR
	private static final String KEY_DATA = "data";
	
	private static final String KEY_COLOR = "color";
	private static final String VALUE_COLOR = "ff0000";
	
	private static final String KEY_SIZE = "size";
	private static final String VALUE_SIZE = "220x220";
	
	private static final String KEY_FORMAT = "format";
	private static final String VALUE_FORMAT = "png";
	
	
	
	private Context context;
	private ImageView codigoQR_QRCodeActivity;
	private ProgressDialog progressDialog;
	private AlertDialog.Builder alertDialog;
	private String mensajeError;
	
	
	public QRCodeHttpClientTask( Context context, ImageView codigoQR_QRCodeActivity ){
		this.context = context;
		this.codigoQR_QRCodeActivity = codigoQR_QRCodeActivity;
		this.mensajeError = "";
		progressDialog = new ProgressDialog( context );
		progressDialog.setCancelable(false);
		progressDialog.setProgressStyle( ProgressDialog.STYLE_SPINNER );
		progressDialog.setMessage( context.getResources().getText( R.string.alert_cargando ) );
		
		alertDialog = new AlertDialog.Builder( context );
		alertDialog.setCancelable( false );
		alertDialog.setPositiveButton( context.getResources().getString(R.string.alert_ok) , null);
	}
	
	
	@Override
	protected void onPreExecute(){
		super.onPreExecute();
		progressDialog.show();
	}
	
	
	
	@Override
	protected void onPostExecute( Bitmap codigoQR ){
		super.onPostExecute( codigoQR );
		if ( progressDialog.isShowing() ){
			progressDialog.dismiss();
		}
		if ( codigoQR != null ){
			codigoQR_QRCodeActivity.setImageBitmap( codigoQR );
			Animation QRCodeRotation = AnimationUtils.loadAnimation(context, R.animator.qrcode_rotation );
			codigoQR_QRCodeActivity.startAnimation( QRCodeRotation );
		}
		else{
			alertDialog.setMessage( mensajeError );
			alertDialog.show();
		}
	}
	
	
	protected void onProgressUpdate(Integer...params){
		super.onProgressUpdate( params[0] );
		progressDialog.setProgress( params[0] );
	}
	

	@Override
	protected Bitmap doInBackground(String... params) {
		return generarCodigoQR( params[0] );
		
	}
	
	
	//Método que realiza una peticion http 
	private Bitmap generarCodigoQR( String documento ){
		Bitmap codigoQR = null;
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost( URL_CREAR_CODIGO_QR );
		List<NameValuePair> parametros = new ArrayList<NameValuePair>();
		parametros.add( new BasicNameValuePair(KEY_DATA, documento) );
		parametros.add( new BasicNameValuePair(KEY_COLOR, VALUE_COLOR ) );
		parametros.add( new BasicNameValuePair(KEY_SIZE, VALUE_SIZE) );
		parametros.add( new BasicNameValuePair(KEY_FORMAT, VALUE_FORMAT) );
		try {
			httpPost.setEntity( new UrlEncodedFormEntity( parametros ) );
			HttpResponse response = httpClient.execute( httpPost );
			InputStream responseContent = response.getEntity().getContent();
			codigoQR = BitmapFactory.decodeStream( responseContent );
			mensajeError = String.valueOf( response.getStatusLine().getStatusCode() );
			Log.i("response", String.valueOf( response.getStatusLine().getStatusCode() ) );
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
		return codigoQR;
	}

}
