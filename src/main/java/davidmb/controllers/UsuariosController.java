package davidmb.controllers;

import java.util.Optional;

import davidmb.models.Usuario;
import davidmb.services.UsuariosService;

/**
 * Controlador para gestionar las operaciones relacionadas con los usuarios.
 * 
 * <p>Este controlador interactúa con el servicio {@link UsuariosService} para realizar
 * operaciones como insertar usuarios, realizar el login, verificar la existencia de usuarios y validar credenciales.</p>
 */
public class UsuariosController {

    private UsuariosService usuariosService;

    /**
     * Constructor por defecto que inicializa el controlador con un servicio de usuarios.
     */
    public UsuariosController() {
        this(new UsuariosService());
    }

    /**
     * Constructor que permite inicializar el controlador con un servicio específico de usuarios.
     * 
     * @param usuariosService el servicio de usuarios a utilizar.
     */
    public UsuariosController(UsuariosService usuariosService) {
        super();
        this.usuariosService = usuariosService;
    }

    /**
     * Inserta un nuevo usuario en el sistema.
     * 
     * <p>Este método interactúa con el servicio de usuarios para realizar la inserción
     * de un usuario y devuelve el ID del usuario insertado.</p>
     * 
     * @param u el usuario que se desea insertar.
     * @return un {@link Optional} que contiene el ID del usuario insertado si la operación es exitosa,
     *         o un {@link Optional} vacío si la inserción falla.
     */
    public Optional<Long> insertarUsuario(Usuario u) {
        return usuariosService.insertarUsuario(u);
    }

    /**
     * Realiza el login de un usuario dado su nombre de usuario y contraseña.
     * 
     * <p>Este método verifica las credenciales de un usuario y devuelve un objeto {@link Optional}
     * que contiene el usuario autenticado si las credenciales son válidas.</p>
     * 
     * @param nombreUsuario el nombre de usuario que se desea autenticar.
     * @param password la contraseña asociada al nombre de usuario.
     * @return un {@link Optional} que contiene el usuario si las credenciales son válidas,
     *         o un {@link Optional} vacío si las credenciales son incorrectas.
     */
    public Optional<Usuario> login(String nombreUsuario, String password) {
        return usuariosService.login(nombreUsuario, password);
    }

    /**
     * Verifica si un usuario existe en el sistema dado su nombre de usuario.
     * 
     * <p>Este método permite comprobar si un usuario con un nombre específico ya existe en el sistema.</p>
     * 
     * @param usuario el nombre de usuario a verificar.
     * @return {@code true} si el usuario existe, {@code false} si no.
     */
    public boolean usuarioExiste(String usuario) {
        return usuariosService.usuarioExiste(usuario);
    }

    /**
     * Valida las credenciales de un usuario dado su nombre y contraseña.
     * 
     * <p>Este método verifica si las credenciales (nombre y contraseña) proporcionadas son válidas.</p>
     * 
     * @param nombre el nombre de usuario a validar.
     * @param password la contraseña asociada al nombre de usuario.
     * @return {@code true} si las credenciales son válidas, {@code false} si no lo son.
     */
    public boolean validarCredenciales(String nombre, String password) {
        return usuariosService.validarCredenciales(nombre, password);
    }
}
