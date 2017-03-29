package longbridge.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 *
 */
@Entity
public class AdminUser extends AbstractEntity implements Staff{

<<<<<<< HEAD
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long Id;

=======
  
>>>>>>> 93ae8a1f5235023912f9e0c871393e5770fea1ae
}
