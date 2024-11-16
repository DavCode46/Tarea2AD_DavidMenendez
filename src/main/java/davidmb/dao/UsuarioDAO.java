package davidmb.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

import davidmb.models.Usuario;

public class UsuarioDAO {
	private static final Logger logger = Logger.getLogger(UsuarioDAO.class.getName());
	ConexionDB con = ConexionDB.getInstancia();
	
	public Long insertar(Usuario u) {
	    String sqlUsuario = "INSERT INTO Usuarios (usuario, password, perfil) VALUES (?, ?, ?)";
	    Long idUsuario = null;

	    try (Connection connection = con.getConexion();
	         PreparedStatement usuarioStmt = connection.prepareStatement(sqlUsuario, Statement.RETURN_GENERATED_KEYS)) {
	        
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
	        System.out.println("Usuario insertado con ID: " + idUsuario);
	    } catch (SQLException ex) {
	        logger.severe("Error al insertar usuario: " + ex.getMessage());
	    }
	    return idUsuario; // Retorna el ID generado
	}

}
