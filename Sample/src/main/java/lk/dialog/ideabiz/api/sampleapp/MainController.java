package lk.dialog.ideabiz.api.sampleapp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lk.dialog.ideabiz.api.model.common.sms.InboundSMSRequestWrap;
import lk.dialog.ideabiz.api.model.common.sms.OutboundSMSMessageRequest;
import lk.dialog.ideabiz.api.model.common.sms.OutboundSMSMessagingRequestWrap;
import lk.dialog.ideabiz.library.LibraryManager;
import lk.dialog.ideabiz.library.model.APICall.APICallResponse;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Malinda_07654 on 2/9/2016.
 */
@Controller
@RequestMapping("/inbound/")
public class MainController {

    public static Gson gson = null;
    final static Logger logger = Logger.getLogger(MainController.class);


    public MainController() {
        gson = new GsonBuilder().serializeNulls().create();
        ;
    }


    @RequestMapping(value = "inbound/SMS", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public ResponseEntity<String> inbound(HttpServletRequest request, @RequestBody String body) {
        logger.info("Inbound SMS : " + body);

        //Creating object from json
        InboundSMSRequestWrap inboundSMSRequestWrap = gson.fromJson(body, InboundSMSRequestWrap.class);

        //Creating outbound request
        OutboundSMSMessagingRequestWrap outboundSMSMessagingRequestWrap = new OutboundSMSMessagingRequestWrap();
        OutboundSMSMessageRequest outboundSMSMessageRequest = new OutboundSMSMessageRequest(
                "tel:" + inboundSMSRequestWrap.getInboundSMSMessageNotification().getInboundSMSMessage().getSenderAddress(),
                inboundSMSRequestWrap.getInboundSMSMessageNotification().getInboundSMSMessage().getMessage(), "tel:7555", "7555");
        outboundSMSMessagingRequestWrap.setOutboundSMSMessageRequest(outboundSMSMessageRequest);

        //Setting headers
        Map<String, String> header = new HashMap<String, String>();
        header.put("Content-Type", "application/json");
        header.put("Accept", "application/json");

        //Sending API call
        APICallResponse response = null;
        try {
            response = LibraryManager.getApiCall().sendAuthAPICall(1, "https://ideabiz.lk/apicall/smsmessaging/v2/outbound/94777339033/requests", "POST", header, gson.toJson(outboundSMSMessagingRequestWrap), false);
        } catch (Exception e) {
            logger.error(e);
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<String>(gson.toJson(response), HttpStatus.OK);

    }

}