package davidmb.services;

import java.util.List;
import java.util.Optional;

import davidmb.dao.ParadaDAO;
import davidmb.models.Parada;
import davidmb.models.Peregrino;

public class ParadasService {
	private ParadaDAO paradaDAO;
	
	public ParadasService() {
		this.paradaDAO = new ParadaDAO();
	}
	
	public Optional<Long> insertarParada(Parada parada) {
		return paradaDAO.insertarParada(parada);
	}
	
	public Parada obtenerParadaPorIdUsuario(Long idUsuario) {
		return paradaDAO.obtenerParadaPorIdUsuario(idUsuario);
	}
	
	public List<Peregrino> obtenerPeregrinosParada(Long idParada) {
		return paradaDAO.obtenerPeregrinosParada(idParada);
	}
	
	public List<Parada> obtenerTodasParadas() {
		return paradaDAO.obtenerTodasParadas();
	}
}
