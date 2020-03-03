package longbridge.repositories;

import longbridge.models.Merchant;
import longbridge.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author Yemi Dalley
 *
 */
public interface ProductRepo extends CommonRepo<Product, Long>{


}
