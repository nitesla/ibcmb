package longbridge.controllers.admin;

import longbridge.dtos.PhishingImageDTO;
import longbridge.dtos.SecQuestionDTO;
import longbridge.exception.InternetBankingException;
import longbridge.models.PhishingImage;
import longbridge.models.SecurityQuestions;
import longbridge.services.PhishingImageService;
import longbridge.services.VerificationService;
import longbridge.models.PhishingImage;
import longbridge.services.PhishingImageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.datatables.repository.DataTablesUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

/**
 * Created by Longbridge on 22/06/2017.
 */
@Controller
@RequestMapping("/admin/phishing")
public class AdmPhishingController {

    @Value("${phishing.image.folder}")
    private String folder;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PhishingImageService phishingImageService;

    @Autowired
    MessageSource messageSource;

    @GetMapping
    public String securityQuestions() {
        return "/adm/phishing/view";
    }

    @GetMapping("/new")
    public String addSecurityQuestions() {
        return "/adm/phishing/add";
    }



    //private static String UPLOAD_FOLDER = "C:\\ibanking\\upload\\";
   // private static String FOLDER = "/Users/user/Documents/UPLOAD_FOLDER/";
    @PostMapping
    public String uploadPhishingImage(@RequestParam("phishingImage") MultipartFile file, RedirectAttributes redirectAttributes, Model model, Locale locale) {
        // Get the file and save it
        try {
            byte[] fileBytes = file.getBytes();
            Path path = Paths.get(folder + file.getOriginalFilename());
            Files.write(path, fileBytes);
            String filename = file.getOriginalFilename();
            System.out.println(path.toString());
            PhishingImage phishingImage = new PhishingImage();
            //phishingImage.setImagePath(path.toString());
            phishingImage.setImagePath(filename);
            phishingImageService.saveImage(phishingImage);
            redirectAttributes.addFlashAttribute("message", messageSource.getMessage("file.upload.success", null, locale));

        } catch (IOException e) {
            logger.error("Error uploading file", e);
            redirectAttributes.addFlashAttribute("failure", messageSource.getMessage("file.upload.failure", null, locale));
        }
        return "/adm/phishing/add";
    }

    @GetMapping("/{id}/delete")
    public String deleteSecurityQuestions(@PathVariable Long id, RedirectAttributes redirectAttributes, Locale locale) {

        return "";
    }


    @GetMapping(path = "/all")
    public
    @ResponseBody
    DataTablesOutput<PhishingImageDTO> getAllPhishingImages(DataTablesInput input) {
        Pageable pageable = DataTablesUtils.getPageable(input);
        Page<PhishingImageDTO> sq = phishingImageService.getAllPhishingImages(pageable);
        DataTablesOutput<PhishingImageDTO> out = new DataTablesOutput<PhishingImageDTO>();
        out.setDraw(input.getDraw());
        out.setData(sq.getContent());
        out.setRecordsFiltered(sq.getTotalElements());
        out.setRecordsTotal(sq.getTotalElements());
        return out;
    }

}
