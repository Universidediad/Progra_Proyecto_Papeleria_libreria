package modelo;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public class LibroDigital extends Producto {
    private final String autor;
    private final Set<FormatoDigital> formatos;

    public LibroDigital(String codigo, String nombre, BigDecimal precio, String autor,
                        Set<FormatoDigital> formatos) {
        super(codigo, nombre, precio);
        if (autor == null || autor.trim().isEmpty()) {
            throw new IllegalArgumentException("El autor no puede estar vacío.");
        }
        if (formatos == null || formatos.isEmpty()) {
            throw new IllegalArgumentException("El libro digital debe ofrecer PDF y/o EPUB.");
        }
        this.autor = autor.trim();
        this.formatos = Collections.unmodifiableSet(EnumSet.copyOf(formatos));
    }

    public String getAutor() {
        return autor;
    }

    public Set<FormatoDigital> getFormatos() {
        return formatos;
    }

    public boolean ofreceFormato(FormatoDigital formato) {
        return formatos.contains(formato);
    }

    @Override
    public String getTipo() {
        return "Libro digital";
    }

    @Override
    public String getDetalle() {
        return "Autor: " + autor + ", formatos: " + formatos;
    }

    @Override
    public String getExistenciasTexto() {
        return "Ilimitadas";
    }

    @Override
    public int getExistenciasDisponibles() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void validarDisponibilidad(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
        }
    }

    @Override
    public void descontarExistencias(int cantidad) {
        validarDisponibilidad(cantidad);
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
