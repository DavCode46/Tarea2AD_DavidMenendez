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
import java.util.logging.Logger;

import davidmb.models.Estancia;

public class EstanciaDAO {
	ConexionDB con = ConexionDB.getInstancia();
	Logger logger = Logger.getLogger(EstanciaDAO.class.getName());

	public Optional<Long> insertarEstancia(Estancia estancia) {
		String sqlEstancias = "INSERT INTO Estancias (id_peregrino, id_parada, fecha, vip) VALUES (?, ?, ?, ?)";

		
		
		try (Connection connection = con.getConexion();
			PreparedStatement estanciaStmt = connection.prepareStatement(sqlEstancias,  Statement.RETURN_GENERATED_KEYS);
			) {
			
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
				return Optional.of(estancia.getId());
			}
		} catch (SQLException e) {
			logger.severe("Error al insertar parada: " + e.getMessage());
		}
		return Optional.empty();
	}

	

	public List<Estancia> obtenerEstanciasPorIdPeregrino(Long id) {
		String sql = "SELECT * FROM Estancias WHERE id_peregrino = ?";
		List<Estancia> estancias = new ArrayList<>();
		
		try(Connection connection = con.getConexion();
			PreparedStatement stmt = connection.prepareStatement(sql);	
			) {
			stmt.setLong(1, id);
			
			try(ResultSet rs = stmt.executeQuery();) {
				while(rs.next()) {
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
			
		}catch(SQLException ex) {
			logger.severe("Error al obtener la estancia del peregrino");
		}
		
		return estancias;
	}

	public List<Estancia> obtenerEstanciasPorIdParada(Long id) {
		String sql = "SELECT * FROM Estancias WHERE id_parada = ?";
		List<Estancia> estancias = new ArrayList<>();
		
		try(Connection connection = con.getConexion();
			PreparedStatement stmt = connection.prepareStatement(sql);	
			) {
			stmt.setLong(1, id);
			
			try(ResultSet rs = stmt.executeQuery();) {
				while(rs.next()) {
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
			
		}catch(SQLException ex) {
			logger.severe("Error al obtener la estancia del peregrino");
		}
		
		return estancias;
	}
//	
//	public static void main(String args[]) {
//		EstanciaDAO dao = new EstanciaDAO();
//		Estancia estancia = new Estancia(java.time.LocalDate.now(), false, 1L, 2L);
//		
//		boolean ret = dao.insertarEstancia(estancia);
//		System.out.println(ret);
//		List<Estancia> estanciasPeregrino = dao.obtenerEstanciaPorIdPeregrino(1L);
//		for(Estancia e : estanciasPeregrino) {
//			System.out.println(e);
//		}
//	}
}
