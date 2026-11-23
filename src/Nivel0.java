import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Nivel 0: maquina de Turing para f(n) = n + 1 en notacion unaria.
 * La cinta se representa con una lista extensible de simbolos 1 y blanco.
 */
public class Nivel0 extends JPanel {
    private static final Color BG = new Color(16, 23, 37);
    private static final Color CARD = new Color(29, 39, 57);
    private static final Color BORDER = new Color(55, 70, 93);
    private static final Color FG = new Color(238, 243, 250);
    private static final Color MUTED = new Color(161, 178, 203);
    private static final Color BLUE = new Color(55, 115, 219);
    private static final Color GREEN = new Color(43, 194, 136);
    private static final Color RED = new Color(239, 105, 112);
    private static final Color GOLD = new Color(252, 195, 89);
    private static final Color ORANGE = new Color(218, 137, 63);
    private static final char BLANCO = '\u25A1';

    private enum Estado { BUSCAR_FIN, REGRESAR, ACEPTAR, RECHAZAR }

    private JTextField entrada;
    private JLabel estadoActual, informacion, operacion, resultado;
    private JTextArea historial;
    private JComboBox<String> ejemplos;
    private JButton paso, automatico, pausar;
    private VistaCinta dibujo;
    private Timer temporizador;

    private final ArrayList<Character> cinta = new ArrayList<>();
    private int cabezal = 1, pasos = 0, longitudInicial = 0, extensiones = 0;
    private Estado estado = Estado.BUSCAR_FIN;
    private boolean preparado = false, terminado = false;

    public Nivel0() {
        setLayout(new BorderLayout());
        setBackground(BG);

        JPanel contenido = new JPanel(new BorderLayout(0, 14));
        contenido.setBackground(BG);
        contenido.setBorder(new EmptyBorder(18, 22, 18, 22));

        JPanel encabezado = new JPanel(new BorderLayout(0, 6));
        encabezado.setOpaque(false);
        encabezado.add(texto("MÁQUINA DE TURING", 23, true, FG), BorderLayout.NORTH);
        encabezado.add(texto("NIVEL 0  /  Incremento unario: f(n) = n + 1", 13, false, MUTED), BorderLayout.SOUTH);
        contenido.add(encabezado, BorderLayout.NORTH);

        JPanel cuerpo = new JPanel(new BorderLayout(0, 12));
        cuerpo.setOpaque(false);
        contenido.add(cuerpo, BorderLayout.CENTER);

        JPanel controles = tarjeta(new BorderLayout(0, 10));
        JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        fila.setOpaque(false);
        fila.add(texto("ENTRADA UNARIA", 12, true, MUTED));
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
            "Elige un ejemplo", "1", "11", "111", "1111", "11111", "Cero (cadena vacía)"
        });
        filaEjemplos.add(ejemplos);
        filaEjemplos.add(texto("Entrada: {1}   ·   Cinta: {1, □}   ·   Sin fronteras fijas", 12, false, MUTED));
        controles.add(filaEjemplos, BorderLayout.SOUTH);
        cuerpo.add(controles, BorderLayout.NORTH);

        JPanel panelVisual = tarjeta(new BorderLayout(0, 9));
        JPanel cabecera = new JPanel(new BorderLayout());
        cabecera.setOpaque(false);
        cabecera.add(texto("CINTA EXTENSIBLE · LECTURA / ESCRITURA", 14, true, FG), BorderLayout.WEST);
        estadoActual = texto("Estado: q0", 13, true, ORANGE);
        cabecera.add(estadoActual, BorderLayout.EAST);
        panelVisual.add(cabecera, BorderLayout.NORTH);

        dibujo = new VistaCinta();
        dibujo.setPreferredSize(new Dimension(760, 185));
        JScrollPane scrollCinta = new JScrollPane(dibujo,
            JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollCinta.setBorder(BorderFactory.createLineBorder(BORDER));
        scrollCinta.getHorizontalScrollBar().setUnitIncrement(22);
        panelVisual.add(scrollCinta, BorderLayout.CENTER);

        JPanel leyenda = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 3));
        leyenda.setOpaque(false);
        leyenda.add(chip("1 = Unidad", BLUE));
        leyenda.add(chip("□ = Blanco", MUTED));
        leyenda.add(chip("Cinta extensible", ORANGE));
        leyenda.add(chip("▼ Cabezal", GOLD));
        panelVisual.add(leyenda, BorderLayout.SOUTH);
        cuerpo.add(panelVisual, BorderLayout.CENTER);

        JPanel inferior = new JPanel(new BorderLayout(0, 10));
        inferior.setOpaque(false);
        JPanel panelEstado = tarjeta(new GridLayout(3, 1, 0, 5));
        informacion = texto("Alfabeto: {1, □}  ·  Sin entrada preparada", 14, true, FG);
        operacion = texto("La máquina busca un blanco, escribe 1 y regresa.", 13, false, MUTED);
        resultado = texto("ESPERANDO ENTRADA", 14, true, MUTED);
        panelEstado.add(informacion);
        panelEstado.add(operacion);
        panelEstado.add(resultado);
        inferior.add(panelEstado, BorderLayout.NORTH);

        JPanel panelHistorial = tarjeta(new BorderLayout(0, 8));
        panelHistorial.add(texto("REGISTRO DE TRANSICIONES", 13, true, FG), BorderLayout.NORTH);
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
            int i = ejemplos.getSelectedIndex();
            if (i <= 0) return;
            entrada.setText(i == 6 ? "" : String.valueOf(ejemplos.getSelectedItem()));
            prepararCadena();
        });
        addHierarchyListener(e -> {
            if ((e.getChangeFlags() & java.awt.event.HierarchyEvent.SHOWING_CHANGED) != 0 && !isShowing()) {
                pausar();
            }
        });
        cinta.add(BLANCO);
        cinta.add(BLANCO);
        actualizarVista();
    }

    private JPanel tarjeta(LayoutManager layout) {
        JPanel p = new JPanel(layout);
        p.setBackground(CARD);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1, true), new EmptyBorder(12, 15, 12, 15)));
        return p;
    }

    private JLabel texto(String s, int size, boolean bold, Color color) {
        JLabel l = new JLabel(s);
        l.setFont(new Font("SansSerif", bold ? Font.BOLD : Font.PLAIN, size));
        l.setForeground(color);
        return l;
    }

    private JLabel chip(String s, Color color) {
        JLabel l = texto("  " + s + "  ", 11, true, FG);
        l.setOpaque(true);
        l.setBackground(new Color(38, 50, 70));
        l.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 3, 0, 0, color), new EmptyBorder(6, 5, 6, 5)));
        return l;
    }

    private JButton boton(String s, Color color) {
        JButton b = new JButton(s);
        b.setFont(new Font("SansSerif", Font.BOLD, 12));
        b.setBackground(color);
        b.setForeground(Color.WHITE);
        b.setOpaque(true);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private String nombreEstado(Estado e) {
        switch (e) {
            case BUSCAR_FIN: return "q0";
            case REGRESAR: return "q1";
            case ACEPTAR: return "qf";
            case RECHAZAR: return "qr";
            default: return "?";
        }
    }

    private void prepararCadena() {
        detener();
        String texto = entrada.getText().trim();
        if (!texto.matches("1*")) {
            preparado = false;
            terminado = false;
            resultado.setText("ERROR: SOLO SE PERMITE EL SÍMBOLO 1");
            resultado.setForeground(RED);
            operacion.setText("La entrada debe ser un número unario (por ejemplo, 111).");
            actualizarBotones();
            return;
        }
        if (texto.length() > 60) {
            preparado = false;
            terminado = false;
            resultado.setText("ERROR: MÁXIMO 60 UNIDADES EN ESTA DEMOSTRACIÓN");
            resultado.setForeground(RED);
            actualizarBotones();
            return;
        }
        cinta.clear();
        cinta.add(BLANCO);
        for (char c : texto.toCharArray()) cinta.add(c);
        cinta.add(BLANCO);
        cabezal = 1;
        pasos = 0;
        longitudInicial = texto.length();
        extensiones = 0;
        estado = Estado.BUSCAR_FIN;
        preparado = true;
        terminado = false;
        historial.setText("MÁQUINA DE TURING - INCREMENTO UNARIO\n" +
            "Función: f(n) = n + 1\nEntrada: " +
            (texto.isEmpty() ? "(vacía = 0)" : texto) +
            "\nEstado inicial: q0   ·   Cinta: " + mostrarCinta() + "\n\n");
        resultado.setText("SIMULACIÓN PREPARADA");
        resultado.setForeground(MUTED);
        operacion.setText("Busca el primer blanco a la derecha de las unidades.");
        actualizarVista();
    }

    /** Un paso de la funcion de transicion delta. */
    private void avanzar() {
        if (!preparado || terminado) return;
        if (cabezal < 0 || cabezal >= cinta.size()) {
            rechazar("Cabezal fuera de la cinta representada.");
            return;
        }
        Estado anterior = estado;
        char leido = cinta.get(cabezal);
        char escrito = leido;
        int antes = cabezal;
        String movimiento;
        String accion;

        if (estado == Estado.BUSCAR_FIN) {
            if (leido == '1') {
                accion = "Leyó 1: continúa hacia la derecha";
                movimiento = "R";
                moverDerecha();
            } else if (leido == BLANCO) {
                cinta.set(cabezal, '1');
                escrito = '1';
                // Nueva celda en blanco: extension visible de la cinta.
                if (cabezal == cinta.size() - 1) {
                    cinta.add(BLANCO);
                    extensiones++;
                }
                estado = Estado.REGRESAR;
                movimiento = "L";
                accion = "Escribió un 1 en el blanco; inicia el regreso";
                moverIzquierda();
            } else {
                rechazar("Símbolo de cinta desconocido.");
                return;
            }
        } else if (estado == Estado.REGRESAR) {
            if (leido == '1') {
                movimiento = "L";
                accion = "Retrocede por las unidades";
                moverIzquierda();
            } else if (leido == BLANCO) {
                movimiento = "R";
                accion = "Encontró el blanco izquierdo; termina";
                moverDerecha();
                estado = Estado.ACEPTAR;
            } else {
                rechazar("Símbolo de cinta desconocido.");
                return;
            }
        } else return;

        pasos++;
        historial.append("Paso " + pasos + " | " + nombreEstado(anterior) + " | Lee " + leido +
            " | Escribe " + escrito + " | " + movimiento +
            " | " + nombreEstado(estado) + " | " + antes + " -> " + cabezal + "\n");
        historial.setCaretPosition(historial.getDocument().getLength());
        operacion.setText(accion);
        if (estado == Estado.ACEPTAR) aceptar();
        else actualizarVista();
    }

    private void moverDerecha() {
        cabezal++;
        if (cabezal >= cinta.size()) {
            cinta.add(BLANCO);
            extensiones++;
        }
    }

    private void moverIzquierda() {
        cabezal--;
        if (cabezal < 0) {
            cinta.add(0, BLANCO);
            cabezal = 0;
            extensiones++;
        }
    }

    private void aceptar() {
        if (terminado) return;
        terminado = true;
        detener();
        String salida = resultadoUnario();
        resultado.setText("RESULTADO: " + longitudInicial + " + 1 = " + salida.length() + " (ACEPTADA)");
        resultado.setForeground(GREEN);
        historial.append("\nDetenida en qf\nResultado unario: " + salida +
            "\nOperación: " + longitudInicial + " + 1 = " + salida.length() +
            "\nCeldas extendidas: " + extensiones + "\n");
        actualizarVista();
    }

    private void rechazar(String motivo) {
        if (terminado) return;
        estado = Estado.RECHAZAR;
        terminado = true;
        detener();
        resultado.setText("EJECUCIÓN RECHAZADA");
        resultado.setForeground(RED);
        historial.append("\nRechazo: " + motivo + "\n");
        actualizarVista();
    }

    private String resultadoUnario() {
        StringBuilder b = new StringBuilder();
        for (char c : cinta) if (c == '1') b.append('1');
        return b.toString();
    }

    private String mostrarCinta() {
        StringBuilder b = new StringBuilder();
        for (char c : cinta) b.append(c);
        return b.toString();
    }

    private void actualizarVista() {
        estadoActual.setText("Estado: " + nombreEstado(estado));
        estadoActual.setForeground(estado == Estado.ACEPTAR ? GREEN : estado == Estado.RECHAZAR ? RED : ORANGE);
        informacion.setText("Cabezal: " + cabezal + "   ·   Pasos: " + pasos +
            "   ·   Celdas visibles: " + cinta.size() + "   ·   Extensiones: " + extensiones);
        int anchura = Math.max(760, 45 + cinta.size() * 62);
        dibujo.setPreferredSize(new Dimension(anchura, 185));
        dibujo.revalidate();
        dibujo.repaint();
        int x = 20 + cabezal * 62;
        SwingUtilities.invokeLater(() -> {
            if (dibujo.isShowing()) dibujo.scrollRectToVisible(new Rectangle(x, 0, 62, 185));
        });
        actualizarBotones();
    }

    private void actualizarBotones() {
        boolean disponible = preparado && !terminado;
        boolean corriendo = temporizador != null && temporizador.isRunning();
        paso.setEnabled(disponible && !corriendo);
        automatico.setEnabled(disponible && !corriendo);
        pausar.setEnabled(disponible && corriendo);
    }

    private void ejecutarAutomatico() {
        if (!preparado || terminado) return;
        detener();
        temporizador = new Timer(550, e -> avanzar());
        temporizador.start();
        actualizarBotones();
    }

    private void pausar() {
        detener();
        actualizarBotones();
    }

    private void detener() {
        if (temporizador != null) {
            temporizador.stop();
            temporizador = null;
        }
    }

    private void reiniciar() {
        detener();
        cinta.clear();
        cinta.add(BLANCO);
        cinta.add(BLANCO);
        cabezal = 1;
        pasos = 0;
        longitudInicial = 0;
        extensiones = 0;
        estado = Estado.BUSCAR_FIN;
        preparado = false;
        terminado = false;
        entrada.setText("");
        historial.setText("");
        informacion.setText("Alfabeto de cinta: {1, □}   ·   Sin entrada preparada");
        operacion.setText("La máquina busca el final del número y añade una unidad.");
        resultado.setText("ESPERANDO ENTRADA");
        resultado.setForeground(MUTED);
        ejemplos.setSelectedIndex(0);
        actualizarVista();
    }

    private class VistaCinta extends JPanel {
        VistaCinta() {
            setBackground(new Color(21, 31, 47));
        }

        @Override
        protected void paintComponent(Graphics raw) {
            super.paintComponent(raw);
            Graphics2D g = (Graphics2D) raw.create();
            try {
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int y = 83, w = 52, h = 51;
                g.setColor(MUTED);
                g.setFont(new Font("SansSerif", Font.PLAIN, 12));
                g.drawString("← La cinta puede extenderse cuando se requieran nuevas celdas →", 20, 24);

                for (int i = 0; i < cinta.size(); i++) {
                    int x = 20 + i * 62;
                    char symbol = cinta.get(i);
                    boolean actual = i == cabezal;
                    g.setColor(actual ? new Color(31, 133, 105) : symbol == BLANCO ? new Color(59, 75, 96) : BLUE);
                    g.fillRoundRect(x, y, w, h, 10, 10);
                    g.setColor(actual ? GOLD : new Color(145, 165, 192));
                    g.setStroke(new BasicStroke(actual ? 3 : 1.3f));
                    g.drawRoundRect(x, y, w, h, 10, 10);
                    g.setColor(FG);
                    g.setFont(new Font("Monospaced", Font.BOLD, 22));
                    String s = String.valueOf(symbol);
                    FontMetrics fm = g.getFontMetrics();
                    g.drawString(s, x + (w - fm.stringWidth(s)) / 2, y + 33);
                    g.setColor(MUTED);
                    g.setFont(new Font("SansSerif", Font.PLAIN, 11));
                    g.drawString(String.valueOf(i), x + 20, y + h + 18);
                    if (actual) {
                        g.setColor(GOLD);
                        int[] px = { x + 12, x + 40, x + 26 };
                        int[] py = { y - 19, y - 19, y - 4 };
                        g.fillPolygon(px, py, 3);
                        g.setFont(new Font("SansSerif", Font.BOLD, 12));
                        g.drawString("CABEZAL", x - 3, y - 28);
                    }
                }
            } finally {
                g.dispose();
            }
        }
    }
}
