package modelo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;

public class Libreria {
    private final ArrayList<Producto> productos;
    private final ArrayList<Cliente> clientes;

    public Libreria() {
        productos = new ArrayList<>();
        clientes = new ArrayList<>();
        cargarDatosDePrueba();
    }

    private void cargarDatosDePrueba() {
        productos.add(new LibroImpreso("LI-001", "El principito", new BigDecimal("6.50"),
                "Antoine de Saint-Exupéry", 8));
        productos.add(new LibroImpreso("LI-002", "Cien años de soledad", new BigDecimal("15.00"),
                "Gabriel García Márquez", 5));
        productos.add(new LibroDigital("LD-001", "Orgullo y prejuicio", new BigDecimal("4.50"),
                "Jane Austen", EnumSet.allOf(FormatoDigital.class)));
        productos.add(new LibroDigital("LD-002", "La metamorfosis", new BigDecimal("3.75"),
                "Franz Kafka", EnumSet.allOf(FormatoDigital.class)));
        productos.add(new Papeleria("PA-001", "Cuaderno", new BigDecimal("2.00"), "Andina", 20));
        productos.add(new Papeleria("PA-002", "Esfero", new BigDecimal("0.75"), "Cóndor", 30));

        clientes.add(new Cliente("Ana Socia", true));
        clientes.add(new Cliente("Carlos Cliente", false));
    }

    public List<Producto> getProductos() {
        return new ArrayList<>(productos);
    }

    public List<Cliente> getClientes() {
        return new ArrayList<>(clientes);
    }

    public void registrarProducto(Producto producto) {
        if (producto == null) {
            throw new IllegalArgumentException("Ingrese los datos del producto.");
        }
        for (Producto existente : productos) {
            if (existente.getCodigo().equalsIgnoreCase(producto.getCodigo())) {
                throw new IllegalArgumentException("Ya existe un producto con ese código.");
            }
        }
        productos.add(producto);
    }

    public String generarCodigoProducto(Class<? extends Producto> tipo) {
        String prefijo;
        if (tipo == LibroImpreso.class) {
            prefijo = "LI";
        } else if (tipo == LibroDigital.class) {
            prefijo = "LD";
        } else if (tipo == Papeleria.class) {
            prefijo = "PA";
        } else {
            throw new IllegalArgumentException("Tipo de producto no válido.");
        }

        int siguiente = 1;
        for (Producto producto : productos) {
            String codigo = producto.getCodigo();
            if (codigo.startsWith(prefijo + "-")) {
                try {
                    int numero = Integer.parseInt(codigo.substring(prefijo.length() + 1));
                    siguiente = Math.max(siguiente, numero + 1);
                } catch (NumberFormatException ignored) {
                    // Los códigos existentes con un formato diferente no afectan la secuencia.
                }
            }
        }
        return prefijo + "-" + String.format(Locale.ROOT, "%03d", siguiente);
    }

    public List<Producto> buscarProductosPorNombre(String texto) {
        ArrayList<Producto> encontrados = new ArrayList<>();
        String consulta = texto == null ? "" : texto.trim().toLowerCase(Locale.ROOT);
        for (Producto producto : productos) {
            if (producto.getNombre().toLowerCase(Locale.ROOT).contains(consulta)) {
                encontrados.add(producto);
            }
        }
        return encontrados;
    }
}
