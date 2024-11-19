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
	
	public Usuario login(String usuario, String password) {
		Usuario u = null;
		String sql = "SELECT * FROM Usuarios WHERE usuario = ? AND password = ?";
		
		try(Connection connection = con.getConexion();
			PreparedStatement stmt = connection.prepareStatement(sql);) {
			
			stmt.setString(1, usuario);
			stmt.setString(2, password);
			ResultSet rs = stmt.executeQuery();
			
		    if(rs.next()) {
		    	u = new Usuario();
		    	u.setId(rs.getLong("id"));
		    	u.setNombreUsuario(rs.getString("usuario"));
		    	//u.setPassword(rs.getString("password"));
		    	u.setPerfil(rs.getString("perfil"));
		    	
		    } 
			
		} catch(SQLException ex) {
			logger.severe("Error al buscar usuario: " + ex.getMessage());
		}
		return u;
	}
	
	public static void main(String args[]) {
		UsuarioDAO uDAO = new UsuarioDAO();
//		Usuario u = new Usuario("David", "david", "peregrino");
//		
//		uDAO.insertar(u);
		Usuario u = uDAO.login("responsable1", "respass1");
		if(u != null) {
			System.out.println("Usuario encontrado: " + u);
		} else {
			System.out.println("Usuario no encontrado");
		}
	}

}
