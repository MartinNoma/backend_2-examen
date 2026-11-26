package cl.duoc.nastyshoes.productos.controller;

import cl.duoc.nastyshoes.productos.dto.ProductoRequest;
import cl.duoc.nastyshoes.productos.exception.RecursoNoEncontradoException;
import cl.duoc.nastyshoes.productos.exception.StockInsuficienteException;
import cl.duoc.nastyshoes.productos.model.Producto;
import cl.duoc.nastyshoes.productos.service.ProductoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductoController.class)
class ProductoControllerTest {

    private static final String PRODUCTO_JSON = """
            {"name":"Sk8-Hi","brand":"Vans","category":"Skate","price":49990,"stock":5,
             "sizes":[39,40],"description":"Clásica","image":""}
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductoService service;

    private Producto producto;

    @BeforeEach
    void setUp() {
        producto = new Producto("Sk8-Hi", "Vans", "Skate", 49990, 5, List.of(39, 40), "Clásica", "");
        producto.setId(1L);
    }

    @Test
    void listarDevuelveElCatalogo() throws Exception {
        when(service.listar()).thenReturn(List.of(producto));

        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Sk8-Hi"))
                .andExpect(jsonPath("$[0].sizes[1]").value(40));
    }

    @Test
    void obtenerDevuelveUnProducto() throws Exception {
        when(service.obtener(1L)).thenReturn(producto);

        mockMvc.perform(get("/api/productos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.brand").value("Vans"));
    }

    @Test
    void obtenerDevuelve404CuandoNoExiste() throws Exception {
        when(service.obtener(9L)).thenThrow(new RecursoNoEncontradoException("No existe el producto con id 9."));

        mockMvc.perform(get("/api/productos/9"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("No existe el producto con id 9."));
    }

    @Test
    void crearDevuelve201() throws Exception {
        when(service.crear(any(ProductoRequest.class))).thenReturn(producto);

        mockMvc.perform(post("/api/productos").contentType(MediaType.APPLICATION_JSON).content(PRODUCTO_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void crearDevuelve400SiFaltanDatos() throws Exception {
        String invalido = """
                {"name":"","brand":"Vans","category":"Skate","price":1,"stock":1,"sizes":[40]}
                """;

        mockMvc.perform(post("/api/productos").contentType(MediaType.APPLICATION_JSON).content(invalido))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("El nombre es obligatorio."));
    }

    @Test
    void actualizarDevuelveElProductoModificado() throws Exception {
        when(service.actualizar(eq(1L), any(ProductoRequest.class))).thenReturn(producto);

        mockMvc.perform(put("/api/productos/1").contentType(MediaType.APPLICATION_JSON).content(PRODUCTO_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Sk8-Hi"));
    }

    @Test
    void eliminarDevuelve204() throws Exception {
        mockMvc.perform(delete("/api/productos/1"))
                .andExpect(status().isNoContent());

        verify(service).eliminar(1L);
    }

    @Test
    void eliminarDevuelve404CuandoNoExiste() throws Exception {
        doThrow(new RecursoNoEncontradoException("No existe")).when(service).eliminar(7L);

        mockMvc.perform(delete("/api/productos/7"))
                .andExpect(status().isNotFound());
    }

    @Test
    void descontarStockDevuelveElProducto() throws Exception {
        producto.setStock(3);
        when(service.descontarStock(1L, 2)).thenReturn(producto);

        mockMvc.perform(put("/api/productos/1/descontar-stock").contentType(MediaType.APPLICATION_JSON).content("{\"cantidad\":2}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock").value(3));
    }

    @Test
    void descontarStockDevuelve409SinStock() throws Exception {
        when(service.descontarStock(1L, 50)).thenThrow(new StockInsuficienteException("Stock insuficiente"));

        mockMvc.perform(put("/api/productos/1/descontar-stock").contentType(MediaType.APPLICATION_JSON).content("{\"cantidad\":50}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error", containsString("Stock insuficiente")));
    }

    @Test
    void descontarStockDevuelve400ConCantidadInvalida() throws Exception {
        mockMvc.perform(put("/api/productos/1/descontar-stock").contentType(MediaType.APPLICATION_JSON).content("{\"cantidad\":0}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("La cantidad debe ser al menos 1."));
    }

    @Test
    void permiteLlamadasDesdeElFrontend() throws Exception {
        mockMvc.perform(options("/api/productos")
                        .header("Origin", "http://localhost:5173")
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Access-Control-Allow-Origin"));
    }
}
