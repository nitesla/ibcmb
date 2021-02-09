package longbridge.utils;

import longbridge.apilayer.models.WebhookResponse;
import longbridge.models.RetailUser;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class RetailWebHook {

    final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Value("${WEBHOOK.URL}")
    private String webhookUrl;

    @Value("${WEBHOOK.header}")
    private String webhookAuthKey;

    public WebhookResponse pushToWebHook(RetailUser user) {
        WebhookResponse webhookResponse = new WebhookResponse();
        try {

            try (
                    DefaultHttpClient client = new DefaultHttpClient()) {
                HttpPost post = new HttpPost(webhookUrl);
                try {

                    List<NameValuePair> urlParameters = new ArrayList<>();
                    urlParameters.add(new BasicNameValuePair("username", user.getUserName()));
                    urlParameters.add(new BasicNameValuePair("password", user.getPassword()));

                    post.setEntity(new UrlEncodedFormEntity(urlParameters));//this is for url encoded

                    //                post.setHeader("Accept", "application/x-www-form-urlencoded");
                    post.setHeader("Authorization", "Bearer  "+webhookAuthKey);
                    post.setHeader("Content-type", "application/x-www-form-urlencoded");

                    try {
                        org.apache.http.HttpResponse resp = client.execute(post);
                        HttpEntity resEntityPost = resp.getEntity();

                        String response;
                        if (resEntityPost != null) {
                            response = EntityUtils.toString(resEntityPost);
                            JSONObject object = new JSONObject(response);
                            logger.info("webhook response {} ", object);
                            String code = object.getString("code");
                            String description = object.getString("description");
                            if (code.equalsIgnoreCase("0")) {
                                webhookResponse.setCode(code);
                                webhookResponse.setDescription(description);
                                return webhookResponse;
                            } else if (code.equalsIgnoreCase("20")) {
                                webhookResponse.setCode(code);
                                webhookResponse.setDescription(description);
                                return webhookResponse;
                            } else if (code.equalsIgnoreCase("40")) {
                                webhookResponse.setCode(code);
                                webhookResponse.setDescription(description);
                                return webhookResponse;
                            } else if (code.equalsIgnoreCase("99")) {
                                webhookResponse.setCode(code);
                                webhookResponse.setDescription(description);
                                return webhookResponse;
                            } else if (code.equalsIgnoreCase("10")) {
                                webhookResponse.setCode(code);
                                webhookResponse.setDescription(description);
                                return webhookResponse;
                            }

                        } else {
                            return webhookResponse;
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return webhookResponse;
    }

}
