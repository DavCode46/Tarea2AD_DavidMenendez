package davidmb.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import davidmb.dao.ParadaDAO;
import davidmb.models.Parada;
import davidmb.models.Peregrino;

/**
 * Servicio para gestionar las operaciones relacionadas con las paradas y los peregrinos en ellas.
 * Utiliza la capa de acceso a datos {@link ParadaDAO} para realizar las operaciones.
 */
public class ParadasService {
    
    private ParadaDAO paradaDAO;

    /**
     * Constructor por defecto que crea una instancia de {@link ParadaDAO}.
     */
    public ParadasService() {
        this(new ParadaDAO());
    }

    /**
     * Constructor que permite inyectar un {@link ParadaDAO} personalizado.
     * 
     * @param paradaDAO Objeto {@link ParadaDAO} que se utilizará para las operaciones de base de datos.
     */
    public ParadasService(ParadaDAO paradaDAO) {
        super();
        this.paradaDAO = paradaDAO;
    }

    /**
     * Inserta una nueva parada en la base de datos.
     * 
     * @param parada El objeto {@link Parada} a insertar.
     * @return Un {@link Optional} que contiene el ID de la parada insertada si la operación es exitosa, 
     *         o un {@link Optional#empty()} si no se pudo insertar la parada.
     */
    public Optional<Long> insertarParada(Parada parada) {
        return paradaDAO.insertarParada(parada);
    }

    /**
     * Asocia un peregrino con una parada en una fecha específica.
     * 
     * @param idPeregrino El ID del peregrino a asociar.
     * @param idParada El ID de la parada a la que se asociará el peregrino.
     * @param fecha La fecha de la asociación entre el peregrino y la parada.
     * @return Un {@link Optional} que contiene un valor si la operación es exitosa, 
     *         o un {@link Optional#empty()} si no se pudo realizar la asociación.
     */
    public Optional<Long> insertarPeregrinosParadas(Long idPeregrino, Long idParada, LocalDate fecha) {
        return paradaDAO.insertarPeregrinosParadas(idPeregrino, idParada, fecha);
    }

    /**
     * Obtiene una lista de paradas asociadas a un peregrino específico.
     * 
     * @param idPeregrino El ID del peregrino para el cual se desean obtener las paradas.
     * @return Una lista de paradas asociadas al peregrino, o una lista vacía si no hay paradas para el peregrino.
     */
    public List<Parada> obtenerParadasPorIdPeregrino(Long idPeregrino) {
        return paradaDAO.obtenerParadasPorIdPeregrino(idPeregrino);
    }

    /**
     * Obtiene una parada específica por su ID.
     * 
     * @param id El ID de la parada que se desea obtener.
     * @return Un {@link Optional} que contiene la parada si se encuentra, o un {@link Optional#empty()} si no existe.
     */
    public Optional<Parada> obtenerParadaPorId(Long id) {
        return paradaDAO.obtenerParadaPorId(id);
    }

    /**
     * Obtiene una parada específica asociada a un usuario por su ID de usuario.
     * 
     * @param idUsuario El ID del usuario para el cual se desea obtener la parada.
     * @return Un {@link Optional} que contiene la parada si se encuentra, o un {@link Optional#empty()} si no existe.
     */
    public Optional<Parada> obtenerParadaPorIdUsuario(Long idUsuario) {
        return paradaDAO.obtenerParadaPorIdUsuario(idUsuario);
    }

    /**
     * Obtiene una parada específica por su nombre.
     * 
     * @param nombre El nombre de la parada que se desea obtener.
     * @return Un {@link Optional} que contiene la parada si se encuentra, o un {@link Optional#empty()} si no existe.
     */
    public Optional<Parada> obtenerParadaPorNombre(String nombre) {
        return paradaDAO.obtenerParadaPorNombre(nombre);
    }

    /**
     * Obtiene una lista de peregrinos asociados a una parada específica.
     * 
     * @param idParada El ID de la parada para la cual se desean obtener los peregrinos.
     * @return Una lista de peregrinos asociados a la parada, o una lista vacía si no hay peregrinos para la parada.
     */
    public List<Peregrino> obtenerPeregrinosParada(Long idParada) {
        return paradaDAO.obtenerPeregrinosParada(idParada);
    }

    /**
     * Obtiene todas las paradas registradas en el sistema.
     * 
     * @return Una lista con todas las paradas registradas.
     */
    public List<Parada> obtenerTodasParadas() {
        return paradaDAO.obtenerTodasParadas();
    }

    /**
     * Verifica si una parada existe en el sistema por su nombre en caso de ser un peregrino
     * y por su nombre y región en caso de ser admin
     * 
     * @param nombre El nombre de la parada que se desea verificar.
     * @param region La región de la parada
     * @esAdmin boolean para identificar si es un admin o no
     * @return {@code true} si la parada existe, {@code false} si no existe.
     */
    public boolean paradaExiste(String nombre, String region, boolean esAdmin) {
        return paradaDAO.paradaExiste(nombre, region, esAdmin);
    }
}
