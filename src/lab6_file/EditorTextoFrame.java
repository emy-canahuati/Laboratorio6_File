/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab6_file;


import javax.swing.*;
import java.awt.*;
import javax.swing.text.*;

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

        italicBtn = new JButton("I");
        italicBtn.setFont(new Font("Arial",Font.ITALIC,14));

        underlineBtn = new JButton("U");

        herramientas.add(new JLabel("Fuente"));
        herramientas.add(fuenteBox);

        herramientas.add(new JLabel("Tamaño"));
        herramientas.add(tamañoBox);

        herramientas.add(boldBtn);
        herramientas.add(italicBtn);
        herramientas.add(underlineBtn);

        panelSuperior.add(herramientas,BorderLayout.WEST);

        // COLORES
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

            colorBtn.addActionListener(e -> {

                int start = areaTexto.getSelectionStart();
                int end = areaTexto.getSelectionEnd();

                if(start != end){

                    StyledDocument doc = areaTexto.getStyledDocument();
                    SimpleAttributeSet attrs = new SimpleAttributeSet();

                    StyleConstants.setForeground(attrs, c);

                    doc.setCharacterAttributes(start, end - start, attrs, false);
                }

            });

            coloresPanel.add(colorBtn);
        }

        panelSuperior.add(coloresPanel,BorderLayout.EAST);

        add(panelSuperior,BorderLayout.NORTH);

        // AREA TEXTO
        areaTexto = new JTextPane();
        areaTexto.setFont(new Font("Arial",Font.PLAIN,20));

        JScrollPane scroll = new JScrollPane(areaTexto);
        add(scroll,BorderLayout.CENTER);

        // PANEL INFERIOR
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.RIGHT,15,10));

        aceptarBtn = new JButton("Aceptar");
        cancelarBtn = new JButton("Cancelar");

        panelInferior.add(aceptarBtn);
        panelInferior.add(cancelarBtn);

        add(panelInferior,BorderLayout.SOUTH);

        // =========================
        // ACCIONES
        // =========================

        boldBtn.addActionListener(e -> toggleBold());
        italicBtn.addActionListener(e -> toggleItalic());
        underlineBtn.addActionListener(e -> toggleUnderline());

        fuenteBox.addActionListener(e -> cambiarFuente());
        tamañoBox.addActionListener(e -> cambiarTamaño());

        aceptarBtn.addActionListener(e -> guardarDocx());
        cancelarBtn.addActionListener(e -> areaTexto.setText(""));
    }

    private void toggleBold(){

        int start = areaTexto.getSelectionStart();
        int end = areaTexto.getSelectionEnd();

        if(start != end){

            StyledDocument doc = areaTexto.getStyledDocument();
            AttributeSet attrs = doc.getCharacterElement(start).getAttributes();

            boolean bold = StyleConstants.isBold(attrs);

            SimpleAttributeSet nuevo = new SimpleAttributeSet();
            StyleConstants.setBold(nuevo,!bold);

            doc.setCharacterAttributes(start,end-start,nuevo,false);
        }
    }

    private void toggleItalic(){

        int start = areaTexto.getSelectionStart();
        int end = areaTexto.getSelectionEnd();

        if(start != end){

            StyledDocument doc = areaTexto.getStyledDocument();
            AttributeSet attrs = doc.getCharacterElement(start).getAttributes();

            boolean italic = StyleConstants.isItalic(attrs);

            SimpleAttributeSet nuevo = new SimpleAttributeSet();
            StyleConstants.setItalic(nuevo,!italic);

            doc.setCharacterAttributes(start,end-start,nuevo,false);
        }
    }

    private void toggleUnderline(){

        int start = areaTexto.getSelectionStart();
        int end = areaTexto.getSelectionEnd();

        if(start != end){

            StyledDocument doc = areaTexto.getStyledDocument();
            AttributeSet attrs = doc.getCharacterElement(start).getAttributes();

            boolean underline = StyleConstants.isUnderline(attrs);

            SimpleAttributeSet nuevo = new SimpleAttributeSet();
            StyleConstants.setUnderline(nuevo,!underline);

            doc.setCharacterAttributes(start,end-start,nuevo,false);
        }
    }

    private void cambiarFuente(){

        String fuente = (String) fuenteBox.getSelectedItem();

        int start = areaTexto.getSelectionStart();
        int end = areaTexto.getSelectionEnd();

        if(start != end){

            StyledDocument doc = areaTexto.getStyledDocument();
            SimpleAttributeSet attrs = new SimpleAttributeSet();

            StyleConstants.setFontFamily(attrs, fuente);

            doc.setCharacterAttributes(start,end-start,attrs,false);
        }
    }

    private void cambiarTamaño(){

        int tamaño = Integer.parseInt((String)tamañoBox.getSelectedItem());

        int start = areaTexto.getSelectionStart();
        int end = areaTexto.getSelectionEnd();

        if(start != end){

            StyledDocument doc = areaTexto.getStyledDocument();
            SimpleAttributeSet attrs = new SimpleAttributeSet();

            StyleConstants.setFontSize(attrs,tamaño);

            doc.setCharacterAttributes(start,end-start,attrs,false);
        }
    }

    private void guardarDocx(){

        JFileChooser chooser = new JFileChooser();

        int opcion = chooser.showSaveDialog(this);

        if(opcion == JFileChooser.APPROVE_OPTION){

            String ruta = chooser.getSelectedFile().getAbsolutePath();

            if(!ruta.endsWith(".docx")){
                ruta += ".docx";
            }

            EditorManager manager = new EditorManager(areaTexto);
            manager.guardarDocumento(ruta);

            JOptionPane.showMessageDialog(this,"Documento guardado");
        }
    }

    public static void main(String[] args){

        SwingUtilities.invokeLater(() -> {

            new EditorTextoFrame().setVisible(true);

        });

    }
}