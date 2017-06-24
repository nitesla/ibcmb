package longbridge.services;

import longbridge.dtos.NotificationsDTO;
import longbridge.models.Notifications;

import java.util.List;

/**
 * Created by Showboy on 24/06/2017.
 */
public interface NotificationsService {

    List<NotificationsDTO> getNotifications();

    NotificationsDTO getNotification(Long id);

    String addNotification(NotificationsDTO notificationsDTO);

    String updateNotification(NotificationsDTO notificationsDTO);

    String deleteNotification(Long id);

    NotificationsDTO convertEntityToDTO(Notifications notifications);

    Notifications convertDTOToEntity(NotificationsDTO notificationsDTO);

    List<NotificationsDTO> convertEntitiesToDTOs(Iterable<Notifications> notifications);

}
