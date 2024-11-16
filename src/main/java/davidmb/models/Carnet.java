package davidmb.models;

import java.time.LocalDate;
import java.util.Objects;

/**
 * 
 * Clase que representa un carnet de peregrino
 *(El carnet se crea en la primera parada que hace un peregrino)
 */
public class Carnet {
	private Long id;
	private LocalDate fechaExp = LocalDate.now();
	private double distancia = 0.0;
	private int nVips = 0;
	private Parada paradaInicial;

	public Carnet() {
		super();
	}

	public Carnet(Parada paradaInicial) {
		super();
		this.paradaInicial = paradaInicial;
	}
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDate getFechaExp() {
		return fechaExp;
	}

	public void setFechaExp(LocalDate fechaExp) {
		this.fechaExp = fechaExp;
	}

	public double getDistancia() {
		return distancia;
	}

	public void setDistancia(double distancia) {
		this.distancia = distancia;
	}

	public int getnVips() {
		return nVips;
	}

	public void setnVips(int nVips) {
		this.nVips = nVips;
	}

	public Parada getParadaInicial() {
		return paradaInicial;
	}

	public void setParadaInicial(Parada paradaInicial) {
		this.paradaInicial = paradaInicial;
	}

	@Override
	public int hashCode() {
		return Objects.hash(distancia, fechaExp, id, nVips, paradaInicial);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Carnet other = (Carnet) obj;
		return Double.doubleToLongBits(distancia) == Double.doubleToLongBits(other.distancia)
				&& Objects.equals(fechaExp, other.fechaExp) && Objects.equals(id, other.id) && nVips == other.nVips
				&& Objects.equals(paradaInicial, other.paradaInicial);
	}

	@Override
	public String toString() {
		return "Carnet [id=" + id + ", fechaExp=" + fechaExp + ", distancia=" + distancia + ", nVips=" + nVips
				+ ", paradaInicial=" + paradaInicial + "]";
	}

}
