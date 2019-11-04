package longbridge.apiLayer.antiFraudApi;

import io.swagger.annotations.Api;
import longbridge.apiLayer.data.ResponseData;
import longbridge.dtos.TransferRequestDTO;
import longbridge.models.TransRequest;
import longbridge.services.TransferService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


    @RestController
    @Api(value = "Transfer AntiFraud Check status", description = "Transfer AntiFraud Check status")
    @RequestMapping(value = "/api/antifraud/")
    public class TransferAntiFraudStatus {

        Logger logger = LoggerFactory.getLogger(this.getClass());
        ResponseData responseData = new ResponseData();
        @Autowired
        private TransferService transferService;

        @PostMapping(value = "status/update")
        public ResponseEntity<?> updateTransferStatus(@RequestBody TransferRequestDTO transferRequestDTO) {
            String errorMessage = "";

            try {

                TransRequest transRequest = transferService.updateTransferStatus(transferRequestDTO);

               if(transferRequestDTO.getChannel().equals("MOBILE")){


               }


                responseData.setMessage(transRequest.getStatusDescription());
                responseData.setData(transRequest.getStatusDescription());
                responseData.setError(false);
                responseData.setCode(transRequest.getStatus());
                return new ResponseEntity<Object>(responseData, HttpStatus.OK);

            }catch (NullPointerException e) {
                logger.error("Error updating transfer", e);
                responseData.setMessage("Entity not found");
                responseData.setData("Not Found");
                responseData.setCode("99");
                responseData.setError(true);
                return new ResponseEntity<Object>(responseData, HttpStatus.INTERNAL_SERVER_ERROR);

            }
            catch (Exception e) {
                errorMessage = e.getMessage();
                logger.error("Error updating transfer", e);
                responseData.setMessage(errorMessage);
                responseData.setCode("99");
                responseData.setError(true);
                return new ResponseEntity<Object>(responseData, HttpStatus.INTERNAL_SERVER_ERROR);

            }

        }
    }