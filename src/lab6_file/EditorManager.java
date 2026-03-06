/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab6_file;

import java.awt.Color;
import java.io.FileOutputStream;

import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
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

    // =============================
    // MÉTODO PRINCIPAL
    // =============================

    public void guardarDocumento(String ruta) {

        try {

            XWPFDocument documento = crearDocumento();

            escribirContenido(documento);

            guardarArchivo(documento, ruta);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // =============================
    // CREAR DOCUMENTO
    // =============================

    private XWPFDocument crearDocumento() {
        return new XWPFDocument();
    }

    // =============================
    // ESCRIBIR CONTENIDO
    // =============================

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
        String hex = convertirColorHex(color);
        run.setColor(hex);

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

    //     =============================
    // GUARDAR ARCHIVO
    // =============================

    private void guardarArchivo(XWPFDocument documento, String ruta) throws Exception {

        FileOutputStream out = new FileOutputStream(ruta);

        documento.write(out);

        out.close();

        documento.close();

    }
}