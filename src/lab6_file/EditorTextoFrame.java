import javax.swing.*;
import java.awt.*;
import javax.swing.text.*;

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
    }

    private void toggleBold() {
        int start = areaTexto.getSelectionStart();
        int end = areaTexto.getSelectionEnd();
        SimpleAttributeSet attrs = new SimpleAttributeSet();
        boolean value = boldBtn.isSelected();
        StyleConstants.setBold(attrs, value);
        isBoldActive = value;
        if (start != end) {
            areaTexto.getStyledDocument().setCharacterAttributes(start, end - start, attrs, false);
        } else {
            areaTexto.setCharacterAttributes(attrs, false);
        }
    }

    private void toggleItalic() {
        int start = areaTexto.getSelectionStart();
        int end = areaTexto.getSelectionEnd();
        SimpleAttributeSet attrs = new SimpleAttributeSet();
        boolean value = italicBtn.isSelected();
        StyleConstants.setItalic(attrs, value);
        isItalicActive = value;
        if (start != end) {
            areaTexto.getStyledDocument().setCharacterAttributes(start, end - start, attrs, false);
        } else {
            areaTexto.setCharacterAttributes(attrs, false);
        }
    }

    private void toggleUnderline() {
        int start = areaTexto.getSelectionStart();
        int end = areaTexto.getSelectionEnd();
        SimpleAttributeSet attrs = new SimpleAttributeSet();
        boolean value = underlineBtn.isSelected();
        StyleConstants.setUnderline(attrs, value);
        isUnderlineActive = value;
        if (start != end) {
            areaTexto.getStyledDocument().setCharacterAttributes(start, end - start, attrs, false);
        } else {
            areaTexto.setCharacterAttributes(attrs, false);
        }
    }

    private void changeFont() {
        String fuente = (String) fuenteBox.getSelectedItem();
        int start = areaTexto.getSelectionStart();
        int end = areaTexto.getSelectionEnd();
        SimpleAttributeSet attrs = new SimpleAttributeSet();
        currentFont = fuente;
        StyleConstants.setFontFamily(attrs, currentFont);
        if (start != end) {
            areaTexto.getStyledDocument().setCharacterAttributes(start, end - start, attrs, false);
        } else {
            areaTexto.setCharacterAttributes(attrs, false);
        }
    }

    private void changeSize() {
        int size = Integer.parseInt((String) tamañoBox.getSelectedItem());
        int start = areaTexto.getSelectionStart();
        int end = areaTexto.getSelectionEnd();
        SimpleAttributeSet attrs = new SimpleAttributeSet();
        currentSize = size;
        StyleConstants.setFontSize(attrs, currentSize);
        if (start != end) {
            areaTexto.getStyledDocument().setCharacterAttributes(start, end - start, attrs, false);
        } else {
            areaTexto.setCharacterAttributes(attrs, false);
        }
    }

    private void changeColor(Color color) {
        int start = areaTexto.getSelectionStart();
        int end = areaTexto.getSelectionEnd();
        SimpleAttributeSet attrs = new SimpleAttributeSet();
        currentColor = color;
        StyleConstants.setForeground(attrs, currentColor);
        if (start != end) {
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EditorTextoFrame().setVisible(true));
    }
}