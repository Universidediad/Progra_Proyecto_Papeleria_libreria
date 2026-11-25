package modelo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Venta {
    private final Cliente cliente;
    private final ArrayList<DetalleVenta> detalles;
    private boolean finalizada;

    public Venta(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("Seleccione un cliente.");
        }
        this.cliente = cliente;
        this.detalles = new ArrayList<>();
    }

    public Cliente getCliente() {
        return cliente;
    }

    public List<DetalleVenta> getDetalles() {
        return Collections.unmodifiableList(new ArrayList<>(detalles));
    }

    public boolean estaVacia() {
        return detalles.isEmpty();
    }

    public void agregarProducto(Producto producto, int cantidad) {
        agregarProducto(producto, cantidad, null);
    }

    public void agregarProducto(Producto producto, int cantidad, FormatoDigital formato) {
        if (finalizada) {
            throw new IllegalStateException("La venta ya fue finalizada.");
        }
        if (producto == null) {
            throw new IllegalArgumentException("Seleccione un producto.");
        }

        for (DetalleVenta detalle : detalles) {
            if (detalle.getProducto() == producto && detalle.getFormato() == formato) {
                detalle.agregarCantidad(cantidad);
                return;
            }
        }
        detalles.add(new DetalleVenta(producto, cantidad, formato));
    }

    public BigDecimal getSubtotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (DetalleVenta detalle : detalles) {
            total = total.add(detalle.getSubtotal());
        }
        return redondear(total);
    }

    public BigDecimal getDescuento() {
        BigDecimal total = BigDecimal.ZERO;
        for (DetalleVenta detalle : detalles) {
            total = total.add(detalle.getDescuento(cliente.esSocio()));
        }
        return redondear(total);
    }

    public BigDecimal getIva() {
        BigDecimal total = BigDecimal.ZERO;
        for (DetalleVenta detalle : detalles) {
            total = total.add(detalle.getIva());
        }
        return redondear(total);
    }

    public BigDecimal getTotal() {
        return redondear(getSubtotal().subtract(getDescuento()).add(getIva()));
    }

    public void finalizar() {
        if (finalizada) {
            throw new IllegalStateException("La venta ya fue finalizada.");
        }
        if (detalles.isEmpty()) {
            throw new IllegalArgumentException("Agregue al menos un producto a la venta.");
        }

        for (DetalleVenta detalle : detalles) {
            detalle.getProducto().validarDisponibilidad(detalle.getCantidad());
        }
        for (DetalleVenta detalle : detalles) {
            detalle.getProducto().descontarExistencias(detalle.getCantidad());
        }
        finalizada = true;
    }

    private BigDecimal redondear(BigDecimal valor) {
        return valor.setScale(2, RoundingMode.HALF_UP);
    }
}
