import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class Main {
    private static final Color FONDO = new Color(15, 23, 42);
    private static final Color SIDEBAR = new Color(20, 30, 48);
    private static final Color TARJETA = new Color(30, 41, 59);
    private static final Color TEXTO = new Color(226, 232, 240);
    private static final Color SUAVE = new Color(148, 163, 184);
    private static final Color AZUL = new Color(37, 99, 235);

    private static final String[] CLAVES = {"INICIO", "NIVEL3", "NIVEL2", "NIVEL1", "NIVEL0"};
    private static final String[] NOMBRES = {"Panel principal", "Nivel 3 - Automata finito", "Nivel 2 - Automata de pila", "Nivel 1 - Automata acotado", "Nivel 0 - Maquina de Turing"};
    private static final String[] ICONOS = {"⌂", "03", "02", "01", "00"};
    private static final Color[] ACENTOS = {AZUL, new Color(59,130,246), new Color(139,92,246), new Color(20,184,166), new Color(245,158,11)};

    private static CardLayout navegacion;
    private static JPanel pantallas;
    private static JLabel tituloActual, rutaActual;
    private static NavButton[] botones;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::crearVentana);
    }

    private static void crearVentana() {
        JFrame ventana = new JFrame("Chomsky Lab | Simulador de Automatas");
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setSize(1280, 800);
        ventana.setMinimumSize(new Dimension(1000, 680));
        ventana.setLocationRelativeTo(null);

        JPanel estructura = new JPanel(new BorderLayout());
        estructura.setBackground(FONDO);
        estructura.add(crearSidebar(), BorderLayout.WEST);

        JPanel derecha = new JPanel(new BorderLayout());
        derecha.setBackground(FONDO);
        derecha.add(crearBarraSuperior(), BorderLayout.NORTH);

        navegacion = new CardLayout();
        pantallas = new JPanel(navegacion);
        pantallas.setBackground(FONDO);
        pantallas.add(crearInicio(), "INICIO");
        pantallas.add(new Nivel3(), "NIVEL3");
        pantallas.add(new Nivel2(), "NIVEL2");
        pantallas.add(new Nivel1(), "NIVEL1");
        pantallas.add(new Nivel0(), "NIVEL0");
        derecha.add(pantallas, BorderLayout.CENTER);
        estructura.add(derecha, BorderLayout.CENTER);

        ventana.setContentPane(estructura);
        cambiarPantalla(0);
        ventana.setVisible(true);
    }

    private static JPanel crearSidebar() {
        JPanel lateral = new JPanel(new BorderLayout());
        lateral.setPreferredSize(new Dimension(245, 0));
        lateral.setBackground(SIDEBAR);
        lateral.setBorder(new EmptyBorder(25, 15, 22, 15));

        JPanel marca = new JPanel();
        marca.setOpaque(false);
        marca.setLayout(new BoxLayout(marca, BoxLayout.Y_AXIS));
        JLabel logo = new JLabel("◈  CHOMSKY LAB");
        logo.setFont(new Font("SansSerif", Font.BOLD, 20));
        logo.setForeground(Color.WHITE);
        JLabel bajada = new JLabel("COMPUTATIONAL STUDIO");
        bajada.setFont(new Font("SansSerif", Font.BOLD, 10));
        bajada.setForeground(SUAVE);
        marca.add(logo);
        marca.add(Box.createVerticalStrut(7));
        marca.add(bajada);
        lateral.add(marca, BorderLayout.NORTH);

        JPanel opciones = new JPanel();
        opciones.setOpaque(false);
        opciones.setLayout(new BoxLayout(opciones, BoxLayout.Y_AXIS));
        opciones.setBorder(new EmptyBorder(45, 0, 0, 0));
        JLabel seccion = new JLabel("  NAVEGACION");
        seccion.setFont(new Font("SansSerif", Font.BOLD, 11));
        seccion.setForeground(SUAVE);
        seccion.setAlignmentX(Component.LEFT_ALIGNMENT);
        opciones.add(seccion);
        opciones.add(Box.createVerticalStrut(15));

        botones = new NavButton[5];
        String[] textos = {"Inicio", "Automata finito", "Automata de pila", "Automata acotado", "Maquina de Turing"};
        for (int i = 0; i < textos.length; i++) {
            final int destino = i;
            botones[i] = new NavButton(ICONOS[i] + "    " + textos[i]);
            botones[i].setAlignmentX(Component.LEFT_ALIGNMENT);
            botones[i].setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
            botones[i].setPreferredSize(new Dimension(205, 44));
            botones[i].addActionListener(e -> cambiarPantalla(destino));
            opciones.add(botones[i]);
            opciones.add(Box.createVerticalStrut(9));
        }
        lateral.add(opciones, BorderLayout.CENTER);

        JPanel pie = new JPanel();
        pie.setOpaque(false);
        pie.setLayout(new BoxLayout(pie, BoxLayout.Y_AXIS));
        JLabel proyecto = new JLabel("PROYECTO FINAL");
        proyecto.setForeground(TEXTO);
        proyecto.setFont(new Font("SansSerif", Font.BOLD, 12));
        JLabel version = new JLabel("Teoria de Automatas | Java Swing");
        version.setForeground(SUAVE);
        version.setFont(new Font("SansSerif", Font.PLAIN, 11));
        pie.add(proyecto);
        pie.add(Box.createVerticalStrut(5));
        pie.add(version);
        lateral.add(pie, BorderLayout.SOUTH);
        return lateral;
    }

    private static JPanel crearBarraSuperior() {
        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(FONDO);
        barra.setBorder(new EmptyBorder(20, 28, 12, 28));
        JPanel nombres = new JPanel();
        nombres.setOpaque(false);
        nombres.setLayout(new BoxLayout(nombres, BoxLayout.Y_AXIS));
        rutaActual = new JLabel("CHOMSKY LAB / INICIO");
        rutaActual.setFont(new Font("SansSerif", Font.BOLD, 11));
        rutaActual.setForeground(new Color(96, 165, 250));
        tituloActual = new JLabel("Panel principal");
        tituloActual.setFont(new Font("SansSerif", Font.BOLD, 26));
        tituloActual.setForeground(Color.WHITE);
        nombres.add(rutaActual);
        nombres.add(Box.createVerticalStrut(5));
        nombres.add(tituloActual);
        barra.add(nombres, BorderLayout.WEST);
        JLabel estado = new JLabel("●  4 MODULOS");
        estado.setForeground(new Color(74, 222, 128));
        estado.setFont(new Font("SansSerif", Font.BOLD, 12));
        barra.add(estado, BorderLayout.EAST);
        return barra;
    }

    private static JPanel crearInicio() {
        JPanel inicio = new JPanel(new BorderLayout(0, 18));
        inicio.setBackground(FONDO);
        inicio.setBorder(new EmptyBorder(8, 28, 26, 28));

        RoundedPanel bienvenida = new RoundedPanel(TARJETA, 22);
        bienvenida.setLayout(new BorderLayout());
        bienvenida.setBorder(new EmptyBorder(22, 26, 22, 26));
        JLabel saludo = new JLabel("Explora los modelos computacionales");
        saludo.setForeground(Color.WHITE);
        saludo.setFont(new Font("SansSerif", Font.BOLD, 23));
        JLabel detalle = new JLabel("Visualiza cadenas, estados, memoria, pilas y cintas paso a paso.");
        detalle.setForeground(SUAVE);
        detalle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        bienvenida.add(saludo, BorderLayout.NORTH);
        bienvenida.add(detalle, BorderLayout.SOUTH);
        inicio.add(bienvenida, BorderLayout.NORTH);

        JPanel tarjetas = new JPanel(new GridLayout(2, 2, 18, 18));
        tarjetas.setOpaque(false);
        tarjetas.add(crearTarjeta(1, "03", "Automata finito", "Reconocimiento de cadenas regulares", "Estados y transiciones"));
        tarjetas.add(crearTarjeta(2, "02", "Automata de pila", "Lenguaje con memoria LIFO", "Operaciones PUSH / POP"));
        tarjetas.add(crearTarjeta(3, "01", "Automata linealmente acotado", "Cinta con fronteras fijas", "Marcado y desplazamiento"));
        tarjetas.add(crearTarjeta(4, "00", "Maquina de Turing", "Incremento de numeros unarios", "Cinta extendible"));
        inicio.add(tarjetas, BorderLayout.CENTER);
        return inicio;
    }

    private static JPanel crearTarjeta(int indice, String numero, String nombre, String descripcion, String capacidad) {
        RoundedPanel tarjeta = new RoundedPanel(TARJETA, 20);
        tarjeta.setLayout(new BorderLayout(12, 12));
        tarjeta.setBorder(new EmptyBorder(20, 22, 18, 22));
        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        JLabel numeroLabel = new JLabel("NIVEL " + numero);
        numeroLabel.setForeground(ACENTOS[indice]);
        numeroLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        JLabel nombreLabel = new JLabel(nombre);
        nombreLabel.setForeground(Color.WHITE);
        nombreLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        JLabel descripcionLabel = new JLabel(descripcion);
        descripcionLabel.setForeground(SUAVE);
        descripcionLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        JLabel capacidadLabel = new JLabel(capacidad);
        capacidadLabel.setForeground(new Color(187, 202, 224));
        capacidadLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        textos.add(numeroLabel);
        textos.add(Box.createVerticalStrut(9));
        textos.add(nombreLabel);
        textos.add(Box.createVerticalStrut(9));
        textos.add(descripcionLabel);
        textos.add(Box.createVerticalStrut(5));
        textos.add(capacidadLabel);
        tarjeta.add(textos, BorderLayout.CENTER);
        JButton abrir = new JButton("ABRIR SIMULADOR  >");
        abrir.setForeground(Color.WHITE);
        abrir.setBackground(ACENTOS[indice]);
        abrir.setFocusPainted(false);
        abrir.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        abrir.setFont(new Font("SansSerif", Font.BOLD, 12));
        abrir.setPreferredSize(new Dimension(0, 39));
        abrir.addActionListener(e -> cambiarPantalla(indice));
        tarjeta.add(abrir, BorderLayout.SOUTH);
        return tarjeta;
    }

    private static void cambiarPantalla(int indice) {
        navegacion.show(pantallas, CLAVES[indice]);
        tituloActual.setText(NOMBRES[indice]);
        rutaActual.setText("CHOMSKY LAB / " + CLAVES[indice]);
        for (int i = 0; i < botones.length; i++) {
            botones[i].setSeleccionado(i == indice);
        }
    }

    private static class RoundedPanel extends JPanel {
        private final Color color;
        private final int radio;
        RoundedPanel(Color color, int radio) {
            this.color = color;
            this.radio = radio;
            setOpaque(false);
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radio, radio);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static class NavButton extends JButton {
        private boolean seleccionado = false;
        private boolean encima = false;
        NavButton(String texto) {
            super(texto);
            setBorder(new EmptyBorder(0, 15, 0, 8));
            setHorizontalAlignment(SwingConstants.LEFT);
            setForeground(TEXTO);
            setFont(new Font("SansSerif", Font.BOLD, 13));
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { encima = true; repaint(); }
                @Override public void mouseExited(MouseEvent e) { encima = false; repaint(); }
            });
        }
        void setSeleccionado(boolean valor) { seleccionado = valor; repaint(); }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (seleccionado || encima) {
                g2.setColor(seleccionado ? AZUL : new Color(43, 56, 77));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
