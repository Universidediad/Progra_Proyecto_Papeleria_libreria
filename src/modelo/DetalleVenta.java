package modelo;

import java.math.BigDecimal;

public class DetalleVenta {
    private final Producto producto;
    private final FormatoDigital formato;
    private int cantidad;

    public DetalleVenta(Producto producto, int cantidad, FormatoDigital formato) {
        if (producto == null) {
            throw new IllegalArgumentException("Seleccione un producto.");
        }
        if (producto instanceof LibroDigital) {
            LibroDigital libroDigital = (LibroDigital) producto;
            if (formato == null || !libroDigital.ofreceFormato(formato)) {
                throw new IllegalArgumentException("Seleccione un formato disponible para el libro digital.");
            }
        } else if (formato != null) {
            throw new IllegalArgumentException("El formato solo se selecciona para libros digitales.");
        }
        producto.validarDisponibilidad(cantidad);
        this.producto = producto;
        this.formato = formato;
        this.cantidad = cantidad;
    }

    public Producto getProducto() {
        return producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public FormatoDigital getFormato() {
        return formato;
    }

    public BigDecimal getSubtotal() {
        return producto.getPrecio().multiply(BigDecimal.valueOf(cantidad));
    }

    public BigDecimal getDescuento(boolean clienteSocio) {
        return producto.calcularDescuento(clienteSocio, cantidad);
    }

    public BigDecimal getIva() {
        return producto.calcularIva(cantidad);
    }

    void agregarCantidad(int cantidadAdicional) {
        int nuevaCantidad = cantidad + cantidadAdicional;
        producto.validarDisponibilidad(nuevaCantidad);
        cantidad = nuevaCantidad;
    }
}
