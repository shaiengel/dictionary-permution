package com.shai.app;

import com.shai.caching.CacheItems;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class HttpServer {

    @Autowired
    CacheItems cacheItems;


    @GetMapping(path = "/api/v1/similar")
    public ResponseEntity words(@RequestParam String word){
        ResponseEntity.BodyBuilder builder = ResponseEntity.status(HttpStatus.OK);
        JSONObject jsonList = new JSONObject();

        System.out.println("New Request: GET /api/v1/similar?word=" + word);

        try {
            List<String> wordList =  cacheItems.getList(word);
            if (wordList == null){
                builder = ResponseEntity.status(HttpStatus.BAD_REQUEST);
            }
            else{
                jsonList.put("similar", wordList);
            }

            System.out.println("Request output: word=" + word + " output = " + jsonList.toString());

        }catch (Exception e){
            jsonList = null;
            builder = ResponseEntity.status(HttpStatus.UNAUTHORIZED);
        }
        return builder.body(jsonList);


    }

    @GetMapping(path = "/api/v1/stats")
    public ResponseEntity stats(){
        ResponseEntity.BodyBuilder builder = ResponseEntity.status(HttpStatus.OK);
        JSONObject jsonList = new JSONObject();

        System.out.println("New Request: GET /api/v1/stats");
        long numOfWords = cacheItems.getNumOfWords();
        double numOfRequests = cacheItems.getNumOfRequests();
        double avgTimeOfRequests = cacheItems.getAvgTimeOfRequests();

        jsonList.put("totalWords", numOfWords);
        jsonList.put("totalRequests", Math.round(numOfRequests));
        jsonList.put("avgProcessingTimeNs", Math.round(avgTimeOfRequests));
        return builder.body(jsonList);

    }
}

