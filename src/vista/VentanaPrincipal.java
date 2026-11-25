package vista;

import modelo.Cliente;
import modelo.DetalleVenta;
import modelo.FormatoDigital;
import modelo.Libreria;
import modelo.LibroDigital;
import modelo.LibroImpreso;
import modelo.Papeleria;
import modelo.Producto;
import modelo.Venta;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;

public class VentanaPrincipal extends JFrame {
    private static final Color AZUL_CLARO = new Color(0xE7F1FA);
    private static final Color AZUL_FONDO = new Color(0xA7C7E7);
    private static final Color AZUL_OSCURO = new Color(0x2C5E8A);
    private static final Color AZUL_MEDIO = new Color(0x6E9FC5);
    private static final Color BLANCO = Color.WHITE;
    private static final Color TEXTO = new Color(0x18344D);
    private static final Font FUENTE_NORMAL = new Font("SansSerif", Font.PLAIN, 18);
    private static final Font FUENTE_BOTON = new Font("SansSerif", Font.BOLD, 20);
    private static final Font FUENTE_TITULO = new Font("SansSerif", Font.BOLD, 30);

    private final Libreria libreria;
    private final CardLayout tarjetas;
    private final JPanel contenido;

    private final JLabel codigoAutomatico;
    private final JTextField nombreProducto;
    private final JTextField precioProducto;
    private final JComboBox<String> tipoProducto;
    private final CardLayout tarjetasTipo;
    private final JPanel panelesTipo;
    private final JTextField autorImpreso;
    private final JTextField stockImpreso;
    private final JTextField autorDigital;
    private final JLabel formatosDigitales;
    private final JTextField marcaPapeleria;
    private final JTextField stockPapeleria;
    private final JTextField busqueda;
    private final DefaultTableModel modeloProductos;
    private final JTable tablaProductos;

    private final JComboBox<Cliente> clientesVenta;
    private final JComboBox<Producto> productosVenta;
    private final JComboBox<FormatoDigital> formatoVenta;
    private final JLabel disponiblesVenta;
    private final JSpinner cantidadVenta;
    private final DefaultTableModel modeloVenta;
    private final JTable tablaVenta;
    private Venta ventaActual;

    public VentanaPrincipal(Libreria libreria) {
        super("Librería Páginas Andinas");
        this.libreria = libreria;
        this.tarjetas = new CardLayout();
        this.contenido = new JPanel(tarjetas);
        this.codigoAutomatico = new JLabel();
        this.nombreProducto = new JTextField(14);
        this.precioProducto = new JTextField(10);
        this.tipoProducto = new JComboBox<>(new String[]{"Libro impreso", "Libro digital", "Papelería"});
        this.tarjetasTipo = new CardLayout();
        this.panelesTipo = new JPanel(tarjetasTipo);
        this.autorImpreso = new JTextField(14);
        this.stockImpreso = new JTextField(6);
        this.autorDigital = new JTextField(14);
        this.formatosDigitales = new JLabel("PDF y EPUB");
        this.marcaPapeleria = new JTextField(14);
        this.stockPapeleria = new JTextField(6);
        this.busqueda = new JTextField(22);
        this.modeloProductos = crearModeloTabla("Código", "Nombre", "Tipo", "Detalles", "Precio", "Existencias");
        this.tablaProductos = new JTable(modeloProductos);
        this.clientesVenta = new JComboBox<>(libreria.getClientes().toArray(new Cliente[0]));
        this.productosVenta = new JComboBox<>(libreria.getProductos().toArray(new Producto[0]));
        this.formatoVenta = new JComboBox<>(FormatoDigital.values());
        this.disponiblesVenta = new JLabel();
        this.cantidadVenta = new JSpinner(new SpinnerNumberModel(1, 1, 1, 1));
        this.modeloVenta = crearModeloTabla("Producto", "Cantidad", "Precio unitario", "Subtotal");
        this.tablaVenta = new JTable(modeloVenta);
        this.ventaActual = new Venta((Cliente) clientesVenta.getSelectedItem());

        configurarVentana();
        construirInterfaz();
        actualizarTablaProductos();
    }

    private void configurarVentana() {
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1100, 800);
        setMinimumSize(new java.awt.Dimension(900, 680));
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    private void construirInterfaz() {
        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(AZUL_FONDO);
        raiz.add(crearEncabezado(), BorderLayout.NORTH);
        contenido.add(crearMenuPrincipal(), "menu");
        contenido.add(crearPanelProductos(), "productos");
        contenido.add(crearPanelVentas(), "ventas");
        raiz.add(contenido, BorderLayout.CENTER);
        raiz.add(crearBarraEstado(), BorderLayout.SOUTH);
        setContentPane(raiz);
    }

    private JPanel crearEncabezado() {
        JPanel encabezado = new JPanel(new GridLayout(1, 3, 12, 0));
        encabezado.setBackground(AZUL_OSCURO);
        encabezado.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));

        JButton ayuda = crearBoton("❔  Ayuda", BLANCO, AZUL_OSCURO);
        ayuda.setHorizontalAlignment(SwingConstants.LEFT);
        ayuda.addActionListener(e -> mostrarAyuda());

        JLabel titulo = new JLabel("PÁGINAS ANDINAS", SwingConstants.CENTER);
        titulo.setFont(FUENTE_TITULO);
        titulo.setForeground(BLANCO);

        JButton cerrar = crearBoton("Cerrar", new Color(0xFFF0E8), new Color(0x8B2E18));
        cerrar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xF28C28), 4),
                BorderFactory.createEmptyBorder(8, 22, 8, 22)));
        cerrar.addActionListener(e -> confirmarCierre());

        encabezado.add(ayuda);
        encabezado.add(titulo);
        encabezado.add(cerrar);
        return encabezado;
    }

    private JPanel crearMenuPrincipal() {
        JPanel pantalla = new JPanel(new BorderLayout(24, 24));
        pantalla.setBackground(AZUL_FONDO);
        pantalla.setBorder(BorderFactory.createEmptyBorder(36, 48, 36, 48));

        JLabel indicacion = new JLabel("¿Qué desea hacer?", SwingConstants.CENTER);
        indicacion.setFont(new Font("SansSerif", Font.BOLD, 28));
        indicacion.setForeground(TEXTO);
        pantalla.add(indicacion, BorderLayout.NORTH);

        JPanel opciones = new JPanel(new GridLayout(1, 2, 32, 24));
        opciones.setBackground(AZUL_FONDO);
        JButton productos = crearBoton("<html><center>📚<br>PRODUCTOS<br><span style='font-size:18pt'>Registrar y consultar</span></center></html>",
                BLANCO, AZUL_OSCURO);
        JButton ventas = crearBoton("<html><center>🛒<br>VENTAS<br><span style='font-size:18pt'>Preparar una venta</span></center></html>",
                BLANCO, AZUL_OSCURO);
        productos.setFont(new Font("SansSerif", Font.BOLD, 28));
        ventas.setFont(new Font("SansSerif", Font.BOLD, 28));
        productos.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AZUL_OSCURO, 3),
                BorderFactory.createEmptyBorder(30, 20, 30, 20)));
        ventas.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AZUL_OSCURO, 3),
                BorderFactory.createEmptyBorder(30, 20, 30, 20)));
        productos.addActionListener(e -> mostrarPantalla("productos"));
        ventas.addActionListener(e -> mostrarPantalla("ventas"));
        opciones.add(productos);
        opciones.add(ventas);
        pantalla.add(opciones, BorderLayout.CENTER);
        return pantalla;
    }

    private JPanel crearBarraEstado() {
        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(AZUL_MEDIO);
        barra.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));

        JLabel fecha = new JLabel("Fecha: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        fecha.setFont(FUENTE_BOTON);
        fecha.setForeground(Color.WHITE);

        JLabel reloj = new JLabel("", SwingConstants.RIGHT);
        reloj.setFont(FUENTE_BOTON);
        reloj.setForeground(Color.WHITE);
        Runnable actualizarReloj = () ->
                reloj.setText("Hora: " + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        actualizarReloj.run();
        Timer temporizador = new Timer(1000, e -> actualizarReloj.run());
        temporizador.start();

        barra.add(fecha, BorderLayout.WEST);
        barra.add(reloj, BorderLayout.EAST);
        return barra;
    }

    private JPanel crearPanelProductos() {
        JPanel pantalla = crearPantallaSeccion("PRODUCTOS");
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setOpaque(false);
        panel.add(crearFormularioProducto(), BorderLayout.NORTH);

        JPanel lista = new JPanel(new BorderLayout(8, 8));
        lista.setBackground(AZUL_CLARO);
        JPanel filaBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        filaBusqueda.setOpaque(false);
        filaBusqueda.add(crearEtiqueta("Buscar por nombre:"));
        prepararCampo(busqueda);
        filaBusqueda.add(busqueda);
        lista.add(filaBusqueda, BorderLayout.NORTH);
        prepararTabla(tablaProductos);
        lista.add(new JScrollPane(tablaProductos), BorderLayout.CENTER);
        panel.add(lista, BorderLayout.CENTER);
        pantalla.add(panel, BorderLayout.CENTER);

        busqueda.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                actualizarTablaProductos();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                actualizarTablaProductos();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                actualizarTablaProductos();
            }
        });
        return pantalla;
    }

    private JPanel crearFormularioProducto() {
        JPanel formulario = new JPanel();
        formulario.setLayout(new BoxLayout(formulario, BoxLayout.Y_AXIS));
        formulario.setBackground(AZUL_CLARO);
        formulario.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(AZUL_OSCURO, 2), "Registrar producto"));

        prepararCampo(nombreProducto);
        prepararCampo(precioProducto);
        prepararCampo(autorImpreso);
        prepararCampo(stockImpreso);
        prepararCampo(autorDigital);
        prepararCampo(marcaPapeleria);
        prepararCampo(stockPapeleria);
        prepararCombo(tipoProducto);
        codigoAutomatico.setFont(FUENTE_NORMAL);
        codigoAutomatico.setForeground(TEXTO);
        formatosDigitales.setFont(FUENTE_NORMAL);
        formatosDigitales.setForeground(TEXTO);

        formulario.add(fila(crearEtiqueta("Tipo:"), tipoProducto, codigoAutomatico));
        formulario.add(fila(crearEtiqueta("Nombre:"), nombreProducto,
                crearEtiqueta("Precio:"), precioProducto));
        panelesTipo.setOpaque(false);
        panelesTipo.add(crearCamposLibroImpreso(), "Libro impreso");
        panelesTipo.add(crearCamposLibroDigital(), "Libro digital");
        panelesTipo.add(crearCamposPapeleria(), "Papelería");
        formulario.add(panelesTipo);

        JButton botonRegistrar = crearBoton("Registrar producto", AZUL_OSCURO, BLANCO);
        JPanel filaBoton = fila(botonRegistrar);
        filaBoton.setOpaque(false);
        formulario.add(filaBoton);

        tipoProducto.addActionListener(e ->
                tarjetasTipo.show(panelesTipo, (String) tipoProducto.getSelectedItem()));
        tipoProducto.addActionListener(e -> actualizarCodigoAutomatico());
        actualizarCodigoAutomatico();
        botonRegistrar.addActionListener(e -> registrarProductoDesdeFormulario());
        return formulario;
    }

    private JPanel crearCamposLibroImpreso() {
        JPanel panel = fila(crearEtiqueta("Autor:"), autorImpreso,
                crearEtiqueta("Existencias:"), stockImpreso);
        panel.setOpaque(false);
        return panel;
    }

    private JPanel crearCamposLibroDigital() {
        JPanel panel = fila(crearEtiqueta("Autor:"), autorDigital,
                crearEtiqueta("Formatos disponibles:"), formatosDigitales);
        panel.setOpaque(false);
        return panel;
    }

    private JPanel crearCamposPapeleria() {
        JPanel panel = fila(crearEtiqueta("Marca:"), marcaPapeleria,
                crearEtiqueta("Existencias:"), stockPapeleria);
        panel.setOpaque(false);
        return panel;
    }

    private JPanel crearPanelVentas() {
        JPanel pantalla = crearPantallaSeccion("VENTAS");
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBackground(AZUL_CLARO);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(AZUL_OSCURO, 2), "Preparar una venta"));

        JPanel controles = new JPanel(new GridLayout(0, 1, 8, 8));
        controles.setBackground(AZUL_CLARO);
        prepararCombo(clientesVenta);
        prepararCombo(productosVenta);
        prepararCombo(formatoVenta);
        prepararSpinner(cantidadVenta);
        disponiblesVenta.setFont(FUENTE_NORMAL);
        disponiblesVenta.setForeground(TEXTO);
        controles.add(fila(crearEtiqueta("Cliente:"), clientesVenta));
        controles.add(fila(crearEtiqueta("Producto:"), productosVenta,
                disponiblesVenta, crearEtiqueta("Cantidad:"), cantidadVenta));
        controles.add(fila(crearEtiqueta("Formato digital:"), formatoVenta));

        JButton botonAgregar = crearBoton("Agregar producto", AZUL_OSCURO, BLANCO);
        JButton botonFinalizar = crearBoton("Finalizar venta", new Color(0x276738), BLANCO);
        controles.add(fila(botonAgregar, botonFinalizar));
        panel.add(controles, BorderLayout.NORTH);

        prepararTabla(tablaVenta);
        panel.add(new JScrollPane(tablaVenta), BorderLayout.CENTER);
        JLabel indicacion = crearEtiqueta("Agregue los productos y finalice para ver el total.");
        panel.add(indicacion, BorderLayout.SOUTH);
        pantalla.add(panel, BorderLayout.CENTER);

        clientesVenta.addActionListener(e -> cambiarClienteVenta());
        productosVenta.addActionListener(e -> actualizarDisponibilidadVenta());
        productosVenta.addActionListener(e -> actualizarFormatosVenta());
        botonAgregar.addActionListener(e -> agregarProductoAVenta());
        botonFinalizar.addActionListener(e -> finalizarVenta());
        actualizarDisponibilidadVenta();
        actualizarFormatosVenta();
        return pantalla;
    }

    private JPanel crearPantallaSeccion(String titulo) {
        JPanel pantalla = new JPanel(new BorderLayout(12, 12));
        pantalla.setBackground(AZUL_FONDO);
        pantalla.setBorder(BorderFactory.createEmptyBorder(18, 24, 24, 24));

        JPanel navegacion = new JPanel(new BorderLayout());
        navegacion.setOpaque(false);
        JButton volver = crearBoton("←  Menú principal", AZUL_OSCURO, BLANCO);
        volver.addActionListener(e -> mostrarPantalla("menu"));
        JLabel encabezado = new JLabel(titulo, SwingConstants.CENTER);
        encabezado.setFont(new Font("SansSerif", Font.BOLD, 26));
        encabezado.setForeground(TEXTO);
        navegacion.add(volver, BorderLayout.WEST);
        navegacion.add(encabezado, BorderLayout.CENTER);
        pantalla.add(navegacion, BorderLayout.NORTH);
        return pantalla;
    }

    private JPanel fila(Component... componentes) {
        JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        for (Component componente : componentes) {
            fila.add(componente);
        }
        return fila;
    }

    private JLabel crearEtiqueta(String texto) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(FUENTE_NORMAL);
        etiqueta.setForeground(TEXTO);
        return etiqueta;
    }

    private JButton crearBoton(String texto, Color fondo, Color letra) {
        JButton boton = new JButton(texto);
        boton.setFont(FUENTE_BOTON);
        boton.setForeground(letra);
        boton.setBackground(fondo);
        boton.setFocusPainted(true);
        boton.setOpaque(true);
        boton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(fondo.darker(), 2),
                BorderFactory.createEmptyBorder(10, 18, 10, 18)));
        return boton;
    }

    private void prepararCampo(JTextField campo) {
        campo.setFont(FUENTE_NORMAL);
        campo.setForeground(TEXTO);
        campo.setBackground(BLANCO);
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AZUL_MEDIO, 1),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));
    }

    private void prepararCombo(JComboBox<?> combo) {
        combo.setFont(FUENTE_NORMAL);
        combo.setForeground(TEXTO);
        combo.setBackground(BLANCO);
        combo.setMaximumRowCount(12);
    }

    private void prepararSpinner(JSpinner spinner) {
        spinner.setFont(FUENTE_NORMAL);
        spinner.setPreferredSize(new java.awt.Dimension(90, 42));
        Component editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            ((JSpinner.DefaultEditor) editor).getTextField().setFont(FUENTE_NORMAL);
        }
    }

    private void prepararTabla(JTable tabla) {
        tabla.setFont(FUENTE_NORMAL);
        tabla.setForeground(TEXTO);
        tabla.setBackground(BLANCO);
        tabla.setRowHeight(36);
        tabla.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tabla.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 17));
        tabla.getTableHeader().setBackground(AZUL_MEDIO);
        tabla.getTableHeader().setForeground(Color.WHITE);
    }

    private DefaultTableModel crearModeloTabla(String... columnas) {
        return new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private void mostrarPantalla(String nombre) {
        tarjetas.show(contenido, nombre);
    }

    private void actualizarTablaProductos() {
        modeloProductos.setRowCount(0);
        List<Producto> productos = libreria.buscarProductosPorNombre(busqueda.getText());
        for (Producto producto : productos) {
            modeloProductos.addRow(new Object[]{
                    producto.getCodigo(),
                    producto.getNombre(),
                    producto.getTipo(),
                    producto.getDetalle(),
                    formatear(producto.getPrecio()),
                    producto.getExistenciasTexto()
            });
        }
    }

    private void registrarProductoDesdeFormulario() {
        try {
            String nombre = nombreProducto.getText();
            BigDecimal precio = new BigDecimal(precioProducto.getText().trim());
            String tipo = (String) tipoProducto.getSelectedItem();
            Producto producto;

            if ("Libro impreso".equals(tipo)) {
                producto = new LibroImpreso(libreria.generarCodigoProducto(LibroImpreso.class),
                        nombre, precio, autorImpreso.getText(),
                        Integer.parseInt(stockImpreso.getText().trim()));
            } else if ("Libro digital".equals(tipo)) {
                producto = new LibroDigital(libreria.generarCodigoProducto(LibroDigital.class),
                        nombre, precio, autorDigital.getText(), EnumSet.allOf(FormatoDigital.class));
            } else {
                producto = new Papeleria(libreria.generarCodigoProducto(Papeleria.class),
                        nombre, precio, marcaPapeleria.getText(),
                        Integer.parseInt(stockPapeleria.getText().trim()));
            }

            libreria.registrarProducto(producto);
            limpiarFormularioProducto();
            actualizarTablaProductos();
            actualizarProductosVenta();
            actualizarCodigoAutomatico();
            mostrarMensaje("Producto registrado correctamente.\nCódigo asignado: " + producto.getCodigo(),
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException e) {
            mostrarMensaje("Revise el precio y las existencias; deben ser números válidos.", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException e) {
            mostrarMensaje(e.getMessage(), JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarFormularioProducto() {
        nombreProducto.setText("");
        precioProducto.setText("");
        autorImpreso.setText("");
        stockImpreso.setText("");
        autorDigital.setText("");
        marcaPapeleria.setText("");
        stockPapeleria.setText("");
    }

    private void actualizarProductosVenta() {
        Producto seleccionado = (Producto) productosVenta.getSelectedItem();
        productosVenta.removeAllItems();
        for (Producto producto : libreria.getProductos()) {
            productosVenta.addItem(producto);
        }
        if (seleccionado != null) {
            productosVenta.setSelectedItem(seleccionado);
        }
        actualizarDisponibilidadVenta();
    }

    private void cambiarClienteVenta() {
        Cliente cliente = (Cliente) clientesVenta.getSelectedItem();
        if (cliente != null && ventaActual.estaVacia()) {
            ventaActual = new Venta(cliente);
        }
    }

    private void agregarProductoAVenta() {
        try {
            Producto producto = (Producto) productosVenta.getSelectedItem();
            int cantidad = (Integer) cantidadVenta.getValue();
            FormatoDigital formato = producto instanceof LibroDigital
                    ? (FormatoDigital) formatoVenta.getSelectedItem() : null;
            ventaActual.agregarProducto(producto, cantidad, formato);
            clientesVenta.setEnabled(false);
            actualizarTablaVenta();
            actualizarDisponibilidadVenta();
        } catch (IllegalArgumentException | IllegalStateException e) {
            mostrarMensaje(e.getMessage(), JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarTablaVenta() {
        modeloVenta.setRowCount(0);
        for (DetalleVenta detalle : ventaActual.getDetalles()) {
            modeloVenta.addRow(new Object[]{
                    detalle.getProducto().getNombre() + obtenerFormato(detalle),
                    detalle.getCantidad(),
                    formatear(detalle.getProducto().getPrecio()),
                    formatear(detalle.getSubtotal())
            });
        }
    }

    private void finalizarVenta() {
        try {
            ventaActual.finalizar();
            mostrarMensaje(crearResumenVenta(), JOptionPane.INFORMATION_MESSAGE);
            actualizarTablaProductos();
            ventaActual = new Venta((Cliente) clientesVenta.getSelectedItem());
            clientesVenta.setEnabled(true);
            actualizarTablaVenta();
            actualizarDisponibilidadVenta();
        } catch (IllegalArgumentException | IllegalStateException e) {
            mostrarMensaje(e.getMessage(), JOptionPane.ERROR_MESSAGE);
        }
    }

    private String crearResumenVenta() {
        StringBuilder resumen = new StringBuilder();
        resumen.append("Cliente: ").append(ventaActual.getCliente()).append("\n\n");
        resumen.append("Productos:\n");
        for (DetalleVenta detalle : ventaActual.getDetalles()) {
            resumen.append("- ").append(detalle.getProducto().getNombre())
                    .append(obtenerFormato(detalle))
                    .append(" x ").append(detalle.getCantidad())
                    .append(" | Subtotal: ").append(formatear(detalle.getSubtotal()))
                    .append(" | Descuento: ").append(formatear(detalle.getDescuento(ventaActual.getCliente().esSocio())))
                    .append(" | IVA: ").append(formatear(detalle.getIva())).append("\n");
        }
        resumen.append("\nSubtotal: ").append(formatear(ventaActual.getSubtotal()));
        resumen.append("\nDescuento: ").append(formatear(ventaActual.getDescuento()));
        resumen.append("\nIVA: ").append(formatear(ventaActual.getIva()));
        resumen.append("\nTOTAL A PAGAR: ").append(formatear(ventaActual.getTotal()));
        return resumen.toString();
    }

    private String obtenerFormato(DetalleVenta detalle) {
        return detalle.getFormato() == null ? "" : " (" + detalle.getFormato() + ")";
    }

    private String formatear(BigDecimal valor) {
        return String.format(Locale.ROOT, "%.2f", valor);
    }

    private void actualizarCodigoAutomatico() {
        String tipo = (String) tipoProducto.getSelectedItem();
        Class<? extends Producto> clase;
        if ("Libro impreso".equals(tipo)) {
            clase = LibroImpreso.class;
        } else if ("Libro digital".equals(tipo)) {
            clase = LibroDigital.class;
        } else {
            clase = Papeleria.class;
        }
        codigoAutomatico.setText("Código automático: " + libreria.generarCodigoProducto(clase));
    }

    private void actualizarDisponibilidadVenta() {
        Producto producto = (Producto) productosVenta.getSelectedItem();
        if (producto == null) {
            disponiblesVenta.setText("Sin productos");
            cantidadVenta.setEnabled(false);
            return;
        }

        int disponibles = producto.getExistenciasDisponibles();
        for (DetalleVenta detalle : ventaActual.getDetalles()) {
            if (detalle.getProducto() == producto && disponibles != Integer.MAX_VALUE) {
                disponibles -= detalle.getCantidad();
                break;
            }
        }

        if (disponibles == Integer.MAX_VALUE) {
            disponiblesVenta.setText("Disponibles: ilimitados");
            SpinnerNumberModel modeloCantidad = (SpinnerNumberModel) cantidadVenta.getModel();
            modeloCantidad.setMaximum(Integer.MAX_VALUE);
            modeloCantidad.setMinimum(1);
            if ((Integer) modeloCantidad.getValue() < 1) {
                modeloCantidad.setValue(1);
            }
            cantidadVenta.setEnabled(true);
            return;
        }

        disponiblesVenta.setText("Disponibles para esta venta: " + disponibles);
        SpinnerNumberModel modeloCantidad = (SpinnerNumberModel) cantidadVenta.getModel();
        if (disponibles <= 0) {
            modeloCantidad.setMinimum(0);
            modeloCantidad.setMaximum(0);
            modeloCantidad.setValue(0);
            cantidadVenta.setEnabled(false);
        } else {
            modeloCantidad.setMaximum(disponibles);
            modeloCantidad.setMinimum(1);
            int valorActual = (Integer) modeloCantidad.getValue();
            if (valorActual < 1 || valorActual > disponibles) {
                modeloCantidad.setValue(Math.min(Math.max(valorActual, 1), disponibles));
            }
            cantidadVenta.setEnabled(true);
        }
    }

    private void actualizarFormatosVenta() {
        Producto producto = (Producto) productosVenta.getSelectedItem();
        boolean esLibroDigital = producto instanceof LibroDigital;
        formatoVenta.setEnabled(esLibroDigital);
        if (esLibroDigital) {
            LibroDigital libroDigital = (LibroDigital) producto;
            FormatoDigital formatoSeleccionado = (FormatoDigital) formatoVenta.getSelectedItem();
            FormatoDigital[] formatosDisponibles = libroDigital.getFormatos().toArray(new FormatoDigital[0]);
            formatoVenta.setModel(new javax.swing.DefaultComboBoxModel<>(formatosDisponibles));
            if (formatoSeleccionado != null && libroDigital.ofreceFormato(formatoSeleccionado)) {
                formatoVenta.setSelectedItem(formatoSeleccionado);
            }
        } else {
            formatoVenta.setModel(new javax.swing.DefaultComboBoxModel<>(FormatoDigital.values()));
        }
    }

    private void mostrarAyuda() {
        JLabel mensaje = new JLabel("<html><div style='width:420px'>"
                + "<b>Ayuda de Páginas Andinas</b><br><br>"
                + "• Pulse PRODUCTOS para registrar o buscar artículos.<br>"
                + "• Pulse VENTAS para elegir cliente, producto y cantidad.<br>"
                + "• Las existencias disponibles aparecen junto al producto.<br>"
                + "• Use “Menú principal” para regresar.</div></html>");
        mensaje.setFont(FUENTE_NORMAL);
        JOptionPane.showMessageDialog(this, mensaje, "Ayuda", JOptionPane.INFORMATION_MESSAGE);
    }

    private void confirmarCierre() {
        JLabel mensaje = new JLabel("¿Desea cerrar el programa?");
        mensaje.setFont(FUENTE_NORMAL);
        int opcion = JOptionPane.showConfirmDialog(this, mensaje, "Confirmar salida",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (opcion == JOptionPane.YES_OPTION) {
            dispose();
        }
    }

    private void mostrarMensaje(String mensaje, int tipo) {
        JLabel etiqueta = new JLabel("<html>" + mensaje.replace("\n", "<br>") + "</html>");
        etiqueta.setFont(FUENTE_NORMAL);
        JOptionPane.showMessageDialog(this, etiqueta, "Páginas Andinas", tipo);
    }
}
