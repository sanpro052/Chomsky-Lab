# CHOMSKY LAB — Simulador interactivo de la Jerarquía de Chomsky

Aplicación de escritorio desarrollada en **Java Swing** que permite explorar y ejecutar modelos representativos de los cuatro niveles de la Jerarquía de Chomsky. Incluye navegación lateral, ejemplos de entrada, controles paso a paso y ejecución automática, visualizaciones de memoria y registros de ejecución.

## Requisitos

- **JDK 8 o superior** instalado (se recomienda una versión LTS actual de Java).
- Visual Studio Code con **Extension Pack for Java** (opcional); también funciona desde la terminal.
- No requiere bibliotecas externas: utiliza Java estándar (`javax.swing`, `java.awt` y colecciones de Java).

## Estructura del proyecto

```text
ProyectoAutomatas/
├── src/                  # Copia aquí las cinco clases Java (sin package)
│   ├── Main.java
│   ├── Nivel3.java
│   ├── Nivel2.java
│   ├── Nivel1.java
│   └── Nivel0.java
├── .vscode/
│   └── settings.json     # Configuración de VS Code
├── docs/                 # Documento explicativo
├── .gitignore            # Ignora archivos compilados
├── ejecutar.bat          # Compilar y ejecutar en Windows
└── README.md             # Manual de usuario
```

**Antes de ejecutar:** copia tus cinco archivos `.java` actuales en la carpeta `src`, sin cambiar su contenido ni añadir declaraciones `package`.

## Ejecución

1. Abre la carpeta `ProyectoAutomatas` en Visual Studio Code.
2. Copia dentro de `src` los cinco archivos Java de tu proyecto.
3. En Windows, puedes abrir `ejecutar.bat` desde el Explorador de archivos para compilar y arrancar el programa.
4. Alternativamente, abre una terminal en la carpeta principal y ejecuta:

```powershell
javac -encoding UTF-8 -d out src\*.java
java -cp out Main
```

El directorio `out` contiene los archivos `.class` compilados y no debe subirse a GitHub. Si la terminal no reconoce `javac`, comprueba que tengas instalado el JDK y configurado el PATH.

## Uso general

1. Selecciona un nivel mediante el menú lateral o las tarjetas de Inicio.
2. Escribe una entrada o selecciona una opción de **Pruebas rápidas**.
3. Pulsa **Preparar** para inicializar la máquina.
4. Pulsa **Siguiente paso** para observar una transición o **Automático** para ejecutar varias.
5. Usa **Pausar** (cuando esté disponible), **Reiniciar** y la navegación lateral para explorar los módulos.
6. Examina la representación gráfica, el estado activo y el registro de ejecución.

## Ejemplos de prueba

| Nivel | Modelo | Entrada aceptada / salida | Entrada rechazada o inválida |
|---|---|---|---|
| **3** | AFD: cadenas sobre `{a,b}` que terminan en `ab` | `ab`, `aab`, `babab` | `aba`, `bbb`, `ba`, `abc` (símbolo inválido) |
| **2** | AP: `aⁿbⁿ`, `n ≥ 1` | `ab`, `aabb`, `aaabbb` | `aab`, `abb`, `abab`, cadena vacía |
| **1** | LBA: `aⁿbⁿcⁿ`, `n ≥ 1` | `abc`, `aabbcc`, `aaabbbccc` | `aabbc`, `abcc`, `ababcc`, cadena vacía |
| **0** | Máquina de Turing: incremento unario `f(n)=n+1` | `1 → 11`, `111 → 1111`, entrada vacía → `1` | `101`, `abc` (entradas inválidas) |

**Importante:** en el Nivel 0 la cadena vacía representa el número cero, no un rechazo. El módulo realiza una función aritmética y muestra la salida unaria resultante. Por diseño, los niveles 1 y 0 limitan la **entrada de demostración** a 60 símbolos; esto no cambia las propiedades teóricas de los modelos.

## Funcionamiento de cada nivel

### Nivel 3 — Autómata finito determinista (AFD)

Lenguaje: cadenas de `{a,b}` que terminan en `ab`. Estados `q0` (inicial), `q1` (última `a`) y `q2` (aceptación). El diagrama muestra el estado activo y las transiciones durante el procesamiento.

### Nivel 2 — Autómata de pila (AP)

Lenguaje: `aⁿbⁿ`, con `n ≥ 1`. Cada `a` apila `A` (PUSH); cada `b` desapila `A` (POP). La pila utiliza `Z` como marcador de fondo. Se acepta cuando termina la entrada con cantidades iguales y orden correcto.

### Nivel 1 — Autómata linealmente acotado (LBA)

Lenguaje: `aⁿbⁿcⁿ`, con `n ≥ 1`. La cinta tiene los delimitadores `⊢` y `⊣`, que no pueden atravesarse. La máquina marca cada terna `a, b, c` como `X, Y, Z` y verifica que no queden símbolos sin procesar.

### Nivel 0 — Máquina de Turing

Función: incrementar en uno un número unario. Alfabeto de entrada `{1}`; alfabeto de cinta `{1, □}`. La máquina recorre los unos hasta el primer blanco, escribe `1`, vuelve hacia la izquierda y finaliza. La cinta puede extenderse al requerirse celdas adicionales.

## Notas técnicas

- Lenguaje: Java. Interfaz: Swing / AWT.
- Navegación: `CardLayout` en `Main.java`.
- Simulación automática: temporizadores `javax.swing.Timer`.
- Colecciones: `ArrayDeque` para la pila y `ArrayList<Character>` para la cinta extensible.
- Los módulos heredan de `JPanel` y se incorporan al panel principal sin abrir ventanas secundarias.

## Verificación previa a la entrega

- [ ] Compila desde una terminal limpia con los cinco `.java`.
- [ ] Se abren los cuatro niveles desde el menú lateral.
- [ ] Se ejecutan ejemplos correctos e incorrectos en cada nivel.
- [ ] Funcionan paso a paso, ejecución automática, pausa y reinicio donde corresponde.
- [ ] El cabezal no atraviesa los límites del LBA.
- [ ] La cinta del Nivel 0 puede incorporar una nueva celda.
- [ ] El repositorio es público y contiene este README.

## Autores y curso

**Proyecto final de Teoría de Autómatas.*Manuel Santiago Sequeda Pinilla
Politecnico GranColombiano
Automatas programables
