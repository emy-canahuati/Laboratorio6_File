package lab6_file;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;

import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

public class EditorManager {

    private JTextPane editor;

    public EditorManager(JTextPane editor) {
        this.editor = editor;
    }

    public void guardarDocumento(String ruta) {
        try (RandomAccessFile rafObj = new RandomAccessFile(ruta, "rw")) {
            rafObj.setLength(0);
            StyledDocument doc = editor.getStyledDocument();
            int totalCaracteres = doc.getLength();

            rafObj.writeInt(totalCaracteres);

            for (int i = 0; i < totalCaracteres; i++) {
                String letra = doc.getText(i, 1);
                AttributeSet attr = doc.getCharacterElement(i).getAttributes();

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

    public void leerDocumento(String ruta) {
        try (RandomAccessFile raf = new RandomAccessFile(ruta, "r")) {
            StyledDocument doc = editor.getStyledDocument();
            doc.remove(0, doc.getLength()); // Limpiar editor actual
            if (raf.length() == 0) {
                return;
            }
            int totalCaracteres = raf.readInt();
            for (int i = 0; i < totalCaracteres; i++) {
                char letra = raf.readChar();
                String fuente = raf.readUTF();
                int tamano = raf.readInt();
                int colorRGB = raf.readInt();
                boolean negrita = raf.readBoolean();
                boolean cursiva = raf.readBoolean();
                boolean subrayado = raf.readBoolean();

                // Crear los atributos para este carácter
                SimpleAttributeSet attrs = new SimpleAttributeSet();
                StyleConstants.setFontFamily(attrs, fuente);
                StyleConstants.setFontSize(attrs, tamano);
                StyleConstants.setForeground(attrs, new Color(colorRGB));
                StyleConstants.setBold(attrs, negrita);
                StyleConstants.setItalic(attrs, cursiva);
                StyleConstants.setUnderline(attrs, subrayado);

                // Insertar en el JTextPane
                doc.insertString(doc.getLength(), String.valueOf(letra), attrs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private XWPFDocument crearDocumento(String ruta) throws Exception {
        File archivo = new File(ruta);
        if (archivo.exists()) {
            FileInputStream fis = new FileInputStream(archivo);
            return new XWPFDocument(fis);
        } else {
            return new XWPFDocument();
        }

    }

    private void escribirContenido(XWPFDocument documento) throws Exception {
        StyledDocument doc = editor.getStyledDocument();
        XWPFParagraph parrafo = documento.createParagraph();
        for (int i = 0; i < doc.getLength(); i++) {
            String letra = obtenerCaracter(doc, i);
            AttributeSet attr = obtenerAtributos(doc, i);
            XWPFRun run = crearRun(parrafo);
            aplicarEstilos(run, attr);
            run.setText(letra);
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
        FileOutputStream out = new FileOutputStream(ruta);
        documento.write(out);
        out.close();
        documento.close();
    }
}