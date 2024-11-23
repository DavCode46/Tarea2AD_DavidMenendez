package davidmb.services;

import java.util.Optional;

import davidmb.dao.CarnetDAO;
import davidmb.models.Carnet;

public class CarnetsService {
	private CarnetDAO carnetDAO;
	
	public CarnetsService() {
		this(new CarnetDAO());
	}
	
	public CarnetsService(CarnetDAO carnetDAO) {
		super();
		this.carnetDAO = carnetDAO;
	}
	
	public Optional<Long> insertarCarnet(Carnet carnet) {
		return carnetDAO.insertarCarnet(carnet);
	}
	
	public boolean modificarCarnet(Carnet carnet) {
		return carnetDAO.modificarCarnet(carnet);
	}
	
}
