package cl.duoc.nastyshoes.productos.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/** Cantidad a descontar del stock cuando pedidos-service confirma una compra. */
public record DescuentoStockRequest(
        @NotNull(message = "La cantidad es obligatoria.") @Min(value = 1, message = "La cantidad debe ser al menos 1.") Integer cantidad
) {
}
