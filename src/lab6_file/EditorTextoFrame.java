/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab6_file;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.text.*;
import java.awt.*;
import javax.swing.JFileChooser;

public class EditorTextoFrame extends JFrame {

    private JComboBox<String> fuenteBox, tamañoBox;
    private JToggleButton boldBtn, italicBtn, underlineBtn;
    private JButton tablaBtn, aceptarBtn, cancelarBtn;
    private JTextPane areaTexto;

    private boolean isBoldActive = false, isItalicActive = false, isUnderlineActive = false;
    private Color currentColor = Color.BLACK;
    private int currentSize = 20;
    private String currentFont = "Arial";

    public EditorTextoFrame() {
        setTitle("Editor de texto");
        setSize(950, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JPanel herramientas = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

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

        tablaBtn = new JButton("Crear Tabla");
        tablaBtn.addActionListener(e -> crearTabla());

        herramientas.add(new JLabel("Fuente"));
        herramientas.add(fuenteBox);
        herramientas.add(new JLabel("Tamaño"));
        herramientas.add(tamañoBox);
        herramientas.add(boldBtn);
        herramientas.add(italicBtn);
        herramientas.add(underlineBtn);
        herramientas.add(tablaBtn);

        panelSuperior.add(herramientas, BorderLayout.WEST);

        // PALETA DE COLORES
        JPanel coloresPanel = new JPanel(new GridLayout(2,8,5,5));
        Color[] colores = {
                Color.BLACK, Color.WHITE, Color.RED, Color.GRAY,
                Color.ORANGE, Color.BLUE, Color.YELLOW, Color.CYAN,
                Color.PINK, Color.LIGHT_GRAY, Color.GREEN, Color.MAGENTA,
                Color.DARK_GRAY, new Color(139,69,19), Color.ORANGE, Color.WHITE
        };
        for(Color c : colores){
            JButton colorBtn = new JButton();
            colorBtn.setBackground(c);
            colorBtn.setPreferredSize(new Dimension(25,25));
            colorBtn.addActionListener(e -> changeColor(c));
            coloresPanel.add(colorBtn);
        }
        panelSuperior.add(coloresPanel, BorderLayout.EAST);

        add(panelSuperior, BorderLayout.NORTH);

        areaTexto = new JTextPane();
        areaTexto.setFont(new Font(currentFont, Font.PLAIN, currentSize));
        JScrollPane scroll = new JScrollPane(areaTexto);
        add(scroll, BorderLayout.CENTER);

        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        aceptarBtn = new JButton("Aceptar");
        aceptarBtn.addActionListener(e -> guardarDocumento());
        cancelarBtn = new JButton("Cancelar");
        aceptarBtn.setPreferredSize(new Dimension(100, 30));
        cancelarBtn.setPreferredSize(new Dimension(100, 30));
        panelInferior.add(aceptarBtn);
        panelInferior.add(cancelarBtn);
        add(panelInferior, BorderLayout.SOUTH);
    }
    
    private void guardarDocumento() {

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Guardar documento");

        int opcion = chooser.showSaveDialog(this);

        if (opcion == JFileChooser.APPROVE_OPTION) {

            String ruta = chooser.getSelectedFile().getAbsolutePath();

            if (!ruta.endsWith(".docx")) {
                ruta += ".docx";
            }

            try {

                EditorManager manager = new EditorManager(areaTexto);
                manager.guardarDocumento(ruta);

                JOptionPane.showMessageDialog(this, "Documento guardado correctamente");

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(this, "Error guardando archivo: " + ex.getMessage());

            }

        }

    }

    private void toggleBold() {
        boolean value = boldBtn.isSelected();
        isBoldActive = value;
        aplicarEstiloSeleccion(value, StyleConstants::setBold);
    }

    private void toggleItalic() {
        boolean value = italicBtn.isSelected();
        isItalicActive = value;
        aplicarEstiloSeleccion(value, StyleConstants::setItalic);
    }

    private void toggleUnderline() {
        boolean value = underlineBtn.isSelected();
        isUnderlineActive = value;
        aplicarEstiloSeleccion(value, StyleConstants::setUnderline);
    }

    private void aplicarEstiloSeleccion(boolean value, BiConsumer<SimpleAttributeSet, Boolean> setter) {
        int start = areaTexto.getSelectionStart();
        int end = areaTexto.getSelectionEnd();
        SimpleAttributeSet attrs = new SimpleAttributeSet();
        setter.accept(attrs, value);
        StyleConstants.setFontFamily(attrs, currentFont);
        StyleConstants.setFontSize(attrs, currentSize);
        StyleConstants.setForeground(attrs, currentColor);
        if(start != end) {
            areaTexto.getStyledDocument().setCharacterAttributes(start, end - start, attrs, false);
        } else {
            areaTexto.setCharacterAttributes(attrs, false);
        }
    }

    private void changeFont() {
        currentFont = (String) fuenteBox.getSelectedItem();
        aplicarEstiloSeleccionFont(currentFont);
    }

    private void changeSize() {
        currentSize = Integer.parseInt((String) tamañoBox.getSelectedItem());
        aplicarEstiloSeleccionFontSize(currentSize);
    }

    private void changeColor(Color color){
        currentColor = color;
        int start = areaTexto.getSelectionStart();
        int end = areaTexto.getSelectionEnd();
        SimpleAttributeSet attrs = new SimpleAttributeSet();
        StyleConstants.setForeground(attrs, currentColor);
        StyleConstants.setFontFamily(attrs, currentFont);
        StyleConstants.setFontSize(attrs, currentSize);
        StyleConstants.setBold(attrs, isBoldActive);
        StyleConstants.setItalic(attrs, isItalicActive);
        StyleConstants.setUnderline(attrs, isUnderlineActive);
        if(start != end) {
            areaTexto.getStyledDocument().setCharacterAttributes(start, end - start, attrs, false);
        } else {
            areaTexto.setCharacterAttributes(attrs, false);
        }
    }

    private void aplicarEstiloSeleccionFont(String font) {
        int start = areaTexto.getSelectionStart();
        int end = areaTexto.getSelectionEnd();
        SimpleAttributeSet attrs = new SimpleAttributeSet();
        StyleConstants.setFontFamily(attrs, font);
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

    private void aplicarEstiloSeleccionFontSize(int size) {
        int start = areaTexto.getSelectionStart();
        int end = areaTexto.getSelectionEnd();
        SimpleAttributeSet attrs = new SimpleAttributeSet();
        StyleConstants.setFontSize(attrs, size);
        StyleConstants.setFontFamily(attrs, currentFont);
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
            tabla.setFillsViewportHeight(true);
            Font tablaFont = new Font(currentFont,
                    (isBoldActive ? Font.BOLD : 0) | (isItalicActive ? Font.ITALIC : 0),
                    currentSize);
            tabla.setFont(tablaFont);
            tabla.setForeground(currentColor);

            tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value,
                                                               boolean isSelected, boolean hasFocus,
                                                               int row, int column) {
                    JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    label.setFont(tablaFont);
                    label.setForeground(currentColor);
                    return label;
                }
            });

            JScrollPane scrollTabla = new JScrollPane(tabla);
            scrollTabla.setPreferredSize(new Dimension(600, 200));

            StyledDocument doc = areaTexto.getStyledDocument();
            SimpleAttributeSet attrs = new SimpleAttributeSet();
            StyleConstants.setComponent(attrs, scrollTabla);
            doc.insertString(areaTexto.getCaretPosition(), " ", attrs);

        } catch(Exception ex) {
            JOptionPane.showMessageDialog(this, "Error creando tabla: " + ex.getMessage());
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