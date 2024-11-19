package davidmb.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import davidmb.models.Carnet;
import davidmb.models.Parada;
import davidmb.models.Peregrino;
import davidmb.models.Usuario;

public class PeregrinoDAO {

	ConexionDB con = ConexionDB.getInstancia();

	public Optional<Long> insertarPeregrino(Peregrino peregrino) {
		// Consultas SQL
		String sqlPeregrino = "INSERT INTO Peregrinos (nombre, nacionalidad, id_carnet, id_usuario) VALUES (?, ?, ?, ?)";
		String sqlCarnet = "SELECT MAX(id) FROM CARNETS";
		String sqlUsuario = "SELECT MAX(id) FROM USUARIOS";

		try (Connection connection = con.getConexion();
				PreparedStatement carnetstmt = connection.prepareStatement(sqlCarnet);
				PreparedStatement usuariostmt = connection.prepareStatement(sqlUsuario);
				PreparedStatement peregrinostmt = connection.prepareStatement(sqlPeregrino)) {

			// Obtener el siguiente ID de carnet
			Long nextCarnetId = 1L;
			try (ResultSet rs = carnetstmt.executeQuery()) {
				if (rs.next()) {
					nextCarnetId = rs.getLong(1);
				}
			} catch (SQLException e) {
				e.printStackTrace();

			}

			// Obtener el siguiente ID de usuario
			Long nextUsuarioId = 1L;
			try (ResultSet rs = usuariostmt.executeQuery()) {
				if (rs.next()) {
					nextUsuarioId = rs.getLong(1);
				}
			}

			// Insertar el peregrino con los nuevos ID
			peregrinostmt.setString(1, peregrino.getNombre());
			peregrinostmt.setString(2, peregrino.getNacionalidad());
			peregrinostmt.setLong(3, nextCarnetId);
			peregrinostmt.setLong(4, nextUsuarioId);

			int rowsAffected = peregrinostmt.executeUpdate();
			System.out.println("Peregrino insertado, filas afectadas: " + rowsAffected);
			return Optional.of(nextUsuarioId);

			// Al llamar al método debe manejar el Optional de la siguiente manera
//	        if (result.isPresent()) {
//	            System.out.println("Peregrino insertado con éxito. ID Usuario: " + result.get());
//	        } else {
//	            System.out.println("Error al insertar el peregrino.");
//	        }

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return Optional.empty();
	}

	public Peregrino obtenerPeregrinoPorId(Long id) {
		String sqlPeregrino = "SELECT p.id AS peregrino_id, p.nombre, p.nacionalidad, p.id_carnet, p.id_usuario, "
				+ "c.id AS carnet_id, c.parada_inicial, c.fechaexp, c.distancia, c.nvips, "
				+ "pa.id AS parada_id, pa.nombre AS parada_nombre, pa.region, pa.responsable " + "FROM Peregrinos p "
				+ "INNER JOIN Carnets c ON p.id_carnet = c.id " + "INNER JOIN Paradas pa ON c.parada_inicial = pa.id "
				+ "WHERE p.id = ?";

		String sqlEstancias = "SELECT id FROM Estancias WHERE id_peregrino = ?";

		String sqlParadas = "SELECT id_parada FROM Peregrinos_paradas WHERE id_peregrino = ?";

		Peregrino peregrino = null;
		Carnet carnet = null;
		Parada parada = null;
		List<Long> estancias = new ArrayList<>();
		List<Long> paradas = new ArrayList<>();

		try (Connection connection = con.getConexion()) {

			// Obtener el peregrino, carnet y parada inicial
			try (PreparedStatement stmt = connection.prepareStatement(sqlPeregrino)) {
				stmt.setLong(1, id);
				try (ResultSet rs = stmt.executeQuery()) {
					if (rs.next()) {
						peregrino = new Peregrino();
						peregrino.setId(rs.getLong("peregrino_id"));
						peregrino.setNombre(rs.getString("nombre"));
						peregrino.setNacionalidad(rs.getString("nacionalidad"));
						peregrino.setIdUsuario(rs.getLong("id_usuario"));

						carnet = new Carnet();
						carnet.setId(rs.getLong("carnet_id"));
						carnet.setFechaExp(rs.getDate("fechaexp").toLocalDate());
						carnet.setDistancia(rs.getDouble("distancia"));
						carnet.setnVips(rs.getInt("nvips"));

						parada = new Parada();
						parada.setId(rs.getLong("parada_id"));
						parada.setNombre(rs.getString("parada_nombre"));
						parada.setResponsable(rs.getString("responsable"));
						parada.setRegion(rs.getString("region").charAt(0));

						carnet.setParadaInicial(parada);
						peregrino.setCarnet(carnet);
					}
				}
			}

			// Obtener las estancias del peregrino
			try (PreparedStatement stmt = connection.prepareStatement(sqlEstancias)) {
				stmt.setLong(1, id);
				try (ResultSet rs = stmt.executeQuery()) {
					while (rs.next()) {
						estancias.add(rs.getLong("id"));
					}
				}
			}

			// Obtener las paradas del peregrino
			try (PreparedStatement stmt = connection.prepareStatement(sqlParadas)) {
				stmt.setLong(1, id);
				try (ResultSet rs = stmt.executeQuery()) {
					while (rs.next()) {
						paradas.add(rs.getLong("id_parada"));
					}
				}
			}

			// Asignar las estancias y paradas al peregrino
			peregrino.setEstancias(estancias);
			peregrino.setParadas(paradas);

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return peregrino;
	}

	public Peregrino obtenerPeregrinoPorIdUsuario(Long id) {
	    String sqlPeregrino = "SELECT p.id AS peregrino_id, p.nombre, p.nacionalidad, p.id_carnet, p.id_usuario, "
	            + "c.id AS carnet_id, c.parada_inicial, c.fechaexp, c.distancia, c.nvips, "
	            + "pa.id AS parada_id, pa.nombre AS parada_nombre, pa.region, pa.responsable "
	            + "FROM Peregrinos p "
	            + "INNER JOIN Carnets c ON p.id_carnet = c.id "
	            + "INNER JOIN Paradas pa ON c.parada_inicial = pa.id "
	            + "WHERE p.id_usuario = ?";

	    String sqlEstancias = "SELECT id FROM Estancias WHERE id_peregrino = ?";

	    String sqlParadas = "SELECT id_parada FROM Peregrinos_paradas WHERE id_peregrino = ?";

	    Peregrino peregrino = null;
	    Carnet carnet = null;
	    Parada parada = null;
	    List<Long> estancias = new ArrayList<>();
	    List<Long> paradas = new ArrayList<>();
	    Long idPeregrino = null;

	    try (Connection connection = con.getConexion()) {

	        // Obtener el peregrino, carnet y parada inicial
	        try (PreparedStatement stmt = connection.prepareStatement(sqlPeregrino)) {
	            stmt.setLong(1, id);
	            try (ResultSet rs = stmt.executeQuery()) {
	                if (rs.next()) {
	                    peregrino = new Peregrino();
	                    idPeregrino = rs.getLong("peregrino_id");
	                    peregrino.setId(idPeregrino);
	                    peregrino.setNombre(rs.getString("nombre"));
	                    peregrino.setNacionalidad(rs.getString("nacionalidad"));
	                    peregrino.setIdUsuario(rs.getLong("id_usuario"));

	                    carnet = new Carnet();
	                    carnet.setId(rs.getLong("carnet_id"));
	                    carnet.setFechaExp(rs.getDate("fechaexp").toLocalDate());
	                    carnet.setDistancia(rs.getDouble("distancia"));
	                    carnet.setnVips(rs.getInt("nvips"));

	                    parada = new Parada();
	                    parada.setId(rs.getLong("parada_id"));
	                    parada.setNombre(rs.getString("parada_nombre"));
	                    parada.setResponsable(rs.getString("responsable"));
	                    parada.setRegion(rs.getString("region").charAt(0));

	                    carnet.setParadaInicial(parada);
	                    peregrino.setCarnet(carnet);
	                }
	            }
	        }

	        if (idPeregrino != null) {
	            // Obtener las estancias del peregrino
	            try (PreparedStatement stmt = connection.prepareStatement(sqlEstancias)) {
	                stmt.setLong(1, idPeregrino);
	                try (ResultSet rs = stmt.executeQuery()) {
	                    while (rs.next()) {
	                        estancias.add(rs.getLong("id"));
	                    }
	                }
	            }

	            // Obtener las paradas del peregrino
	            try (PreparedStatement stmt = connection.prepareStatement(sqlParadas)) {
	                stmt.setLong(1, idPeregrino);
	                try (ResultSet rs = stmt.executeQuery()) {
	                    while (rs.next()) {
	                        paradas.add(rs.getLong("id_parada"));
	                    }
	                }
	            }

	            // Asignar las estancias y paradas al peregrino
	            peregrino.setEstancias(estancias);
	            peregrino.setParadas(paradas);
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	        
	    }

	    return peregrino;
	}


	public List<Peregrino> obtenerTodosPeregrinos() {
		String sqlPeregrino = "SELECT p.id AS peregrino_id, p.nombre, p.nacionalidad, p.id_carnet, p.id_usuario, "
				+ "c.id AS carnet_id, c.parada_inicial, c.fechaexp, c.distancia, c.nvips, "
				+ "pa.id AS parada_id, pa.nombre AS parada_nombre, pa.region, pa.responsable " + "FROM Peregrinos p "
				+ "INNER JOIN Carnets c ON p.id_carnet = c.id " + "INNER JOIN Paradas pa ON c.parada_inicial = pa.id";
		String sqlEstancias = "SELECT id FROM Estancias WHERE id_peregrino = ?";
		String sqlParadas = "SELECT id_parada FROM Peregrinos_paradas WHERE id_peregrino = ?";

		List<Peregrino> peregrinos = new ArrayList<>();

		try (Connection connection = con.getConexion();
				PreparedStatement stmt = connection.prepareStatement(sqlPeregrino);
				ResultSet rs = stmt.executeQuery()) {

			while (rs.next()) {
				Peregrino peregrino = new Peregrino();
				Long peregrinoId = rs.getLong("peregrino_id");
				List<Long> estancias = new ArrayList<>();
				List<Long> paradas = new ArrayList<>();
				peregrino.setId(peregrinoId);
				peregrino.setNombre(rs.getString("nombre"));
				peregrino.setNacionalidad(rs.getString("nacionalidad"));
				peregrino.setIdUsuario(rs.getLong("id_usuario"));

				// Obtener datos del carnet
				Carnet carnet = new Carnet();
				carnet.setId(rs.getLong("carnet_id"));
				carnet.setFechaExp(rs.getDate("fechaexp").toLocalDate());
				carnet.setDistancia(rs.getDouble("distancia"));
				carnet.setnVips(rs.getInt("nvips"));

				// Obtener datos de la parada inicial
				Parada parada = new Parada();
				parada.setId(rs.getLong("parada_id"));
				parada.setNombre(rs.getString("parada_nombre"));
				parada.setResponsable(rs.getString("responsable"));
				parada.setRegion(rs.getString("region").charAt(0));
				parada.setIdUsuario(rs.getLong("id_usuario"));

				// Asignar la parada inicial al carnet
				carnet.setParadaInicial(parada);

				// Asignar el carnet y la parada al peregrino
				peregrino.setCarnet(carnet);

				// Obtener las estancias del peregrino
				try (PreparedStatement estanciasStmt = connection.prepareStatement(sqlEstancias)) {
					estanciasStmt.setLong(1, peregrinoId);
					try (ResultSet estanciasRs = estanciasStmt.executeQuery()) {
						while (estanciasRs.next()) {
							estancias.add(estanciasRs.getLong("id"));
						}
					}
				}

				// Obtener las paradas del peregrino
				try (PreparedStatement paradasStmt = connection.prepareStatement(sqlParadas)) {
					paradasStmt.setLong(1, peregrinoId);
					try (ResultSet paradasRs = paradasStmt.executeQuery()) {
						while (paradasRs.next()) {
							paradas.add(paradasRs.getLong("id_parada"));
						}
					}
				}

				// Asignar las estancias y paradas al peregrino
				peregrino.setEstancias(estancias);
				peregrino.setParadas(paradas);

				// Agregar el peregrino a la lista
				peregrinos.add(peregrino);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return peregrinos;
	}

//	public static void main(String[] args) {
//		PeregrinoDAO peregrino = new PeregrinoDAO();
//		
//		Peregrino p = peregrino.obtenerPeregrinoPorIdUsuario(7L);
//		
//		System.out.println(p);
//		UsuarioDAO usuarioDAO = new UsuarioDAO();
//		CarnetDAO carnetDAO = new CarnetDAO();
//
//		Carnet carnet = new Carnet();
//		carnet.setParadaInicial(new Parada(1L, "Parada Inicial", 'N', "Responsable"));
//		carnetDAO.insertarCarnet(carnet);
//
//		Usuario usuario = new Usuario("david", "david", "peregrino");
//		usuarioDAO.insertar(usuario);
//
//		Peregrino p = new Peregrino("David", "Español", carnet, 0L);
//
//		Optional<Long> resultado = peregrino.insertarPeregrino(p);
//
//		if (resultado.isPresent()) {
//			System.out.println("El peregrino fue insertado correctamente." + resultado.get());
//		} else {
//			System.out.println("Hubo un error al insertar el peregrino.");
//		}
//		 Peregrino p = peregrino.obtenerPeregrinoPorId(1L);
//		List<Peregrino> listaPeregrinos = peregrino.obtenerTodosPeregrinos();
//		for(Peregrino p : listaPeregrinos) {
//			System.out.println(p);
//		}
//		 System.out.println(p);
//
//	}

}
