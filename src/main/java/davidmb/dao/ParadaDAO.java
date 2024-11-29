package davidmb.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import davidmb.models.Carnet;
import davidmb.models.Parada;
import davidmb.models.Peregrino;

/**
 * DAO (Data Access Object) para gestionar las operaciones relacionadas con las
 * paradas.
 * 
 * <p>
 * Esta clase proporciona métodos para insertar y obtener paradas en la base de
 * datos.
 * </p>
 */
public class ParadaDAO {

	ConexionDB con = ConexionDB.getInstancia();

	/**
	 * Inserta una nueva parada en la base de datos.
	 * 
	 * <p>
	 * Este método inserta una parada en la base de datos y devuelve el ID de la
	 * parada insertada.
	 * </p>
	 * 
	 * @param parada la parada que se desea insertar.
	 * @return un {@link Optional} que contiene el ID de la parada insertada si la
	 *         operación es exitosa, o un {@link Optional} vacío si la inserción
	 *         falla.
	 */
	public Optional<Long> insertarParada(Parada parada) {
		String sqlParadas = "INSERT INTO Paradas (nombre, region, responsable, id_usuario) VALUES (?, ?, ?, ?)";

		try (Connection connection = con.getConexion();

		) {
			connection.setAutoCommit(false);

			try (PreparedStatement paradaStmt = connection.prepareStatement(sqlParadas);) {

				paradaStmt.setString(1, parada.getNombre());
				paradaStmt.setString(2, String.valueOf(parada.getRegion()));
				paradaStmt.setString(3, parada.getResponsable());
				paradaStmt.setLong(4, parada.getIdUsuario());

				paradaStmt.executeUpdate();

				connection.commit();
				return Optional.of(parada.getIdUsuario());

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
	 * Obtiene una parada de la base de datos por su ID.
	 * 
	 * <p>
	 * Este método obtiene una parada de la base de datos por su ID.
	 * </p>
	 * 
	 * @param id el ID de la parada que se desea obtener.
	 * @return un {@link Optional} que contiene la parada si existe, o un
	 *         {@link Optional} vacío si no existe.
	 */
	public Optional<Parada> obtenerParadaPorId(Long id) {
		String sql = "SELECT * FROM Paradas WHERE id = ?";
		Parada parada = null;
		try (Connection connection = con.getConexion(); PreparedStatement stmt = connection.prepareStatement(sql);) {
			stmt.setLong(1, id);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					parada = new Parada();
					parada.setId(rs.getLong("id"));
					parada.setNombre(rs.getString("nombre"));
					parada.setRegion(rs.getString("region").charAt(0));
					parada.setResponsable(rs.getString("responsable"));
				}
			}

		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return Optional.ofNullable(parada);
	}

	/**
	 * Obtiene una parada de la base de datos por el ID de su usuario.
	 * 
	 * <p>
	 * Este método obtiene una parada de la base de datos por el ID de su usuario.
	 * </p>
	 * 
	 * @param id el ID del usuario asociado a la parada que se desea obtener.
	 * @return un {@link Optional} que contiene la parada si existe, o un
	 *         {@link Optional} vacío si no existe.
	 */
	public Optional<Parada> obtenerParadaPorIdUsuario(Long id) {
		String paradaSql = "SELECT * FROM Paradas WHERE id_usuario = ?";

		Parada parada = null;

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
			e.printStackTrace();
		}
		return Optional.ofNullable(parada);
	}

	/**
	 * Obtiene una lista de paradas asociadas a un peregrino según su ID.
	 * 
	 * <p>
	 * Este método obtiene las paradas asociadas al ID de un peregrino específico.
	 * </p>
	 * 
	 * @param id el ID del peregrino cuyas paradas se desean obtener.
	 * @return una lista de paradas asociadas al peregrino.
	 */
	public List<Parada> obtenerParadasPorIdPeregrino(Long idPeregrino) {
		String sql = "SELECT * \r\n" + "FROM Peregrinos_paradas \r\n"
				+ "INNER JOIN Paradas ON Peregrinos_paradas.id_parada = Paradas.id\r\n"
				+ "WHERE Peregrinos_paradas.id_peregrino = ?\r\n" + "ORDER BY Fecha ASC";
		List<Parada> paradas = new ArrayList<>();

		try (Connection connection = con.getConexion(); PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setLong(1, idPeregrino);
			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					Parada parada = new Parada();
					parada.setId(rs.getLong("id"));
					parada.setNombre(rs.getString("nombre"));
					parada.setRegion(rs.getString("region").charAt(0));
					parada.setResponsable(rs.getString("responsable"));
					paradas.add(parada);
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return paradas;
	}

	/**
	 * Inserta una relación entre un peregrino y una parada en la base de datos.
	 * 
	 * <p>
	 * Este método inserta una relación entre un peregrino y una parada en la base
	 * de datos y devuelve el ID del peregrino insertado.
	 * </p>
	 * 
	 * @param idPeregrino el ID del peregrino.
	 * @param idParada    el ID de la parada.
	 * @param fecha       la fecha en la que el peregrino visitó la parada.
	 * @return un {@link Optional} que contiene el ID del peregrino si la operación
	 *         es exitosa, o un {@link Optional} vacío si la inserción falla.
	 */
	public Optional<Long> insertarPeregrinosParadas(Long idPeregrino, Long idParada, LocalDate fecha) {
		String sql = "INSERT INTO Peregrinos_paradas (id_peregrino, id_parada, fecha) VALUES (?, ?, ?)";
		try (Connection connection = con.getConexion();) {
			connection.setAutoCommit(false);
			try (PreparedStatement stmt = connection.prepareStatement(sql);) {
				stmt.setLong(1, idPeregrino);
				stmt.setLong(2, idParada);
				stmt.setDate(3, Date.valueOf(fecha));
				int rowsAffected = stmt.executeUpdate();
				if (rowsAffected > 0) {

					return Optional.of(idPeregrino);
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
	 * Obtiene una parada de la base de datos por su nombre.
	 * 
	 * <p>
	 * Este método obtiene una parada de la base de datos por su nombre.
	 * </p>
	 * 
	 * @param nombre el nombre de la parada que se desea obtener.
	 * @return un {@link Optional} que contiene la parada si existe, o un
	 *         {@link Optional} vacío si no existe
	 * 
	 */
	public Optional<Parada> obtenerParadaPorNombre(String nombre) {
		String paradaSql = "SELECT * FROM Paradas WHERE nombre = ?";

		Parada parada = null;

		try (Connection connection = con.getConexion();
				PreparedStatement paradaStmt = connection.prepareStatement(paradaSql);) {
			paradaStmt.setString(1, nombre);
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
			e.printStackTrace();
		}
		return Optional.ofNullable(parada);
	}

	/**
	 * Obtiene una lista de peregrinos asociados a una parada según su ID.
	 * 
	 * <p>
	 * Este método obtiene los peregrinos asociados al ID de una parada específica.
	 * </p>
	 * 
	 * @param idParada el ID de la parada cuyos peregrinos se desean obtener.
	 * @return una lista de peregrinos asociados a la parada.
	 */
	public List<Peregrino> obtenerPeregrinosParada(Long idParada) {

		String sql = "SELECT p.id AS peregrino_id, p.nombre, p.nacionalidad, p.id_usuario, "
				+ "c.id AS carnet_id, c.fechaexp, c.distancia, c.nvips, "
				+ "pa.id AS parada_inicial_id, pa.nombre AS parada_inicial_nombre, pa.region, pa.responsable "
				+ "FROM Peregrinos p " + "INNER JOIN Carnets c ON p.id_carnet = c.id "
				+ "INNER JOIN Paradas pa ON c.parada_inicial = pa.id "
				+ "INNER JOIN Peregrinos_paradas pp ON pp.id_peregrino = p.id " + "WHERE pp.id_parada = ?";

		String sqlEstancias = "SELECT id FROM Estancias WHERE id_peregrino = ?";

		String sqlParadas = "SELECT id_parada FROM Peregrinos_paradas WHERE id_peregrino = ?";

		String sqlPeregrinosParadaInicial = "SELECT p.id, p.nombre, p.nacionalidad, p.id_usuario "
				+ "FROM Peregrinos p " + "INNER JOIN Peregrinos_paradas pp ON pp.id_peregrino = p.id "
				+ "WHERE pp.id_parada = ?";

		List<Peregrino> peregrinos = new ArrayList<>();

		try (Connection connection = con.getConexion(); PreparedStatement stmt = connection.prepareStatement(sql)) {

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
					try (PreparedStatement paradaInicialStmt = connection
							.prepareStatement(sqlPeregrinosParadaInicial)) {
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
			e.printStackTrace();
		}

		return peregrinos;
	}

	/**
	 * Obtiene una lista de todas las paradas de la base de datos.
	 * 
	 * <p>
	 * Este método obtiene una lista de todas las paradas de la base de datos.
	 * </p>
	 * 
	 * @return una lista de todas las paradas de la base de datos
	 * 
	 */
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
			e.printStackTrace();
		}
		return paradas;
	}

	/**
	 * Comprueba si una parada ya existe.
	 * 
	 * <p>
	 * Este método comprueba si una parada ya existe, si es el administrador tiene en cuenta
	 * el nombre y la región, esto permite crear paradas con mismo nombre pero en diferentes regiones
	 * si no es admin solo se tiene en cuenta el nombre, para validar qeu el usuario introduce una parada existente
	 * al registrarse como peregrino.
	 * </p>
	 * 
	 * @param nombre el nombre de la parada
	 * @param region la región de la parada
	 * @param esAdmin boolean para comprobar si el método se utiliza en un admin o un peregrino
	 * @return boolean true si la parada existe o false en caso contrario.
	 */
	public boolean paradaExiste(String nombre, String region, boolean esAdmin) {
		boolean existe = false;
		String sql = "";
		if (esAdmin) {
			sql = "SELECT * FROM Paradas WHERE nombre = ? AND region = ?";
		} else {
			sql = "SELECT * FROM Paradas WHERE nombre = ?";
		}
		try (Connection connection = con.getConexion(); PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setString(1, nombre);
			if (esAdmin) {
				stmt.setString(2, region);
			}
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					existe = true;

				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return existe;
	}

}
