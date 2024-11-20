package davidmb.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import davidmb.models.Carnet;
import davidmb.models.Parada;
import davidmb.models.Peregrino;

public class ParadaDAO {

	ConexionDB con = ConexionDB.getInstancia();
	Logger logger = Logger.getLogger(ParadaDAO.class.getName());

	public Optional<Long> insertarParada(Parada parada) {
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

	public Optional<Parada> obtenerParadaPorIdUsuario(Long id) {
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
		return Optional.ofNullable(parada);
	}

	public List<Peregrino> obtenerPeregrinosParada(Long idParada) {
	
	    String sql = "SELECT p.id AS peregrino_id, p.nombre, p.nacionalidad, p.id_usuario, "
	               + "c.id AS carnet_id, c.fechaexp, c.distancia, c.nvips, "
	               + "pa.id AS parada_inicial_id, pa.nombre AS parada_inicial_nombre, pa.region, pa.responsable "
	               + "FROM Peregrinos p "
	               + "INNER JOIN Carnets c ON p.id_carnet = c.id "
	               + "INNER JOIN Paradas pa ON c.parada_inicial = pa.id "
	               + "INNER JOIN Peregrinos_paradas pp ON pp.id_peregrino = p.id "
	               + "WHERE pp.id_parada = ?";
	    
	   
	    String sqlEstancias = "SELECT id FROM Estancias WHERE id_peregrino = ?";

	
	    String sqlParadas = "SELECT id_parada FROM Peregrinos_paradas WHERE id_peregrino = ?";

	    String sqlPeregrinosParadaInicial = "SELECT p.id, p.nombre, p.nacionalidad, p.id_usuario "
	                                      + "FROM Peregrinos p "
	                                      + "INNER JOIN Peregrinos_paradas pp ON pp.id_peregrino = p.id "
	                                      + "WHERE pp.id_parada = ?";

	    List<Peregrino> peregrinos = new ArrayList<>();

	    try (Connection connection = con.getConexion();
	         PreparedStatement stmt = connection.prepareStatement(sql)) {

	        stmt.setLong(1, idParada);
	        try (ResultSet rs = stmt.executeQuery()) {
	            while (rs.next()) {
	               
	                Peregrino peregrino = new Peregrino();
	                peregrino.setId(rs.getLong("peregrino_id"));
	                peregrino.setNombre(rs.getString("nombre"));
	                peregrino.setNacionalidad(rs.getString("nacionalidad"));
	                peregrino.setIdUsuario(rs.getLong("id_usuario"));

	              
	                Carnet carnet = new Carnet();
	                carnet.setId(rs.getLong("carnet_id"));
	                carnet.setFechaExp(rs.getDate("fechaexp").toLocalDate());
	                carnet.setDistancia(rs.getDouble("distancia"));
	                carnet.setnVips(rs.getInt("nvips"));

	              
	                Parada paradaInicial = new Parada();
	                paradaInicial.setId(rs.getLong("parada_inicial_id"));
	                paradaInicial.setNombre(rs.getString("parada_inicial_nombre"));
	                paradaInicial.setRegion(rs.getString("region").charAt(0));
	                paradaInicial.setResponsable(rs.getString("responsable"));

	              
	                List<Long> peregrinosParadaInicial = new ArrayList<>();
	                try (PreparedStatement paradaInicialStmt = connection.prepareStatement(sqlPeregrinosParadaInicial)) {
	                    paradaInicialStmt.setLong(1, paradaInicial.getId());
	                    try (ResultSet rsPeregrinosParadaInicial = paradaInicialStmt.executeQuery()) {
	                        while (rsPeregrinosParadaInicial.next()) {
	                            Long idPeregrinoParadaInicial = rsPeregrinosParadaInicial.getLong("id");

	                            peregrinosParadaInicial.add(idPeregrinoParadaInicial);
	                        }
	                    }
	                }
	                paradaInicial.setPeregrinos(peregrinosParadaInicial);
	                carnet.setParadaInicial(paradaInicial);

	                peregrino.setCarnet(carnet);

	              
	                List<Long> estancias = new ArrayList<>();
	                try (PreparedStatement estanciasStmt = connection.prepareStatement(sqlEstancias)) {
	                    estanciasStmt.setLong(1, peregrino.getId());
	                    try (ResultSet rsEstancias = estanciasStmt.executeQuery()) {
	                        while (rsEstancias.next()) {
	                            estancias.add(rsEstancias.getLong("id"));
	                        }
	                    }
	                }
	                peregrino.setEstancias(estancias);

	               
	                List<Long> paradasVisitadas = new ArrayList<>();
	                try (PreparedStatement paradasStmt = connection.prepareStatement(sqlParadas)) {
	                    paradasStmt.setLong(1, peregrino.getId());
	                    try (ResultSet rsParadas = paradasStmt.executeQuery()) {
	                        while (rsParadas.next()) {
	                            paradasVisitadas.add(rsParadas.getLong("id_parada"));
	                        }
	                    }
	                }
	                peregrino.setParadas(paradasVisitadas);

	             
	                peregrinos.add(peregrino);
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


//	public static void main(String args[]) {
//		ParadaDAO dao = new ParadaDAO();
//
//		List<Peregrino> peregrinos = dao.obtenerPeregrinosParada(1L);
//		for (Peregrino p : peregrinos) {
//			System.out.println(p);
//		}
//		List<Parada> paradas = dao.obtenerTodasParadas();
//		for (Parada p : paradas) {
//			System.out.println(p);
//		}
//	}

}
