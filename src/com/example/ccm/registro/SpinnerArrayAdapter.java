package com.example.ccm.registro;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.R;
import android.content.Context;
import android.widget.ArrayAdapter;


/**
 * Adapter especifico para asignar al Spinner de Seleccion de Tipo de Documento, Pais de Procedencia
 * e Institucion. Este adapter invoca al webservice para extraer los datos de completitud asociados al
 * tipo de documento (C.C o C.Extr), Pais de Procedencia (Colombia, USA, ...), Insitucion (UNAL,Texas U, Caltech) 
 * ES NECESARIO CONEXION A INTERNET PARA EXTRAER LOS DATOS DE COMPLETITUD
 * @author Santiago Céspedes Zapata - cespedesz07@gmail.com
 *
 */
public class SpinnerArrayAdapter extends ArrayAdapter<String> {
	
	
	//Nombres de las tablas de completitud que se capturan del WebService
	public static final String TIPO_DOCUMENTO = "tipo_doc";
	public static final String PAIS_PROCEDENCIA = "pais_procedencia";
	public static final String INSTITUCION = "institucion";
	
	

	
	public SpinnerArrayAdapter(Context context, String nombreTablaCompletitud) {
		super( context, android.R.layout.simple_spinner_item, obtenerDatosCompletitud(nombreTablaCompletitud) );		
	}
	
	
	private static List<String> obtenerDatosCompletitud( String nombreTablaCompletitud ){
		List<String> datosCompletitud = new ArrayList<String>();
		if ( nombreTablaCompletitud.equals(TIPO_DOCUMENTO) ){
			datosCompletitud.add( "Cédula de Ciudadania" );
			datosCompletitud.add( "Cédula de Extranjería" );
			datosCompletitud.add( "Pasaporte" );
			datosCompletitud.add( "Tarjeta de Identidad" );
		}
		else if ( nombreTablaCompletitud.equals(PAIS_PROCEDENCIA) ){
			datosCompletitud.add( "Colombia" );
			datosCompletitud.add( "USA" );
			datosCompletitud.add( "Alemania" );
			datosCompletitud.add( "India" );
			datosCompletitud.add( "Chile" );
		}
		else if ( nombreTablaCompletitud.equals(INSTITUCION) ){
			datosCompletitud.add( "Universidad Nacional de Colombia" );
			datosCompletitud.add( "University of California" );
			datosCompletitud.add( "California Institute of Technology" );
			datosCompletitud.add( "MIT" );
		}
		Collections.sort( datosCompletitud );
		return datosCompletitud;
	}

}


