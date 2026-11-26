package cl.duoc.nastyshoes.productos.config;

import cl.duoc.nastyshoes.productos.repository.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DataInitializerTest {

    @Mock
    private ProductoRepository repository;

    @InjectMocks
    private DataInitializer initializer;

    @Test
    void cargaElCatalogoCuandoLaTablaEstaVacia() {
        when(repository.count()).thenReturn(0L);

        initializer.run();

        verify(repository).saveAll(anyList());
    }

    @Test
    void noDuplicaDatosSiYaHayProductos() {
        when(repository.count()).thenReturn(8L);

        initializer.run();

        verify(repository, never()).saveAll(anyList());
    }

    @Test
    void elCatalogoInicialTieneOchoZapatillas() {
        assertThat(DataInitializer.catalogoInicial()).hasSize(8)
                .allSatisfy(producto -> assertThat(producto.getSizes()).isNotEmpty());
    }
}
