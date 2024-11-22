package davidmb.services;

import java.util.List;
import java.util.Optional;

import davidmb.dao.EstanciaDAO;
import davidmb.models.Estancia;

public class EstanciasService {
	private EstanciaDAO estanciaDAO;
	
	public EstanciasService() {
        this(new EstanciaDAO());
	}
	
	public EstanciasService(EstanciaDAO estanciaDAO) {
		super();
		this.estanciaDAO = estanciaDAO;
	}
	
	public Optional<Long> insertarEstancia(Estancia estancia) {
		return estanciaDAO.insertarEstancia(estancia);
	}
	
	public List<Estancia> obtenerEstanciasPorIdPeregrino(Long idPeregrino) {
		return estanciaDAO.obtenerEstanciasPorIdPeregrino(idPeregrino);
	}
	
	public List<Estancia> obtenerEstanciasPorIdParada(Long idParada) {
		return estanciaDAO.obtenerEstanciasPorIdParada(idParada);
	}
}
