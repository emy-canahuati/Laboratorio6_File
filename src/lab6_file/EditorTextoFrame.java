package lab6_file;

import javax.swing.*;
import java.awt.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class EditorTextoFrame extends JFrame {

    private JComboBox<String> fuenteBox;
    private JComboBox<String> tamañoBox;

    private JButton boldBtn;
    private JButton italicBtn;
    private JButton underlineBtn;

    private JButton aceptarBtn;
    private JButton cancelarBtn;

    private JTextPane areaTexto;

    public EditorTextoFrame(){

        setTitle("Editor de texto");
        setSize(950,600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // PANEL SUPERIOR
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JPanel herramientas = new JPanel(new FlowLayout(FlowLayout.LEFT,10,5));

        // FUENTES
        String[] fuentes = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getAvailableFontFamilyNames();

        fuenteBox = new JComboBox<>(fuentes);
        fuenteBox.setPreferredSize(new Dimension(180,25));

        // TAMAÑOS
        String[] tamaños = {
                "8","10","12","14","16","18","24","36",
                "42","48","64","92","144","190","240","300"
        };

        tamañoBox = new JComboBox<>(tamaños);
        tamañoBox.setPreferredSize(new Dimension(70,25));

        // BOTONES ESTILO
        boldBtn = new JButton("B");
        boldBtn.setFont(new Font("Arial",Font.BOLD,14));
        
        boldBtn.addActionListener(e -> {
            int start = areaTexto.getSelectionStart();
            int end = areaTexto.getSelectionEnd();
            if(start != end){

                    StyledDocument doc = areaTexto.getStyledDocument();

                    AttributeSet currentAttrs = doc.getCharacterElement(start).getAttributes();
                    boolean isBold = StyleConstants.isBold(currentAttrs);
                    
                    
                    
                    SimpleAttributeSet attrs = new SimpleAttributeSet();
                    StyleConstants.setBold(attrs, !isBold);

                    doc.setCharacterAttributes(start, end - start, attrs, false);
                    System.out.println(areaTexto.getSelectedText());
            }

            
        });
        italicBtn = new JButton("I");
        italicBtn.setFont(new Font("Arial",Font.ITALIC,14));

        italicBtn.addActionListener(e -> {
            int start = areaTexto.getSelectionStart();
            int end = areaTexto.getSelectionEnd();
            if(start != end){

                    StyledDocument doc = areaTexto.getStyledDocument();

                    AttributeSet currentAttrs = doc.getCharacterElement(start).getAttributes();
                    boolean isItalic = StyleConstants.isItalic(currentAttrs);
                    
                    
                    
                    SimpleAttributeSet attrs = new SimpleAttributeSet();
                    StyleConstants.setItalic(attrs, !isItalic);

                    doc.setCharacterAttributes(start, end - start, attrs, false);
                    System.out.println(areaTexto.getSelectedText());
            }

            
        });
        underlineBtn = new JButton("U");
        underlineBtn.addActionListener(e -> {
                    int start = areaTexto.getSelectionStart();
                    int end = areaTexto.getSelectionEnd();
                    if(start != end){

                            StyledDocument doc = areaTexto.getStyledDocument();

                            AttributeSet currentAttrs = doc.getCharacterElement(start).getAttributes();
                            boolean isUnderline = StyleConstants.isUnderline(currentAttrs);



                            SimpleAttributeSet attrs = new SimpleAttributeSet();
                            StyleConstants.setUnderline(attrs, !isUnderline);

                            doc.setCharacterAttributes(start, end - start, attrs, false);
                            System.out.println(areaTexto.getSelectedText());
                    }

            
        });
        
        herramientas.add(new JLabel("Fuente"));
        herramientas.add(fuenteBox);

        herramientas.add(new JLabel("Tamaño"));
        herramientas.add(tamañoBox);

        herramientas.add(boldBtn);
        herramientas.add(italicBtn);
        herramientas.add(underlineBtn);

        panelSuperior.add(herramientas,BorderLayout.WEST);

        // PALETA DE COLORES
        JPanel coloresPanel = new JPanel(new GridLayout(2,8,5,5));
        coloresPanel.setBorder(BorderFactory.createTitledBorder("Colores utilizados"));

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

            coloresPanel.add(colorBtn);
        }

        panelSuperior.add(coloresPanel,BorderLayout.EAST);

        add(panelSuperior,BorderLayout.NORTH);

        // AREA DE TEXTO
        areaTexto = new JTextPane();
        areaTexto.setFont(new Font("Arial",Font.PLAIN,20));

        JScrollPane scroll = new JScrollPane(areaTexto);

        add(scroll,BorderLayout.CENTER);

        // PANEL INFERIOR
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.RIGHT,15,10));

        aceptarBtn = new JButton("Aceptar");
        cancelarBtn = new JButton("Cancelar");

        aceptarBtn.setPreferredSize(new Dimension(100,30));
        cancelarBtn.setPreferredSize(new Dimension(100,30));

        panelInferior.add(aceptarBtn);
        panelInferior.add(cancelarBtn);

        add(panelInferior,BorderLayout.SOUTH);
    }

    public static void main(String[] args){

        SwingUtilities.invokeLater(() -> {

            new EditorTextoFrame().setVisible(true);

        });

    }
}



