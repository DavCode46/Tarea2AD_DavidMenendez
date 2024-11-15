package davidmb.models;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * 
 * Clase que representa a un peregrino, cuando se crea un peregrino
 * también se crea un carnet con los datos de la parada donde se
 * está registrando el peregrino.
 * 
 */
public class Peregrino {
	private Long id;
	private String nombre;
	private String nacionalidad;
	private Long idUsuario;
	private Carnet carnet;
	
	/* Carga pesada de estancias y paradas
	 * Posiblemente en un futuro se cambie a una carga ligera 
	 * para evitar recursividad
	 */
	private List<Long> estancias = new ArrayList<>(); 
	private List<Long> paradas = new LinkedList<>();
	

	public Peregrino() {
		super();
	}

	public Peregrino(Long id, String nombre, String nacionalidad, Carnet carnet, Long idUsuario) {
		super();
		this.id = id;
		this.nombre = nombre;
		this.nacionalidad = nacionalidad;
		this.carnet = carnet;
		this.idUsuario = idUsuario;
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

	public String getNacionalidad() {
		return nacionalidad;
	}

	public void setNacionalidad(String nacionalidad) {
		this.nacionalidad = nacionalidad;
	}

	public List<Long> getEstancias() {
		return estancias;
	}

	public void setEstancias(List<Long> estancias) {
		this.estancias = estancias;
	}

	public List<Long> getParadas() {
		return paradas;
	}

	public void setParadas(List<Long> paradas) {
		this.paradas = paradas;
	}

	public Carnet getCarnet() {
		return carnet;
	}

	public void setCarnet(Carnet carnet) {
		this.carnet = carnet;
	}
	
	

	public Long getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(Long idUsuario) {
		this.idUsuario = idUsuario;
	}

	@Override
	public int hashCode() {
		return Objects.hash(carnet, estancias, id, idUsuario, nacionalidad, nombre, paradas);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Peregrino other = (Peregrino) obj;
		return Objects.equals(carnet, other.carnet) && Objects.equals(estancias, other.estancias)
				&& Objects.equals(id, other.id) && Objects.equals(idUsuario, other.idUsuario)
				&& Objects.equals(nacionalidad, other.nacionalidad) && Objects.equals(nombre, other.nombre)
				&& Objects.equals(paradas, other.paradas);
	}

	@Override
	public String toString() {
		return "Peregrino [id=" + id + ", nombre=" + nombre + ", nacionalidad=" + nacionalidad + ", idUsuario="
				+ idUsuario + ", carnet=" + carnet + ", estancias=" + estancias + ", paradas=" + paradas + "]";
	}

	


}
