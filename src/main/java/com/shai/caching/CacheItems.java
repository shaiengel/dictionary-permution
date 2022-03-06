package com.shai.caching;

import com.shai.app.ConfigurationReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Component
public class CacheItems {

    long numOfWords;

    int asciiOffset;
    int alphbeitSize;
    int collisionCount = 0;


    boolean isReady;

    HashMap<BigInteger, List<String>> cacheLocal = new HashMap<>();

    @Autowired
    ConfigurationReader config;

    public void CacheItems(){
        numOfWords = 0;
        isReady = false;
    }

    public long getNumOfWords() {
        return numOfWords;
    }

    public boolean isReady() {
        return isReady;
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
            isReady = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("number of words is = " + numOfWords + ". number of element is = " + cacheLocal.size());
        System.out.println("number of collision = " + collisionCount);

    }

    public boolean isCollision(){
        int k;
        for (BigInteger i : cacheLocal.keySet()){
            k = 0;
            for (BigInteger j : cacheLocal.keySet()){
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
        for (BigInteger i : cacheLocal.keySet()){

            newList = cacheLocal.get(i);
            if (newList.size() > 1){
                k++;
                System.out.println(newList);
            }

        }
        System.out.println("number of many is = " + k);
    }

    public void insertToCache(String word) throws NoSuchAlgorithmException {
        BigInteger hash;
        List<String> newList;

        try {
            hash = getHashCode(word);
            if (cacheLocal.containsKey(hash)){
                newList = cacheLocal.get(hash);
                checkCollisionInCache(newList, word);
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

    public void checkCollisionInCache(List<String> newList, String word){
        ArrayList<Integer> wordArray = new ArrayList<Integer>();
        Integer[] data = new Integer[this.alphbeitSize];
        Integer[] data_1 = new Integer[this.alphbeitSize];
        Arrays.fill(data, 0);

        for (char i : word.toLowerCase().toCharArray()) {
            int j = i - this.asciiOffset;
            data[j]++;
        }


        for (String check : newList){
            Arrays.fill(data_1, 0);
            for (char i : check.toLowerCase().toCharArray()) {
                int j = i - this.asciiOffset;
                data_1[j]++;
            }

            for (int m=0 ; m < this.alphbeitSize; m++) {
                if (data[m] != data_1[m]){
                    System.err.println("ERROR. read collision. new word = " + word + " List = " + newList);
                    collisionCount++;
                    return;
                }
            }
        }
    }

    public boolean checkInCache(String word) throws NoSuchAlgorithmException {
        BigInteger hash;
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

    public BigInteger getHashCode(String word) throws NoSuchAlgorithmException {

            char[] charsFromString = word.toLowerCase().toCharArray();
            ArrayList<Integer> wordArray = new ArrayList<Integer>();
            Integer[] data = new Integer[this.alphbeitSize];
            Arrays.fill(data, 0);
            for (char i : charsFromString) {
                int j = i - this.asciiOffset;
                data[j]++;
            }
            //return BigInteger.valueOf(Arrays.deepHashCode(data));
            return calulateHash(Arrays.toString(data));
    }

    public BigInteger calulateHash(String input) throws NoSuchAlgorithmException {
        MessageDigest msgDst = MessageDigest.getInstance("MD5");

        // the digest() method is invoked to compute the message digest
        // from an input digest() and it returns an array of byte
        byte[] msgArr = msgDst.digest(input.getBytes());

        // getting signum representation from byte array msgArr
        BigInteger bi = new BigInteger(1, msgArr);
        return bi;

    }

    public List<String> removeCollisionsFromReturnList(List<String> tmpList, String word){
        ArrayList<Integer> wordArray = new ArrayList<Integer>();
        List<String> newList = new ArrayList<>();
        Integer[] data = new Integer[this.alphbeitSize];
        Integer[] data_1 = new Integer[this.alphbeitSize];
        int flag = 0;
        Arrays.fill(data, 0);

        for (char i : word.toLowerCase().toCharArray()) {
            int j = i - this.asciiOffset;
            data[j]++;
        }


        for (String check : tmpList){
            Arrays.fill(data_1, 0);
            for (char i : check.toLowerCase().toCharArray()) {
                int j = i - this.asciiOffset;
                data_1[j]++;
            }

            for (int m=0 ; m < this.alphbeitSize; m++) {
                if (data[m] != data_1[m]){
                    flag = 1;
                    break;
                }
            }

            if (flag == 0){
                newList.add(check);
            }
        }
        return newList;
    }

    public List<String> getList(String word) throws Exception {
        List<String> newList;

        try {
            inputValidation(word);

            BigInteger hash = getHashCode(word);
            if (cacheLocal.containsKey(hash)) {
                List<String> tmpList = new ArrayList<>(cacheLocal.get(hash));
                tmpList.remove(word);
                newList = removeCollisionsFromReturnList(tmpList, word);

            } else {
                return newList = new ArrayList<>();
            }

            return newList;
        }catch (Exception e){
            System.err.println("ERROR. getList");
            throw e;
        }
    }

    private void inputValidation(String word) throws Exception {
        for (char ch: word.toLowerCase().toCharArray()) {
            if ((ch - asciiOffset < 0) || (ch > (asciiOffset+alphbeitSize))){
                System.err.println("ERROR. getList. wrong input");
                throw new Exception("inputValidation failed");
            }
        }
    }
}
