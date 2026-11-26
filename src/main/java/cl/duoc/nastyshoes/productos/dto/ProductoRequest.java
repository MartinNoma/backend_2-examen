package cl.duoc.nastyshoes.productos.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/** Datos que envía el panel de administración para crear o editar una zapatilla. */
public record ProductoRequest(
        @NotBlank(message = "El nombre es obligatorio.") String name,
        @NotBlank(message = "La marca es obligatoria.") String brand,
        @NotBlank(message = "La categoría es obligatoria.") String category,
        @NotNull(message = "El precio es obligatorio.") @Min(value = 0, message = "El precio no puede ser negativo.") Integer price,
        @NotNull(message = "El stock es obligatorio.") @Min(value = 0, message = "El stock no puede ser negativo.") Integer stock,
        @NotEmpty(message = "Debes indicar al menos una talla.") List<Integer> sizes,
        String description,
        String image
) {
}
