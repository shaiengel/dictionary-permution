package com.shai.caching;

import com.shai.app.ConfigurationReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.*;

@Component
public class CacheItems {

    long numOfWords;
    double numOfRequests;
    double avgTimeOfRequests;

    int asciiOffset;
    int alphbeitSize;

    HashMap<Integer, List<String>> cacheLocal = new HashMap<>();

    @Autowired
    ConfigurationReader config;

    public void CacheItems(){
        numOfWords = 0;
        numOfRequests = 0;
        avgTimeOfRequests = 0;
    }

    public long getNumOfWords() {
        return numOfWords;
    }

    public double getNumOfRequests() {
        return numOfRequests;
    }

    public double getAvgTimeOfRequests() {
        return avgTimeOfRequests;
    }

    public void readFile(){

        String word;
        asciiOffset = config.getAsciiOffset();
        alphbeitSize = config.getAlphaBeitSize();
        System.out.println("ascii offset = " + asciiOffset + ". alphbeit size = " + alphbeitSize);
        try {
            Scanner scanner = new Scanner(new File("config/words_clean.txt"));
            while (scanner.hasNextLine()) {
                word = scanner.nextLine();
                insertToCache(word);
                numOfWords++;
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("number of words is = " + numOfWords + ". number of element is = " + cacheLocal.size());

    }

    public boolean isCollision(){
        int k;
        for (Integer i : cacheLocal.keySet()){
            k = 0;
            for (Integer j : cacheLocal.keySet()){
                if (i == j){
                    k++;
                    if (k > 1)
                        return true;
                }
            }
        }
        return false;
    }

    public void printManyFromCache(){
        int k = 0;
        List<String> newList;
        for (Integer i : cacheLocal.keySet()){

            newList = cacheLocal.get(i);
            if (newList.size() > 1){
                k++;
                System.out.println(newList);
            }

        }
        System.out.println("number of many is = " + k);
    }

    public void insertToCache(String word){
        int hash;
        List<String> newList;

        try {
            hash = getHashCode(word);
            if (cacheLocal.containsKey(hash)){
                newList = cacheLocal.get(hash);
                newList.add(word);
            }
            else{
                newList = new LinkedList<String>();
                newList.add(word);
                cacheLocal.put(hash, newList);
            }
        }catch (Exception e){
            System.err.println("ERROR. insertToCache");
            throw e;
        }


    }

    public boolean checkInCache(String word){
        int hash;
        List<String> newList;

        hash = getHashCode(word);
        if (cacheLocal.containsKey(hash)){
            newList = cacheLocal.get(hash);
            return true;
        }
        else{
            return false;
        }

    }

    public Integer getHashCode(String word){

        try {
            char[] charsFromString = word.toLowerCase().toCharArray();
            ArrayList<Integer> wordArray = new ArrayList<Integer>();
            Integer[] data = new Integer[this.alphbeitSize];
            Arrays.fill(data, 0);
            for (char i : charsFromString) {
                int j = i - this.asciiOffset;
                data[j]++;
            }
            return Arrays.deepHashCode(data);
        }catch (Exception e){
            System.err.println("ERROR. getHashCode");
            e.printStackTrace();
            throw e;
        }



    }

    public List<String> getList(String word){
        List<String> newList;

        try {
            double startTime = System.nanoTime();
            if (word.matches("[a-zA-Z]+") == false) {
                System.err.println("ERROR. getList. wrong input");
                return null;
            }
            Integer hash = getHashCode(word);
            if (cacheLocal.containsKey(hash)) {
                newList = new ArrayList<>(cacheLocal.get(hash));
                newList.remove(word);
            } else {
                return newList = new ArrayList<>();
            }
            double handleTime = System.nanoTime() - startTime;

            setAvgTime(handleTime);
            numOfRequests++;
            return newList;
        }catch (Exception e){
            System.err.println("ERROR. getList");
            throw e;
        }
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
