import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/** Nivel 3: AFD que reconoce las cadenas sobre {a,b} terminadas en "ab". */
public class Nivel3 extends JPanel {
    private static final Color BG = new Color(16, 23, 37);
    private static final Color CARD = new Color(29, 39, 57);
    private static final Color BORDER = new Color(55, 70, 93);
    private static final Color FG = new Color(238, 243, 250);
    private static final Color MUTED = new Color(161, 178, 203);
    private static final Color BLUE = new Color(55, 115, 219);
    private static final Color GREEN = new Color(43, 194, 136);
    private static final Color RED = new Color(239, 105, 112);
    private static final Color GOLD = new Color(252, 195, 89);

    private static final int[][] DELTA = {{1, 0}, {1, 2}, {1, 0}};
    private JTextField entrada;
    private JLabel resultado, estadoActual, progreso, descripcionPaso;
    private JTextArea historial;
    private JButton preparar, paso, automatico, pausar, reiniciar;
    private JComboBox<String> ejemplos;
    private Diagrama diagrama;
    private String cadena = "";
    private int indice = 0, estado = 0, anterior = -1;
    private char ultimo = '\0';
    private boolean preparado = false, terminado = false;
    private Timer reloj;

    public Nivel3() {
        setLayout(new BorderLayout());
        setBackground(BG);

        JPanel contenido = new JPanel(new BorderLayout(0, 14));
        contenido.setBackground(BG);
        contenido.setBorder(new EmptyBorder(18, 22, 18, 22));

        JPanel encabezado = new JPanel(new BorderLayout(0, 6));
        encabezado.setOpaque(false);
        JLabel titulo = texto("AUTOMATA FINITO DETERMINISTA", 23, true, FG);
        JLabel subtitulo = texto("NIVEL 3  /  Lenguaje regular: cadenas terminadas en 'ab'", 13, false, MUTED);
        encabezado.add(titulo, BorderLayout.NORTH);
        encabezado.add(subtitulo, BorderLayout.SOUTH);
        contenido.add(encabezado, BorderLayout.NORTH);

        JPanel cuerpo = new JPanel(new BorderLayout(0, 12));
        cuerpo.setOpaque(false);
        contenido.add(cuerpo, BorderLayout.CENTER);

        JPanel controles = tarjeta(new BorderLayout(0, 10));
        JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        fila.setOpaque(false);
        fila.add(texto("CADENA", 12, true, MUTED));
        entrada = new JTextField(12);
        entrada.setFont(new Font("SansSerif", Font.PLAIN, 16));
        fila.add(entrada);
        preparar = boton("Preparar", BLUE);
        paso = boton("Siguiente paso", BLUE);
        automatico = boton("Automático", new Color(31, 130, 105));
        pausar = boton("Pausar", new Color(137, 99, 48));
        reiniciar = boton("Reiniciar", new Color(82, 97, 121));
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
            "Elige un ejemplo", "ab", "aab", "babab", "aba", "bbb", "ba", "Cadena vacía"
        });
        filaEjemplos.add(ejemplos);
        filaEjemplos.add(texto("Alfabeto: {a, b}  ·  Estado inicial: q0  ·  Final: q2", 12, false, MUTED));
        controles.add(filaEjemplos, BorderLayout.SOUTH);
        cuerpo.add(controles, BorderLayout.NORTH);

        JPanel tarjetaDiagrama = tarjeta(new BorderLayout(0, 8));
        JPanel cabeceraDiagrama = new JPanel(new BorderLayout());
        cabeceraDiagrama.setOpaque(false);
        cabeceraDiagrama.add(texto("DIAGRAMA DE TRANSICIONES", 14, true, FG), BorderLayout.WEST);
        estadoActual = texto("Estado actual: q0", 13, true, GREEN);
        cabeceraDiagrama.add(estadoActual, BorderLayout.EAST);
        tarjetaDiagrama.add(cabeceraDiagrama, BorderLayout.NORTH);
        diagrama = new Diagrama();
        diagrama.setPreferredSize(new Dimension(700, 220));
        tarjetaDiagrama.add(diagrama, BorderLayout.CENTER);
        cuerpo.add(tarjetaDiagrama, BorderLayout.CENTER);

        JPanel parteInferior = new JPanel(new BorderLayout(0, 10));
        parteInferior.setOpaque(false);
        JPanel estadoPanel = tarjeta(new GridLayout(3, 1, 0, 5));
        progreso = texto("Sin cadena preparada", 14, true, FG);
        descripcionPaso = texto("Selecciona un ejemplo o escribe tu propia cadena.", 13, false, MUTED);
        resultado = texto("ESPERANDO ENTRADA", 14, true, MUTED);
        estadoPanel.add(progreso);
        estadoPanel.add(descripcionPaso);
        estadoPanel.add(resultado);
        parteInferior.add(estadoPanel, BorderLayout.NORTH);

        JPanel tarjetaHistorial = tarjeta(new BorderLayout(0, 8));
        tarjetaHistorial.add(texto("REGISTRO DE EJECUCIÓN", 13, true, FG), BorderLayout.NORTH);
        historial = new JTextArea(6, 30);
        historial.setEditable(false);
        historial.setFont(new Font("Monospaced", Font.PLAIN, 13));
        historial.setBackground(new Color(21, 31, 47));
        historial.setForeground(FG);
        historial.setBorder(new EmptyBorder(8, 10, 8, 10));
        JScrollPane scroll = new JScrollPane(historial);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER));
        tarjetaHistorial.add(scroll, BorderLayout.CENTER);
        parteInferior.add(tarjetaHistorial, BorderLayout.CENTER);
        parteInferior.setPreferredSize(new Dimension(700, 257));
        cuerpo.add(parteInferior, BorderLayout.SOUTH);

        add(contenido, BorderLayout.CENTER);
        preparar.addActionListener(e -> prepararCadena());
        entrada.addActionListener(e -> prepararCadena());
        paso.addActionListener(e -> avanzar());
        automatico.addActionListener(e -> ejecutarAutomatico());
        pausar.addActionListener(e -> pausar());
        reiniciar.addActionListener(e -> reiniciar());
        ejemplos.addActionListener(e -> {
            int selected = ejemplos.getSelectedIndex();
            if (selected <= 0) return;
            entrada.setText(selected == 7 ? "" : String.valueOf(ejemplos.getSelectedItem()));
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
        JPanel panel = new JPanel(layout);
        panel.setBackground(CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1, true), new EmptyBorder(12, 15, 12, 15)));
        return panel;
    }

    private JLabel texto(String s, int size, boolean bold, Color color) {
        JLabel l = new JLabel(s);
        l.setFont(new Font("SansSerif", bold ? Font.BOLD : Font.PLAIN, size));
        l.setForeground(color);
        return l;
    }

    private JButton boton(String s, Color color) {
        JButton b = new JButton(s);
        b.setBackground(color);
        b.setForeground(Color.WHITE);
        b.setOpaque(true);
        b.setFocusPainted(false);
        b.setFont(new Font("SansSerif", Font.BOLD, 12));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private void prepararCadena() {
        detener();
        String s = entrada.getText().trim();
        if (!s.matches("[ab]*")) {
            preparado = false;
            terminado = false;
            resultado.setText("ERROR: solo se admiten símbolos a y b");
            resultado.setForeground(RED);
            descripcionPaso.setText("Corrige la entrada y pulsa Preparar.");
            actualizarBotones();
            return;
        }
        cadena = s;
        indice = 0;
        estado = 0;
        anterior = -1;
        ultimo = '\0';
        preparado = true;
        terminado = false;
        resultado.setText("SIMULACIÓN PREPARADA");
        resultado.setForeground(FG);
        historial.setText("AFD: cadenas terminadas en ab\nEntrada: " +
            (s.isEmpty() ? "(vacía)" : s) + "\nInicial: q0\n\n");
        actualizarVista();
        if (s.isEmpty()) finalizar();
        actualizarBotones();
    }

    private void avanzar() {
        if (!preparado || terminado) return;
        if (indice >= cadena.length()) { finalizar(); return; }
        ultimo = cadena.charAt(indice);
        anterior = estado;
        estado = DELTA[estado][ultimo == 'a' ? 0 : 1];
        indice++;
        historial.append(String.format("Paso %d: q%d --%c--> q%d%n", indice, anterior, ultimo, estado));
        historial.setCaretPosition(historial.getDocument().getLength());
        actualizarVista();
        if (indice == cadena.length()) finalizar();
    }

    private void finalizar() {
        if (terminado) return;
        terminado = true;
        detener();
        boolean aceptada = estado == 2;
        resultado.setText(aceptada ? "CADENA ACEPTADA" : "CADENA RECHAZADA");
        resultado.setForeground(aceptada ? GREEN : RED);
        descripcionPaso.setText(aceptada ? "La cadena termina en 'ab' y finaliza en q2." :
            "No terminó en el estado de aceptación q2.");
        historial.append("\nResultado: " + (aceptada ? "ACEPTADA" : "RECHAZADA") + "\n");
        actualizarBotones();
        diagrama.repaint();
    }

    private void actualizarVista() {
        estadoActual.setText("Estado actual: q" + estado);
        progreso.setText("Procesado: [" + cadena.substring(0, indice) + "]   Pendiente: [" +
            cadena.substring(indice) + "]   ·   " + indice + "/" + cadena.length());
        descripcionPaso.setText(indice == 0 ? "Listo para iniciar desde q0." :
            "Última transición: q" + anterior + " --" + ultimo + "--> q" + estado);
        diagrama.repaint();
    }

    private void ejecutarAutomatico() {
        if (!preparado || terminado) return;
        detener();
        reloj = new Timer(700, e -> avanzar());
        reloj.start();
        actualizarBotones();
    }

    private void pausar() {
        detener();
        actualizarBotones();
    }

    private void detener() {
        if (reloj != null) {
            reloj.stop();
            reloj = null;
        }
    }

    private void reiniciar() {
        detener();
        entrada.setText("");
        cadena = "";
        indice = 0;
        estado = 0;
        anterior = -1;
        ultimo = '\0';
        preparado = false;
        terminado = false;
        ejemplos.setSelectedIndex(0);
        historial.setText("");
        estadoActual.setText("Estado actual: q0");
        progreso.setText("Sin cadena preparada");
        descripcionPaso.setText("Selecciona un ejemplo o escribe tu propia cadena.");
        resultado.setText("ESPERANDO ENTRADA");
        resultado.setForeground(MUTED);
        actualizarBotones();
        diagrama.repaint();
    }

    private void actualizarBotones() {
        boolean listo = preparado && !terminado;
        boolean ejecutando = reloj != null;
        paso.setEnabled(listo && !ejecutando);
        automatico.setEnabled(listo && !ejecutando);
        pausar.setEnabled(ejecutando);
    }

    private class Diagrama extends JPanel {
        Diagrama() { setOpaque(false); }

        @Override protected void paintComponent(Graphics original) {
            super.paintComponent(original);
            Graphics2D g = (Graphics2D) original.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            double sx = getWidth() / 760.0;
            double sy = getHeight() / 220.0;
            g.scale(sx, sy);
            int[] xs = {135, 380, 625};
            int y = 106, r = 29;
            // Dibuja las seis transiciones del AFD.
            g.setStroke(new BasicStroke(2f));
            transicion(g, new QuadCurve2D.Double(xs[0] + r, y - 9, 255, 37, xs[1] - r, y - 9),
                0, 1, 'a', "a", 252, 52, 1, -0.28);
            transicion(g, new QuadCurve2D.Double(xs[1] + r, y - 9, 500, 37, xs[2] - r, y - 9),
                1, 2, 'b', "b", 497, 52, 1, -0.28);
            transicion(g, new QuadCurve2D.Double(xs[2] - r, y + 11, 499, 156, xs[1] + r, y + 11),
                2, 1, 'a', "a", 501, 151, -1, -0.2);
            transicion(g, new QuadCurve2D.Double(xs[2] - 17, y + 23, 375, 218, xs[0] + 17, y + 23),
                2, 0, 'b', "b", 380, 192, -1, -0.4);
            bucle(g, xs[0], y, 0, 'b');
            bucle(g, xs[1], y, 1, 'a');

            // Flecha de inicio
            g.setColor(MUTED);
            g.setStroke(new BasicStroke(2));
            g.drawLine(55, y, xs[0] - r - 3, y);
            punta(g, xs[0] - r - 3, y, 1, 0);
            for (int i = 0; i < 3; i++) {
                g.setColor(estado == i ? GREEN : BLUE);
                g.fillOval(xs[i] - r, y - r, 2 * r, 2 * r);
                g.setColor(estado == i ? GOLD : FG);
                g.setStroke(new BasicStroke(estado == i ? 3 : 2));
                g.drawOval(xs[i] - r, y - r, 2 * r, 2 * r);
                if (i == 2) g.drawOval(xs[i] - r + 6, y - r + 6, 2 * r - 12, 2 * r - 12);
                g.setColor(Color.WHITE);
                g.setFont(new Font("SansSerif", Font.BOLD, 17));
                g.drawString("q" + i, xs[i] - 11, y + 6);
            }
            g.dispose();
        }

        private void transicion(Graphics2D g, Shape path, int from, int to, char symbol,
                String label, int tx, int ty, double dx, double dy) {
            boolean activo = anterior == from && estado == to && ultimo == symbol;
            g.setColor(activo ? GREEN : MUTED);
            g.setStroke(new BasicStroke(activo ? 3.3f : 1.9f));
            g.draw(path);
            PathIterator it = path.getPathIterator(null, 0.5);
            double[] points = new double[6];
            double prevX = 0, prevY = 0, lastX = 0, lastY = 0;
            while (!it.isDone()) {
                int type = it.currentSegment(points);
                if (type == PathIterator.SEG_MOVETO || type == PathIterator.SEG_LINETO) {
                    prevX = lastX; prevY = lastY;
                    lastX = points[0]; lastY = points[1];
                }
                it.next();
            }
            punta(g, lastX, lastY, lastX - prevX, lastY - prevY);
            g.setFont(new Font("SansSerif", Font.BOLD, 14));
            g.drawString(label, tx, ty);
        }

        private void bucle(Graphics2D g, int x, int y, int q, char symbol) {
            boolean activo = anterior == q && estado == q && ultimo == symbol;
            g.setColor(activo ? GREEN : MUTED);
            g.setStroke(new BasicStroke(activo ? 3.3f : 1.9f));
            CubicCurve2D curva = new CubicCurve2D.Double(x - 15, y - 25,
                x - 65, y - 98, x + 65, y - 98, x + 15, y - 25);
            g.draw(curva);
            punta(g, x + 15, y - 25, -1, 1.5);
            g.setFont(new Font("SansSerif", Font.BOLD, 14));
            g.drawString(String.valueOf(symbol), x - 4, y - 86);
        }

        private void punta(Graphics2D g, double x, double y, double dx, double dy) {
            double a = Math.atan2(dy, dx), s = 9;
            Path2D p = new Path2D.Double();
            p.moveTo(x, y);
            p.lineTo(x - s * Math.cos(a - 0.48), y - s * Math.sin(a - 0.48));
            p.lineTo(x - s * Math.cos(a + 0.48), y - s * Math.sin(a + 0.48));
            p.closePath();
            g.fill(p);
        }
    }
}
