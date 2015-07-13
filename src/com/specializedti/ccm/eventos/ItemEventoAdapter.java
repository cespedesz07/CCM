package com.specializedti.ccm.eventos;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.specializedti.ccm.R;
import com.specializedti.ccm.eventos.model.Evento;
import com.specializedti.ccm.eventos.model.Ubicacion;
import com.specializedti.ccm.preferences.CCMPreferences;

/**
 * Adapter encargado de desplegar o inflar la vista de cada uno de los item_evento.xml 
 * dentro de lista_eventos.xml. Este adapter se invoca desde TabFragment.java
 * @author Santiago Céspedes Zapata - cespedesz07@gmail.com
 *
 */
public class ItemEventoAdapter extends BaseAdapter {
	
	
	
	
	static class ViewHolder{
		TextView textViewTipoEvento;
		TextView textViewNombreEvento;
		TextView textViewDescripcionEvento;
		UbicacionesMultiSelectSpinner multiSelectSpinnerHoraLugarEvento;
	}
	
	
	
	private Context context;
	private ArrayList<Object[]> result;
	
	
	
	public ItemEventoAdapter( Context context, ArrayList<Object[]> result ){
		super();
		this.context = context;
		this.result = result;
	}
	
	
	@SuppressWarnings("unchecked")
	@SuppressLint("ViewHolder")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;	
		
		if ( convertView == null ){
			//Se Infla el Layout
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate( R.layout.item_evento, null, false );
			
			holder = new ViewHolder();
			holder.textViewTipoEvento = (TextView) convertView.findViewById( R.id.textview_tipo_evento );
			holder.textViewNombreEvento = (TextView) convertView.findViewById( R.id.textview_nombre_evento );
			holder.textViewDescripcionEvento = (TextView) convertView.findViewById( R.id.textview_descripcion_evento );
			holder.multiSelectSpinnerHoraLugarEvento = (UbicacionesMultiSelectSpinner) convertView.findViewById( R.id.multi_select_spinner_hora_lugar_evento );
			
			//Se envia la view padre que contiene el spinner para poder asignar un color
			//cuando se marquen y se desmarquen ubicaciones del mismo spinner
			holder.multiSelectSpinnerHoraLugarEvento.setParentView( convertView );	
			
			//Se almacena el holder con el view
			convertView.setTag( holder );				
		}
		//Si el view no es nulo, se llama el view almacenado y se hace casting
		//al holder para poder extraer los textView y Spinner facilmente desde el holder
		else{			 
			holder = (ViewHolder) convertView.getTag();
		}
		
		
		//Se asigna a cada Item el Tipo del Evento, Nombre del Evento, Descripcion, y Ubicaciones
		Object[] eventoUbicacion = result.get(position);
		Evento evento = (Evento) eventoUbicacion[0];
		ArrayList<Ubicacion> ubicaciones = (ArrayList<Ubicacion>) eventoUbicacion[1];
		
		ArrayList< ArrayList<String> > ubicacionesExtraidas = new ArrayList< ArrayList<String> >();
		ArrayList<String> idsUbicaciones = new ArrayList<String>();
		ArrayList<String> horaLugarUbicacionesString = new ArrayList<String>();
		for ( Ubicacion u : ubicaciones ){
			idsUbicaciones.add( u.idUbicacion );
			horaLugarUbicacionesString.add( String.format(UbicacionesMultiSelectSpinner.FORMATO_UBICACION, u.horaInicio, u.lugar, u.cuposDisponibles) );
		}
		ubicacionesExtraidas.add( idsUbicaciones );
		ubicacionesExtraidas.add( horaLugarUbicacionesString );
		
		holder.textViewTipoEvento.setText( evento.tipoEvento );
		holder.textViewNombreEvento.setText( evento.nombre );
		holder.textViewDescripcionEvento.setText( evento.descripcion );
		holder.multiSelectSpinnerHoraLugarEvento.setDialogTitle( evento.nombre );
		holder.multiSelectSpinnerHoraLugarEvento.setTipoEvento( evento.tipoEvento );
		holder.multiSelectSpinnerHoraLugarEvento.setItems( ubicacionesExtraidas );
		
		//Se restauran los estados de las ubicaciones ya almacenadas en preferencias
		List<String> ubicacionesSeleccionadas = new CCMPreferences( this.context ).obtenerIdRegistrosUbicacion();
		holder.multiSelectSpinnerHoraLugarEvento.setSelectedItems( ubicacionesSeleccionadas );
		
		return convertView;
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
