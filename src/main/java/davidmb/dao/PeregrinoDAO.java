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
		System.out.println(peregrino);

		try (Connection connection = con.getConexion();
				
				PreparedStatement peregrinostmt = connection.prepareStatement(sqlPeregrino)) {		

			// Insertar el peregrino con los nuevos ID
			peregrinostmt.setString(1, peregrino.getNombre());
			peregrinostmt.setString(2, peregrino.getNacionalidad());
			peregrinostmt.setLong(3, peregrino.getCarnet().getId());
			peregrinostmt.setLong(4, peregrino.getIdUsuario());

			int rowsAffected = peregrinostmt.executeUpdate();
			System.out.println("Peregrino insertado, filas afectadas: " + rowsAffected);
			return Optional.of(peregrino.getIdUsuario());

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return Optional.empty();
	}

	public Optional<Peregrino> obtenerPeregrinoPorId(Long id) {
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

		return Optional.ofNullable(peregrino);
	}

	public Optional<Peregrino> obtenerPeregrinoPorIdUsuario(Long id) {
		String sqlPeregrino = "SELECT p.id AS peregrino_id, p.nombre, p.nacionalidad, p.id_carnet, p.id_usuario, "
				+ "c.id AS carnet_id, c.parada_inicial, c.fechaexp, c.distancia, c.nvips, "
				+ "pa.id AS parada_id, pa.nombre AS parada_nombre, pa.region, pa.responsable " + "FROM Peregrinos p "
				+ "INNER JOIN Carnets c ON p.id_carnet = c.id " + "INNER JOIN Paradas pa ON c.parada_inicial = pa.id "
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

		return Optional.ofNullable(peregrino);
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

	public boolean peregrinoExiste(Long id) {
		String sql = "SELECT * FROM Peregrinos WHERE id = ?";
		try (Connection connection = con.getConexion(); 
			PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setLong(1, id);
			try (ResultSet rs = stmt.executeQuery()) {
				return rs.next();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean nombrePeregrinoExiste(String nombre) {
		String sql = "SELECT * FROM Peregrinos WHERE nombre = ?";
		try (Connection connection = con.getConexion(); 
			PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setString(1, nombre);
			try (ResultSet rs = stmt.executeQuery()) {
				return rs.next();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

}
