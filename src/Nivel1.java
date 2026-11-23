import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Nivel 1 de la jerarquia de Chomsky.
 * Automata linealmente acotado para L = {a^n b^n c^n | n >= 1}.
 * Utiliza una cinta de longitud fija: marcador izquierdo + entrada + marcador derecho.
 */
public class Nivel1 extends JPanel {
    private static final Color BG = new Color(16, 23, 37);
    private static final Color CARD = new Color(29, 39, 57);
    private static final Color BORDER = new Color(55, 70, 93);
    private static final Color FG = new Color(238, 243, 250);
    private static final Color MUTED = new Color(161, 178, 203);
    private static final Color BLUE = new Color(55, 115, 219);
    private static final Color TEAL = new Color(32, 180, 164);
    private static final Color PURPLE = new Color(151, 104, 225);
    private static final Color GREEN = new Color(43, 194, 136);
    private static final Color RED = new Color(239, 105, 112);
    private static final Color ORANGE = new Color(218, 137, 63);
    private static final Color GOLD = new Color(252, 195, 89);
    private static final char LEFT = '\u22A2';
    private static final char RIGHT = '\u22A3';

    private enum Estado { BUSCAR_A, BUSCAR_B, BUSCAR_C, REGRESAR, VERIFICAR, ACEPTAR, RECHAZAR }

    private JTextField entrada;
    private JLabel estadoActual, informacion, resultado, detalle;
    private JTextArea historial;
    private JComboBox<String> ejemplos;
    private JButton paso, automatico, pausar;
    private VistaCinta dibujo;
    private Timer temporizador;

    private char[] cinta = { LEFT, RIGHT };
    private int cabezal = 1, pasos = 0;
    private Estado estado = Estado.BUSCAR_A;
    private boolean preparado = false, terminado = false;

    public Nivel1() {
        setLayout(new BorderLayout());
        setBackground(BG);

        JPanel contenido = new JPanel(new BorderLayout(0, 14));
        contenido.setBackground(BG);
        contenido.setBorder(new EmptyBorder(18, 22, 18, 22));

        JPanel encabezado = new JPanel(new BorderLayout(0, 5));
        encabezado.setOpaque(false);
        encabezado.add(texto("AUTÓMATA LINEALMENTE ACOTADO", 23, true, FG), BorderLayout.NORTH);
        encabezado.add(texto("NIVEL 1  /  Lenguaje sensible al contexto: a^n b^n c^n, n >= 1", 13, false, MUTED), BorderLayout.SOUTH);
        contenido.add(encabezado, BorderLayout.NORTH);

        JPanel cuerpo = new JPanel(new BorderLayout(0, 12));
        cuerpo.setOpaque(false);
        contenido.add(cuerpo, BorderLayout.CENTER);

        JPanel controles = tarjeta(new BorderLayout(0, 8));
        JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        fila.setOpaque(false);
        fila.add(texto("CADENA", 12, true, MUTED));
        entrada = new JTextField(10);
        entrada.setFont(new Font("SansSerif", Font.PLAIN, 16));
        fila.add(entrada);
        JButton preparar = boton("Preparar", BLUE);
        paso = boton("Siguiente paso", BLUE);
        automatico = boton("Automático", new Color(31, 130, 105));
        pausar = boton("Pausar", new Color(137, 99, 48));
        JButton reiniciar = boton("Reiniciar", new Color(82, 97, 121));
        fila.add(preparar);
        fila.add(paso);
        fila.add(automatico);
        fila.add(pausar);
        fila.add(reiniciar);
        controles.add(fila, BorderLayout.NORTH);

        JPanel filaEjemplos = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 1));
        filaEjemplos.setOpaque(false);
        filaEjemplos.add(texto("PRUEBAS RÁPIDAS", 12, true, MUTED));
        ejemplos = new JComboBox<>(new String[] {
            "Elige un ejemplo", "abc", "aabbcc", "aaabbbccc", "aabbc", "aabbbccc", "abcc", "ababcc", "Cadena vacía"
        });
        filaEjemplos.add(ejemplos);
        filaEjemplos.add(texto("Alfabeto: {a, b, c}   ·   Cinta fija: entrada + 2 fronteras", 12, false, MUTED));
        controles.add(filaEjemplos, BorderLayout.SOUTH);
        cuerpo.add(controles, BorderLayout.NORTH);

        JPanel panelVisual = tarjeta(new BorderLayout(0, 9));
        JPanel cabecera = new JPanel(new BorderLayout());
        cabecera.setOpaque(false);
        cabecera.add(texto("CINTA DE MEMORIA ACOTADA", 14, true, FG), BorderLayout.WEST);
        estadoActual = texto("Estado: BUSCAR_A", 13, true, TEAL);
        cabecera.add(estadoActual, BorderLayout.EAST);
        panelVisual.add(cabecera, BorderLayout.NORTH);

        dibujo = new VistaCinta();
        dibujo.setPreferredSize(new Dimension(750, 190));
        JScrollPane scrollCinta = new JScrollPane(dibujo,
            JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollCinta.setBorder(BorderFactory.createLineBorder(BORDER));
        scrollCinta.getHorizontalScrollBar().setUnitIncrement(22);
        panelVisual.add(scrollCinta, BorderLayout.CENTER);

        JPanel leyenda = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 3));
        leyenda.setOpaque(false);
        leyenda.add(chip("⊢  INICIO", ORANGE));
        leyenda.add(chip("⊣  FIN", ORANGE));
        leyenda.add(chip("X = a marcada", PURPLE));
        leyenda.add(chip("Y = b marcada", PURPLE));
        leyenda.add(chip("Z = c marcada", PURPLE));
        leyenda.add(chip("▼  Cabezal", GOLD));
        panelVisual.add(leyenda, BorderLayout.SOUTH);
        cuerpo.add(panelVisual, BorderLayout.CENTER);

        JPanel inferior = new JPanel(new BorderLayout(0, 10));
        inferior.setOpaque(false);
        JPanel panelEstado = tarjeta(new GridLayout(3, 1, 0, 5));
        informacion = texto("Sin cadena preparada", 14, true, FG);
        detalle = texto("La máquina marca a, b y c por grupos y regresa al inicio en cada pasada.", 13, false, MUTED);
        resultado = texto("ESPERANDO ENTRADA", 14, true, MUTED);
        panelEstado.add(informacion);
        panelEstado.add(detalle);
        panelEstado.add(resultado);
        inferior.add(panelEstado, BorderLayout.NORTH);

        JPanel panelHistorial = tarjeta(new BorderLayout(0, 8));
        panelHistorial.add(texto("REGISTRO DE MOVIMIENTOS", 13, true, FG), BorderLayout.NORTH);
        historial = new JTextArea(6, 30);
        historial.setEditable(false);
        historial.setFont(new Font("Monospaced", Font.PLAIN, 13));
        historial.setBackground(new Color(21, 31, 47));
        historial.setForeground(FG);
        historial.setBorder(new EmptyBorder(8, 10, 8, 10));
        JScrollPane scrollHistorial = new JScrollPane(historial);
        scrollHistorial.setBorder(BorderFactory.createLineBorder(BORDER));
        panelHistorial.add(scrollHistorial, BorderLayout.CENTER);
        inferior.add(panelHistorial, BorderLayout.CENTER);
        inferior.setPreferredSize(new Dimension(700, 244));
        cuerpo.add(inferior, BorderLayout.SOUTH);

        add(contenido, BorderLayout.CENTER);

        preparar.addActionListener(e -> prepararCadena());
        entrada.addActionListener(e -> prepararCadena());
        paso.addActionListener(e -> avanzar());
        automatico.addActionListener(e -> ejecutarAutomatico());
        pausar.addActionListener(e -> pausar());
        reiniciar.addActionListener(e -> reiniciar());
        ejemplos.addActionListener(e -> {
            int indice = ejemplos.getSelectedIndex();
            if (indice <= 0) return;
            entrada.setText(indice == 8 ? "" : String.valueOf(ejemplos.getSelectedItem()));
            prepararCadena();
        });
        addHierarchyListener(e -> {
            if ((e.getChangeFlags() & java.awt.event.HierarchyEvent.SHOWING_CHANGED) != 0 && !isShowing()) {
                pausar();
            }
        });
        actualizarBotones();
    }

    private JPanel tarjeta(LayoutManager layout) {
        JPanel p = new JPanel(layout);
        p.setBackground(CARD);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1, true), new EmptyBorder(12, 15, 12, 15)));
        return p;
    }

    private JLabel texto(String valor, int size, boolean bold, Color color) {
        JLabel l = new JLabel(valor);
        l.setFont(new Font("SansSerif", bold ? Font.BOLD : Font.PLAIN, size));
        l.setForeground(color);
        return l;
    }

    private JLabel chip(String valor, Color color) {
        JLabel l = texto("  " + valor + "  ", 11, true, FG);
        l.setOpaque(true);
        l.setBackground(new Color(38, 50, 70));
        l.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 3, 0, 0, color), new EmptyBorder(6, 5, 6, 5)));
        return l;
    }

    private JButton boton(String valor, Color color) {
        JButton b = new JButton(valor);
        b.setFont(new Font("SansSerif", Font.BOLD, 12));
        b.setBackground(color);
        b.setForeground(Color.WHITE);
        b.setOpaque(true);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private void prepararCadena() {
        detenerTemporizador();
        String texto = entrada.getText().trim();
        if (!texto.matches("[abc]*")) {
            preparado = false;
            resultado.setText("ERROR: SOLO SE ADMITEN a, b, c");
            resultado.setForeground(RED);
            detalle.setText("Corrige la entrada y pulsa Preparar.");
            actualizarBotones();
            return;
        }
        if (texto.length() > 60) {
            preparado = false;
            resultado.setText("ERROR: MÁXIMO 60 SÍMBOLOS EN ESTA DEMOSTRACIÓN");
            resultado.setForeground(RED);
            actualizarBotones();
            return;
        }
        cinta = ("" + LEFT + texto + RIGHT).toCharArray();
        cabezal = 1;
        pasos = 0;
        estado = Estado.BUSCAR_A;
        preparado = true;
        terminado = false;
        historial.setText("L = {a^n b^n c^n | n >= 1}\nEntrada: " +
            (texto.isEmpty() ? "(vacía)" : texto) + "\nCinta inicial: " + new String(cinta) + "\n\n");
        resultado.setText("SIMULACIÓN PREPARADA");
        resultado.setForeground(MUTED);
        detalle.setText("Busca una a sin marcar y empareja una b y una c.");
        if (texto.isEmpty()) rechazar("La cadena vacía no pertenece a este lenguaje.");
        actualizarVista();
    }

    /** Ejecuta una transición de la máquina acotada por cada pulsación. */
    private void avanzar() {
        if (!preparado || terminado) return;
        if (cabezal < 0 || cabezal >= cinta.length) {
            rechazar("Intento de atravesar un delimitador.");
            return;
        }
        Estado anterior = estado;
        char leido = cinta[cabezal];
        int antes = cabezal;
        String accion;
        switch (estado) {
            case BUSCAR_A:
                if (leido == 'X') {
                    cabezal++;
                    accion = "Saltar X ya marcada";
                } else if (leido == 'a') {
                    cinta[cabezal] = 'X';
                    cabezal++;
                    estado = Estado.BUSCAR_B;
                    accion = "Marcar a como X";
                } else if (leido == 'Y' || leido == 'Z') {
                    // La primera marca queda verificada en esta misma transición.
                    cabezal++;
                    estado = Estado.VERIFICAR;
                    accion = "Primera marca verificada";
                } else {
                    rechazar("Se esperaba una a sin marcar.");
                    return;
                }
                break;
            case BUSCAR_B:
                if (leido == 'a' || leido == 'X' || leido == 'Y') {
                    cabezal++;
                    accion = "Buscar b correspondiente";
                } else if (leido == 'b') {
                    cinta[cabezal] = 'Y';
                    cabezal++;
                    estado = Estado.BUSCAR_C;
                    accion = "Marcar b como Y";
                } else {
                    rechazar("No se encontró una b correspondiente.");
                    return;
                }
                break;
            case BUSCAR_C:
                if (leido == 'b' || leido == 'Z') {
                    cabezal++;
                    accion = "Buscar c correspondiente";
                } else if (leido == 'c') {
                    cinta[cabezal] = 'Z';
                    cabezal--;
                    estado = Estado.REGRESAR;
                    accion = "Marcar c como Z";
                } else {
                    rechazar("No se encontró una c correspondiente.");
                    return;
                }
                break;
            case REGRESAR:
                if (leido == LEFT) {
                    cabezal++;
                    estado = Estado.BUSCAR_A;
                    accion = "Comenzar otra pasada";
                } else {
                    cabezal--;
                    accion = "Volver hacia el inicio";
                }
                break;
            case VERIFICAR:
                if (leido == 'Y' || leido == 'Z') {
                    cabezal++;
                    accion = "Verificar símbolo marcado";
                } else if (leido == RIGHT) {
                    pasos++;
                    historial.append(String.format("Paso %d | %s | lee %s | ACEPTAR\n", pasos, anterior, leido));
                    aceptar();
                    return;
                } else {
                    rechazar("Quedaron símbolos sin procesar.");
                    return;
                }
                break;
            default:
                return;
        }
        // El cabezal nunca debe salir de los límites [0, cinta.length - 1].
        if (cabezal < 0 || cabezal >= cinta.length) {
            rechazar("Movimiento fuera de la cinta acotada.");
            return;
        }
        pasos++;
        historial.append(String.format("Paso %d | %s | lee %s | %s | %d -> %d\n",
            pasos, anterior, leido, accion, antes, cabezal));
        detalle.setText(accion);
        actualizarVista();
    }

    private void aceptar() {
        if (terminado) return;
        estado = Estado.ACEPTAR;
        terminado = true;
        detenerTemporizador();
        resultado.setText("CADENA ACEPTADA");
        resultado.setForeground(GREEN);
        detalle.setText("Todas las a, b y c quedaron emparejadas correctamente.");
        historial.append("\nRESULTADO: ACEPTADA\n");
        actualizarVista();
    }

    private void rechazar(String motivo) {
        if (terminado) return;
        estado = Estado.RECHAZAR;
        terminado = true;
        detenerTemporizador();
        resultado.setText("CADENA RECHAZADA");
        resultado.setForeground(RED);
        detalle.setText(motivo);
        historial.append("\nRECHAZO: " + motivo + "\n");
        actualizarVista();
    }

    private void actualizarBotones() {
        boolean habilitado = preparado && !terminado;
        boolean ejecutando = temporizador != null && temporizador.isRunning();
        paso.setEnabled(habilitado && !ejecutando);
        automatico.setEnabled(habilitado && !ejecutando);
        pausar.setEnabled(habilitado && ejecutando);
    }

    private void actualizarVista() {
        estadoActual.setText("Estado: " + estado + "  ·  Cabezal: " + cabezal);
        informacion.setText("Paso " + pasos + "   ·   Cinta " + cinta.length + " celdas   ·   Entrada " + (cinta.length - 2) + " símbolos");
        dibujo.setPreferredSize(new Dimension(Math.max(720, cinta.length * 62 + 40), 190));
        dibujo.revalidate();
        dibujo.repaint();
        historial.setCaretPosition(historial.getDocument().getLength());
        actualizarBotones();
        final int x = 20 + cabezal * 62;
        SwingUtilities.invokeLater(() -> {
            if (dibujo.isShowing()) dibujo.scrollRectToVisible(new Rectangle(x, 0, 62, 175));
        });
    }

    private void ejecutarAutomatico() {
        if (!preparado || terminado) return;
        detenerTemporizador();
        temporizador = new Timer(400, e -> avanzar());
        temporizador.start();
        actualizarBotones();
    }

    private void pausar() {
        detenerTemporizador();
        actualizarBotones();
    }

    private void detenerTemporizador() {
        if (temporizador != null) {
            temporizador.stop();
            temporizador = null;
        }
    }

    private void reiniciar() {
        detenerTemporizador();
        cinta = new char[] { LEFT, RIGHT };
        cabezal = 1;
        pasos = 0;
        estado = Estado.BUSCAR_A;
        preparado = false;
        terminado = false;
        entrada.setText("");
        historial.setText("");
        resultado.setText("ESPERANDO ENTRADA");
        resultado.setForeground(MUTED);
        detalle.setText("Selecciona un ejemplo o escribe una cadena para iniciar.");
        ejemplos.setSelectedIndex(0);
        actualizarVista();
    }

    @Override
    public void removeNotify() {
        detenerTemporizador();
        super.removeNotify();
    }

    /** Dibujo de la cinta, delimitadores, marcas y cabezal en la celda activa. */
    private class VistaCinta extends JPanel {
        VistaCinta() { setBackground(new Color(21, 31, 47)); }

        @Override
        protected void paintComponent(Graphics original) {
            super.paintComponent(original);
            Graphics2D g = (Graphics2D) original.create();
            try {
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int y = 83;
                int w = 52;
                for (int i = 0; i < cinta.length; i++) {
                    int x = 20 + i * 62;
                    boolean frontera = i == 0 || i == cinta.length - 1;
                    boolean activo = i == cabezal;
                    char simbolo = cinta[i];
                    Color base = frontera ? ORANGE :
                        (simbolo == 'X' || simbolo == 'Y' || simbolo == 'Z') ? PURPLE : BLUE;

                    if (frontera) {
                        g.setColor(new Color(255, 194, 117));
                        g.setFont(new Font("SansSerif", Font.BOLD, 12));
                        g.drawString(i == 0 ? "INICIO" : "FIN", x + (i == 0 ? 1 : 15), 48);
                    }
                    g.setColor(base);
                    g.fillRoundRect(x, y, w, 50, 10, 10);
                    g.setStroke(new BasicStroke(activo ? 3.2f : 1.3f));
                    g.setColor(activo ? GOLD : new Color(136, 160, 194));
                    g.drawRoundRect(x, y, w, 50, 10, 10);
                    g.setColor(FG);
                    g.setFont(new Font("SansSerif", Font.BOLD, 22));
                    String letra = String.valueOf(simbolo);
                    FontMetrics fm = g.getFontMetrics();
                    g.drawString(letra, x + (w - fm.stringWidth(letra)) / 2, y + 33);
                    g.setColor(MUTED);
                    g.setFont(new Font("SansSerif", Font.PLAIN, 11));
                    g.drawString(String.valueOf(i), x + 21, y + 69);
                    if (activo) {
                        g.setColor(GOLD);
                        int[] xs = {x + 15, x + 37, x + 26};
                        int[] ys = {y - 23, y - 23, y - 5};
                        g.fillPolygon(xs, ys, 3);
                        g.setFont(new Font("SansSerif", Font.BOLD, 11));
                        g.drawString("CABEZAL", x - 4, y - 31);
                    }
                }
            } finally {
                g.dispose();
            }
        }
    }
}
