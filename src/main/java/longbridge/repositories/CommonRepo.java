package longbridge.repositories;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Fortune on 4/5/2017.
 */
@NoRepositoryBean
public interface CommonRepo<T, ID extends Serializable> extends JpaRepository<T, ID>
{
	Page<T> findUsingPattern(String pattern, Pageable details);
	void delete(ID id);
	void delete(Iterable<? extends T> entities);


	}