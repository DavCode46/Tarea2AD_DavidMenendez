package davidmb.services;

import java.util.List;
import java.util.Optional;

import davidmb.dao.PeregrinoDAO;
import davidmb.models.Peregrino;

/**
 * Servicio para gestionar las operaciones relacionadas con los peregrinos.
 * Utiliza la capa de acceso a datos {@link PeregrinoDAO} para realizar las operaciones.
 */
public class PeregrinosService {
    
    private PeregrinoDAO peregrinoDAO;

    /**
     * Constructor por defecto que crea una instancia de {@link PeregrinoDAO}.
     */
    public PeregrinosService() {
        this(new PeregrinoDAO());
    }

    /**
     * Constructor que permite inyectar un {@link PeregrinoDAO} personalizado.
     * 
     * @param peregrinoDAO Objeto {@link PeregrinoDAO} que se utilizará para las operaciones de base de datos.
     */
    public PeregrinosService(PeregrinoDAO peregrinoDAO) {
        super();
        this.peregrinoDAO = peregrinoDAO;
    }

    /**
     * Inserta un nuevo peregrino en la base de datos.
     * 
     * @param p El objeto {@link Peregrino} a insertar.
     * @return Un {@link Optional} que contiene el ID del peregrino insertado si la operación es exitosa, 
     *         o un {@link Optional#empty()} si no se pudo insertar el peregrino.
     */
    public Optional<Long> insertarPeregrino(Peregrino p) {
        return peregrinoDAO.insertarPeregrino(p);
    }

    /**
     * Obtiene un peregrino específico por su ID.
     * 
     * @param id El ID del peregrino que se desea obtener.
     * @return Un {@link Optional} que contiene el peregrino si se encuentra, o un {@link Optional#empty()} si no existe.
     */
    public Optional<Peregrino> obtenerPeregrinoPorId(Long id) {
        return peregrinoDAO.obtenerPeregrinoPorId(id);
    }

    /**
     * Obtiene un peregrino específico asociado a un usuario por su ID de usuario.
     * 
     * @param id El ID del usuario para el cual se desea obtener el peregrino.
     * @return Un {@link Optional} que contiene el peregrino si se encuentra, o un {@link Optional#empty()} si no existe.
     */
    public Optional<Peregrino> obtenerPeregrinoPorIdUsuario(Long id) {
        return peregrinoDAO.obtenerPeregrinoPorIdUsuario(id);
    }

    /**
     * Obtiene todos los peregrinos registrados en el sistema.
     * 
     * @return Una lista con todos los peregrinos registrados.
     */
    public List<Peregrino> obtenerTodosPeregrinos() {
        return peregrinoDAO.obtenerTodosPeregrinos();
    }

    /**
     * Verifica si un peregrino existe en el sistema por su ID.
     * 
     * @param id El ID del peregrino a verificar.
     * @return {@code true} si el peregrino existe, {@code false} si no existe.
     */
    public boolean peregrinoExiste(Long id) {
        return peregrinoDAO.peregrinoExiste(id);
    }

    /**
     * Verifica si un peregrino existe en el sistema por su nombre.
     * 
     * @param nombre El nombre del peregrino a verificar.
     * @return {@code true} si el peregrino con el nombre dado existe, {@code false} si no existe.
     */
    public boolean nombrePeregrinoExiste(String nombre) {
        return peregrinoDAO.nombrePeregrinoExiste(nombre);
    }
}
