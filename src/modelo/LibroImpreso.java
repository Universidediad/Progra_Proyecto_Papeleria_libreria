package modelo;

import java.math.BigDecimal;

public class LibroImpreso extends Producto {
    private final String autor;
    private int existencias;

    public LibroImpreso(String codigo, String nombre, BigDecimal precio, String autor, int existencias) {
        super(codigo, nombre, precio);
        if (autor == null || autor.trim().isEmpty()) {
            throw new IllegalArgumentException("El autor no puede estar vacío.");
        }
        validarExistencias(existencias);
        this.autor = autor.trim();
        this.existencias = existencias;
    }

    private void validarExistencias(int cantidad) {
        if (cantidad < 0) {
            throw new IllegalArgumentException("Las existencias no pueden ser negativas.");
        }
    }

    public String getAutor() {
        return autor;
    }

    public int getExistencias() {
        return existencias;
    }

    @Override
    public String getTipo() {
        return "Libro impreso";
    }

    @Override
    public String getDetalle() {
        return "Autor: " + autor;
    }

    @Override
    public String getExistenciasTexto() {
        return String.valueOf(existencias);
    }

    @Override
    public int getExistenciasDisponibles() {
        return existencias;
    }

    @Override
    public void validarDisponibilidad(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
        }
        if (cantidad > existencias) {
            throw new IllegalArgumentException(
                    "No hay suficientes unidades de \"" + getNombre() + "\". Disponibles: " + existencias + ".");
        }
    }

    @Override
    public void descontarExistencias(int cantidad) {
        validarDisponibilidad(cantidad);
        existencias -= cantidad;
    }

    @Override
    public BigDecimal calcularDescuento(boolean clienteSocio, int cantidad) {
        BigDecimal subtotal = calcularSubtotal(cantidad);
        if (!clienteSocio) {
            return redondear(BigDecimal.ZERO);
        }
        return redondear(subtotal.multiply(new BigDecimal("0.10")));
    }

    @Override
    public BigDecimal calcularIva(int cantidad) {
        calcularSubtotal(cantidad);
        return redondear(BigDecimal.ZERO);
    }
}
