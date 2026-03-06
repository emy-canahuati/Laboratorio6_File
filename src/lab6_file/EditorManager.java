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

    public void guardarDocumento(String ruta) throws Exception {

        XWPFDocument documento = crearDocumento(ruta);

        escribirContenido(documento);

        guardarArchivo(documento, ruta);

    }

    public void guardarConRAF(String ruta) {
        try (RandomAccessFile raf = new RandomAccessFile(ruta, "rw")) {
            raf.setLength(0); 
            StyledDocument doc = editor.getStyledDocument();
            
            int numElementos = 0;
            for (int i = 0; i < doc.getLength(); i++) {
                numElementos++;
            }
            
            raf.writeInt(numElementos); 
            
            for (int i = 0; i < doc.getLength(); i++) {
                AttributeSet attr = doc.getCharacterElement(i).getAttributes();
                Component comp = StyleConstants.getComponent(attr);
                
                if (comp != null && comp instanceof JScrollPane) {
                    JScrollPane scroll = (JScrollPane) comp;
                    Component vista = scroll.getViewport().getView();
                    if (vista instanceof JTable) {
                        JTable tabla = (JTable) vista;
                        raf.writeBoolean(true);
                        guardarTablaRAF(raf, tabla);
                    }
                } else {
                    
                    String letra = doc.getText(i, 1);
                    raf.writeBoolean(false); 
                    raf.writeChar(letra.charAt(0));
                    raf.writeUTF(StyleConstants.getFontFamily(attr));
                    raf.writeInt(StyleConstants.getFontSize(attr));
                    raf.writeInt(StyleConstants.getForeground(attr).getRGB());
                    raf.writeBoolean(StyleConstants.isBold(attr));
                    raf.writeBoolean(StyleConstants.isItalic(attr));
                    raf.writeBoolean(StyleConstants.isUnderline(attr));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void guardarTablaRAF(RandomAccessFile raf, JTable tabla) throws Exception {
        int filas = tabla.getRowCount();
        int cols = tabla.getColumnCount();
        
        raf.writeInt(filas);
        raf.writeInt(cols);
        
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < cols; j++) {
                Object valor = tabla.getValueAt(i, j);
                String texto = (valor != null) ? valor.toString() : "";
                raf.writeUTF(texto);
            }
        }
    }

   
    public void leerConRAF(String ruta) {
        try (RandomAccessFile raf = new RandomAccessFile(ruta, "r")) {
            StyledDocument doc = editor.getStyledDocument();
            doc.remove(0, doc.getLength()); 
            
            if (raf.length() == 0) return;
            
            int numElementos = raf.readInt();
            
            for (int i = 0; i < numElementos; i++) {
                boolean esTabla = raf.readBoolean();
                
                if (esTabla) {
                    // Leer tabla
                    leerTablaRAF(raf, doc);
                } else {
                    // Leer texto
                    char letra = raf.readChar();
                    String fuente = raf.readUTF();
                    int tamano = raf.readInt();
                    int colorRGB = raf.readInt();
                    boolean negrita = raf.readBoolean();
                    boolean cursiva = raf.readBoolean();
                    boolean subrayado = raf.readBoolean();
                    
                    SimpleAttributeSet attrs = new SimpleAttributeSet();
                    StyleConstants.setFontFamily(attrs, fuente);
                    StyleConstants.setFontSize(attrs, tamano);
                    StyleConstants.setForeground(attrs, new Color(colorRGB));
                    StyleConstants.setBold(attrs, negrita);
                    StyleConstants.setItalic(attrs, cursiva);
                    StyleConstants.setUnderline(attrs, subrayado);
                    
                    doc.insertString(doc.getLength(), String.valueOf(letra), attrs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void leerTablaRAF(RandomAccessFile raf, StyledDocument doc) throws Exception {
        int filas = raf.readInt();
        int cols = raf.readInt();
        
        Object[][] datos = new Object[filas][cols];
        String[] columnas = new String[cols];
        for (int i = 0; i < cols; i++) columnas[i] = "";
        
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < cols; j++) {
                datos[i][j] = raf.readUTF();
            }
        }
        
        JTable tabla = new JTable(datos, columnas);
        tabla.setPreferredScrollableViewportSize(tabla.getPreferredSize());
        tabla.setFillsViewportHeight(true);
        JScrollPane scrollTabla = new JScrollPane(tabla);
        
        SimpleAttributeSet attrs = new SimpleAttributeSet();
        StyleConstants.setComponent(attrs, scrollTabla);
        doc.insertString(doc.getLength(), " ", attrs);
    }

  
    private XWPFDocument crearDocumento(String ruta) throws Exception {

        return new XWPFDocument();

    }

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
                        parrafo = documento.createParagraph();
                    }
                }
                continue;
            }
            
            String letra = obtenerCaracter(doc, i);
            
            if (letra.equals("\n")) {
                parrafo = documento.createParagraph();
                continue;
            }
            
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
        tblWidth.setW(BigInteger.valueOf(5000));    
        for (int i = 0; i < filas; i++) {
            XWPFTableRow fila = tablaDocx.getRow(i);
            for (int j = 0; j < cols; j++) {
                XWPFTableCell celda = fila.getCell(j);
                Object valor = tabla.getValueAt(i, j);
                String texto = (valor != null) ? valor.toString() : "";      
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
        
        File archivo = new File(ruta);
        
        if (archivo.exists()) {
            archivo.delete();
        }
        
        try (RandomAccessFile raf = new RandomAccessFile(archivo, "rw")) {
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            documento.write(baos);
            byte[] bytes = baos.toByteArray();
            
            raf.write(bytes);
            
            baos.close();
        } finally {
            if (documento != null) {
                try {
                    documento.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        
    }

    public void abrirDocumento(String ruta) throws Exception {
        
        XWPFDocument documento = null;
        
        try {
            
            editor.setText("");
            StyledDocument doc = editor.getStyledDocument();
            
            File archivo = new File(ruta);
            byte[] bytes = new byte[(int) archivo.length()];
            
            try (RandomAccessFile raf = new RandomAccessFile(archivo, "r")) {
                raf.readFully(bytes);
            }
            
            java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(bytes);
            documento = new XWPFDocument(bais);
            bais.close();
            
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
            
        } finally {
            if (documento != null) {
                try {
                    documento.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        
    }
    private void leerParrafo(StyledDocument doc, XWPFParagraph parrafo) throws Exception {
        
        List<XWPFRun> runs = parrafo.getRuns();
        
        for (XWPFRun run : runs) {
            
            String texto = run.getText(0);
            if (texto == null) continue;
            
            SimpleAttributeSet attrs = new SimpleAttributeSet();
            
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
        doc.insertString(doc.getLength(), "\n", null);
        
    }
    
    private void leerTabla(StyledDocument doc, XWPFTable tablaDocx) throws Exception {
        
        int filas = tablaDocx.getNumberOfRows();
        int cols = (filas > 0) ? tablaDocx.getRow(0).getTableCells().size() : 0;
        
        if (filas == 0 || cols == 0) return;
        
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
        
        SimpleAttributeSet attrs = new SimpleAttributeSet();
        StyleConstants.setComponent(attrs, scrollTabla);
        doc.insertString(doc.getLength(), " ", attrs);
        doc.insertString(doc.getLength(), "\n", null);
        
    }

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