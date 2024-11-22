package davidmb.controllers;

import java.util.List;
import java.util.Optional;

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
	
	public Optional<Long> insertarEstancia(Estancia estancia) {
		return estanciasService.insertarEstancia(estancia);
	}
	
	public List<Estancia> obtenerEstanciasPorIdPeregrino(Long idPeregrino) {
		return estanciasService.obtenerEstanciasPorIdPeregrino(idPeregrino);
	}
	
	// Si no se encuentra devolver collection.emptyList(); No null --> A implementar
	public List<Estancia> obtenerEstanciasPorIdParada(Long idParada) {
		return estanciasService.obtenerEstanciasPorIdParada(idParada);
	}
}
