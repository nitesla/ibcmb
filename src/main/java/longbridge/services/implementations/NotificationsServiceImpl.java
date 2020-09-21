package longbridge.services.implementations;

import longbridge.dtos.NotificationsDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.Notifications;
import longbridge.repositories.NotificationsRepo;
import longbridge.services.NotificationsService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Wunmi on 24/06/2017.
 */
@Service
public class NotificationsServiceImpl implements NotificationsService {

    @Autowired
    private ModelMapper modelMapper;

    private final Logger logger= LoggerFactory.getLogger(this.getClass());

    private final Locale locale = LocaleContextHolder.getLocale();

    @Autowired
    MessageSource messageSource;

    @Autowired
    NotificationsRepo notificationsRepo;

    @Override
    public List<NotificationsDTO> getNotifications() {
        List<Notifications> notifications = this.notificationsRepo.findAll();
        return convertEntitiesToDTOs(notifications);
    }

    @Override
    public NotificationsDTO getNotification(Long id) {
        Notifications notifications = this.notificationsRepo.findById(id).get();
        return convertEntityToDTO(notifications);
    }

    @Override
    public Page<NotificationsDTO> getNotifications(Pageable pageDetails) {
        Page<Notifications> page = notificationsRepo.findAll(pageDetails);
        List<NotificationsDTO> dtOs = convertEntitiesToDTOs(page.getContent());
        long t = page.getTotalElements();

        return new PageImpl<NotificationsDTO>(dtOs, pageDetails, t);
    }

    @Override
    public String addNotification(NotificationsDTO notificationsDTO) {
        try {
            Notifications notifications = convertDTOToEntity(notificationsDTO);
            notificationsRepo.save(notifications);
            logger.info("Added new Notification {} ", notifications.getMessage());
            return messageSource.getMessage("notification.add.success", null, locale);
        }catch (Exception e){
            throw new InternetBankingException(messageSource.getMessage("failure",null,locale));
        }
    }

    @Override
    public String updateNotification(NotificationsDTO notificationsDTO) {
        try {
            Notifications notifications = convertDTOToEntity(notificationsDTO);
            //check if maker checker is enabled
            notificationsRepo.save(notifications);
            logger.info("Updated Notification with Id {}",notifications.getId());
            return messageSource.getMessage("notification.update.success", null, locale);
        }
        catch (Exception e){
            throw new InternetBankingException(messageSource.getMessage("failure",null,locale));
        }
    }

    @Override
    public String deleteNotification(Long id) {
        try{
            notificationsRepo.deleteById(id);
            logger.info("Notification {} has been deleted",id.toString());
            return messageSource.getMessage("notifications.delete.success",null,locale);
        }
        catch (Exception e){
            throw new InternetBankingException(messageSource.getMessage("failure",null,locale));
        }
    }

    @Override
    public NotificationsDTO convertEntityToDTO(Notifications notifications) {
        return  modelMapper.map(notifications,NotificationsDTO.class);
    }

    @Override
    public Notifications convertDTOToEntity(NotificationsDTO notificationsDTO) {
        return  modelMapper.map(notificationsDTO,Notifications.class);
    }

    @Override
    public List<NotificationsDTO> convertEntitiesToDTOs(Iterable<Notifications> notifications) {
        List<NotificationsDTO> notificationsDTOList = new ArrayList<>();
        for(Notifications notification: notifications){
            NotificationsDTO notificationsDTO = convertEntityToDTO(notification);
            notificationsDTOList.add(notificationsDTO);
        }
        return notificationsDTOList;
    }

}
