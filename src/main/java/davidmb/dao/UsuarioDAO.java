package davidmb.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;


import davidmb.models.Perfil;
import davidmb.models.Usuario;


/**
 * DAO (Data Access Object) para gestionar las operaciones relacionadas con los
 * usuarios.
 * 
 * <p>
 * Esta clase proporciona métodos para insertar y obtener usuarios en la base de
 * datos.
 * </p>
 */
public class UsuarioDAO {

	ConexionDB con = ConexionDB.getInstancia();

	/**
	 * Inserta un nuevo usuario en la base de datos.
	 * 
	 * <p>
	 * Este método inserta un usuario en la base de datos y devuelve el ID del usuario insertado.
	 * </p>
	 * 
	 * @param usuario el usuario que se desea insertar.
	 * @return un {@link Optional} que contiene el ID del usuario insertado si la
	 *         operación es exitosa, o un {@link Optional} vacío si la inserción
	 *         falla.
	 */
	public Optional<Long> insertarUsuario(Usuario u) {
		String sqlUsuario = "INSERT INTO Usuarios (usuario, password, perfil) VALUES (?, ?, ?)";
		Long idUsuario = null;

		try (Connection connection = con.getConexion();) {
			connection.setAutoCommit(false);

			try (PreparedStatement usuarioStmt = connection.prepareStatement(sqlUsuario,
					Statement.RETURN_GENERATED_KEYS)) {
				usuarioStmt.setString(1, u.getNombreUsuario());
				usuarioStmt.setString(2, u.getPassword());
				usuarioStmt.setString(3, u.getPerfil().name());
				int rowsAffected = usuarioStmt.executeUpdate();

				if (rowsAffected > 0) {
					try (ResultSet generatedKeys = usuarioStmt.getGeneratedKeys()) {
						if (generatedKeys.next()) {
							idUsuario = generatedKeys.getLong(1); // Obtener el ID generado
							connection.commit();
						} else {
						
							connection.rollback();
						}
					}
				}

			
			} catch (SQLException e) {
				e.printStackTrace();
				try {
					connection.rollback();
					
				} catch (SQLException rollbackEx) {
					rollbackEx.printStackTrace();
				}
			} finally {
				connection.setAutoCommit(true);
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return Optional.ofNullable(idUsuario); // Retorna el ID generado
	}

	/**
	 * Busca un usuario en la base de datos por su nombre de usuario y contraseña.
	 * 
	 * <p>
	 * Este método busca un usuario en la base de datos por su nombre de usuario y
	 * contraseña y devuelve un {@link Optional} que contiene el usuario si se
	 * encuentra, o un {@link Optional} vacío si no se encuentra.
	 * </p>
	 * 
	 * @param usuario  el nombre de usuario del usuario que se desea buscar.
	 * @param password la contraseña del usuario que se desea buscar.
	 * @return un {@link Optional} que contiene el usuario si se encuentra, o un
	 *         {@link Optional} vacío si no se encuentra.
	 */
	public Optional<Usuario> login(String usuario, String password) {
		Usuario u = null;
		String sql = "SELECT * FROM Usuarios WHERE usuario = ? AND password = ?";

		try (Connection connection = con.getConexion(); PreparedStatement stmt = connection.prepareStatement(sql);

		) {

			stmt.setString(1, usuario);
			stmt.setString(2, password);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					u = new Usuario();
					u.setId(rs.getLong("id"));
					u.setNombreUsuario(rs.getString("usuario"));
					// u.setPassword(rs.getString("password"));
					u.setPerfil(Perfil.valueOf(rs.getString("perfil")));
				}
			}
		
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return Optional.ofNullable(u);
	}

	/**
	 * Valida las credenciales de un usuario.
	 * 
	 * <p>
	 * Este método valida las credenciales de un usuario y devuelve un valor
	 * booleano que indica si las credenciales son válidas.
	 * </p>
	 * 
	 * @param nombre   el nombre de usuario del usuario.
	 * @param password la contraseña del usuario.
	 * @return {@code true} si las credenciales son válidas, {@code false} si no lo
	 *         son.
	 */
	public boolean validarCredenciales(String nombre, String password) {
		String sql = "SELECT * FROM Usuarios WHERE usuario = ? AND password = ?";

		boolean ret = false;
		try (Connection connection = con.getConexion(); PreparedStatement stmt = connection.prepareStatement(sql);) {

			stmt.setString(1, nombre);
			stmt.setString(2, password);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					ret = true;
				}
			}
		

		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return ret;
	}

	/**
	 * Verifica si un usuario existe en la base de datos.
	 * 
	 * <p>
	 * Este método verifica si un usuario existe en la base de datos y devuelve un
	 * valor booleano que indica si el usuario existe.
	 * </p>
	 * 
	 * @param usuario el nombre de usuario del usuario.
	 * @return {@code true} si el usuario existe, {@code false} si no existe.
	 */
	public boolean usuarioExiste(String usuario) {
		boolean existe = false;
		String sql = "SELECT * FROM Usuarios WHERE usuario = ?";

		try (Connection connection = con.getConexion(); PreparedStatement stmt = connection.prepareStatement(sql);) {
			stmt.setString(1, usuario);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					existe = true;
				}
			}
			
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return existe;
	}

}
