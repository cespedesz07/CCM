package com.specializedti.ccm.eventos.model;

public class Ubicacion{
	
	public String idUbicacion;
	public String horaInicio;
	//public String horaFin;
	public String lugar;
	public String fecha;
	public String cuposDisponibles;
	
	public Ubicacion( String idUbicacion, String horaInicio, String lugar, String fecha, String cuposDisponibles ){
		this.idUbicacion = idUbicacion;
		this.horaInicio = horaInicio;
		//this.horaFin = horaFin;
		this.lugar = lugar;
		this.fecha = fecha;
		this.cuposDisponibles = cuposDisponibles;
	}
	
	
	public String toString(){
		return "[" + idUbicacion + ", " + horaInicio + ", " + lugar + ", " + fecha + ", " + cuposDisponibles + "]"; 
	}
	
}
