package davidmb.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

import davidmb.models.Carnet;

public class CarnetDAO {

	private static final Logger logger = Logger.getLogger(CarnetDAO.class.getName());
	ConexionDB con = ConexionDB.getInstancia();

	public boolean insertar(Carnet carnet) {
		
	    String sql = "INSERT INTO Carnets (parada_inicial) VALUES (?)";
	    boolean ret = false;

	    try (Connection connection = con.getConexion();
	         PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

	        // Asegúrate de que la parada no sea nula y tenga un ID válido
	        if (carnet.getParadaInicial() == null || carnet.getParadaInicial().getId() == null) {
	            throw new IllegalArgumentException("La parada inicial no es válida.");
	        }

	        // Configurar los valores para el INSERT
	        //stmt.setDate(1, carnet.getFechaExp());
	        //stmt.setDouble(2, carnet.getDistancia());
	        //stmt.setInt(3, carnet.getnVips());
	        stmt.setLong(1, carnet.getParadaInicial().getId());

	        int rowsAffected = stmt.executeUpdate();
	        if (rowsAffected > 0) {
	            // Obtener la clave generada automáticamente
	            try (ResultSet rs = stmt.getGeneratedKeys()) {
	                if (rs.next()) {
	                    carnet.setId(rs.getLong(1)); // Asignar el ID generado al objeto Carnet
	                }
	            }
	            logger.info("Carnet insertado con ID: " + carnet.getId());
	            ret = true;
	        }

		} catch (IllegalArgumentException e) {
			logger.severe("Error al insertar carnet: " + e.getMessage());
		} catch (SQLException e) {
			logger.severe("Error al insertar carnet: " + e.getMessage());
		}
	    return ret;
	}


	public Carnet obtenerPorId(int id) {
		System.out.println("Obteniendo carnet por id");
		return new Carnet();
	}
}
