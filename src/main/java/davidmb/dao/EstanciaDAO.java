package davidmb.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


import davidmb.models.Estancia;

/**
 * DAO (Data Access Object) para gestionar las operaciones relacionadas con las
 * estancias.
 * 
 * <p>
 * Esta clase proporciona métodos para insertar y obtener estancias en la base
 * de datos.
 * </p>
 */
public class EstanciaDAO {
	ConexionDB con = ConexionDB.getInstancia();
	
	/**
	 * Inserta una nueva estancia en la base de datos.
	 * 
	 * <p>
	 * Este método inserta una estancia en la base de datos y devuelve el ID de la
	 * estancia insertada.
	 * </p>
	 * 
	 * @param estancia la estancia que se desea insertar.
	 * @return un {@link Optional} que contiene el ID de la estancia insertada si la
	 *         operación es exitosa, o un {@link Optional} vacío si la inserción
	 *         falla.
	 */
	public Optional<Long> insertarEstancia(Estancia estancia) {
	    String sqlEstancias = "INSERT INTO Estancias (id_peregrino, id_parada, fecha, vip) VALUES (?, ?, ?, ?)";

	    try (Connection connection = con.getConexion()) {
	        connection.setAutoCommit(false); 

	        try (PreparedStatement estanciaStmt = connection.prepareStatement(sqlEstancias, Statement.RETURN_GENERATED_KEYS)) {
	            
	            estanciaStmt.setLong(1, estancia.getPeregrino());
	            estanciaStmt.setLong(2, estancia.getParada());
	            estanciaStmt.setDate(3, Date.valueOf(estancia.getFecha()));
	            estanciaStmt.setBoolean(4, estancia.isVip());

	           
	            int rowsAffected = estanciaStmt.executeUpdate();
	            if (rowsAffected > 0) {
	                try (ResultSet rs = estanciaStmt.getGeneratedKeys()) {
	                    if (rs.next()) {
	                        estancia.setId(rs.getLong(1));
	                    }
	                }
	                connection.commit(); 
	              
	                return Optional.of(estancia.getId());
	            } else {
	                
	                connection.rollback(); 
	            }
	        } catch (SQLException e) {
	          
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
	 * Obtiene una lista de estancias asociadas a un peregrino según su ID.
	 * 
	 * <p>
	 * Este método obtiene las estancias asociadas al ID de un peregrino específico.
	 * </p>
	 * 
	 * @param id el ID del peregrino cuyas estancias se desean obtener.
	 * @return una lista de estancias asociadas al peregrino.
	 */
	public List<Estancia> obtenerEstanciasPorIdPeregrino(Long id) {
		String sql = "SELECT * FROM Estancias WHERE id_peregrino = ?";
		List<Estancia> estancias = new ArrayList<>();

		try (Connection connection = con.getConexion(); PreparedStatement stmt = connection.prepareStatement(sql);) {
			stmt.setLong(1, id);

			try (ResultSet rs = stmt.executeQuery();) {
				while (rs.next()) {
					Estancia estancia = new Estancia();
					estancia = new Estancia();
					estancia.setId(rs.getLong("id"));
					estancia.setPeregrino(rs.getLong("id_peregrino"));
					estancia.setParada(rs.getLong("id_parada"));
					estancia.setFecha(rs.getDate("fecha").toLocalDate());
					estancia.setVip(rs.getBoolean("vip"));
					estancias.add(estancia);
				}
			}
			

		} catch (SQLException ex) {
			ex.printStackTrace();
		}

		return estancias;
	}

	/**
	 * Obtiene una lista de estancias asociadas a una parada según su ID.
	 * 
	 * <p>
	 * Este método obtiene las estancias asociadas al ID de una parada específica.
	 * </p>
	 * 
	 * @param id el ID de la parada cuyas estancias se desean obtener.
	 * @return una lista de estancias asociadas a la parada.
	 */
	public List<Estancia> obtenerEstanciasPorIdParada(Long id) {
		String sql = "SELECT * FROM Estancias WHERE id_parada = ?";
		List<Estancia> estancias = new ArrayList<>();

		try (Connection connection = con.getConexion(); PreparedStatement stmt = connection.prepareStatement(sql);) {
			stmt.setLong(1, id);

			try (ResultSet rs = stmt.executeQuery();) {
				while (rs.next()) {
					Estancia estancia = new Estancia();
					estancia = new Estancia();
					estancia.setId(rs.getLong("id"));
					estancia.setPeregrino(rs.getLong("id_peregrino"));
					estancia.setParada(rs.getLong("id_parada"));
					estancia.setFecha(rs.getDate("fecha").toLocalDate());
					estancia.setVip(rs.getBoolean("vip"));
					estancias.add(estancia);
				}
			}
			

		} catch (SQLException ex) {
			ex.printStackTrace();
		}

		return estancias;
	}
}
