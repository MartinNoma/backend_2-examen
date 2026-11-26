package cl.duoc.nastyshoes.productos.config;

import cl.duoc.nastyshoes.productos.model.Producto;
import cl.duoc.nastyshoes.productos.repository.ProductoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/** Carga el catálogo inicial (el mismo que tenía el frontend) solo si la tabla está vacía. */
@Component
public class DataInitializer implements CommandLineRunner {

    private final ProductoRepository repository;

    public DataInitializer(ProductoRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        if (repository.count() > 0) {
            return;
        }
        repository.saveAll(catalogoInicial());
    }

    static List<Producto> catalogoInicial() {
        return List.of(
                new Producto("Reax 8 LTR Negras", "Nike", "Urbano", 59990, 12, List.of(39, 40, 41, 42),
                        "Zapatilla urbana liviana, ideal para uso diario.",
                        "https://api-prd.ynk.cl/medias/515Wx515H-BELSPORT-NIIR1458006-VIEW1.webp?context=bWFzdGVyfGltYWdlc3w1MjA2fGltYWdlL3dlYnB8YUdObUwyaGlNQzh4TURFME5UQTJNalU0TkRNMU1DODFNVFZYZURVeE5VaGZRa1ZNVTFCUFVsUmZUa2xKVWpFME5UZ3dNRFpmVmtsRlZ6RXVkMlZpY0F8NDE0ZDI5OGMxNGJmNmU5MDg4NzEwOTZmMDRhNGYzOTczOGE2NDA1OGM5NjliZGNiMjQ4N2RlMzA4OTMyM2FiOQ"),
                new Producto("Adidas Supernova Rise 3", "Adidas", "Running", 64990, 8, List.of(38, 39, 40, 41, 42, 43),
                        "Amortiguación suave pensada para trote y running.",
                        "https://hips.hearstapps.com/vader-prod.s3.amazonaws.com/1770047333-zapatilla-de-running-supernova-r-6980c6909b9cb.jpg?crop=1xw:1xh;center,top&resize=980:*"),
                new Producto("LaMelo Ball MB.04", "Puma", "Basketball", 72990, 0, List.of(41, 42, 43, 44),
                        "Soporte en el tobillo y suela de alta tracción.",
                        "https://images.stockx.com/images/Puma-LaMelo-Ball-MB04-LaFrance-PS.jpg?fit=fill&bg=FFFFFF&w=480&h=320&q=60&dpr=1&trim=color&updated_at=1725655545"),
                new Producto("Sk8-Hi", "Vans", "Skate", 49990, 15, List.of(38, 39, 40, 41),
                        "Diseño clásico de skate, suela plana resistente.",
                        "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQhzW9co_ZuF8OCLVx7fhTeTQGOoJOP_zgPztBx9ykPy1oj644IIkOvmzvo&s=10"),
                new Producto("530", "New Balance", "Casual", 54990, 6, List.of(39, 40, 41, 42, 43),
                        "Estilo casual cómodo para uso todo el día.",
                        "https://cl-dam-resizer.ecomm.cencosud.com/unsafe/adaptive-fit-in/3840x0/filters:quality(75)/cl/paris/786622/variant/68a4d5702be65c255c658d7b/images/92635d83-8bed-4761-bcaa-b7c0c22e2583/786622-0100-001.jpg"),
                new Producto("Flex Train", "Nike", "Running", 68990, 10, List.of(38, 39, 40, 41, 42),
                        "Malla transpirable y entresuela flexible.",
                        "https://www.marathon.cl/on/demandware.static/-/Sites-catalog-equinox/default/dw301bf113/images/hi-res/198482507626_1_20251219-mrtPeru.jpg"),
                new Producto("VL Court 3.0", "Adidas", "Urbano", 57990, 0, List.of(40, 41, 42, 43),
                        "Línea urbana con terminaciones en cuero sintético.",
                        "https://cl-dam-resizer.ecomm.cencosud.com/unsafe/adaptive-fit-in/3840x0/filters:quality(75)/cl/paris/483380/variant/6a42bc971fe2cf44f634f5ca/images/fd2ec458-f9bc-4698-a334-c8f10f4c3311/483380-0200-001.jpg"),
                new Producto("Smash 3.0", "Puma", "Casual", 45990, 20, List.of(38, 39, 40, 41, 42, 43, 44),
                        "Opción económica y versátil para el día a día.",
                        "https://www.marathon.cl/dw/image/v2/BJKZ_PRD/on/demandware.static/-/Sites-catalog-equinox/default/dw30fc0d2f/images/marathon/4065452493189_1-20240626120000-mrtChile.jpeg?sw=800&sh=800")
        );
    }
}
