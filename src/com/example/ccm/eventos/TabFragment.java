package com.example.ccm.eventos;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListView;

import com.example.ccm.R;

public class TabFragment extends Fragment implements OnItemSelectedListener {	
	
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		super.onCreateView(inflater, container, savedInstanceState);
		
		View vistaFragment = inflater.inflate( R.layout.lista_eventos, container, false );
		ListView listaEventos = (ListView) vistaFragment.findViewById( R.id.listview_lista_eventos );
		
		ArrayList<String[]> listaEventosPrueba = new ArrayList<String[]>();
		listaEventosPrueba.add( new String[]{ "Nombre1", "Descripcion1" } );
		listaEventosPrueba.add( new String[]{ "Nombre2", "Descripcion2" } );
		listaEventosPrueba.add( new String[]{ "Nombre3", "Descripcion3" } );
		listaEventosPrueba.add( new String[]{ "Nombre4", "Descripcion4" } );
		listaEventosPrueba.add( new String[]{ "Nombre5", "Descripcion5" } );
		listaEventosPrueba.add( new String[]{ "Nombre6", "Descripcion6" } );
		
		
		listaEventos.setAdapter(  new ItemEventoAdapter(getActivity(), inflater, listaEventosPrueba)  );
		return vistaFragment;
		
	}
	
	
	
	


	
	//====================================================== MÉTODOS DE OnItemSelectedListener===============================
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		Log.v("pos" , String.valueOf(position) );
	}


	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		
	}
	
	

	
}
