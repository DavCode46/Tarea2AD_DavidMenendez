package davidmb.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.logging.Logger;

import davidmb.models.Carnet;
import davidmb.models.Parada;

public class CarnetDAO {

	private static final Logger logger = Logger.getLogger(CarnetDAO.class.getName());
	ConexionDB con = ConexionDB.getInstancia();

	public Optional<Long> insertarCarnet(Carnet carnet) {

		String sql = "INSERT INTO Carnets (parada_inicial) VALUES (?)";

		try (Connection connection = con.getConexion();
				PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

			// Asegúrate de que la parada no sea nula y tenga un ID válido
			if (carnet.getParadaInicial() == null || carnet.getParadaInicial().getId() == null) {
				throw new IllegalArgumentException("La parada inicial no es válida.");
			}

			stmt.setLong(1, carnet.getParadaInicial().getId());

			int rowsAffected = stmt.executeUpdate();

			if (rowsAffected > 0) {
				// Obtener la clave generada automáticamente
				try (ResultSet rs = stmt.getGeneratedKeys()) {
					if (rs.next()) {
						carnet.setId(rs.getLong(1));
					}
				}
				logger.info("Carnet insertado con ID: " + carnet.getId());
				return Optional.of(carnet.getId());
			}

		} catch (IllegalArgumentException e) {
			logger.severe("Error al insertar carnet: " + e.getMessage());
		} catch (SQLException e) {
			logger.severe("Error al insertar carnet: " + e.getMessage());
		}
		return Optional.empty();
	}
	
	public boolean modificarCarnet(Carnet carnet) {
		String sqlCarnet = "UPDATE Carnets SET distancia = ?, nvips = ? WHERE id = ?";
		boolean ret = false;
		
		try(Connection connection = con.getConexion();
			PreparedStatement carnetStmt = connection.prepareStatement(sqlCarnet)	
			) {
			carnetStmt.setDouble(1, carnet.getDistancia());
			carnetStmt.setInt(2, carnet.getnVips());
			carnetStmt.setLong(3, carnet.getId());
			
			int rowsAffected = carnetStmt.executeUpdate();
			if(rowsAffected > 0) {
                ret = true;
                logger.info("Carnet modificado: " + carnet);
			}else {
                logger.info("No se ha modificado el carnet: " + carnet);
                }
			} catch (SQLException e) {
				logger.severe("Error al modificar carnet: " + e.getMessage());
			}
		return ret;
	}

	public Carnet obtenerCarnetPorId(Long id) {
		 String sqlCarnet = "SELECT Carnets.id, Carnets.fechaexp, Carnets.distancia, Carnets.nvips, " +
                 "Paradas.id AS parada_id, Paradas.nombre AS parada_nombre, Paradas.region AS parada_region, Paradas.responsable AS parada_responsable " +
                 "FROM Carnets " +
                 "INNER JOIN Paradas ON Carnets.parada_inicial = Paradas.id " +
                 "WHERE Carnets.id = ?";
		 Carnet carnet = null;
		 Parada paradaInicial = null;


		try (Connection connection = con.getConexion();
				PreparedStatement carnetStmt = connection.prepareStatement(sqlCarnet)) {
			carnetStmt.setLong(1, id);
			try (ResultSet rs = carnetStmt.executeQuery()) {
				if (rs.next()) {
					carnet = new Carnet();
					carnet.setId(rs.getLong("id"));
					carnet.setFechaExp(rs.getDate("fechaexp").toLocalDate());
					carnet.setDistancia(rs.getDouble("distancia"));
					carnet.setnVips(rs.getInt("nvips"));
					
					paradaInicial = new Parada();
					paradaInicial.setId(rs.getLong("parada_id"));
					paradaInicial.setNombre(rs.getString("parada_nombre"));
					paradaInicial.setRegion(rs.getString("parada_region").charAt(0));
					paradaInicial.setResponsable(rs.getString("parada_responsable"));
					
					carnet.setParadaInicial(paradaInicial);
					
					logger.info("Carnet obtenido: " + carnet);
				}
			}

		} catch (SQLException e) {
			logger.severe("Error al obtener carnet: " + e.getMessage());
		}

		return carnet;
	}
	
//	public static void main(String args[]) {
//		CarnetDAO dao = new CarnetDAO();
//		
//		Carnet c = dao.obtenerCarnetPorId(1L);
//		System.out.println(c);
//		c.setDistancia(100.0);
//		c.setnVips(5);
//		
//		boolean res = dao.modificarCarnet(c);
//		
//		
//		System.out.println(c);
//	}
}
