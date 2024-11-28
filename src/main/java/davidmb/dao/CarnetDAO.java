package davidmb.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

import davidmb.models.Carnet;

/**
 * DAO (Data Access Object) para gestionar las operaciones relacionadas con los carnets.
 * 
 * <p>Esta clase proporciona métodos para insertar y modificar carnets en la base de datos.</p>
 */
public class CarnetDAO {

	ConexionDB con = ConexionDB.getInstancia();


    /**
     * Inserta un nuevo carnet en la base de datos.
     * 
     * <p>Este método inserta un carnet con la parada inicial proporcionada en la base de datos
     * y devuelve el ID del carnet insertado.</p>
     * 
     * @param carnet el carnet que se desea insertar.
     * @return un {@link Optional} que contiene el ID del carnet insertado si la operación es exitosa,
     *         o un {@link Optional} vacío si la inserción falla.
     */
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

					connection.commit();
					return Optional.of(carnet.getId());
				}
			} catch (IllegalArgumentException | SQLException e) {

				try {
					connection.rollback();

				} catch (SQLException rollbackEx) {
					rollbackEx.printStackTrace();
				}
			} finally {
				connection.setAutoCommit(true);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return Optional.empty();
	}

	/**
     * Modifica un carnet existente en la base de datos.
     * 
     * <p>Este método actualiza la distancia y el número de vips del carnet con el ID especificado.</p>
     * 
     * @param carnet el carnet con los datos actualizados.
     * @return {@code true} si la actualización fue exitosa, {@code false} si no se pudo realizar la actualización.
     */
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

					connection.commit();
				} else {

					connection.rollback();
				}
			} catch (SQLException e) {
				e.printStackTrace();
				connection.rollback();
				throw e;
			} finally {
				connection.setAutoCommit(true);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ret;
	}
}
