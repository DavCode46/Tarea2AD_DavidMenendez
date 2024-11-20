package davidmb.controllers;

import java.util.List;
import java.util.Optional;

import davidmb.models.Peregrino;
import davidmb.services.PeregrinosService;

public class PeregrinosController {

	private PeregrinosService peregrinosService;
	
	public PeregrinosController() {
		this(new PeregrinosService());
	}
	
	public PeregrinosController(PeregrinosService peregrinosService) {
		super();
		this.peregrinosService = peregrinosService;
	}
	
	public Optional<Long> insertarPeregrino(Peregrino p) {
		return peregrinosService.insertarPeregrino(p);
	}

	public Optional<Peregrino> obtenerPeregrinoPorId(Long id) {
		return peregrinosService.obtenerPeregrinoPorId(id);
	}

	public Optional<Peregrino> obtenerPeregrinoPorIdUsuario(Long id) {
		return peregrinosService.obtenerPeregrinoPorIdUsuario(id);
	}
	
	public List<Peregrino> obtenerTodosPeregrinos() {
		return peregrinosService.obtenerTodosPeregrinos();
	}
}
