package davidmb.controllers;

import java.util.List;

import davidmb.models.Estancia;
import davidmb.services.EstanciasService;

public class EstanciasController {

	private EstanciasService estanciasService;
	
	public EstanciasController() {
		this(new EstanciasService());
	}
	
	public EstanciasController(EstanciasService estanciasService) {
		super();
		this.estanciasService = estanciasService;
	}
	
	public boolean insertarEstancia(Estancia estancia) {
		return estanciasService.insertarEstancia(estancia);
	}
	
	public List<Estancia> obtenerEstanciaPorIdPeregrino(Long idPeregrino) {
		return estanciasService.obtenerEstanciaPorIdPeregrino(idPeregrino);
	}
	
	// Si no se encuentra devolver collection.emptyList(); No null --> A implementar
	public List<Estancia> obtenerEstanciasPorIdParada(Long idParada) {
		return estanciasService.obtenerEstanciasPorIdParada(idParada);
	}
}
