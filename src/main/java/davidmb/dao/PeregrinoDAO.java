package davidmb.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import davidmb.models.Carnet;
import davidmb.models.Parada;
import davidmb.models.Peregrino;

public class PeregrinoDAO {

	ConexionDB con = ConexionDB.getInstancia();
	

	public void insertar(Peregrino peregrino) {
	    // Consultas SQL
	    String sqlPeregrino = "INSERT INTO Peregrinos (nombre, nacionalidad, id_carnet, id_usuario) VALUES (?, ?, ?, ?)";
	    String sqlCarnet = "SELECT MAX(id_carnet) FROM CARNETS"; 
	    String sqlUsuario = "SELECT MAX(id_usuario) FROM USUARIOS"; 
	    
	    try (Connection connection = con.getConexion();
	         PreparedStatement carnetstmt = connection.prepareStatement(sqlCarnet);
	         PreparedStatement usuariostmt = connection.prepareStatement(sqlUsuario);
	         PreparedStatement peregrinostmt = connection.prepareStatement(sqlPeregrino)) {
	        
	        // Obtener el siguiente ID de carnet
	        int nextCarnetId = 1; 
	        try (ResultSet rs = carnetstmt.executeQuery()) {
	            if (rs.next()) {
	                nextCarnetId = rs.getInt(1) + 1; 
	            }
	        }
	        
	        // Obtener el siguiente ID de usuario
	        int nextUsuarioId = 1; 
	        try (ResultSet rs = usuariostmt.executeQuery()) {
	            if (rs.next()) {
	                nextUsuarioId = rs.getInt(1) + 1; 
	            }
	        }
	        
	        // Insertar el peregrino con los nuevos ID
	        peregrinostmt.setString(1, peregrino.getNombre());
	        peregrinostmt.setString(2, peregrino.getNacionalidad());
	        peregrinostmt.setLong(3, nextCarnetId); 
	        peregrinostmt.setLong(4, nextUsuarioId); 
	        
	        int rowsAffected = peregrinostmt.executeUpdate();
	        System.out.println("Peregrino insertado, filas afectadas: " + rowsAffected);
	        
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}


	public Peregrino obtenerPorId(Long id) {
	    String sqlPeregrino = "SELECT p.id AS peregrino_id, p.nombre, p.nacionalidad, p.id_carnet, p.id_usuario, " +
	                          "c.id AS carnet_id, c.parada_inicial, c.fechaexp, c.distancia, c.nvips, " +
	                          "pa.id AS parada_id, pa.nombre AS parada_nombre, pa.region, pa.responsable " +
	                          "FROM Peregrinos p " +
	                          "INNER JOIN Carnets c ON p.id_carnet = c.id " +
	                          "INNER JOIN Paradas pa ON c.parada_inicial = pa.id " +
	                          "WHERE p.id = ?";

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




	public List<Peregrino> obtenerTodos() {
	    String sql = "SELECT p.id AS peregrino_id, p.nombre, p.nacionalidad, p.id_carnet, p.id_usuario, " +
	                 "c.id AS carnet_id, c.parada_inicial, c.fechaexp, c.distancia, c.nvips, " +
	                 "pa.id AS parada_id, pa.nombre AS parada_nombre, pa.region, pa.responsable " +
	                 "FROM Peregrinos p " +
	                 "INNER JOIN Carnets c ON p.id_carnet = c.id " +
	                 "INNER JOIN Paradas pa ON c.parada_inicial = pa.id";
	    
	    List<Peregrino> peregrinos = new ArrayList<>();
	    
	    try (Connection connection = con.getConexion(); 
	         PreparedStatement stmt = connection.prepareStatement(sql); 
	         ResultSet rs = stmt.executeQuery()) {
	        
	        while (rs.next()) {
	            Peregrino peregrino = new Peregrino();
	            peregrino.setId(rs.getLong("peregrino_id"));
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
	            
	            // Asignar la parada inicial al carnet
	            carnet.setParadaInicial(parada);
	            
	            // Asignar el carnet y la parada al peregrino
	            peregrino.setCarnet(carnet);
	            
	            // Agregar el peregrino a la lista
	            peregrinos.add(peregrino);
	        }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return peregrinos;
    }
	
	public static void main(String[] args) {
		PeregrinoDAO peregrino = new PeregrinoDAO();
		Peregrino p = peregrino.obtenerPorId(1L);
//		List<Peregrino> listaPeregrinos = peregrino.obtenerTodos();
//		for(Peregrino p : listaPeregrinos) {
//			System.out.println(p);
//		}
		System.out.println(p);
		
		
	}

    
 
}
