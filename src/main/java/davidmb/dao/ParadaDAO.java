package davidmb.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import davidmb.models.Parada;
import davidmb.models.Peregrino;

public class ParadaDAO {

	ConexionDB con = ConexionDB.getInstancia();
	Logger logger = Logger.getLogger(ParadaDAO.class.getName());

	public Optional<Long> insertar(Parada parada) {
		String sqlParadas = "INSERT INTO Paradas (nombre, region, responsable, id_usuario) VALUES (?, ?, ?, ?)";
		String sqlUsuarios = "SELECT MAX(id) FROM Usuarios";

		try (Connection connection = con.getConexion();
				PreparedStatement paradaStmt = connection.prepareStatement(sqlParadas);
				PreparedStatement usuarioStmt = connection.prepareStatement(sqlUsuarios);) {

			Long nextUsuarioId = 1L;
			try (ResultSet rs = usuarioStmt.executeQuery()) {
				if (rs.next()) {
					nextUsuarioId = rs.getLong(1);
				}
			}

			paradaStmt.setString(1, parada.getNombre());
			paradaStmt.setString(2, String.valueOf(parada.getRegion()));
			paradaStmt.setString(3, parada.getResponsable());
			paradaStmt.setLong(4, nextUsuarioId);

			int rowsAffected = paradaStmt.executeUpdate();
			System.out.println("Parada insertada, filas afectadas: " + rowsAffected);
			return Optional.of(nextUsuarioId);
		} catch (SQLException e) {
			logger.severe("Error al insertar parada: " + e.getMessage());
		}
		return Optional.empty();
	}

	public Parada obtenerParadaPorIdUsuario(Long id) {
		String paradaSql = "SELECT * FROM Paradas WHERE id_usuario = ?";

		Parada parada = null;
		Long paradaId = null;

		try (Connection connection = con.getConexion();
				PreparedStatement paradaStmt = connection.prepareStatement(paradaSql);) {
			paradaStmt.setLong(1, id);
			try (ResultSet rs = paradaStmt.executeQuery()) {
				if (rs.next()) {
					parada = new Parada();
					parada.setId(rs.getLong("id"));
					parada.setNombre(rs.getString("nombre"));
					parada.setRegion(rs.getString("region").charAt(0));
					parada.setResponsable(rs.getString("responsable"));
				}
			}

		} catch (SQLException e) {
			logger.severe("Error al obtener parada por id de usuario: " + e.getMessage());
		}
		return parada;
	}

	public List<Peregrino> obtenerPeregrinosParada(Long idParada) {
		String sqlPeregrinosParadas = "SELECT id_peregrino FROM Peregrinos_paradas WHERE id_parada = ?";
		String sqlPeregrino = "SELECT * FROM Peregrinos WHERE id = ?";
		List<Peregrino> peregrinos = new ArrayList<>();

		try (Connection connection = con.getConexion();
				PreparedStatement paradasStmt = connection.prepareStatement(sqlPeregrinosParadas)) {

			paradasStmt.setLong(1, idParada);
			try (ResultSet rsParadas = paradasStmt.executeQuery()) {
				while (rsParadas.next()) {
					Long idPeregrino = rsParadas.getLong("id_peregrino");

					try (PreparedStatement peregrinoStmt = connection.prepareStatement(sqlPeregrino)) {
						peregrinoStmt.setLong(1, idPeregrino);
						try (ResultSet rsPeregrino = peregrinoStmt.executeQuery()) {
							if (rsPeregrino.next()) {
								Peregrino peregrino = new Peregrino();
								peregrino.setId(rsPeregrino.getLong("id"));
								peregrino.setNombre(rsPeregrino.getString("nombre"));
								peregrino.setNacionalidad(rsPeregrino.getString("nacionalidad"));

								peregrinos.add(peregrino);
							}
						}
					}
				}
			}
		} catch (SQLException e) {
			logger.severe("Error al obtener peregrinos de la parada: " + e.getMessage());
		}

		return peregrinos;
	}

	public List<Parada> obtenerTodasParadas() {
	    String sql = "SELECT * FROM Paradas";
	    String sqlPeregrinosParadas = "SELECT id_peregrino FROM Peregrinos_paradas WHERE id_parada = ?";
	    List<Parada> paradas = new ArrayList<>();

	    try (Connection connection = con.getConexion();
	         PreparedStatement stmt = connection.prepareStatement(sql);
	         ResultSet rs = stmt.executeQuery()) {

	        while (rs.next()) {
	            Parada parada = new Parada();
	            parada.setId(rs.getLong("id"));
	            parada.setNombre(rs.getString("nombre"));
	            parada.setRegion(rs.getString("region").charAt(0));
	            parada.setResponsable(rs.getString("responsable"));
	            parada.setIdUsuario(rs.getLong("id_usuario"));
            
	            List<Long> idsPeregrinos = new ArrayList<>();
	         
	            try (PreparedStatement paradasStmt = connection.prepareStatement(sqlPeregrinosParadas)) {
	                paradasStmt.setLong(1, parada.getId());
	                try (ResultSet rsParadas = paradasStmt.executeQuery()) {
	                    while (rsParadas.next()) {
	                        Long idPeregrino = rsParadas.getLong("id_peregrino");
	                        idsPeregrinos.add(idPeregrino);
	                    }
	                }
	            }
    
	            parada.setPeregrinos(idsPeregrinos);
         
	            paradas.add(parada);
	        }

	    } catch (SQLException e) {
	        logger.severe("Error al obtener todas las paradas: " + e.getMessage());
	    }
	    return paradas;
	}


	public static void main(String args[]) {
		ParadaDAO dao = new ParadaDAO();

		List<Peregrino> peregrinos = dao.obtenerPeregrinosParada(1L);
		for (Peregrino p : peregrinos) {
			System.out.println(p);
		}
//		List<Parada> paradas = dao.obtenerTodasParadas();
//		for (Parada p : paradas) {
//			System.out.println(p);
//		}
	}

}
