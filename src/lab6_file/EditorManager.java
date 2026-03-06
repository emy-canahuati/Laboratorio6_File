package lab6_file;

import java.awt.Color;
import java.awt.Component;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
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
        try (RandomAccessFile rafObj = new RandomAccessFile(ruta, "rw")) {
            rafObj.setLength(0); // Limpiar archivo previo
            StyledDocument doc = editor.getStyledDocument();
            int totalCaracteres = doc.getLength();

            rafObj.writeInt(totalCaracteres); // Guardar cuántas letras hay

            for (int i = 0; i < totalCaracteres; i++) {
                String letra = doc.getText(i, 1);
                AttributeSet attr = doc.getCharacterElement(i).getAttributes();

                // Escribir datos del carácter
                rafObj.writeChar(letra.charAt(0));
                rafObj.writeUTF(StyleConstants.getFontFamily(attr));
                rafObj.writeInt(StyleConstants.getFontSize(attr));
                rafObj.writeInt(StyleConstants.getForeground(attr).getRGB());
                rafObj.writeBoolean(StyleConstants.isBold(attr));
                rafObj.writeBoolean(StyleConstants.isItalic(attr));
                rafObj.writeBoolean(StyleConstants.isUnderline(attr));
            }
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
            Component comp = StyleConstants.getComponent(attr);
            
            if (comp != null) {
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

    private void crearTablaDocx(XWPFDocument documento, JTable tabla) {        
        int filas = tabla.getRowCount();
        int cols = tabla.getColumnCount();       
        XWPFTable tablaDocx = documento.createTable(filas, cols);
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


    private String obtenerCaracter(StyledDocument doc, int posicion) throws Exception {
        return doc.getText(posicion, 1);
    }

    private AttributeSet obtenerAtributos(StyledDocument doc, int posicion) {
        return doc.getCharacterElement(posicion).getAttributes();
    }

    private XWPFRun crearRun(XWPFParagraph parrafo) {
        return parrafo.createRun();
    }

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

    private String convertirColorHex(Color color) {
        return String.format("%02X%02X%02X",
                color.getRed(),
                color.getGreen(),
                color.getBlue());
    }


    private void guardarArchivo(XWPFDocument documento, String ruta) throws Exception {
        FileOutputStream out = null;
        
        try {
            out = new FileOutputStream(ruta);
            documento.write(out);
        } finally {
            if (out != null) {
                try {
                    out.flush();
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (documento != null) {
                try {
                    documento.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // =============================
    // ABRIR DOCUMENTO
    // =============================

    public void abrirDocumento(String ruta) {
        
        FileInputStream fis = null;
        XWPFDocument documento = null;
        
        try {
            
            // Limpiar el editor
            editor.setText("");
            StyledDocument doc = editor.getStyledDocument();
            
            // Abrir el archivo DOCX con try-with-resources
            fis = new FileInputStream(ruta);
            documento = new XWPFDocument(fis);
            
            // Leer párrafos y tablas
            List<org.apache.poi.xwpf.usermodel.IBodyElement> elementos = documento.getBodyElements();
            
            for (org.apache.poi.xwpf.usermodel.IBodyElement elemento : elementos) {
                
                if (elemento instanceof XWPFParagraph) {
                    XWPFParagraph parrafo = (XWPFParagraph) elemento;
                    leerParrafo(doc, parrafo);
                    
                } else if (elemento instanceof XWPFTable) {
                    XWPFTable tabla = (XWPFTable) elemento;
                    leerTabla(doc, tabla);
                }
                
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al abrir el documento: " + e.getMessage(), e);
        } finally {
            // Cerrar recursos en orden inverso
            try {
                if (documento != null) {
                    documento.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
    }

    // =============================
    // LEER PÁRRAFO
    // =============================

    private void leerParrafo(StyledDocument doc, XWPFParagraph parrafo) throws Exception {
        
        List<XWPFRun> runs = parrafo.getRuns();
        
        for (XWPFRun run : runs) {
            
            String texto = run.getText(0);
            if (texto == null) continue;
            
            SimpleAttributeSet attrs = new SimpleAttributeSet();
            
            // Aplicar estilos del run
            String fuente = run.getFontFamily();
            if (fuente != null) StyleConstants.setFontFamily(attrs, fuente);
            
            int tamano = run.getFontSize();
            if (tamano > 0) StyleConstants.setFontSize(attrs, tamano);
            
            StyleConstants.setBold(attrs, run.isBold());
            StyleConstants.setItalic(attrs, run.isItalic());
            
            if (run.getUnderline() != UnderlinePatterns.NONE) {
                StyleConstants.setUnderline(attrs, true);
            }
            
            String colorHex = run.getColor();
            if (colorHex != null) {
                Color color = hexAColor(colorHex);
                StyleConstants.setForeground(attrs, color);
            }
            
            doc.insertString(doc.getLength(), texto, attrs);
            
        }
        
        // Agregar salto de línea al final del párrafo
        doc.insertString(doc.getLength(), "\n", null);
        
    }

    // =============================
    // LEER TABLA
    // =============================

    private void leerTabla(StyledDocument doc, XWPFTable tablaDocx) throws Exception {
        
        int filas = tablaDocx.getNumberOfRows();
        int cols = (filas > 0) ? tablaDocx.getRow(0).getTableCells().size() : 0;
        
        if (filas == 0 || cols == 0) return;
        
        // Crear JTable con los datos
        Object[][] datos = new Object[filas][cols];
        String[] columnas = new String[cols];
        for (int i = 0; i < cols; i++) columnas[i] = "";
        
        for (int i = 0; i < filas; i++) {
            XWPFTableRow fila = tablaDocx.getRow(i);
            List<XWPFTableCell> celdas = fila.getTableCells();
            for (int j = 0; j < cols && j < celdas.size(); j++) {
                XWPFTableCell celda = celdas.get(j);
                datos[i][j] = celda.getText();
            }
        }
        
        JTable tabla = new JTable(datos, columnas);
        tabla.setPreferredScrollableViewportSize(tabla.getPreferredSize());
        tabla.setFillsViewportHeight(true);
        JScrollPane scrollTabla = new JScrollPane(tabla);
        
        // Insertar la tabla en el documento
        SimpleAttributeSet attrs = new SimpleAttributeSet();
        StyleConstants.setComponent(attrs, scrollTabla);
        doc.insertString(doc.getLength(), " ", attrs);
        doc.insertString(doc.getLength(), "\n", null);
        
    }

    // =============================
    // CONVERTIR HEX A COLOR
    // =============================

    private Color hexAColor(String hex) {
        
        try {
            int r = Integer.parseInt(hex.substring(0, 2), 16);
            int g = Integer.parseInt(hex.substring(2, 4), 16);
            int b = Integer.parseInt(hex.substring(4, 6), 16);
            return new Color(r, g, b);
        } catch (Exception e) {
            return Color.BLACK;
        }
        
    }

}