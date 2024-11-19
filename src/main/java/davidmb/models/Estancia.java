package davidmb.models;

import java.time.LocalDate;
import java.util.Objects;

/**
 * 
 * Clase que representa una estancia de un peregrino en una parada 
 * (No todas las paradas tienen estancia)
 */
public class Estancia {
	private Long id;
	private LocalDate fecha;
	private boolean vip = false;
	private Long peregrino;
	private Long parada;

	public Estancia() {
		super();
	}

	public Estancia(LocalDate fecha, boolean vip, Long peregrino, Long parada) {
		super();
		this.fecha = fecha;
		this.vip = vip;
		this.peregrino = peregrino;
		this.parada = parada;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDate getFecha() {
		return fecha;
	}

	public void setFecha(LocalDate fecha) {
		this.fecha = fecha;
	}

	public boolean isVip() {
		return vip;
	}

	public void setVip(boolean vip) {
		this.vip = vip;
	}

	public Long getPeregrino() {
		return peregrino;
	}

	public void setPeregrino(Long peregrino) {
		this.peregrino = peregrino;
	}

	public Long getParada() {
		return parada;
	}

	public void setParada(Long parada) {
		this.parada = parada;
	}

	@Override
	public int hashCode() {
		return Objects.hash(fecha, id, parada, peregrino, vip);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Estancia other = (Estancia) obj;
		return Objects.equals(fecha, other.fecha) && Objects.equals(id, other.id)
				&& Objects.equals(parada, other.parada) && Objects.equals(peregrino, other.peregrino)
				&& vip == other.vip;
	}

	@Override
	public String toString() {
		return "Estancia [id=" + id + ", fecha=" + fecha + ", vip=" + vip + ", peregrino=" + peregrino + ", parada="
				+ parada + "]";
	}

}
