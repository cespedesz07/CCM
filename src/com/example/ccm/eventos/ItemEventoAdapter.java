package com.example.ccm.eventos;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.ccm.R;
import com.example.ccm.eventos.model.Evento;
import com.example.ccm.eventos.model.Ubicacion;

/**
 * Adapter encargado de desplegar o inflar la vista de cada uno de los item_evento.xml 
 * dentro de lista_eventos.xml. Este adapter se invoca desde TabFragment.java
 * @author Santiago Céspedes Zapata - cespedesz07@gmail.com
 *
 */
public class ItemEventoAdapter extends BaseAdapter {
	
	
	
	private Context context;
	private ArrayList<Object[]> result;
	private TextView textViewNombreEvento;
	private TextView textViewDescripcionEvento;
	private TextView textViewCuposDisponiblesEvento;
	private Spinner spinnerHoraLugarEvento;
	
	
	
	public ItemEventoAdapter( Context context, ArrayList<Object[]> result ){
		super();
		this.context = context;
		this.result = result;
	}
	
	
	@SuppressLint("ViewHolder")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate( R.layout.item_evento, null, false );
		
		textViewNombreEvento = (TextView) view.findViewById( R.id.textview_nombre_evento );
		textViewDescripcionEvento = (TextView) view.findViewById( R.id.textview_descripcion_evento );
		textViewCuposDisponiblesEvento = (TextView) view.findViewById( R.id.textview_cupos_disponibles_evento );
		spinnerHoraLugarEvento = (Spinner) view.findViewById( R.id.spinner_hora_lugar_evento );
		
		Object[] eventoUbicacion = result.get(position);
		Evento evento = (Evento) eventoUbicacion[0];
		ArrayList<Ubicacion> ubicaciones = (ArrayList<Ubicacion>) eventoUbicacion[1];
		
		ArrayList<String> horaLugarEventoString = new ArrayList<String>();
		for ( Ubicacion u : ubicaciones ){
			horaLugarEventoString.add( String.format("%s - %s", u.lugar, u.horaInicio) );
		}
		
		textViewNombreEvento.setText( evento.nombre );
		textViewDescripcionEvento.setText( evento.descripcion );
		spinnerHoraLugarEvento.setAdapter( new ArrayAdapter(context, 
				android.R.layout.simple_spinner_dropdown_item, 
				horaLugarEventoString) 
		);
		
		return view;		
	}


	@Override
	public int getCount() {
		return result.size();
	}


	@Override
	public Object getItem(int position) {
		return result.get(position);
	}


	@Override
	public long getItemId(int position) {
		return position;
	}

	

}
