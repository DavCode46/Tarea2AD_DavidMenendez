package davidmb.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 
 * Clase que representa una parada, en cada parada se almacena una lista de peregrinos
 */
public class Parada implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String nombre;
	private char region;
	private String responsable;
	private Long idUsuario;
	private List<Peregrino> peregrinos = new ArrayList<Peregrino>();

	public Parada() {
		super();
	}

	public Parada(Long id, String nombre, char region, String responsable) {
		super();
		this.id = id;
		this.nombre = nombre;
		this.region = region;
		this.responsable = responsable;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public char getRegion() {
		return region;
	}

	public void setRegion(char region) {
		this.region = region;
	}

	public String getResponsable() {
		return responsable;
	}

	public void setResponsable(String responsable) {
		this.responsable = responsable;
	}

	public List<Peregrino> getPeregrinos() {
		return peregrinos;
	}

	public void setPeregrinos(List<Peregrino> peregrinos) {
		this.peregrinos = peregrinos;
	}
	
	public Long getIdUsuario() {
		return idUsuario;
	}
	
	public void setIdUsuario(Long idUsuario) {
		this.idUsuario = idUsuario;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, idUsuario, nombre, peregrinos, region, responsable);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Parada other = (Parada) obj;
		return Objects.equals(id, other.id) && Objects.equals(idUsuario, other.idUsuario)
				&& Objects.equals(nombre, other.nombre) && Objects.equals(peregrinos, other.peregrinos)
				&& region == other.region && Objects.equals(responsable, other.responsable);
	}

	@Override
	public String toString() {
		return "Parada [id=" + id + ", nombre=" + nombre + ", region=" + region + ", responsable=" + responsable
				+ ", idUsuario=" + idUsuario + ", peregrinos=" + peregrinos + "]";
	}

}