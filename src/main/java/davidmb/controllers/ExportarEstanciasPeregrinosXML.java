package davidmb.controllers;

import java.io.File;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import davidmb.models.Estancia;
import davidmb.models.Parada;
import davidmb.models.Peregrino;


/**
 * Clase encargada de la exportación de las estancias de los peregrinos a un archivo XML.
 * 
 * <p>Esta clase permite exportar las estancias asociadas a una parada específica dentro de un rango de fechas
 * dado, generando un archivo XML con la información relevante sobre las estancias y los peregrinos.</p>
 */
public class ExportarEstanciasPeregrinosXML {
	
	private LocalDate fechaInicio;
	private LocalDate fechaFin;
	private Parada parada;

	  /**
     * Constructor por defecto.
     */
	public ExportarEstanciasPeregrinosXML() {
		super();
	}

	/**
     * Constructor que inicializa la exportación con los parámetros de fecha y parada específicos.
     * 
     * @param fechaInicio la fecha de inicio del rango de exportación.
     * @param fechaFin la fecha de fin del rango de exportación.
     * @param parada la parada cuyas estancias se desean exportar.
     */
	public ExportarEstanciasPeregrinosXML(LocalDate fechaInicio, LocalDate fechaFin, Parada parada) {
		super();
		this.fechaInicio = fechaInicio;
		this.fechaFin = fechaFin;
		this.parada = parada;
	}

	/**
     * Exporta las estancias de los peregrinos asociadas a una parada en el rango de fechas especificado a un archivo XML.
     * 
     * <p>Este método obtiene las estancias de la parada en el rango de fechas proporcionado, luego construye un
     * archivo XML con la siguiente estructura:
     * <ul>
     *   <li>ID de la parada</li>
     *   <li>Nombre de la parada</li>
     *   <li>Región de la parada</li>
     *   <li>Fechas de inicio y fin</li>
     *   <li>Información de cada estancia</li>
     * </ul>
     * Cada estancia incluye el ID, el nombre del peregrino, la fecha y si fue una estancia VIP.</p>
     * 
     * @throws Exception si ocurre un error durante el proceso de exportación, como problemas al generar el XML o al obtener datos.
     */
	public void exportarEstancias() throws Exception {
		ControladorPrincipal sistema = new ControladorPrincipal();
		List<Estancia> estanciasParada = sistema.obtenerEstanciasPorIdParada(parada.getId());

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.newDocument();

		Element paradaElem = doc.createElement("parada");
		doc.appendChild(paradaElem);

		// ID
		Element idElem = doc.createElement("id");
		idElem.setTextContent(String.valueOf(parada.getId()));
		paradaElem.appendChild(idElem);

		// Nombre parada
		Element nombreParadaElem = doc.createElement("parada");
		nombreParadaElem.setTextContent(parada.getNombre());
		paradaElem.appendChild(nombreParadaElem);

		// Región parada
		Element regionElem = doc.createElement("region");
		regionElem.setTextContent(String.valueOf(parada.getRegion()));
		paradaElem.appendChild(regionElem);

		// Rango de fechas
		Element fechaIniElem = doc.createElement("fecha-inicio");
		fechaIniElem.setTextContent(fechaInicio.toString());
		paradaElem.appendChild(fechaIniElem);

		Element fechaFinElem = doc.createElement("fecha-fin");
		fechaFinElem.setTextContent(fechaFin.toString());
		paradaElem.appendChild(fechaFinElem);

		// Estancias
		Element estanciasElem = doc.createElement("estancias");
		paradaElem.appendChild(estanciasElem);

		// Peregrinos de la parada

		/**
		 * posteriormente la lista de las estancias de los peregrinos en ese periodo,
		 * indicando el id de la estancia, el nombre del peregrino, la fecha en que se
		 * realizó y si fue de tipo VIP o no.
		 */

		for (Estancia e : estanciasParada) {
			Optional<Peregrino> peregrinoOptional = sistema.obtenerPeregrinoPorId(e.getPeregrino());
			Peregrino p = null;

			if (peregrinoOptional.isPresent()) {
				p = peregrinoOptional.get();
			}

			if((e.getFecha().minusDays(-1).isAfter(fechaInicio)) && e.getFecha().isBefore(fechaFin)) {
				Element estanciaElem = doc.createElement("estancia");

				Element idEstanciaElem = doc.createElement("id");
				idEstanciaElem.setTextContent(String.valueOf(e.getId()));
				estanciaElem.appendChild(idEstanciaElem);

				Element peregrinoElem = doc.createElement("peregrino");
				peregrinoElem.setTextContent(p.getNombre());
				estanciaElem.appendChild(peregrinoElem);

				Element fechaEstanciaElem = doc.createElement("fecha");
				fechaEstanciaElem.setTextContent(e.getFecha().toString());
				estanciaElem.appendChild(fechaEstanciaElem);

				Element vipElem = doc.createElement("vip");
				vipElem.setTextContent(e.isVip() ? "Sí" : "No");
				estanciaElem.appendChild(vipElem);

				estanciasElem.appendChild(estanciaElem);
			}

		}

		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		DOMSource source = new DOMSource(doc);

		StreamResult result = new StreamResult(new File("exports/estancias/" + parada.getNombre() + ".xml"));
		transformer.transform(source, result);

		JOptionPane.showMessageDialog(null, "Estancias exportadas con éxito en formato XML.");

	}
}
