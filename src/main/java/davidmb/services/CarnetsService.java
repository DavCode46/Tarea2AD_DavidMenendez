package davidmb.services;

import java.util.Optional;

import davidmb.dao.CarnetDAO;
import davidmb.models.Carnet;

/**
 * Servicio para gestionar las operaciones relacionadas con los carnets.
 * Utiliza la capa de acceso a datos {@link CarnetDAO} para realizar las operaciones.
 */
public class CarnetsService {

    private CarnetDAO carnetDAO;
    
    /**
     * Constructor por defecto que crea una instancia de {@link CarnetDAO}.
     */
    public CarnetsService() {
        this(new CarnetDAO());
    }
    
    /**
     * Constructor que permite inyectar un {@link CarnetDAO} personalizado.
     * 
     * @param carnetDAO Objeto {@link CarnetDAO} que se utilizará para las operaciones de base de datos.
     */
    public CarnetsService(CarnetDAO carnetDAO) {
        super();
        this.carnetDAO = carnetDAO;
    }
    
    /**
     * Inserta un nuevo carnet en la base de datos.
     * 
     * @param carnet El objeto {@link Carnet} a insertar.
     * @return Un {@link Optional} que contiene el ID del carnet insertado si la operación es exitosa, 
     *         o un {@link Optional#empty()} si no se pudo insertar el carnet.
     */
    public Optional<Long> insertarCarnet(Carnet carnet) {
        return carnetDAO.insertarCarnet(carnet);
    }
    
    /**
     * Modifica un carnet existente en la base de datos.
     * 
     * @param carnet El objeto {@link Carnet} con los datos actualizados.
     * @return {@code true} si la modificación fue exitosa, {@code false} en caso contrario.
     */
    public boolean modificarCarnet(Carnet carnet) {
        return carnetDAO.modificarCarnet(carnet);
    }
}
