package longbridge.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;

/**
 * Created by mac on 21/09/2018.
 */
public class DataTablesUtils {

    public static Pageable getPageable(DataTablesInput input) {
        if(input.getLength() < 0)
            return PageRequest.of(input.getStart(),Integer.MAX_VALUE);
        return PageRequest.of(input.getStart()/input.getLength(),input.getLength());
    }
}