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

    double numOfRequests = 0;
    double avgTimeOfRequests = 0;

    @Autowired
    CacheItems cacheItems;


    @GetMapping(path = "/api/v1/similar")
    public ResponseEntity words(@RequestParam String word){
        ResponseEntity.BodyBuilder builder = ResponseEntity.status(HttpStatus.OK);
        JSONObject jsonList = new JSONObject();

        System.out.println("New Request: GET /api/v1/similar?word=" + word);

        try {
            double startTime = System.nanoTime();
            if (cacheItems.isReady() == false){
                System.err.println("ERROR. didn't ready dictionary file");
                throw new Exception();
            }

            List<String> wordList =  cacheItems.getList(word);

            if (wordList == null){
                throw new Exception();
            }
            else{
                jsonList.put("similar", wordList);
            }

            double handleTime = System.nanoTime() - startTime;
            setAvgTime(handleTime);
            numOfRequests++;
            System.out.println("Request output: word=" + word + " output = " + jsonList.toString());
            //System.out.println("Request time " + (long)handleTime);

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


        jsonList.put("totalWords", numOfWords);
        jsonList.put("totalRequests", (long)numOfRequests);
        jsonList.put("avgProcessingTimeNs", (long)avgTimeOfRequests);
        return builder.body(jsonList);

    }

    private void setAvgTime(double handleTime){
        try {
            //avgTimeOfRequests = (avgTimeOfRequests * numOfRequests + handleTime)/(numOfRequests + 1);
            double temp = numOfRequests / (numOfRequests + 1);
            if (numOfRequests == 0) {
                avgTimeOfRequests = handleTime;
            } else {
                avgTimeOfRequests = (avgTimeOfRequests + handleTime / numOfRequests) * temp;
            }
        }catch (Exception e){
            System.err.println("ERROR. setAvgTime, numOfRequests = " + numOfRequests + " avgTimeOfRequests = " + avgTimeOfRequests);
        }
    }
}

