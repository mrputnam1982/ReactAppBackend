package com.mikep.ReactApp.Utils;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikep.ReactApp.Mixins.GuestMixin;
import com.mikep.ReactApp.Models.Guest;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Optional;

@Log4j2
public class GuestToJson {

    ObjectMapper objectMapper;
    public GuestToJson() {
        objectMapper = GuestToJson.buildMapper();
        objectMapper.addMixIn(Guest.class, GuestMixin.class);

    }
    public String objToJson(Guest guest) {

        String objJackson = "";
        try {
            objJackson = objectMapper.writeValueAsString(guest);
        } catch (Exception e) {
            log.info("failed conversion: Pfra object to Json", e);
        }
        return objJackson;
    }

    private static ObjectMapper buildMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(mapper.getSerializationConfig()
                .getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
        return mapper;
    }
}

