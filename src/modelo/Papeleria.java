package modelo;

import java.math.BigDecimal;

public class Papeleria extends Producto {
    private final String marca;
    private int existencias;

    public Papeleria(String codigo, String nombre, BigDecimal precio, String marca, int existencias) {
        super(codigo, nombre, precio);
        if (marca == null || marca.trim().isEmpty()) {
            throw new IllegalArgumentException("La marca no puede estar vacía.");
        }
        if (existencias < 0) {
            throw new IllegalArgumentException("Las existencias no pueden ser negativas.");
        }
        this.marca = marca.trim();
        this.existencias = existencias;
    }

    public String getMarca() {
        return marca;
    }

    public int getExistencias() {
        return existencias;
    }

    @Override
    public String getTipo() {
        return "Papelería";
    }

    @Override
    public String getDetalle() {
        return "Marca: " + marca;
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
        calcularSubtotal(cantidad);
        return redondear(BigDecimal.ZERO);
    }

    @Override
    public BigDecimal calcularIva(int cantidad) {
        BigDecimal subtotal = calcularSubtotal(cantidad);
        return redondear(subtotal.multiply(new BigDecimal("0.15")));
    }
}
