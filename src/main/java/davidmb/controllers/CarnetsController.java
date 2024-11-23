package davidmb.controllers;

import java.util.Optional;

import davidmb.models.Carnet;
import davidmb.services.CarnetsService;

public class CarnetsController {

	private CarnetsService carnetService;
	
	public CarnetsController() {
        this(new CarnetsService());
	}
	
	public CarnetsController(CarnetsService carnetService) {
		super();
		this.carnetService = carnetService;
	}
	
	public Optional<Long> insertarCarnet(Carnet carnet) {
		return carnetService.insertarCarnet(carnet);
	}
	
	public boolean modificarCarnet(Carnet carnet) {
		return carnetService.modificarCarnet(carnet);
	}
	
}
