package davidmb.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.logging.Logger;

import davidmb.models.Usuario;

public class UsuarioDAO {
	private static final Logger logger = Logger.getLogger(UsuarioDAO.class.getName());
	ConexionDB con = ConexionDB.getInstancia();

	public Optional<Long> insertarUsuario(Usuario u) {
		String sqlUsuario = "INSERT INTO Usuarios (usuario, password, perfil) VALUES (?, ?, ?)";
		Long idUsuario = null;

		try (Connection connection = con.getConexion();
				PreparedStatement usuarioStmt = connection.prepareStatement(sqlUsuario,
						Statement.RETURN_GENERATED_KEYS)) {

			usuarioStmt.setString(1, u.getNombreUsuario());
			usuarioStmt.setString(2, u.getPassword());
			usuarioStmt.setString(3, u.getPerfil());
			int rowsAffected = usuarioStmt.executeUpdate();

			if (rowsAffected > 0) {
				try (ResultSet generatedKeys = usuarioStmt.getGeneratedKeys()) {
					if (generatedKeys.next()) {
						idUsuario = generatedKeys.getLong(1); // Obtener el ID generado
					}
				}
			}
			logger.info("Usuario insertado: " + u);
		} catch (SQLException ex) {
			logger.severe("Error al insertar usuario: " + ex.getMessage());
		}
		return Optional.ofNullable(idUsuario); // Retorna el ID generado
	}

	public Optional<Usuario> login(String usuario, String password) {
		Usuario u = null;
		String sql = "SELECT * FROM Usuarios WHERE usuario = ? AND password = ?";

		try (Connection connection = con.getConexion();
			PreparedStatement stmt = connection.prepareStatement(sql);
		
			) {

			stmt.setString(1, usuario);
			stmt.setString(2, password);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					u = new Usuario();
					u.setId(rs.getLong("id"));
					u.setNombreUsuario(rs.getString("usuario"));
					// u.setPassword(rs.getString("password"));
					u.setPerfil(rs.getString("perfil"));
				}
			}
			logger.info("Usuario encontrado: " + u);
		} catch (SQLException ex) {
			logger.severe("Error al buscar usuario: " + ex.getMessage());
		}
		return Optional.ofNullable(u);
	}
	
	public boolean validarCredenciales(String nombre, String password) {
		String sql = "SELECT * FROM Usuarios WHERE usuario = ? AND password = ?";

		System.out.println("Validando credenciales: " + nombre + ", " + password);
		boolean ret = false;
		try (Connection connection = con.getConexion();
			PreparedStatement stmt = connection.prepareStatement(sql);
			) {

			stmt.setString(1, nombre);
			stmt.setString(2, password);

			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					ret = true;
				}
			}
			logger.info("Usuario encontrado: " + ret);

		} catch (SQLException ex) {
			logger.severe("Error al buscar usuario: " + ex.getMessage());
		}
		return ret;
	}
	
	public boolean usuarioExiste(String usuario) {
		boolean existe = false;
		String sql = "SELECT * FROM Usuarios WHERE usuario = ?";
		
		try(Connection connection = con.getConexion();
			PreparedStatement stmt = connection.prepareStatement(sql);
			) {
			stmt.setString(1, usuario); 
	        try (ResultSet rs = stmt.executeQuery()) { 
	            if (rs.next()) {
	                existe = true; 
	            }
	        }
	        logger.info("Usuario encontrado: " + existe);
			} catch (SQLException ex) {
				logger.severe("Error al buscar usuario: " + ex.getMessage());
			}
		return existe;
	}

}
