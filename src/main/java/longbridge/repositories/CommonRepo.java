package longbridge.repositories;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

/**
 * Created by Fortune on 4/5/2017.
 */
@NoRepositoryBean
public interface CommonRepo<T, ID extends Serializable> extends JpaRepository<T, ID> {

	public Page<T> findUsingPattern(String pattern, Pageable details);
}