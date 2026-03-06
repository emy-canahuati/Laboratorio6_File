package lab6_file;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.JFileChooser;

public class EditorTextoFrame extends JFrame {

    private JComboBox<String> fuenteBox, tamañoBox;
    private JToggleButton boldBtn, italicBtn, underlineBtn;
    private JButton tablaBtn, aceptarBtn, cancelarBtn, abrirBtn;
    private JTextPane areaTexto;
    
    private boolean isBoldActive = false, isItalicActive = false, isUnderlineActive = false;
    private Color currentColor = Color.BLACK;
    private int currentSize = 20;
    private String currentFont = "Arial";
    private String rutaDocumentoActual = null;

    public EditorTextoFrame() {
        setTitle("Editor de texto");
        setSize(950, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel primeraFila = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        
        abrirBtn = new JButton("Abrir");
        abrirBtn.addActionListener(e -> abrirDocumento());
        
        String[] fuentes = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        fuenteBox = new JComboBox<>(fuentes);
        fuenteBox.setPreferredSize(new Dimension(180, 25));
        fuenteBox.addActionListener(e -> changeFont());

        String[] tamaños = {"8","10","12","14","16","18","20","24","36","42","48","64","92","144","190","240","300"};
        tamañoBox = new JComboBox<>(tamaños);
        tamañoBox.setPreferredSize(new Dimension(70, 25));
        tamañoBox.addActionListener(e -> changeSize());

        boldBtn = new JToggleButton("B");
        boldBtn.setFont(new Font("Arial", Font.BOLD, 14));
        boldBtn.addActionListener(e -> toggleBold());

        italicBtn = new JToggleButton("I");
        italicBtn.setFont(new Font("Arial", Font.ITALIC, 14));
        italicBtn.addActionListener(e -> toggleItalic());

        underlineBtn = new JToggleButton("U");
        underlineBtn.setFont(new Font("Arial", Font.PLAIN, 14));
        underlineBtn.addActionListener(e -> toggleUnderline());

        primeraFila.add(abrirBtn);
        primeraFila.add(new JLabel("Fuente"));
        primeraFila.add(fuenteBox);
        primeraFila.add(new JLabel("Tamaño"));
        primeraFila.add(tamañoBox);
        primeraFila.add(boldBtn);
        primeraFila.add(italicBtn);
        primeraFila.add(underlineBtn);

        JPanel segundaFila = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        
        tablaBtn = new JButton("Crear Tabla");
        tablaBtn.addActionListener(e -> crearTabla());
        
        segundaFila.add(new JLabel("Colores:"));
        Color[] colores = {Color.BLACK, Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE, Color.MAGENTA, Color.CYAN, Color.PINK, Color.GRAY};
        for (Color c : colores) {
            JButton colorBtn = new JButton();
            colorBtn.setBackground(c);
            colorBtn.setPreferredSize(new Dimension(25, 25));
            colorBtn.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            colorBtn.addActionListener(e -> changeColor(c));
            segundaFila.add(colorBtn);
        }
        segundaFila.add(tablaBtn);

        JPanel herramientasCompleto = new JPanel(new BorderLayout());
        herramientasCompleto.add(primeraFila, BorderLayout.NORTH);
        herramientasCompleto.add(segundaFila, BorderLayout.CENTER);
        
        panelSuperior.add(herramientasCompleto, BorderLayout.CENTER);

        add(panelSuperior, BorderLayout.NORTH);

        areaTexto = new JTextPane();
        areaTexto.setFont(new Font(currentFont, Font.PLAIN, currentSize));
        JScrollPane scroll = new JScrollPane(areaTexto);
        add(scroll, BorderLayout.CENTER);

        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        aceptarBtn = new JButton("Aceptar");
        cancelarBtn = new JButton("Cancelar");
        aceptarBtn.setPreferredSize(new Dimension(100, 30));
        cancelarBtn.setPreferredSize(new Dimension(100, 30));
        panelInferior.add(aceptarBtn);
        panelInferior.add(cancelarBtn);
        add(panelInferior, BorderLayout.SOUTH);

        aceptarBtn.addActionListener(e -> guardarDocumento());
        cancelarBtn.addActionListener(e -> cancelarCambios());
    }

    private void toggleBold() { aplicarEstiloSeleccion(StyleConstants::setBold, boldBtn.isSelected(), (v) -> isBoldActive = v); }
    private void toggleItalic() { aplicarEstiloSeleccion(StyleConstants::setItalic, italicBtn.isSelected(), (v) -> isItalicActive = v); }
    private void toggleUnderline() { aplicarEstiloSeleccion(StyleConstants::setUnderline, underlineBtn.isSelected(), (v) -> isUnderlineActive = v); }

    private void aplicarEstiloSeleccion(BiConsumer<SimpleAttributeSet, Boolean> setter, boolean value, java.util.function.Consumer<Boolean> flagSetter) {
        int start = areaTexto.getSelectionStart();
        int end = areaTexto.getSelectionEnd();
        SimpleAttributeSet attrs = new SimpleAttributeSet();
        setter.accept(attrs, value);
        StyleConstants.setFontFamily(attrs, currentFont);
        StyleConstants.setFontSize(attrs, currentSize);
        StyleConstants.setForeground(attrs, currentColor);
        flagSetter.accept(value);
        if (start != end) {
            areaTexto.getStyledDocument().setCharacterAttributes(start, end - start, attrs, false);
        } else {
            areaTexto.setCharacterAttributes(attrs, false);
        }
    }

    private void changeFont() {
        currentFont = (String) fuenteBox.getSelectedItem();
        aplicarAtributosSeleccion();
    }

    private void changeSize() {
        currentSize = Integer.parseInt((String) tamañoBox.getSelectedItem());
        aplicarAtributosSeleccion();
    }

    private void changeColor(Color color){
        currentColor = color;
        aplicarAtributosSeleccion();
    }

    private void aplicarAtributosSeleccion() {
        int start = areaTexto.getSelectionStart();
        int end = areaTexto.getSelectionEnd();
        SimpleAttributeSet attrs = new SimpleAttributeSet();
        StyleConstants.setFontFamily(attrs, currentFont);
        StyleConstants.setFontSize(attrs, currentSize);
        StyleConstants.setForeground(attrs, currentColor);
        StyleConstants.setBold(attrs, isBoldActive);
        StyleConstants.setItalic(attrs, isItalicActive);
        StyleConstants.setUnderline(attrs, isUnderlineActive);
        if(start != end) {
            areaTexto.getStyledDocument().setCharacterAttributes(start, end - start, attrs, false);
        } else {
            areaTexto.setCharacterAttributes(attrs, false);
        }
    }

    private void crearTabla() {
        try {
            String filasStr = JOptionPane.showInputDialog(this, "Número de filas:");
            String colsStr = JOptionPane.showInputDialog(this, "Número de columnas:");
            if(filasStr == null || colsStr == null) return;

            int filas = Integer.parseInt(filasStr);
            int cols = Integer.parseInt(colsStr);

            Object[][] datos = new Object[filas][cols];
            String[] columnas = new String[cols];
            for(int i=0;i<cols;i++) columnas[i] = "";

            JTable tabla = new JTable(datos, columnas);
            tabla.setPreferredScrollableViewportSize(tabla.getPreferredSize());
            tabla.setFillsViewportHeight(true);
            JScrollPane scrollTabla = new JScrollPane(tabla);

            StyledDocument doc = areaTexto.getStyledDocument();
            SimpleAttributeSet attrs = new SimpleAttributeSet();
            StyleConstants.setComponent(attrs, scrollTabla);
            doc.insertString(areaTexto.getCaretPosition(), " ", attrs);

        } catch(Exception ex) {
            JOptionPane.showMessageDialog(this, "Error creando tabla: " + ex.getMessage());
        }
    }

    private void guardarDocumento() {
        if (rutaDocumentoActual == null) {

            try {
                ImageIcon icono = new ImageIcon("abrir_documento.png");
                JOptionPane.showMessageDialog(
                        this,
                        "Debe abrir un documento primero",
                        "Advertencia",
                        JOptionPane.WARNING_MESSAGE,
                        icono
                );
            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                        this,
                        "Debe abrir un documento primero",
                        "Advertencia",
                        JOptionPane.WARNING_MESSAGE
                );
            }
            return;
        }
        try {

            EditorManager manager = new EditorManager(areaTexto);
            manager.guardarDocumento(rutaDocumentoActual);
            JOptionPane.showMessageDialog(this, "Documento guardado correctamente");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error guardando archivo: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void abrirDocumento() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Abrir documento");
        int opcion = chooser.showOpenDialog(this);
        if (opcion == JFileChooser.APPROVE_OPTION) {

            File archivo = chooser.getSelectedFile();
            String ruta = archivo.getAbsolutePath();

            if (!ruta.toLowerCase().endsWith(".docx")) {
                JOptionPane.showMessageDialog(
                        this,
                        "El archivo debe ser formato .docx",
                        "Formato inválido",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }
            try {
                if (!archivo.exists()) {
                    archivo.createNewFile();
                }
                EditorManager manager = new EditorManager(areaTexto);

                if (archivo.length() > 0) {
                    manager.abrirDocumento(ruta);
                } else {
                    areaTexto.setText("");
                }
                rutaDocumentoActual = ruta;
                JOptionPane.showMessageDialog(this, "Documento abierto correctamente");

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error abriendo archivo: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    
    private void cancelarCambios() {
        if (rutaDocumentoActual == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "No hay documento abierto",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        int opcion = JOptionPane.showConfirmDialog(
                this,
                "Se perderán los cambios no guardados.\n¿Restaurar el documento original?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION
        );
        if (opcion != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            EditorManager manager = new EditorManager(areaTexto);
            manager.abrirDocumento(rutaDocumentoActual);
            JOptionPane.showMessageDialog(
                    this,
                    "Documento restaurado al estado original"
            );

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error restaurando documento: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EditorTextoFrame().setVisible(true));
    }

    @FunctionalInterface
    interface BiConsumer<T, U> {
        void accept(T t, U u);
    }
}