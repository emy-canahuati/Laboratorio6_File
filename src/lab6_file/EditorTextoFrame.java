import javax.swing.*;
import java.awt.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class EditorTextoFrame extends JFrame {

    private JComboBox<String> fuenteBox;
    private JComboBox<String> tamañoBox;

    private JToggleButton boldBtn;
    private JToggleButton italicBtn;
    private JToggleButton underlineBtn;

    private JButton aceptarBtn;
    private JButton cancelarBtn;

    private JTextPane areaTexto;

    // Estados para estilos futuros
    private boolean isBoldActive = false;
    private boolean isItalicActive = false;
    private boolean isUnderlineActive = false;
    private Color currentColor = Color.BLACK;
    private int currentSize = 20;
    private String currentFont = "Arial";

    public EditorTextoFrame() {

        setTitle("Editor de texto");
        setSize(950, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // PANEL SUPERIOR
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel herramientas = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        // FUENTES
        String[] fuentes = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getAvailableFontFamilyNames();

        fuenteBox = new JComboBox<>(fuentes);
        fuenteBox.setPreferredSize(new Dimension(180, 25));
        fuenteBox.addActionListener(e -> changeFont());

        // TAMAÑOS
        String[] tamaños = {
                "8", "10", "12", "14", "16", "18", "20", "24", "36",
                "42", "48", "64", "92", "144", "190", "240", "300"
        };

        tamañoBox = new JComboBox<>(tamaños);
        tamañoBox.setPreferredSize(new Dimension(70, 25));
        tamañoBox.addActionListener(e -> changeSize());

        // BOTONES TOGGLE ESTILO
        boldBtn = new JToggleButton("B");
        boldBtn.setFont(new Font("Arial", Font.BOLD, 14));
        boldBtn.addActionListener(e -> toggleBold());

        italicBtn = new JToggleButton("I");
        italicBtn.setFont(new Font("Arial", Font.ITALIC, 14));
        italicBtn.addActionListener(e -> toggleItalic());

        underlineBtn = new JToggleButton("U");
        underlineBtn.setFont(new Font("Arial", Font.PLAIN, 14));
        underlineBtn.addActionListener(e -> toggleUnderline());

        herramientas.add(new JLabel("Fuente"));
        herramientas.add(fuenteBox);
        herramientas.add(new JLabel("Tamaño"));
        herramientas.add(tamañoBox);
        herramientas.add(boldBtn);
        herramientas.add(italicBtn);
        herramientas.add(underlineBtn);

        panelSuperior.add(herramientas, BorderLayout.WEST);

        // PALETA DE COLORES
        JPanel coloresPanel = new JPanel(new GridLayout(2, 8, 5, 5));
        coloresPanel.setBorder(BorderFactory.createTitledBorder("Colores utilizados"));

        Color[] colores = {
                Color.BLACK, Color.WHITE, Color.RED, Color.GRAY,
                Color.ORANGE, Color.BLUE, Color.YELLOW, Color.CYAN,
                Color.PINK, Color.LIGHT_GRAY, Color.GREEN, Color.MAGENTA,
                Color.DARK_GRAY, new Color(139, 69, 19), Color.ORANGE, Color.WHITE
        };

        for (Color c : colores) {
            JButton colorBtn = new JButton();
            colorBtn.setBackground(c);
            colorBtn.setPreferredSize(new Dimension(25, 25));
            colorBtn.addActionListener(e -> changeColor(c));
            coloresPanel.add(colorBtn);
        }

        panelSuperior.add(coloresPanel, BorderLayout.EAST);

        add(panelSuperior, BorderLayout.NORTH);

        // AREA DE TEXTO
        areaTexto = new JTextPane();
        areaTexto.setFont(new Font(currentFont, Font.PLAIN, currentSize));

        JScrollPane scroll = new JScrollPane(areaTexto);
        add(scroll, BorderLayout.CENTER);

        // PANEL INFERIOR
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
        StyledDocument doc = areaTexto.getStyledDocument();
        SimpleAttributeSet attrs = new SimpleAttributeSet();

        boolean value = boldBtn.isSelected(); // valor del toggle
        StyleConstants.setBold(attrs, value);
        isBoldActive = value;

        if (start != end) {
            doc.setCharacterAttributes(start, end - start, attrs, false);
        } else {
            areaTexto.setCharacterAttributes(attrs, false);
        }
    }

    private void toggleItalic() {
        int start = areaTexto.getSelectionStart();
        int end = areaTexto.getSelectionEnd();
        StyledDocument doc = areaTexto.getStyledDocument();
        SimpleAttributeSet attrs = new SimpleAttributeSet();

        boolean value = italicBtn.isSelected();
        StyleConstants.setItalic(attrs, value);
        isItalicActive = value;

        if (start != end) {
            doc.setCharacterAttributes(start, end - start, attrs, false);
        } else {
            areaTexto.setCharacterAttributes(attrs, false);
        }
    }

    private void toggleUnderline() {
        int start = areaTexto.getSelectionStart();
        int end = areaTexto.getSelectionEnd();
        StyledDocument doc = areaTexto.getStyledDocument();
        SimpleAttributeSet attrs = new SimpleAttributeSet();

        boolean value = underlineBtn.isSelected();
        StyleConstants.setUnderline(attrs, value);
        isUnderlineActive = value;

        if (start != end) {
            doc.setCharacterAttributes(start, end - start, attrs, false);
        } else {
            areaTexto.setCharacterAttributes(attrs, false);
        }
    }

    private void changeColor(Color color) {
        int start = areaTexto.getSelectionStart();
        int end = areaTexto.getSelectionEnd();
        StyledDocument doc = areaTexto.getStyledDocument();
        SimpleAttributeSet attrs = new SimpleAttributeSet();
        currentColor = color;

        StyleConstants.setForeground(attrs, currentColor);

        if (start != end) {
            doc.setCharacterAttributes(start, end - start, attrs, false);
        } else {
            areaTexto.setCharacterAttributes(attrs, false);
        }
    }

    private void changeFont() {
        String fuente = (String) fuenteBox.getSelectedItem();
        int start = areaTexto.getSelectionStart();
        int end = areaTexto.getSelectionEnd();
        StyledDocument doc = areaTexto.getStyledDocument();
        SimpleAttributeSet attrs = new SimpleAttributeSet();
        currentFont = fuente;

        StyleConstants.setFontFamily(attrs, currentFont);

        if (start != end) {
            doc.setCharacterAttributes(start, end - start, attrs, false);
        } else {
            areaTexto.setCharacterAttributes(attrs, false);
        }
    }

    private void changeSize() {
        int size = Integer.parseInt((String) tamañoBox.getSelectedItem());
        int start = areaTexto.getSelectionStart();
        int end = areaTexto.getSelectionEnd();
        StyledDocument doc = areaTexto.getStyledDocument();
        SimpleAttributeSet attrs = new SimpleAttributeSet();
        currentSize = size;

        StyleConstants.setFontSize(attrs, currentSize);

        if (start != end) {
            doc.setCharacterAttributes(start, end - start, attrs, false);
        } else {
            areaTexto.setCharacterAttributes(attrs, false);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EditorTextoFrame().setVisible(true));
    }
}