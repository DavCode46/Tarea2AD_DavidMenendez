package davidmb.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import davidmb.dao.ParadaDAO;
import davidmb.models.Parada;
import davidmb.models.Peregrino;

public class ParadasService {
	private ParadaDAO paradaDAO;
	
	public ParadasService() {
		this(new ParadaDAO());
	}
	
	public ParadasService(ParadaDAO paradaDAO) {
		super();
		this.paradaDAO = paradaDAO;
	}
	
	public Optional<Long> insertarParada(Parada parada) {
		return paradaDAO.insertarParada(parada);
	}
	
	public Optional<Long> insertarPeregrinosParadas(Long idPeregrino, Long idParada, LocalDate fecha) {
		return paradaDAO.insertarPeregrinosParadas(idPeregrino, idParada, fecha);
	}
	
	public List<Parada> obtenerParadasPorIdPeregrino(Long idPeregrino) {
		return paradaDAO.obtenerParadasPorIdPeregrino(idPeregrino);
	}
	public Optional<Parada> obtenerParadaPorId(Long id) {
		return paradaDAO.obtenerParadaPorId(id);
	}
	
	public Optional<Parada> obtenerParadaPorIdUsuario(Long idUsuario) {
		return paradaDAO.obtenerParadaPorIdUsuario(idUsuario);
	}
	
	public Optional<Parada> obtenerParadaPorNombre(String nombre) {
		return paradaDAO.obtenerParadaPorNombre(nombre);
	}
	
	public List<Peregrino> obtenerPeregrinosParada(Long idParada) {
		return paradaDAO.obtenerPeregrinosParada(idParada);
	}
	
	public List<Parada> obtenerTodasParadas() {
		return paradaDAO.obtenerTodasParadas();
	}
	
	public boolean paradaExiste(String nombre) {
		return paradaDAO.paradaExiste(nombre);
	}
}
