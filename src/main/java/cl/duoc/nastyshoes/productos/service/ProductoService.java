package cl.duoc.nastyshoes.productos.service;

import cl.duoc.nastyshoes.productos.dto.ProductoRequest;
import cl.duoc.nastyshoes.productos.exception.RecursoNoEncontradoException;
import cl.duoc.nastyshoes.productos.exception.StockInsuficienteException;
import cl.duoc.nastyshoes.productos.model.Producto;
import cl.duoc.nastyshoes.productos.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductoService {

    private final ProductoRepository repository;

    public ProductoService(ProductoRepository repository) {
        this.repository = repository;
    }

    public List<Producto> listar() {
        return repository.findAll();
    }

    public Producto obtener(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el producto con id " + id + "."));
    }

    public Producto crear(ProductoRequest request) {
        Producto producto = new Producto();
        copiarDatos(request, producto);
        return repository.save(producto);
    }

    public Producto actualizar(Long id, ProductoRequest request) {
        Producto producto = obtener(id);
        copiarDatos(request, producto);
        return repository.save(producto);
    }

    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new RecursoNoEncontradoException("No existe el producto con id " + id + ".");
        }
        repository.deleteById(id);
    }

    /** Lo usa pedidos-service al confirmar una compra. */
    @Transactional
    public Producto descontarStock(Long id, int cantidad) {
        Producto producto = obtener(id);
        if (producto.getStock() < cantidad) {
            throw new StockInsuficienteException("Stock insuficiente para " + producto.getName()
                    + ": quedan " + producto.getStock() + " unidades.");
        }
        producto.setStock(producto.getStock() - cantidad);
        return repository.save(producto);
    }

    private void copiarDatos(ProductoRequest request, Producto producto) {
        producto.setName(request.name().trim());
        producto.setBrand(request.brand().trim());
        producto.setCategory(request.category().trim());
        producto.setPrice(request.price());
        producto.setStock(request.stock());
        producto.setSizes(request.sizes());
        producto.setDescription(request.description() == null ? "" : request.description().trim());
        producto.setImage(request.image() == null ? "" : request.image().trim());
    }
}
