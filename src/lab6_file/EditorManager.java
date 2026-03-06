package lab6_file;

import java.awt.Color;
import java.awt.Component;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigInteger;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;

public class EditorManager {

    private JTextPane editor;

    public EditorManager(JTextPane editor) {
        this.editor = editor;
    }

    // =============================
    // MÉTODO PRINCIPAL
    // =============================

    public void guardarDocumento(String ruta) {

        try {

            XWPFDocument documento = crearDocumento(ruta);

            escribirContenido(documento);

            guardarArchivo(documento, ruta);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // =============================
    // CREAR O ABRIR DOCUMENTO
    // =============================

    private XWPFDocument crearDocumento(String ruta) throws Exception {

        // Siempre crear un documento nuevo para evitar fusión de contenidos
        return new XWPFDocument();

    }

    // =============================
    // ESCRIBIR CONTENIDO
    // =============================

    private void escribirContenido(XWPFDocument documento) throws Exception {

        StyledDocument doc = editor.getStyledDocument();

        XWPFParagraph parrafo = documento.createParagraph();

        for (int i = 0; i < doc.getLength(); i++) {

            AttributeSet attr = obtenerAtributos(doc, i);
            
            // Verificar si hay un componente embebido (tabla)
            Component comp = StyleConstants.getComponent(attr);
            
            if (comp != null) {
                // Es una tabla embebida
                if (comp instanceof JScrollPane) {
                    JScrollPane scroll = (JScrollPane) comp;
                    Component vista = scroll.getViewport().getView();
                    if (vista instanceof JTable) {
                        JTable tabla = (JTable) vista;
                        crearTablaDocx(documento, tabla);
                        parrafo = documento.createParagraph(); // Nuevo párrafo después de la tabla
                    }
                }
                continue; // Saltar el carácter del componente
            }

            String letra = obtenerCaracter(doc, i);

            XWPFRun run = crearRun(parrafo);

            aplicarEstilos(run, attr);

            run.setText(letra);

        }

    }

    // =============================
    // CREAR TABLA DOCX
    // =============================

    private void crearTablaDocx(XWPFDocument documento, JTable tabla) {
        
        int filas = tabla.getRowCount();
        int cols = tabla.getColumnCount();
        
        XWPFTable tablaDocx = documento.createTable(filas, cols);
        
        // Configurar ancho de tabla (100% del ancho de página)
        CTTblWidth tblWidth = tablaDocx.getCTTbl().addNewTblPr().addNewTblW();
        tblWidth.setType(STTblWidth.PCT);
        tblWidth.setW(BigInteger.valueOf(5000)); // 100% = 5000
        
        // Llenar contenido de la tabla
        for (int i = 0; i < filas; i++) {
            XWPFTableRow fila = tablaDocx.getRow(i);
            for (int j = 0; j < cols; j++) {
                XWPFTableCell celda = fila.getCell(j);
                Object valor = tabla.getValueAt(i, j);
                String texto = (valor != null) ? valor.toString() : "";
                
                // Limpiar contenido previo y agregar nuevo
                if (celda.getParagraphs().size() > 0) {
                    celda.removeParagraph(0);
                }
                XWPFParagraph parrafoCelda = celda.addParagraph();
                XWPFRun runCelda = parrafoCelda.createRun();
                runCelda.setText(texto);
            }
        }
        
    }

    // =============================
    // OBTENER CARACTER
    // =============================

    private String obtenerCaracter(StyledDocument doc, int posicion) throws Exception {
        return doc.getText(posicion, 1);
    }

    // =============================
    // OBTENER ATRIBUTOS
    // =============================

    private AttributeSet obtenerAtributos(StyledDocument doc, int posicion) {
        return doc.getCharacterElement(posicion).getAttributes();
    }

    // =============================
    // CREAR RUN
    // =============================

    private XWPFRun crearRun(XWPFParagraph parrafo) {
        return parrafo.createRun();
    }

    // =============================
    // APLICAR ESTILOS
    // =============================

    private void aplicarEstilos(XWPFRun run, AttributeSet attr) {

        aplicarFuente(run, attr);
        aplicarTamano(run, attr);
        aplicarColor(run, attr);
        aplicarNegrita(run, attr);
        aplicarCursiva(run, attr);
        aplicarSubrayado(run, attr);

    }

    private void aplicarFuente(XWPFRun run, AttributeSet attr) {

        String fuente = StyleConstants.getFontFamily(attr);
        run.setFontFamily(fuente);

    }

    private void aplicarTamano(XWPFRun run, AttributeSet attr) {

        int size = StyleConstants.getFontSize(attr);
        run.setFontSize(size);

    }

    private void aplicarColor(XWPFRun run, AttributeSet attr) {

        Color color = StyleConstants.getForeground(attr);

        if (color != null) {
            String hex = convertirColorHex(color);
            run.setColor(hex);
        }

    }

    private void aplicarNegrita(XWPFRun run, AttributeSet attr) {
        run.setBold(StyleConstants.isBold(attr));
    }

    private void aplicarCursiva(XWPFRun run, AttributeSet attr) {
        run.setItalic(StyleConstants.isItalic(attr));
    }

    private void aplicarSubrayado(XWPFRun run, AttributeSet attr) {

        if (StyleConstants.isUnderline(attr)) {
            run.setUnderline(UnderlinePatterns.SINGLE);
        }

    }

    // =============================
    // COLOR HEX
    // =============================

    private String convertirColorHex(Color color) {

        return String.format("%02X%02X%02X",
                color.getRed(),
                color.getGreen(),
                color.getBlue());

    }

    // =============================
    // GUARDAR ARCHIVO
    // =============================

    private void guardarArchivo(XWPFDocument documento, String ruta) throws Exception {

        FileOutputStream out = new FileOutputStream(ruta);

        documento.write(out);

        out.close();

        documento.close();

    }

}