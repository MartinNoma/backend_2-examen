package cl.duoc.nastyshoes.productos.service;

import cl.duoc.nastyshoes.productos.dto.ProductoRequest;
import cl.duoc.nastyshoes.productos.exception.RecursoNoEncontradoException;
import cl.duoc.nastyshoes.productos.exception.StockInsuficienteException;
import cl.duoc.nastyshoes.productos.model.Producto;
import cl.duoc.nastyshoes.productos.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository repository;

    @InjectMocks
    private ProductoService service;

    private Producto producto;

    @BeforeEach
    void setUp() {
        producto = new Producto("Sk8-Hi", "Vans", "Skate", 49990, 5, List.of(39, 40), "Clásica", "img.jpg");
        producto.setId(1L);
    }

    @Test
    void listarDevuelveTodosLosProductos() {
        when(repository.findAll()).thenReturn(List.of(producto));

        assertThat(service.listar()).containsExactly(producto);
    }

    @Test
    void obtenerDevuelveElProductoCuandoExiste() {
        when(repository.findById(1L)).thenReturn(Optional.of(producto));

        assertThat(service.obtener(1L).getName()).isEqualTo("Sk8-Hi");
    }

    @Test
    void obtenerLanzaErrorCuandoNoExiste() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.obtener(99L))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("99");
    }

    @Test
    void crearGuardaElProductoConLosDatosLimpios() {
        ProductoRequest request = new ProductoRequest("  Nueva  ", " Nike ", " Running ", 1000, 3, List.of(40), " Desc ", " foto.png ");
        when(repository.save(any(Producto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Producto creado = service.crear(request);

        assertThat(creado.getName()).isEqualTo("Nueva");
        assertThat(creado.getBrand()).isEqualTo("Nike");
        assertThat(creado.getCategory()).isEqualTo("Running");
        assertThat(creado.getPrice()).isEqualTo(1000);
        assertThat(creado.getStock()).isEqualTo(3);
        assertThat(creado.getSizes()).containsExactly(40);
        assertThat(creado.getDescription()).isEqualTo("Desc");
        assertThat(creado.getImage()).isEqualTo("foto.png");
    }

    @Test
    void crearUsaTextoVacioCuandoNoHayDescripcionNiImagen() {
        ProductoRequest request = new ProductoRequest("Nueva", "Nike", "Running", 1000, 3, List.of(40), null, null);
        when(repository.save(any(Producto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Producto creado = service.crear(request);

        assertThat(creado.getDescription()).isEmpty();
        assertThat(creado.getImage()).isEmpty();
    }

    @Test
    void actualizarModificaElProductoExistente() {
        ProductoRequest request = new ProductoRequest("Sk8-Hi Pro", "Vans", "Skate", 55990, 8, List.of(41), "Nueva", "");
        when(repository.findById(1L)).thenReturn(Optional.of(producto));
        when(repository.save(producto)).thenReturn(producto);

        Producto actualizado = service.actualizar(1L, request);

        assertThat(actualizado.getName()).isEqualTo("Sk8-Hi Pro");
        assertThat(actualizado.getPrice()).isEqualTo(55990);
        assertThat(actualizado.getStock()).isEqualTo(8);
    }

    @Test
    void eliminarBorraCuandoExiste() {
        when(repository.existsById(1L)).thenReturn(true);

        service.eliminar(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    void eliminarLanzaErrorCuandoNoExiste() {
        when(repository.existsById(5L)).thenReturn(false);

        assertThatThrownBy(() -> service.eliminar(5L)).isInstanceOf(RecursoNoEncontradoException.class);
        verify(repository, never()).deleteById(5L);
    }

    @Test
    void descontarStockRestaLaCantidad() {
        when(repository.findById(1L)).thenReturn(Optional.of(producto));
        when(repository.save(producto)).thenReturn(producto);

        Producto resultado = service.descontarStock(1L, 2);

        assertThat(resultado.getStock()).isEqualTo(3);
    }

    @Test
    void descontarStockFallaSiNoHaySuficiente() {
        when(repository.findById(1L)).thenReturn(Optional.of(producto));

        assertThatThrownBy(() -> service.descontarStock(1L, 10))
                .isInstanceOf(StockInsuficienteException.class)
                .hasMessageContaining("quedan 5");
        verify(repository, never()).save(any());
    }
}
