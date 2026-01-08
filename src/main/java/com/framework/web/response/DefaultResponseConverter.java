package com.framework.web.response;
import com.framework.enums.ContentType;
import com.framework.util.SimpleSerialization;

import java.util.Map;

public class DefaultResponseConverter implements ResponseConverter {

    private final SimpleSerialization simpleSerialization;

    public DefaultResponseConverter(SimpleSerialization simpleSerialization) {
        this.simpleSerialization = simpleSerialization;
    }

    @Override
    public ResponsePayload convert(Object result) {
        if(result == null){return empty();}

        if(result instanceof String s){
            return new ResponsePayload(s.getBytes(ContentType.TEXT_PLAIN.charset()),
                    ContentType.TEXT_PLAIN);
        }

        try{
            String json = simpleSerialization.toJson(result);
            return new ResponsePayload(json.getBytes(ContentType.APPLICATION_JSON.charset()),
                    ContentType.APPLICATION_JSON);
        }catch (Exception e){

            String fallbackJson =
                    "{\"error\":\"Fallo al convertir la respuesta en JSON.\"}";
            return new ResponsePayload(fallbackJson.getBytes(ContentType.APPLICATION_JSON.charset())
                    ,ContentType.APPLICATION_JSON);
        }

    }

    @Override
    public ResponsePayload convertError(int code, String messageError) {
        Map<String, Object> errorResponse = Map.of(
                "status", code,
                "message", messageError
        );
        String errorJson = simpleSerialization.toJson(errorResponse);
        return new ResponsePayload(errorJson.getBytes(ContentType.APPLICATION_JSON.charset()),
                ContentType.APPLICATION_JSON);
    }

    public static ResponsePayload empty() {
        return new ResponsePayload(new byte[0], ContentType.TEXT_PLAIN);
    }

}
