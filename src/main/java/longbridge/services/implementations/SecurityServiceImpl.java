package longbridge.services.implementations;


import longbridge.config.entrust.CustomHttpClient;
import longbridge.config.entrust.EntrustServiceResponse;
import longbridge.exception.InternetBankingSecurityException;
import longbridge.exception.InternetBankingTransferException;
import longbridge.models.RetailUser;
import longbridge.security.IpAddressUtils;
import longbridge.services.IntegrationService;
import longbridge.services.SecurityService;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.*;


/**
 * Created by ayoade_farooq@yahoo.com on 3/29/2017.
 */

@Service
public class SecurityServiceImpl implements SecurityService {


    @Value("${ENTRUST.app.code}")
    private String appCode;
    @Value("${ENTRUST.app.group}")
    private String appGroup;
    @Value("${ENTRUST.app.desc}")
    private String appDesc;
    @Value("${ENTRUST.app.user}")
    private String defaultUser;

    private BCryptPasswordEncoder passwordEncoder;
    private ModelMapper modelMapper;
    private IpAddressUtils ipAddressUtils;
    private IntegrationService integrationService;
    private Logger logger = LoggerFactory.getLogger(getClass());
    private VelocityEngine ve;
    private Template t;
    private VelocityContext context;
    private CustomHttpClient httpClient;

    @Autowired
    public SecurityServiceImpl(BCryptPasswordEncoder passwordEncoder, IntegrationService integrationService, ModelMapper modelMapper
            , IpAddressUtils ipAddressUtils, CustomHttpClient httpClient
    ) {
        this.passwordEncoder = passwordEncoder;
        this.integrationService = integrationService;

        this.modelMapper = modelMapper;
        this.ipAddressUtils = ipAddressUtils;
        Properties props = new Properties();
        props.put("resource.loader", "class");
        props.put("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        this.ve = new VelocityEngine();
        this.ve.init(props);
        this.context = new VelocityContext();
        this.httpClient = httpClient;
    }

    @Override
    public String generatePassword() {
        SecureRandom random = new SecureRandom();
        return new BigInteger(130, random).toString(32).substring(0, 12);
    }

    @Override
    public List<String> getSecurityQuestions() {

        return null;
    }

    @Override
    public boolean validateSecurityQuestion(RetailUser retailUser, String securityQuestion, String securityAnswer) {
        //TODO implement this through entrust
        return true;
    }

    @Override
    public boolean performTokenValidation(String username, String tokenString) {
        boolean result = false;

        try {
            StringWriter writer = new StringWriter();
            this.t = this.ve.getTemplate("entrust/performTokenAuth.vm");
            this.context.put("appCode", appCode);
            this.context.put("appDesc", appDesc);
            this.context.put("appGroup", appGroup);
            this.context.put("userName", username);
            this.context.put("token", tokenString);


            this.t.merge(this.context, writer);
            String payload = writer.toString();
            EntrustServiceResponse webServiceResponse = httpClient.sendHttpRequest(payload);
            String responseMessage = webServiceResponse.getResponseMessage();
            logger.trace("response {}", responseMessage);
            CharSequence charSequence = "<respCode>1</respCode>";
            boolean isSuccessful = responseMessage.contains(charSequence);
            result = isSuccessful;

            String respMesg = StringUtils.substringBetween(responseMessage, "<respMessageCode>", "</respMessageCode>");

            logger.trace("response is {}", respMesg);

            if (!isSuccessful) {
                String erroMessg = StringUtils.substringBetween(responseMessage, "<respMessage>", "</respMessage>");
                throw new InternetBankingSecurityException(erroMessg);
            }


            logger.info("******************END RESPONSE***********");

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new InternetBankingSecurityException(e.getMessage(), e);

        }
        return result;


    }

    @Override
    public boolean performOtpValidation(String username, String otp) {
        boolean result = false;

        try {
            StringWriter writer = new StringWriter();
            this.t = this.ve.getTemplate("entrust/performOTPAuthentication.vm");
            this.context.put("appCode", appCode);
            this.context.put("appDesc", appDesc);
            this.context.put("otp", otp);
            this.context.put("appGroup", appGroup);
            this.context.put("userName", username);

            this.t.merge(this.context, writer);
            String payload = writer.toString();
            EntrustServiceResponse webServiceResponse = httpClient.sendHttpRequest(payload);
            String responseMessage = webServiceResponse.getResponseMessage();
            logger.trace("response {}", responseMessage);
            CharSequence charSequence = "<respCode>1</respCode>";
            boolean isSuccessful = responseMessage.contains(charSequence);
            result = isSuccessful;

            String respMesg = StringUtils.substringBetween(responseMessage, "<respMessageCode>", "</respMessageCode>");

            logger.trace("response is {}", respMesg);


            logger.info("******************END RESPONSE***********");
            if (!isSuccessful) {
                String erroMessg = StringUtils.substringBetween(responseMessage, "<respMessage>", "</respMessage>");
                throw new InternetBankingSecurityException(erroMessg);
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new InternetBankingSecurityException(e.getMessage(), e);

        }
        return result;


    }

    @Override
    public boolean synchronizeToken(String username, String sNo, String tokenResp1, String tokenResp2) {
        boolean result = false;

        try {
            StringWriter writer = new StringWriter();
            this.t = this.ve.getTemplate("entrust/performTokenSync.vm");
            this.context.put("appCode", appCode);
            this.context.put("appDesc", appDesc);
            this.context.put("token1", tokenResp1);
            this.context.put("token2", tokenResp2);
            this.context.put("appGroup", appGroup);
            this.context.put("userName", username);
            this.context.put("sn", sNo);


            this.t.merge(this.context, writer);
            String payload = writer.toString();
            EntrustServiceResponse webServiceResponse = httpClient.sendHttpRequest(payload);
            String responseMessage = webServiceResponse.getResponseMessage();
            logger.trace("response {}", responseMessage);
            CharSequence charSequence = "<respCode>1</respCode>";
            boolean isSuccessful = responseMessage.contains(charSequence);
            result = isSuccessful;

            String respMesg = StringUtils.substringBetween(responseMessage, "<respMessageCode>", "</respMessageCode>");

            logger.trace("response is {}", respMesg);


            logger.info("******************END RESPONSE***********");
            if (!isSuccessful) {
                String erroMessg = StringUtils.substringBetween(responseMessage, "<respMessage>", "</respMessage>");
                throw new InternetBankingSecurityException(erroMessg);
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new InternetBankingSecurityException(e.getMessage(), e);

        }
        return result;


    }

    @Override
    public boolean sendOtp(String username) {
        boolean result;

        try {
            StringWriter writer = new StringWriter();
            this.t = this.ve.getTemplate("entrust/performCreateSendOTP.vm");
            this.context.put("appCode", appCode);
            this.context.put("appDesc", appDesc);
            this.context.put("appGroup", appGroup);
            this.context.put("userName", username);
            this.t.merge(this.context, writer);
            String payload = writer.toString();
            EntrustServiceResponse webServiceResponse = httpClient.sendHttpRequest(payload);
            String responseMessage = webServiceResponse.getResponseMessage();
            logger.trace("response {}", responseMessage);
            CharSequence charSequence = "<respCode>1</respCode>";
            boolean isSuccessful = responseMessage.contains(charSequence);
            result = isSuccessful;
            String respMesg = StringUtils.substringBetween(responseMessage, "<respMessageCode>", "</respMessageCode>");
            logger.trace("response code is {}", respMesg);


            logger.info("******************END RESPONSE***********");
            if (!isSuccessful) {
                String erroMessg = StringUtils.substringBetween(responseMessage, "<respMessage>", "</respMessage>");
                throw new InternetBankingSecurityException(erroMessg);
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new InternetBankingSecurityException(e.getMessage(), e);

        }
        return result;

    }

    @Override
    public boolean createEntrustUser(String username, String fullName, boolean enableOtp) {
        boolean result = false;

        try {
            StringWriter writer = new StringWriter();
            this.t = this.ve.getTemplate("entrust/performCreateEntrustUser.vm");
            this.context.put("appCode", appCode);
            this.context.put("appDesc", appDesc);
            this.context.put("otp", enableOtp);
            this.context.put("fullname", fullName);
            this.context.put("appGroup", appGroup);
            this.context.put("userName", username);
            this.t.merge(this.context, writer);
            String payload = writer.toString();
            EntrustServiceResponse webServiceResponse = httpClient.sendHttpRequest(payload);
            String responseMessage = webServiceResponse.getResponseMessage();
            CharSequence charSequence = "<respCode>1</respCode>";
            boolean isSuccessful = responseMessage.contains(charSequence);
            result = isSuccessful;
            String resp = StringUtils.substringBetween(responseMessage, "<respCode>", "</respCode>");
            System.out.println("Response from webservice: " + resp + " Successful status is : " + isSuccessful);


            logger.info("******************END RESPONSE***********");
            if (!isSuccessful) {
                String erroMessg = StringUtils.substringBetween(responseMessage, "<respMessage>", "</respMessage>");
                throw new InternetBankingSecurityException(erroMessg);
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new InternetBankingSecurityException(e.getMessage(), e);

        }
        return result;
    }

    @Override
    public void deleteEntrustUser(String username) {


        try {
            StringWriter writer = new StringWriter();
            this.t = this.ve.getTemplate("entrust/performDeleteEntrustUser.vm");
            this.context.put("appCode", appCode);
            this.context.put("appDesc", appDesc);
            this.context.put("userName", username);
            this.context.put("appGroup", appGroup);
            this.t.merge(this.context, writer);
            String payload = writer.toString();
            EntrustServiceResponse webServiceResponse = httpClient.sendHttpRequest(payload);
            String responseMessage = webServiceResponse.getResponseMessage();
            logger.trace("response {}", responseMessage);
            CharSequence charSequence = "<respCode>1</respCode>";
            boolean isSuccessful = responseMessage.contains(charSequence);


            String respMesg = StringUtils.substringBetween(responseMessage, "<respMessageCode>", "</respMessageCode>");


            logger.trace("response is {}", respMesg);


            logger.info("******************END RESPONSE***********");
            if (!isSuccessful) {
                String erroMessg = StringUtils.substringBetween(responseMessage, "<respMessage>", "</respMessage>");
                throw new InternetBankingSecurityException(erroMessg);
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new InternetBankingSecurityException(e.getMessage(), e);

        }


    }

    @Override
    public boolean assignToken(String username, String serialNumber) {
        boolean result = false;

        try {
            StringWriter writer = new StringWriter();
            this.t = this.ve.getTemplate("entrust/performAssignToken.vm");
            this.context.put("appCode", appCode);
            this.context.put("appDesc", appDesc);
            this.context.put("sn", serialNumber);
            this.context.put("appGroup", appGroup);
            this.context.put("userName", username);
            this.t.merge(this.context, writer);
            String payload = writer.toString();
            EntrustServiceResponse webServiceResponse = httpClient.sendHttpRequest(payload);
            String responseMessage = webServiceResponse.getResponseMessage();
            logger.trace("response {}", responseMessage);
            CharSequence charSequence = "<respCode>1</respCode>";
            boolean isSuccessful = responseMessage.contains(charSequence);
            result = isSuccessful;

            String respMesg = StringUtils.substringBetween(responseMessage, "<respMessageCode>", "</respMessageCode>");
            String resp = StringUtils.substringBetween(responseMessage, "<respCode>", "</respCode>");
            logger.trace("response is {}", respMesg);


            logger.info("******************END RESPONSE***********");
            if (!isSuccessful) {
                String erroMessg = StringUtils.substringBetween(responseMessage, "<respMessage>", "</respMessage>");
                throw new InternetBankingSecurityException(erroMessg);
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new InternetBankingSecurityException(e.getMessage(), e);

        }
        return result;
    }

    @Override
    public boolean activateToken(String username, String serialNumber) {

        boolean result = false;

        try {
            StringWriter writer = new StringWriter();
            this.t = this.ve.getTemplate("entrust/performActivateToken.vm");
            this.context.put("appCode", appCode);
            this.context.put("appDesc", appDesc);
            this.context.put("sn", serialNumber);
            this.context.put("appGroup", appGroup);
            this.context.put("userName", username);

            this.t.merge(this.context, writer);
            String payload = writer.toString();
            EntrustServiceResponse webServiceResponse = httpClient.sendHttpRequest(payload);
            String responseMessage = webServiceResponse.getResponseMessage();
            logger.trace("response {}", responseMessage);
            CharSequence charSequence = "<respCode>1</respCode>";
            boolean isSuccessful = responseMessage.contains(charSequence);
            result = isSuccessful;

            String respMesg = StringUtils.substringBetween(responseMessage, "<respMessageCode>", "</respMessageCode>");
            String resp = StringUtils.substringBetween(responseMessage, "<respCode>", "</respCode>");
            logger.trace("response is {}", respMesg);


            logger.info("******************END RESPONSE***********");
            if (!isSuccessful) {
                String erroMessg = StringUtils.substringBetween(responseMessage, "<respMessage>", "</respMessage>");
                throw new InternetBankingSecurityException(erroMessg);
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new InternetBankingSecurityException(e.getMessage(), e);

        }
        return result;


    }

    @Override
    public boolean deActivateToken(String username, String serialNumber) {

        boolean result = false;

        try {
            StringWriter writer = new StringWriter();
            this.t = this.ve.getTemplate("entrust/performDeactivateToken.vm");
            this.context.put("appCode", appCode);
            this.context.put("appDesc", appDesc);
            this.context.put("sn", serialNumber);
            this.context.put("appGroup", appGroup);
            this.context.put("userName", username);
            this.t.merge(this.context, writer);
            String payload = writer.toString();
            EntrustServiceResponse webServiceResponse = httpClient.sendHttpRequest(payload);
            String responseMessage = webServiceResponse.getResponseMessage();
            logger.trace("response {}", responseMessage);
            CharSequence charSequence = "<respCode>1</respCode>";
            boolean isSuccessful = responseMessage.contains(charSequence);
            result = isSuccessful;

            String respMesg = StringUtils.substringBetween(responseMessage, "<respMessageCode>", "</respMessageCode>");
            logger.trace("response is {}", respMesg);


            logger.info("******************END RESPONSE***********");
            if (!isSuccessful) {
                String erroMessg = StringUtils.substringBetween(responseMessage, "<respMessage>", "</respMessage>");
                throw new InternetBankingSecurityException(erroMessg);
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new InternetBankingSecurityException(e.getMessage(), e);

        }
        return result;


    }

    @Override
    public void setUserQA(String username, String question, String answer) {
        try {
           List<String>  questions= new ArrayList<>();
           List<String>  answers= new ArrayList<>();
             questions.add(question);
             answers.add(answer);
             setUserQA(username,questions,answers);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new InternetBankingSecurityException(e.getMessage(), e);

        }


    }

    @Override
    public void setUserQA(String username, List<String> questions, List<String> answers) throws InternetBankingTransferException
    {
        if ( questions==null  ||questions.isEmpty())throw new IllegalArgumentException();
        if ( answers==null  ||answers.isEmpty())throw new IllegalArgumentException();

        try {
            StringWriter writer = new StringWriter();
            this.t = this.ve.getTemplate("entrust/performSetQA.vm");
            this.context.put("appCode", appCode);
            this.context.put("appDesc", appDesc);
            this.context.put("userName", username);
            this.context.put("appGroup", appGroup);
            this.context.put("questions", questions);
            this.context.put("answers", answers);
            this.t.merge(this.context, writer);
            String payload = writer.toString();
            EntrustServiceResponse webServiceResponse = httpClient.sendHttpRequest(payload);
            String responseMessage = webServiceResponse.getResponseMessage();
            logger.trace("response {}", responseMessage);
            CharSequence charSequence = "<respCode>1</respCode>";
            boolean isSuccessful = responseMessage.contains(charSequence);


            String respMesg = StringUtils.substringBetween(responseMessage, "<respMessageCode>", "</respMessageCode>");


            logger.trace("response is {}", respMesg);


            logger.info("******************END RESPONSE***********");
            if (!isSuccessful) {
                String erroMessg = StringUtils.substringBetween(responseMessage, "<respMessage>", "</respMessage>");
                throw new InternetBankingSecurityException(erroMessg);
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new InternetBankingSecurityException(e.getMessage(), e);

        }



    }

    @Override
    public Map<String, List<String>> getUserQA(String username) {
        Map<String, List<String>> list = new HashMap<>();
        try {
            StringWriter writer = new StringWriter();
            this.t = this.ve.getTemplate("entrust/performGetQA.vm");
            this.context.put("appCode", appCode);
            this.context.put("appDesc", appDesc);
            this.context.put("appGroup", appGroup);
            this.context.put("userName", username);
            this.t.merge(this.context, writer);
            String payload = writer.toString();
            EntrustServiceResponse webServiceResponse = httpClient.sendHttpRequest(payload);
            String responseMessage = webServiceResponse.getResponseMessage();
            logger.trace("response {}", responseMessage);
            CharSequence charSequence = "<respCode>1</respCode>";


            boolean isSuccessful = responseMessage.contains(charSequence);
            if (!isSuccessful) {
                String erroMessg = StringUtils.substringBetween(responseMessage, "<respMessage>", "</respMessage>");
                throw new InternetBankingSecurityException(erroMessg);
            }
            String msg = StringUtils.substringBetween(responseMessage, "<respMessageCode>", "</respMessageCode>");

            logger.trace("response message code : {}", msg);

            String[] questions = StringUtils.substringsBetween(responseMessage, "<questions>", "</questions>");
            String[] answers = StringUtils.substringsBetween(responseMessage, "<answers>", "</answers>");
            List<String> questionList = Arrays.asList(questions);
            List<String> answerList = Arrays.asList(answers);

            list.put("questions", questionList);
            list.put("answers", answerList);

            logger.info("******************END RESPONSE***********");
           
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new InternetBankingSecurityException(e.getMessage(), e);

        }


        return list;


    }

    @Override
    public Integer getMinUserQA(String username) {
        try {
            StringWriter writer = new StringWriter();
            this.t = this.ve.getTemplate("entrust/performGetMinQASize.vm");
            this.context.put("appCode", appCode);
            this.context.put("appDesc", appDesc);
            this.context.put("appGroup", appGroup);
            this.context.put("userName", username);
            this.t.merge(this.context, writer);
            String payload = writer.toString();
            EntrustServiceResponse webServiceResponse = httpClient.sendHttpRequest(payload);
            String responseMessage = webServiceResponse.getResponseMessage();
            logger.trace("response {}", responseMessage);
            CharSequence charSequence = "<respCode>1</respCode>";


            boolean isSuccessful = responseMessage.contains(charSequence);
            if (!isSuccessful) {
                String erroMessg = StringUtils.substringBetween(responseMessage, "<respMessage>", "</respMessage>");
                throw new InternetBankingSecurityException(erroMessg);
            }

            String msg = StringUtils.substringBetween(responseMessage, "<respMessageCode>", "</respMessageCode>");

            logger.trace("response message code : {}", msg);

            logger.info("******************END RESPONSE***********");
           
            Integer number = Integer.parseInt(StringUtils.substringBetween(responseMessage, "<questionSize>", "</questionSize>"));
            return number;

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new InternetBankingSecurityException(e.getMessage(), e);

        }


    }

    @Override
    public Integer getMinUserQA() {
        try {
            StringWriter writer = new StringWriter();
            this.t = this.ve.getTemplate("entrust/performGetMinQASize.vm");
            this.context.put("appCode", appCode);
            this.context.put("appDesc", appDesc);
            this.context.put("appGroup", appGroup);
            this.context.put("userName", defaultUser);
            this.t.merge(this.context, writer);
            String payload = writer.toString();
            EntrustServiceResponse webServiceResponse = httpClient.sendHttpRequest(payload);
            String responseMessage = webServiceResponse.getResponseMessage();
            logger.trace("response {}", responseMessage);
            CharSequence charSequence = "<respCode>1</respCode>";


            boolean isSuccessful = responseMessage.contains(charSequence);
            String msg = StringUtils.substringBetween(responseMessage, "<respMessageCode>", "</respMessageCode>");

            logger.trace("response message code : {}", msg);
            if (!isSuccessful) throw new InternetBankingSecurityException(msg);


            logger.info("******************END RESPONSE***********");
            if (!isSuccessful) {
                String erroMessg = StringUtils.substringBetween(responseMessage, "<respMessage>", "</respMessage>");
                throw new InternetBankingSecurityException(erroMessg);
            }

            Integer number = Integer.parseInt(StringUtils.substringBetween(responseMessage, "<questionSize>", "</questionSize>"));
            return number;

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new InternetBankingSecurityException(e.getMessage(), e);

        }
    }

    @Override
    public Integer geUserQASize(String username) {
        try {
            StringWriter writer = new StringWriter();
            this.t = this.ve.getTemplate("entrust/performGetQASize.vm");
            this.context.put("appCode", appCode);
            this.context.put("appDesc", appDesc);
            this.context.put("appGroup", appGroup);
            this.context.put("userName", username);
            this.t.merge(this.context, writer);
            String payload = writer.toString();
            EntrustServiceResponse webServiceResponse = httpClient.sendHttpRequest(payload);
            String responseMessage = webServiceResponse.getResponseMessage();
            logger.trace("response {}", responseMessage);
            CharSequence charSequence = "<respCode>1</respCode>";


            boolean isSuccessful = responseMessage.contains(charSequence);
            if (!isSuccessful) {
                String erroMessg = StringUtils.substringBetween(responseMessage, "<respMessage>", "</respMessage>");
                throw new InternetBankingSecurityException(erroMessg);
            }
            String msg = StringUtils.substringBetween(responseMessage, "<respMessageCode>", "</respMessageCode>");

            logger.trace("response message code : {}", msg);

            logger.info("******************END RESPONSE***********");
           

            Integer number = Integer.parseInt(StringUtils.substringBetween(responseMessage, "<questionSize>", "</questionSize>"));
            return number;

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new InternetBankingSecurityException(e.getMessage(), e);

        }

    }

    @Override
    public Map<String, List<String>> getMutualAuth(String username) throws InternetBankingTransferException {
        Map<String, List<String>> list = new HashMap<>();

        try {
            StringWriter writer = new StringWriter();
            this.t = this.ve.getTemplate("entrust/performGetMutualAuth.vm");
            this.context.put("appCode", appCode);
            this.context.put("appDesc", appDesc);
            this.context.put("appGroup", appGroup);
            this.context.put("userName", username);
            this.t.merge(this.context, writer);
            String payload = writer.toString();
            EntrustServiceResponse webServiceResponse = httpClient.sendHttpRequest(payload);
            String responseMessage = webServiceResponse.getResponseMessage();
            logger.trace("response {}", responseMessage);
            CharSequence charSequence = "<respCode>1</respCode>";
            boolean isSuccessful = responseMessage.contains(charSequence);
            String msg = StringUtils.substringBetween(responseMessage, "<respMessageCode>", "</respMessageCode>");

            logger.trace("response message code : {}", msg);
            if (!isSuccessful) {
                String erroMessg = StringUtils.substringBetween(responseMessage, "<respMessage>", "</respMessage>");
                throw new InternetBankingSecurityException(erroMessg);
            }

            String[] captions = StringUtils.substringsBetween(responseMessage, "<captionSecret>", "</captionSecret>");
            String[] images = StringUtils.substringsBetween(responseMessage, "<imageSecret>", "</imageSecret>");
            List<String> captionSecret = Arrays.asList(captions);
            List<String> imageSecret = Arrays.asList(images);


            list.put("imageSecret", imageSecret);
            list.put("captionSecret", captionSecret);


            logger.info("******************END RESPONSE***********");

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new InternetBankingSecurityException(e.getMessage(), e);

        }


        return list;

    }

    @Override
    public void setMutualAuth(String username, List<String> mutualCaption, List<String> mutualImage) {


    }

    @Override
    public void setMutualAuth(String username, String mutualCaption, String mutualImagePath) {

        try {
            StringWriter writer = new StringWriter();
            this.t = this.ve.getTemplate("entrust/performSetMutualAuthX.vm");
            this.context.put("appCode", appCode);
            this.context.put("appDesc", appDesc);
            this.context.put("appGroup", appGroup);
            this.context.put("caption", mutualCaption);
            this.context.put("image", mutualImagePath);
            this.t.merge(this.context, writer);
            String payload = writer.toString();
            EntrustServiceResponse webServiceResponse = httpClient.sendHttpRequest(payload);
            String responseMessage = webServiceResponse.getResponseMessage();
            logger.trace("response {}", responseMessage);
            CharSequence charSequence = "<respCode>1</respCode>";
            boolean isSuccessful = responseMessage.contains(charSequence);
            String msg = StringUtils.substringBetween(responseMessage, "<respMessageCode>", "</respMessageCode>");

            logger.trace("response message code : {}", msg);


            logger.info("******************END RESPONSE***********");
            if (!isSuccessful) {
                String erroMessg = StringUtils.substringBetween(responseMessage, "<respMessage>", "</respMessage>");
                throw new InternetBankingSecurityException(erroMessg);
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new InternetBankingSecurityException(e.getMessage(), e);

        }


    }

    @Override
    public void setMutualAuth(String username, List<String> mutualCaption, List<String> mutualImage, String token) {


    }

    @Override
    public String getTokenSerials(String username) {
        String result = "";

        try {
            StringWriter writer = new StringWriter();
            this.t = this.ve.getTemplate("entrust/performGetTokenSerial.vm");
            this.context.put("appCode", appCode);
            this.context.put("appDesc", appDesc);
            this.context.put("appGroup", appGroup);
            this.context.put("userName", username);


            this.t.merge(this.context, writer);
            String payload = writer.toString();
            EntrustServiceResponse webServiceResponse = httpClient.sendHttpRequest(payload);
            String responseMessage = webServiceResponse.getResponseMessage();
            logger.trace("response {}", responseMessage);
            CharSequence charSequence = "<respCode>1</respCode>";
            boolean isSuccessful = responseMessage.contains(charSequence);


            String respMesg = StringUtils.substringBetween(responseMessage, "<respMessageCode>", "</respMessageCode>");

            logger.trace("response is {}", respMesg);
            if (!isSuccessful) {
                String erroMessg = StringUtils.substringBetween(responseMessage, "<respMessage>", "</respMessage>");
                throw new InternetBankingSecurityException(erroMessg);
            }

            result = StringUtils.substringBetween(responseMessage, "<tokenSerials>", "</tokenSerials>");
            logger.info("******************END RESPONSE***********");

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new InternetBankingSecurityException(e.getMessage(), e);

        }
        return result;
    }

    @Override
    public boolean unLockUser(String username) {

        boolean result = false;

        try {
            StringWriter writer = new StringWriter();
            this.t = this.ve.getTemplate("entrust/performUnLockUser.vm");
            this.context.put("appCode", appCode);
            this.context.put("appDesc", appDesc);
            this.context.put("appGroup", appGroup);
            this.context.put("userName", username);
            this.t.merge(this.context, writer);
            String payload = writer.toString();
            EntrustServiceResponse webServiceResponse = httpClient.sendHttpRequest(payload);
            String responseMessage = webServiceResponse.getResponseMessage();
            logger.trace("response {}", responseMessage);
            CharSequence charSequence = "<respCode>1</respCode>";
            boolean isSuccessful = responseMessage.contains(charSequence);
            result = isSuccessful;

            String respMesg = StringUtils.substringBetween(responseMessage, "<respMessageCode>", "</respMessageCode>");
            logger.trace("response is {}", respMesg);


            logger.info("******************END RESPONSE***********");
            if (!isSuccessful) {
                String erroMessg = StringUtils.substringBetween(responseMessage, "<respMessage>", "</respMessage>");
                throw new InternetBankingSecurityException(erroMessg);
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new InternetBankingSecurityException(e.getMessage(), e);

        }
        return result;


    }

    @Override
    public boolean updateUser(String username, String fullName, boolean enableOtp) {
        boolean result = false;

        try {
            StringWriter writer = new StringWriter();
            this.t = this.ve.getTemplate("entrust/performUpdateEntrustUser.vm");
            this.context.put("appCode", appCode);
            this.context.put("appDesc", appDesc);
            this.context.put("fullName", fullName);
            this.context.put("otp", enableOtp);
            this.context.put("appGroup", appGroup);
            this.context.put("userName", username);
            this.t.merge(this.context, writer);
            String payload = writer.toString();
            EntrustServiceResponse webServiceResponse = httpClient.sendHttpRequest(payload);
            String responseMessage = webServiceResponse.getResponseMessage();
            logger.trace("response {}", responseMessage);
            CharSequence charSequence = "<respCode>1</respCode>";
            boolean isSuccessful = responseMessage.contains(charSequence);
            result = isSuccessful;

            String respMesg = StringUtils.substringBetween(responseMessage, "<respMessageCode>", "</respMessageCode>");
            logger.trace("response is {}", respMesg);


            logger.info("******************END RESPONSE***********");
            if (!isSuccessful) {
                String erroMessg = StringUtils.substringBetween(responseMessage, "<respMessage>", "</respMessage>");
                throw new InternetBankingSecurityException(erroMessg);
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new InternetBankingSecurityException(e.getMessage(), e);

        }
        return result;


    }

    @Override
    public boolean addUserContacts(String email, String phone, boolean phoneDefault, String userName) {
        boolean result = false;

        try {
            StringWriter writer = new StringWriter();
            this.t = this.ve.getTemplate("entrust/performAddContactDetail.vm");
            this.context.put("appCode", appCode);
            this.context.put("appDesc", appDesc);
            this.context.put("phoneDefault", phoneDefault);
            this.context.put("email", email);
            this.context.put("appGroup", appGroup);
            this.context.put("userName", userName);
            this.context.put("emailDevLabel", "CmbEmailOtp");
            this.context.put("emailDefault", false);
            this.context.put("PhoneDevLabel", "Vanso");
            this.context.put("phone", phone);

            this.t.merge(this.context, writer);
            String payload = writer.toString();
            EntrustServiceResponse webServiceResponse = httpClient.sendHttpRequest(payload);
            String responseMessage = webServiceResponse.getResponseMessage();
            logger.trace("User Contact Details", context);
            logger.trace("response {}", responseMessage);
            CharSequence charSequence = "<respCode>1</respCode>";
            boolean isSuccessful = responseMessage.contains(charSequence);
            result = isSuccessful;

            String respMesg = StringUtils.substringBetween(responseMessage, "<respMessageCode>", "</respMessageCode>");
            String resp = StringUtils.substringBetween(responseMessage, "<respCode>", "</respCode>");
            logger.trace("response is {}", respMesg);


            logger.info("******************END RESPONSE***********");
            if (!isSuccessful) {
                String erroMessg = StringUtils.substringBetween(responseMessage, "<respMessage>", "</respMessage>");
                throw new InternetBankingSecurityException(erroMessg);
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new InternetBankingSecurityException(e.getMessage(), e);

        }
        return result;
    }
}


