package com.example.ccm.eventos;

import java.util.ArrayList;

import com.example.ccm.R;
import com.example.ccm.R.id;
import com.example.ccm.R.layout;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

public class TabFragment extends Fragment {
	
	
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		super.onCreateView(inflater, container, savedInstanceState);
		
		View vistaFragment = inflater.inflate( R.layout.lista_expandible_eventos, container, false );

		ExpandableListView listaExpandibleEventos = (ExpandableListView) vistaFragment.findViewById( R.id.expandableListView );
		
		//Se definen los grupos y los contenidos child de cada grupo
		ArrayList<String> eventos = new ArrayList<String>();
		ArrayList<String[]> contenidoEventos = new ArrayList<String[]>();
		for ( int i=0; i<20; i++ ){
			eventos.add( String.format("Evento %d", i+1) );
			contenidoEventos.add(  new String[]{ String.format("Descripción evento %d", i+1) }  );
		}	
		listaExpandibleEventos.setAdapter(  new ListaExpandibleEventosAdapter(getActivity(), inflater, eventos, contenidoEventos)  );
		
		 
		return vistaFragment;
	}
	
	

	
}
