package davidmb.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import davidmb.models.Carnet;

public class CarnetDAO {

	private static final Logger logger = Logger.getLogger(CarnetDAO.class.getName());
	ConexionDB con = ConexionDB.getInstancia();

	public Optional<Long> insertarCarnet(Carnet carnet) {

		String sql = "INSERT INTO Carnets (parada_inicial) VALUES (?)";

		try (Connection connection = con.getConexion()) {

			connection.setAutoCommit(false);

			try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
				if (carnet.getParadaInicial() == null || carnet.getParadaInicial().getId() == null) {
					throw new IllegalArgumentException("La parada inicial no es válida.");
				}

				stmt.setLong(1, carnet.getParadaInicial().getId());
				int rowsAffected = stmt.executeUpdate();

				if (rowsAffected > 0) {
					try (ResultSet rs = stmt.getGeneratedKeys()) {
						if (rs.next()) {
							carnet.setId(rs.getLong(1));
						}
					}
					logger.log(Level.INFO, "Carnet insertado: " + carnet);
					connection.commit();
					return Optional.of(carnet.getId());
				}
			} catch (IllegalArgumentException | SQLException e) {
				logger.severe("Error al insertar carnet: " + e.getMessage());
				try {
					connection.rollback();
					logger.warning("Transacción revertida.");
				} catch (SQLException rollbackEx) {
					logger.severe("Error al hacer rollback: " + rollbackEx.getMessage());
				}
			} finally {
				connection.setAutoCommit(true);
			}

		} catch (SQLException e) {
			logger.severe("Error de conexión o rollback al insertar carnet: " + e.getMessage());
		}
		return Optional.empty();
	}

	public boolean modificarCarnet(Carnet carnet) {
		String sqlCarnet = "UPDATE Carnets SET distancia = ?, nvips = ? WHERE id = ?";
		boolean ret = false;

		try (Connection connection = con.getConexion()) {

			connection.setAutoCommit(false);

			try (PreparedStatement carnetStmt = connection.prepareStatement(sqlCarnet)) {
				carnetStmt.setDouble(1, carnet.getDistancia());
				carnetStmt.setInt(2, carnet.getnVips());
				carnetStmt.setLong(3, carnet.getId());

				int rowsAffected = carnetStmt.executeUpdate();
				if (rowsAffected > 0) {
					ret = true;
					logger.info("Carnet modificado: " + carnet);
					connection.commit();
				} else {
					logger.info("No se ha modificado el carnet: " + carnet);
					connection.rollback();
				}
			} catch (SQLException e) {
				logger.severe("Error al modificar carnet: " + e.getMessage());
				connection.rollback();
				throw e;
			} finally {
				connection.setAutoCommit(true);
			}

		} catch (SQLException e) {
			logger.severe("Error de conexión o rollback al modificar carnet: " + e.getMessage());
		}
		return ret;
	}
}
