package com.example.ccm.restclient;

import java.util.ArrayList;

import android.os.AsyncTask;


/**
 * Hilo usado para procesar la consulta de aquellos 
 * EVENTOS QUE PERTENECEN A UN ÁREA Y DÍA EN PARTICULAR.
 * 
 * Este hilo es llamado especialmente cuando ocurren cambios de Tabs
 * y cuando se selecciona un área en particular del patron Master/Detail
 * @author Santiago Céspedes Zapata - cespedesz07@gmail.com
 *
 */
public class EventosRestClientTask extends AsyncTask<String, Integer, ArrayList<String>> {

	@Override
	protected ArrayList<String> doInBackground(String... params) {
		// TODO Auto-generated method stub
		return null;
	}

}
