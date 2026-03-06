
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab6_file;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.text.*;
import java.awt.*;

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
        tamañoBox.setSelectedItem("20");
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
        aceptarBtn.setPreferredSize(new Dimension(100, 30));
        aceptarBtn.addActionListener(e -> guardarDocumento());
        
        cancelarBtn = new JButton("Cancelar");
        cancelarBtn.setPreferredSize(new Dimension(100, 30));
        cancelarBtn.addActionListener(e -> {
            // Regla especial: Si se rinde, es victoria y 3 puntos para el otro
            int salir = JOptionPane.showConfirmDialog(this, "¿Deseas salir? (Rendirse otorga 3 puntos al oponente)", "Salir", JOptionPane.YES_NO_OPTION);
            if(salir == JOptionPane.YES_OPTION) System.exit(0);
        });

        panelInferior.add(aceptarBtn);
        panelInferior.add(cancelarBtn);
        add(panelInferior, BorderLayout.SOUTH);
    }
    
    private void guardarDocumento() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Guardar documento .docx");

        int opcion = chooser.showSaveDialog(this);

        if (opcion == JFileChooser.APPROVE_OPTION) {
            String ruta = chooser.getSelectedFile().getAbsolutePath();

            if (!ruta.toLowerCase().endsWith(".docx")) {
                ruta += ".docx";
            }

            try {
                // Instanciamos tu EditorManager con el JTextPane actual
                EditorManager manager = new EditorManager(areaTexto);
                manager.guardarDocumento(ruta);

                JOptionPane.showMessageDialog(this, "Documento guardado correctamente en:\n" + ruta);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error guardando archivo: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void toggleBold() {
        isBoldActive = boldBtn.isSelected();
        aplicarEstiloGeneral();
    }

    private void toggleItalic() {
        isItalicActive = italicBtn.isSelected();
        aplicarEstiloGeneral();
    }

    private void toggleUnderline() {
        isUnderlineActive = underlineBtn.isSelected();
        aplicarEstiloGeneral();
    }

    private void changeFont() {
        currentFont = (String) fuenteBox.getSelectedItem();
        aplicarEstiloGeneral();
    }

    private void changeSize() {
        currentSize = Integer.parseInt((String) tamañoBox.getSelectedItem());
        aplicarEstiloGeneral();
    }

    private void changeColor(Color color){
        currentColor = color;
        aplicarEstiloGeneral();
    }

    // Método unificado para aplicar todos los estados actuales (Color, Fuente, Size, Estilos)
    private void aplicarEstiloGeneral() {
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
            for(int i=0; i<cols; i++) columnas[i] = "Col " + (i+1);

            JTable tabla = new JTable(datos, columnas);
            tabla.setFillsViewportHeight(true);
            
            // Estilo de la tabla basado en el editor actual
            Font tablaFont = new Font(currentFont, 
                    (isBoldActive ? Font.BOLD : 0) | (isItalicActive ? Font.ITALIC : 0), 
                    currentSize);
            tabla.setFont(tablaFont);
            tabla.setForeground(currentColor);
            tabla.setRowHeight(currentSize + 5);

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
            scrollTabla.setPreferredSize(new Dimension(600, 150));

            // Insertar la tabla como un componente en el JTextPane
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