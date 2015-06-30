package com.example.ccm.eventos;

import java.util.ArrayList;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import com.example.ccm.R;

/**
 * Adapter encargado de desplegar o inflar la vista de cada uno de los item_evento.xml 
 * dentro de lista_eventos.xml. Este adapter se invoca desde TabFragment.java
 * @author Santiago Céspedes Zapata - cespedesz07@gmail.com
 *
 */
public class ItemEventoAdapter extends BaseAdapter {
	
	
	
	//Atributos que se traen de TabFragmentActivity.java
	private Activity tabFragmentActivity;
	private LayoutInflater tabFragmentInflater;
	private ArrayList<String[]> listaEventosPrueba;
	
	private TextView textViewNombreEvento;
	private TextView textViewDescripcionEvento;
	private CheckBox checkBoxEvento;
	private TextView textViewCuposDisponiblesEvento;
	private TextView textViewHoraLugarEvento;
	
	
	
	public ItemEventoAdapter( Activity tabFragmentActivity, LayoutInflater tabFragmentInflater, ArrayList<String[]> listaEventosPrueba ){
		super();
		this.tabFragmentActivity = tabFragmentActivity;
		this.tabFragmentInflater = tabFragmentInflater;
		this.listaEventosPrueba = listaEventosPrueba;
	}
	
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = tabFragmentInflater.inflate( R.layout.item_evento, null, false );
		
		textViewNombreEvento = (TextView) view.findViewById( R.id.textview_nombre_evento );
		textViewDescripcionEvento = (TextView) view.findViewById( R.id.textview_descripcion_evento );
		checkBoxEvento = (CheckBox) view.findViewById( R.id.checkbox_evento );
		textViewCuposDisponiblesEvento = (TextView) view.findViewById( R.id.textview_cupos_disponibles_evento );
		textViewHoraLugarEvento = (TextView) view.findViewById( R.id.textview_hora_lugar_evento );		
		
		textViewNombreEvento.setText( listaEventosPrueba.get(position)[0] );
		textViewDescripcionEvento.setText( listaEventosPrueba.get(position)[1] );
		
		return view;		
	}
	

	@Override
	public int getCount() {
		return this.listaEventosPrueba.size();
	}

	
	@Override
	public Object getItem(int pos) {
		return this.listaEventosPrueba.get( pos );
	}

	
	@Override
	public long getItemId(int pos) {
		return pos;
	}

	

}
