package com.example.ccm.registro;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.json.JSONArray;

import android.app.Activity;
import android.content.Context;
import android.widget.ArrayAdapter;

import com.example.ccm.restclient.SpinnerRestClientTask;


/**
 * Adapter especifico para asignar al Spinner de Seleccion de Tipo de Documento, Pais de Procedencia
 * e Institucion. Este adapter invoca al webservice para extraer los datos de completitud asociados al
 * tipo de documento (C.C o C.Extr), Pais de Procedencia (Colombia, USA, ...), Insitucion (UNAL,Texas U, Caltech) 
 * ES NECESARIO CONEXION A INTERNET PARA EXTRAER LOS DATOS DE COMPLETITUD
 * 
 * IMPORTANTE: NO ENVIAR AL METODO super EL PARAMETRO getApplicationContext() sino el context de RegistroActivity.java
 * YA QUE SI SE ENVIA EL PRIMERO, LA LETRA DE LOS ITEMS DEL SPINNER SE TORNAN BLANCAS Y NO VISIBLES
 * @author Santiago Céspedes Zapata - cespedesz07@gmail.com
 *
 */
public class SpinnerArrayAdapter extends ArrayAdapter<String> {
	
	
	public SpinnerArrayAdapter(Context context, String nombreTablaCompletitud) {
		super( context, android.R.layout.simple_spinner_item );
		setDropDownViewResource( android.R.layout.simple_spinner_item );
		
		//Se consultan los datos de completitud al WebService enviando como parámetros:
		//1) El contexto (traido desde RegistroActivity.java)
		//2) El Spinner actual (HAY 3 INSTANCIAS DE SPINNERS ADAPTERS, uno para tipo_doc, pais_procedencia, institucion),
		//   al enviar SpinnerArrayAdapter.this se esá haciendo referencia a un SpinnerArrayAdapter específico de los 3 instanciados
		//3) El nombre de la tabla de completitud (traido desde RegistroActivity.java)

		SpinnerRestClientTask spinnerRestClientTask = new SpinnerRestClientTask(context, SpinnerArrayAdapter.this, nombreTablaCompletitud);
		spinnerRestClientTask.execute();
	}
}


