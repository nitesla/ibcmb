package longbridge.services;

import longbridge.dtos.GreetingDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface GreetingService {

    @PreAuthorize("hasAuthority('ADD_GREETING')")
    String addGreeting(GreetingDTO greeting);

    @PreAuthorize("hasAuthority('DELETE_GREETING')")
    String deleteGreeting(Long greetingId);

    GreetingDTO getGreetingDTO(Long greetingId);

    @PreAuthorize("hasAuthority('UPDATE_GREETING')")
    String updateGreeting(GreetingDTO dto);

    Page<GreetingDTO> getGreetingDTOs(Pageable pageDetails);

    List<GreetingDTO> getCurrentGreetingsForUser();

}
