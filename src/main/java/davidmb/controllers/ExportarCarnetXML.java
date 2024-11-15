//package davidmb.controllers;
//
//import java.io.File;
//import java.time.LocalDate;
//
//import javax.swing.JOptionPane;
//import javax.xml.parsers.DocumentBuilder;
//import javax.xml.parsers.DocumentBuilderFactory;
//import javax.xml.transform.OutputKeys;
//import javax.xml.transform.Transformer;
//import javax.xml.transform.TransformerFactory;
//import javax.xml.transform.dom.DOMSource;
//import javax.xml.transform.stream.StreamResult;
//
//import org.w3c.dom.Document;
//import org.w3c.dom.Element;
//
//import davidmb.models.Estancia;
//import davidmb.models.Parada;
//import davidmb.models.Peregrino;
//
///**
// * La clase ExportarCarnetXML se encarga de generar un archivo XML que contiene 
// * los datos de un objeto Peregrino, incluyendo su carnet, paradas y estancias.
// * El archivo se exporta a una ubicación en el sistema de archivos, y notifica
// * al usuario al finalizar.
// */
//public class ExportarCarnetXML {
//
//	 /**
//     * Exporta los datos de un peregrino a un archivo XML en el sistema de archivos.
//     * El archivo contiene la información del carnet, las paradas y las estancias 
//     * asociadas al peregrino.
//     *
//     * @param peregrino El objeto Peregrino que se exportará en formato XML.
//     * @throws Exception Si ocurre un error durante la creación del archivo XML.
//     */
//    public void exportarCarnet(Peregrino peregrino) throws Exception {
//       
//        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//        DocumentBuilder builder = factory.newDocumentBuilder();
//        Document doc = builder.newDocument();
//
//        // ------------------- Crear el elemento carnet ----------------
//        Element carnetElem = doc.createElement("carnet");
//        doc.appendChild(carnetElem);
//
//        // ------------------- Crear los elementos hijos de carnet ----------------
//        
//        
//        // ID
//        Element idElem = doc.createElement("id");
//        idElem.setTextContent(String.valueOf(peregrino.getId()));
//        carnetElem.appendChild(idElem);
//
//        // Fecha de expedición
//        Element fechaExpElem = doc.createElement("fechaexp");
//        fechaExpElem.setTextContent(peregrino.getCarnet().getFechaExp().toString());
//        carnetElem.appendChild(fechaExpElem);
//
//        // Parada de expedición
//        Element expedidoEn = doc.createElement("expedidoen");
//        expedidoEn.setTextContent(peregrino.getCarnet().getParadaInicial().getNombre());
//        carnetElem.appendChild(expedidoEn);
//        
//        // Elemento Peregrino
//        Element peregrinoElem = doc.createElement("peregrino");
//        
//        // Nombre
//        Element nombreElem = doc.createElement("nombre");
//        nombreElem.setTextContent(peregrino.getNombre());
//        
//        // Nacionalidad
//        Element nacionalidadElem = doc.createElement("nacionalidad");
//        nacionalidadElem.setTextContent(peregrino.getNacionalidad());
//        peregrinoElem.appendChild(nombreElem);
//        peregrinoElem.appendChild(nacionalidadElem);
//        carnetElem.appendChild(peregrinoElem);
//
//        // Fecha actual
//        Element hoyElem = doc.createElement("hoy");
//        hoyElem.setTextContent(LocalDate.now().toString());
//        carnetElem.appendChild(hoyElem);
//
//        // Distancia total
//        Element distanciaElem = doc.createElement("distanciatotal");
//        distanciaElem.setTextContent(String.format("%.1f", peregrino.getCarnet().getDistancia()));
//        carnetElem.appendChild(distanciaElem);
//
//        
//        Element paradasElem = doc.createElement("paradas");
//        for(int i = 0; i < peregrino.getParadas().size(); i++) {
//        	Parada parada = peregrino.getParadas().get(i);
//        	Element paradaElem = doc.createElement("parada");
//        	Element orden = doc.createElement("orden");
//        	orden.setTextContent(String.valueOf(i + 1));
//        	paradaElem.appendChild(orden);
//        	Element nombreParada = doc.createElement("nombre");
//        	nombreParada.setTextContent(parada.getNombre());
//        	paradaElem.appendChild(nombreParada);
//        	Element region = doc.createElement("region");
//        	region.setTextContent(String.valueOf(parada.getRegion()));
//        	paradaElem.appendChild(region);
//        	paradasElem.appendChild(paradaElem);
//        }
//        carnetElem.appendChild(paradasElem);
//
//       
//        Element estanciasElem = doc.createElement("estancias");
//        for (Estancia estancia : peregrino.getEstancias()) {
//            Element estanciaElem = doc.createElement("estancia");
//
//            Element idEstanciaElem = doc.createElement("id");
//            idEstanciaElem.setTextContent(String.valueOf(estancia.getId()));
//            estanciaElem.appendChild(idEstanciaElem);
//
//            Element fechaElem = doc.createElement("fecha");
//            fechaElem.setTextContent(estancia.getFecha().toString());
//            estanciaElem.appendChild(fechaElem);
//
//            Element paradaEstanciaElem = doc.createElement("parada");
//            paradaEstanciaElem.setTextContent(estancia.getParada().getNombre());
//            estanciaElem.appendChild(paradaEstanciaElem);
//
//           
//            if (estancia.isVip()) {
//                Element vip = doc.createElement("vip");
//                vip.setTextContent("Sí");
//                estanciaElem.appendChild(vip);
//            }
//
//            estanciasElem.appendChild(estanciaElem);
//        }
//        carnetElem.appendChild(estanciasElem);
//
//       
//        TransformerFactory transformerFactory = TransformerFactory.newInstance();
//        Transformer transformer = transformerFactory.newTransformer();
//        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
//        DOMSource source = new DOMSource(doc);
//        
//        StreamResult result = new StreamResult(new File("exports/" + peregrino.getNombre() + ".xml"));
//        transformer.transform(source, result);
//
//        JOptionPane.showMessageDialog(null, "Carnet exportado con éxito en formato XML.");
//    }
//}
