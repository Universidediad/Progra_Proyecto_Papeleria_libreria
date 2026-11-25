package modelo;

import java.math.BigDecimal;
import java.math.RoundingMode;

public abstract class Producto {
    private final String codigo;
    private final String nombre;
    private final BigDecimal precio;

    protected Producto(String codigo, String nombre, BigDecimal precio) {
        this.codigo = validarTexto(codigo, "El código");
        this.nombre = validarTexto(nombre, "El nombre");

        if (precio == null || precio.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor que cero.");
        }
        this.precio = precio.setScale(2, RoundingMode.HALF_UP);
    }

    private static String validarTexto(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException(campo + " no puede estar vacío.");
        }
        return valor.trim();
    }

    public final String getCodigo() {
        return codigo;
    }

    public final String getNombre() {
        return nombre;
    }

    public final BigDecimal getPrecio() {
        return precio;
    }

    public abstract String getTipo();

    public abstract String getDetalle();

    public abstract String getExistenciasTexto();

    public abstract int getExistenciasDisponibles();

    public abstract void validarDisponibilidad(int cantidad);

    public abstract void descontarExistencias(int cantidad);

    public abstract BigDecimal calcularDescuento(boolean clienteSocio, int cantidad);

    public abstract BigDecimal calcularIva(int cantidad);

    protected final BigDecimal calcularSubtotal(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
        }
        return precio.multiply(BigDecimal.valueOf(cantidad)).setScale(2, RoundingMode.HALF_UP);
    }

    protected final BigDecimal redondear(BigDecimal valor) {
        return valor.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String toString() {
        return codigo + " - " + nombre;
    }
}
