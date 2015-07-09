package com.example.ccm.eventos;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListView;

import com.example.ccm.R;
import com.example.ccm.restclient.CargaEventosRestClientTask;



public class TabFragment extends Fragment{	
	
	
	private String idTipoAreaActual; 
	private String pageTitle;
	
	
	public TabFragment( String idTipoAreaActual, String pageTitle ){
		this.idTipoAreaActual = idTipoAreaActual;
		this.pageTitle = pageTitle;
	}
	
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		super.onCreateView(inflater, container, savedInstanceState);
		
		View vistaFragment = inflater.inflate( R.layout.lista_eventos, container, false );
		ListView listaEventos = (ListView) vistaFragment.findViewById( R.id.listview_lista_eventos );
		CargaEventosRestClientTask cargaEventosRestClientTask = new CargaEventosRestClientTask( getActivity(), listaEventos );
		cargaEventosRestClientTask.execute( idTipoAreaActual, pageTitle );
		
		return vistaFragment;
		
	}
	
}
















