package longbridge.controllers.corporate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLConnection;
import java.nio.charset.Charset;
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

    //private static String UPLOAD_FOLDER = "C:\\ibanking\\upload\\";
    private static String UPLOAD_FOLDER = "/Users/user/Documents/UPLOAD_FOLDER/";

    @GetMapping("/bulk")
    public String getBulkTransfer() {
        return "/corp/transfer/bulktransfer/add";
    }

    /*
        * Download a file from
        *   - inside project, located in resources folder.
        *   Added by Bimpe Ayoola
     */
    //private static final String SERVER_FILE_PATH="C:\\ibanking\\files\\Copy-of-NEFT-ECOB-ABC-old-mutual.xls\\";
    private static final String FILENAME= "Copy-of-NEFT-ECOB-ABC-old-mutual.xls";

    @GetMapping("/bulk/download")
    public void downloadFile(HttpServletResponse response) throws IOException {


        File file = null;
        //file = new File(SERVER_FILE_PATH);
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        file = new File(classloader.getResource(FILENAME).getFile());


        if(!file.exists()){
            String errorMessage = "Sorry. The file you are looking for does not exist";
            System.out.println(errorMessage);
            OutputStream outputStream = response.getOutputStream();
            outputStream.write(errorMessage.getBytes(Charset.forName("UTF-8")));
            outputStream.close();
            return;
        }

        String mimeType= URLConnection.guessContentTypeFromName(file.getName());
        if(mimeType==null){
            System.out.println("mimetype is not detectable, will take default");
            mimeType = "application/octet-stream";
        }

        System.out.println("mimetype : "+mimeType);

        response.setContentType(mimeType);

        /* "Content-Disposition : inline" will show viewable types [like images/text/pdf/anything viewable by browser] right on browser
            while others(zip e.g) will be directly downloaded [may provide save as popup, based on your browser setting.]*/
        response.setHeader("Content-Disposition", String.format("inline; filename=\"" + file.getName() +"\""));


        /* "Content-Disposition : attachment" will be directly download, may provide save as popup, based on your browser setting*/
        //response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", file.getName()));

        response.setContentLength((int)file.length());

        InputStream inputStream = new BufferedInputStream(new FileInputStream(file));

        //Copy bytes from source to destination(outputstream in this example), closes both streams.
        FileCopyUtils.copy(inputStream, response.getOutputStream());
    }

 /* End of the addition for file download by Bimpe Ayoola*/

    @PostMapping("/bulk/upload")
    public String uploadBulkTransfeFile(@RequestParam("transferFile") MultipartFile file, RedirectAttributes redirectAttributes, Model model, Locale locale) {

        if (file.isEmpty()) {
            model.addAttribute("failure", messageSource.getMessage("file.required", null, locale));
            return "/corp/transfer/bulktransfer/add";
        }

        try {

            // Get the file and save it
            byte[] bytes = file.getBytes();
            System.out.println(bytes);
            Path path = Paths.get(UPLOAD_FOLDER + file.getOriginalFilename());
            System.out.println(path);
            Files.write(path, bytes);

            redirectAttributes.addFlashAttribute("message",
                    messageSource.getMessage("file.upload.success", null, locale));

        } catch (IOException e) {
            logger.error("Error uploading file", e);
            redirectAttributes.addFlashAttribute("failure", messageSource.getMessage("file.upload.failure", null, locale));
        }

        return "/corp/transfer/bulktransfer/add";
    }
}
