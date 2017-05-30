package longbridge.controllers.corporate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

/**
 * Created by Fortune on 5/30/2017.
 */

@Controller
@RequestMapping("/corporate/transfer")
public class CorpNAPSTransferController {

    @Autowired
    private MessageSource messageSource;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    private static String UPLOAD_FOLDER = "c://";

    @GetMapping("/bulk")
    public String getBulkTransfer() {
        return "/corp/bulktransfer/add";
    }

    @PostMapping("/bulk/upload")
    public String uploadBulkTransfeFile(@RequestParam("transferFile") MultipartFile file, RedirectAttributes redirectAttributes, Model model, Locale locale) {

        if (file.isEmpty()) {
            model.addAttribute("failure", messageSource.getMessage("file.required", null, locale));
            return "/corp/bulktransfer/add";
        }

        try {

            // Get the file and save it
            byte[] bytes = file.getBytes();
            Path path = Paths.get(UPLOAD_FOLDER + file.getOriginalFilename());
            Files.write(path, bytes);

            redirectAttributes.addFlashAttribute("message",
                    messageSource.getMessage("file.upload.success", null, locale));

        } catch (IOException e) {
            logger.error("Error uploading file", e);
            redirectAttributes.addFlashAttribute("failure", messageSource.getMessage("file.upload.failure", null, locale));
        }

        return "";
    }
}
