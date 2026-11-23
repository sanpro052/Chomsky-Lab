import java.awt.*;
import java.util.ArrayDeque;
import java.util.Deque;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/** Nivel 2: automata de pila determinista para L={a^n b^n | n>=1}. */
public class Nivel2 extends JPanel {
    private static final Color BG = new Color(16, 23, 37);
    private static final Color CARD = new Color(29, 39, 57);
    private static final Color BORDER = new Color(55, 70, 93);
    private static final Color FG = new Color(238, 243, 250);
    private static final Color MUTED = new Color(161, 178, 203);
    private static final Color BLUE = new Color(55, 115, 219);
    private static final Color PURPLE = new Color(151, 104, 225);
    private static final Color GREEN = new Color(43, 194, 136);
    private static final Color RED = new Color(239, 105, 112);
    private static final Color GOLD = new Color(252, 195, 89);

    private final Deque<Character> pila = new ArrayDeque<>();
    private JTextField entrada;
    private JLabel estadoActual, progreso, descripcionPaso, resultado, tamanoPila;
    private JTextArea historial;
    private JButton paso, automatico, pausar;
    private JComboBox<String> ejemplos;
    private VistaPila visual;
    private String cadena = "";
    private int indice = 0;
    private String estado = "q0";
    private boolean preparado = false, terminado = false;
    private Timer reloj;

    public Nivel2() {
        setLayout(new BorderLayout());
        setBackground(BG);
        JPanel contenido = new JPanel(new BorderLayout(0, 14));
        contenido.setBackground(BG);
        contenido.setBorder(new EmptyBorder(18, 22, 18, 22));

        JPanel encabezado = new JPanel(new BorderLayout(0, 6));
        encabezado.setOpaque(false);
        encabezado.add(texto("AUTOMATA DE PILA", 23, true, FG), BorderLayout.NORTH);
        encabezado.add(texto("NIVEL 2  /  Lenguaje libre de contexto: a^n b^n, n >= 1", 13, false, MUTED), BorderLayout.SOUTH);
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
            "Elige un ejemplo", "ab", "aabb", "aaabbb", "aab", "abb", "abab", "Cadena vacía"
        });
        filaEjemplos.add(ejemplos);
        filaEjemplos.add(texto("Alfabeto: {a, b}  ·  Fondo: Z  ·  Cima: A", 12, false, MUTED));
        controles.add(filaEjemplos, BorderLayout.SOUTH);
        cuerpo.add(controles, BorderLayout.NORTH);

        JPanel panelVisual = tarjeta(new BorderLayout(0, 8));
        JPanel cabecera = new JPanel(new BorderLayout());
        cabecera.setOpaque(false);
        cabecera.add(texto("ESTADOS Y MEMORIA LIFO", 14, true, FG), BorderLayout.WEST);
        estadoActual = texto("Estado: q0", 13, true, GREEN);
        cabecera.add(estadoActual, BorderLayout.EAST);
        panelVisual.add(cabecera, BorderLayout.NORTH);
        visual = new VistaPila();
        visual.setPreferredSize(new Dimension(700, 230));
        panelVisual.add(visual, BorderLayout.CENTER);
        tamanoPila = texto("PILA: Z  ·  PUSH = apilar  ·  POP = desapilar", 12, false, MUTED);
        panelVisual.add(tamanoPila, BorderLayout.SOUTH);
        cuerpo.add(panelVisual, BorderLayout.CENTER);

        JPanel inferior = new JPanel(new BorderLayout(0, 10));
        inferior.setOpaque(false);
        JPanel panelEstado = tarjeta(new GridLayout(3, 1, 0, 5));
        progreso = texto("Sin cadena preparada", 14, true, FG);
        descripcionPaso = texto("Selecciona un ejemplo o escribe tu propia cadena.", 13, false, MUTED);
        resultado = texto("ESPERANDO ENTRADA", 14, true, MUTED);
        panelEstado.add(progreso);
        panelEstado.add(descripcionPaso);
        panelEstado.add(resultado);
        inferior.add(panelEstado, BorderLayout.NORTH);

        JPanel panelHistorial = tarjeta(new BorderLayout(0, 8));
        panelHistorial.add(texto("REGISTRO DE EJECUCIÓN", 13, true, FG), BorderLayout.NORTH);
        historial = new JTextArea(6, 30);
        historial.setEditable(false);
        historial.setFont(new Font("Monospaced", Font.PLAIN, 13));
        historial.setBackground(new Color(21, 31, 47));
        historial.setForeground(FG);
        historial.setBorder(new EmptyBorder(8, 10, 8, 10));
        JScrollPane scroll = new JScrollPane(historial);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER));
        panelHistorial.add(scroll, BorderLayout.CENTER);
        inferior.add(panelHistorial, BorderLayout.CENTER);
        inferior.setPreferredSize(new Dimension(700, 257));
        cuerpo.add(inferior, BorderLayout.SOUTH);

        add(contenido, BorderLayout.CENTER);
        pila.push('Z');
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
            if ((e.getChangeFlags() & java.awt.event.HierarchyEvent.SHOWING_CHANGED) != 0 && !isShowing()) pausar();
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
        estado = "q0";
        pila.clear();
        pila.push('Z');
        preparado = true;
        terminado = false;
        resultado.setText("SIMULACIÓN PREPARADA");
        resultado.setForeground(FG);
        historial.setText("AP: L = {a^n b^n, n >= 1}\nEntrada: " +
            (s.isEmpty() ? "(vacía)" : s) + "\nPila inicial: [Z]\n\n");
        descripcionPaso.setText("Por cada 'a' se apila A; por cada 'b' se desapila A.");
        actualizarVista();
        if (s.isEmpty()) rechazar("La cadena vacía no pertenece al lenguaje.");
        actualizarBotones();
    }

    private void avanzar() {
        if (!preparado || terminado) return;
        if (indice >= cadena.length()) { comprobarFin(); return; }
        char simbolo = cadena.charAt(indice);
        String previo = estado;
        String accion;
        if (estado.equals("q0")) {
            if (simbolo == 'a') {
                pila.push('A');
                accion = "PUSH(A)";
            } else if (pila.size() > 1) {
                pila.pop();
                estado = "q1";
                accion = "POP(A)";
            } else {
                indice++;
                rechazar("Se leyó una 'b' sin una 'a' correspondiente.");
                return;
            }
        } else if (estado.equals("q1")) {
            if (simbolo == 'b' && pila.size() > 1) {
                pila.pop();
                accion = "POP(A)";
            } else {
                indice++;
                rechazar(simbolo == 'a' ? "Una 'a' apareció después de comenzar las 'b'." :
                    "Hay más letras 'b' que letras 'a'.");
                return;
            }
        } else return;
        indice++;
        historial.append("Paso " + indice + ": " + previo + " --" + simbolo + "--> " + estado +
            " | " + accion + " | Pila: " + cadenaPila() + "\n");
        descripcionPaso.setText("Leído '" + simbolo + "' · " + accion + " · " + previo + " → " + estado);
        actualizarVista();
        if (indice == cadena.length()) comprobarFin();
    }

    private void comprobarFin() {
        if (terminado) return;
        if (estado.equals("q1") && pila.size() == 1 && pila.peek() == 'Z') {
            estado = "qf";
            terminado = true;
            detener();
            resultado.setText("CADENA ACEPTADA");
            resultado.setForeground(GREEN);
            descripcionPaso.setText("Se consumió toda la cadena y solo queda Z en la pila.");
            historial.append("Transición final: q1 → qf (fin de entrada, cima Z)\nResultado: ACEPTADA\n");
            actualizarVista();
            actualizarBotones();
        } else rechazar("Quedaron símbolos sin correspondencia o nunca se procesaron las 'b'.");
    }

    private void rechazar(String motivo) {
        if (terminado) return;
        estado = "qr";
        terminado = true;
        detener();
        resultado.setText("CADENA RECHAZADA");
        resultado.setForeground(RED);
        descripcionPaso.setText(motivo);
        historial.append("Rechazo: " + motivo + "\nResultado: RECHAZADA\n");
        actualizarVista();
        actualizarBotones();
    }

    private String cadenaPila() {
        StringBuilder b = new StringBuilder("[");
        boolean primero = true;
        for (char c : pila) {
            if (!primero) b.append(", ");
            b.append(c);
            primero = false;
        }
        return b.append(']').toString();
    }

    private void actualizarVista() {
        estadoActual.setText("Estado: " + estado);
        progreso.setText("Procesado: [" + cadena.substring(0, indice) + "]   Pendiente: [" +
            cadena.substring(indice) + "]   ·   " + indice + "/" + cadena.length());
        tamanoPila.setText("PILA: " + pila.size() + " elemento(s)  ·  Cima: " +
            (pila.isEmpty() ? "—" : pila.peek()) + "  ·  PUSH = apilar  ·  POP = desapilar");
        historial.setCaretPosition(historial.getDocument().getLength());
        visual.repaint();
    }

    private void ejecutarAutomatico() {
        if (!preparado || terminado) return;
        detener();
        reloj = new Timer(750, e -> avanzar());
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

    private void actualizarBotones() {
        boolean habilitado = preparado && !terminado;
        boolean corriendo = reloj != null && reloj.isRunning();
        paso.setEnabled(habilitado && !corriendo);
        automatico.setEnabled(habilitado && !corriendo);
        pausar.setEnabled(corriendo);
    }

    private void reiniciar() {
        detener();
        cadena = "";
        indice = 0;
        estado = "q0";
        pila.clear();
        pila.push('Z');
        preparado = false;
        terminado = false;
        entrada.setText("");
        historial.setText("");
        estadoActual.setText("Estado: q0");
        progreso.setText("Sin cadena preparada");
        descripcionPaso.setText("Selecciona un ejemplo o escribe tu propia cadena.");
        resultado.setText("ESPERANDO ENTRADA");
        resultado.setForeground(MUTED);
        tamanoPila.setText("PILA: Z  ·  PUSH = apilar  ·  POP = desapilar");
        ejemplos.setSelectedIndex(0);
        visual.repaint();
        actualizarBotones();
    }

    private class VistaPila extends JPanel {
        VistaPila() { setBackground(new Color(23, 32, 49)); }

        @Override protected void paintComponent(Graphics original) {
            super.paintComponent(original);
            Graphics2D g = (Graphics2D) original.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            int areaEstados = Math.max(240, (int)(w * 0.61));
            g.setFont(new Font("SansSerif", Font.BOLD, 12));
            g.setColor(MUTED);
            g.drawString("CONTROL DE ESTADOS", 22, 25);
            g.drawString("MEMORIA LIFO (CIMA ARRIBA)", areaEstados + 8, 25);

            String[] nombres = {"q0", "q1", "qf", "qr"};
            String[] notas = {"Apilar", "Desapilar", "Aceptar", "Rechazar"};
            int radio = 26;
            int espacio = Math.max(60, (areaEstados - 34) / 4);
            int cy = Math.max(100, h / 2);
            for (int i = 0; i < nombres.length; i++) {
                int cx = 20 + espacio * (i + 1);
                if (i < nombres.length - 1) {
                    int fin = cx + espacio - radio - 6;
                    g.setColor(BORDER);
                    g.setStroke(new BasicStroke(2));
                    g.drawLine(cx + radio, cy, fin, cy);
                    g.fillPolygon(new int[]{fin, fin-7, fin-7}, new int[]{cy, cy-5, cy+5}, 3);
                }
                g.setColor(estado.equals(nombres[i]) ? (estado.equals("qr") ? RED : GREEN) : BLUE);
                g.fillOval(cx-radio, cy-radio, radio*2, radio*2);
                g.setColor(FG);
                g.setStroke(new BasicStroke(2));
                g.drawOval(cx-radio, cy-radio, radio*2, radio*2);
                if (i == 2) g.drawOval(cx-radio+6, cy-radio+6, (radio-6)*2, (radio-6)*2);
                g.setFont(new Font("SansSerif", Font.BOLD, 15));
                FontMetrics fm = g.getFontMetrics();
                g.drawString(nombres[i], cx-fm.stringWidth(nombres[i])/2, cy+5);
                g.setFont(new Font("SansSerif", Font.PLAIN, 11));
                g.setColor(MUTED);
                g.drawString(notas[i], cx-g.getFontMetrics().stringWidth(notas[i])/2, cy+radio+23);
            }
            g.setColor(MUTED);
            g.setFont(new Font("SansSerif", Font.PLAIN, 12));
            g.drawString("q0: leer 'a' → PUSH(A)", 25, h-35);
            g.drawString("q1: leer 'b' → POP(A)", 25, h-17);

            int cellH = 27, cellW = 72;
            int stackX = Math.min(w-94, areaEstados+26);
            int base = h-20;
            int capacidad = Math.max(1, (h-85)/cellH);
            Object[] elementos = pila.toArray();
            int visibles = Math.min(capacidad, elementos.length);
            // Pila almacenada cima -> fondo; dibujar fondo -> cima.
            g.setColor(MUTED);
            g.setStroke(new BasicStroke(3));
            g.drawLine(stackX-7, base, stackX+cellW+7, base);
            g.drawLine(stackX-7, 41, stackX-7, base);
            g.drawLine(stackX+cellW+7, 41, stackX+cellW+7, base);
            for (int i=0; i<visibles; i++) {
                char simbolo = (char)elementos[elementos.length-1-i];
                int y = base-(i+1)*cellH;
                g.setColor(i==visibles-1 ? GREEN : PURPLE);
                g.fillRoundRect(stackX, y, cellW, cellH-3, 7, 7);
                g.setColor(FG);
                g.setFont(new Font("SansSerif", Font.BOLD, 15));
                String s = String.valueOf(simbolo);
                g.drawString(s, stackX+(cellW-g.getFontMetrics().stringWidth(s))/2, y+18);
            }
            if (elementos.length > visibles) {
                g.setColor(GOLD);
                g.setFont(new Font("SansSerif", Font.BOLD, 11));
                g.drawString("+"+(elementos.length-visibles)+" ocultos", stackX-5, 38);
            }
            g.dispose();
        }
    }
}
