package com.comicvaultraiders.comicvaultraiders.service;

import com.comicvaultraiders.comicvaultraiders.dto.ComicDto;
import com.comicvaultraiders.comicvaultraiders.integration.google.Bookmodel;
import com.comicvaultraiders.comicvaultraiders.integration.google.Item;
import com.comicvaultraiders.comicvaultraiders.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class GoogleAPIService {

    @Value("${google.books.api.prefix:}")
    private String apiPrefix;

    @Value("${google.api.key:}")
    private String apiKey;

    private final ObjectMapper objectMapper;
    private final Logger logger = Logger.getLogger(this.getClass());


    public GoogleAPIService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<ComicDto> comicBulkUpload(String scrapeKeyword, Long startIndex){

        List<ComicDto> retVal = new ArrayList<>();
        String apiUrl = apiPrefix + "volumes?q=intitle:" + scrapeKeyword + "&maxResults=40&startIndex=" + startIndex + "&orderBy=newest" +  "&key="+ apiKey;

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);
        Map<String, String> uriVariables = new HashMap<>();
        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, String.class, uriVariables);

        try{
            Bookmodel comicInfo = objectMapper.readValue(response.getBody(), Bookmodel.class);

            if(comicInfo.getItems()!=null){
                for(int i = 0; i < Arrays.stream(comicInfo.getItems()).toList().size(); i++){
                    Item respItem = Arrays.stream(comicInfo.getItems()).toList().get(i);
                    Comic responseComicData = new Comic();
                    if(respItem.getVolumeInfo().getTitle() != null ){
                        responseComicData.setTitle(respItem.getVolumeInfo().getTitle());
                        String comicTitle = respItem.getVolumeInfo().getTitle();
                        if(comicTitle.toLowerCase().contains("deluxe")){
                            responseComicData.setFormat("Deluxe Edition");
                        }else if(comicTitle.toLowerCase().contains("paperback")){
                            responseComicData.setFormat("Paperback");
                        }else if(comicTitle.toLowerCase().contains("absolute")){
                            responseComicData.setFormat("Absolute Edition");
                        }else if(comicTitle.toLowerCase().contains("omnibus")){
                            responseComicData.setFormat("Omnibus");
                        }else if(comicTitle.toLowerCase().contains("compendium")){
                            responseComicData.setFormat("Compendium");
                        }else if(comicTitle.toLowerCase().contains("box set")){
                            responseComicData.setFormat("Box Set");
                        }else{
                            responseComicData.setFormat("Single Issue");
                        }
                        if(respItem.getVolumeInfo().getSubtitle()!=null){
                            responseComicData.setTitle(respItem.getVolumeInfo().getTitle() + " - " + respItem.getVolumeInfo().getSubtitle());
                        }
                        Long lastNumber = null;
                        Matcher m = Pattern.compile("(\\d+)(?!.*\\d)").matcher(comicTitle);
                        if (m.find()) {
                            lastNumber = Long.parseLong(m.group(1));
                        }else {
                            lastNumber = 1L;
                        }
                        responseComicData.setIssueNumber(lastNumber);
                    }else{
                        responseComicData.setTitle("");
                    }
                    if(respItem.getVolumeInfo().getPublisher()!=null){
                        String publisher = respItem.getVolumeInfo().getPublisher();
                        if(publisher.toLowerCase().contains("dc comics") || publisher.toLowerCase().contains("vertigo") || publisher.toLowerCase().contains("black label") || publisher.toLowerCase().contains("dc")){
                            responseComicData.setPublisher("DC Comics");
                        }else if(publisher.toLowerCase().contains("marvel")){
                            responseComicData.setPublisher("Marvel Comics");
                        }else if(publisher.toLowerCase().contains("image")){
                            responseComicData.setPublisher("Image Comics");
                        }else if(publisher.toLowerCase().contains("dark horse")){
                            responseComicData.setPublisher("Dark Horse Comics");
                        }else if(publisher.toLowerCase().contains("idw")){
                            responseComicData.setPublisher("IDW Publishing");
                        }else if(publisher.toLowerCase().contains("valiant")){
                            responseComicData.setPublisher("Valiant Comics");
                        }else if(publisher.toLowerCase().contains("boom")){
                            responseComicData.setPublisher("BOOM! Studios");
                        }else if(publisher.toLowerCase().contains("titan")){
                            responseComicData.setPublisher("Titan Comics");
                        }else{
                            responseComicData.setPublisher("Other");
                        }
                    }else{
                        responseComicData.setPublisher("Other");
                    }
                    String authors = "";
                    if(respItem.getVolumeInfo().getAuthors()!=null){
                        for(String author: respItem.getVolumeInfo().getAuthors()){
                            if(authors.isBlank()){
                                authors = author;
                            }else{
                                authors+=";" + author;
                            }
                        }
                    }
                    responseComicData.setAuthor(authors);
                    if(respItem.getVolumeInfo().getImageLinks()!=null){
                        responseComicData.setCoverImgUrl(respItem.getVolumeInfo().getImageLinks().getThumbnail() == null ? "" : respItem.getVolumeInfo().getImageLinks().getThumbnail());
                    }
                    if(respItem.getVolumeInfo().getPublishedDate() != null && parseFlexibleDate(respItem.getVolumeInfo().getPublishedDate())!=null) {
                        responseComicData.setReleaseDate(parseFlexibleDate(respItem.getVolumeInfo().getPublishedDate()));
                    }
                    ComicDto newComic =  new ComicDto(responseComicData);
                    retVal.add(newComic);
                }
            }
        } catch(JsonProcessingException jsonExp){
            logger.info(jsonExp.getMessage());
        }
        return retVal;
    }

    public Optional<ComicDto> getComicInfoByBarcode(String barcode) {
        String apiUrl = apiPrefix + "volumes?q=isbn:" + barcode + "&key="+ apiKey;
        Optional<ComicDto> newComic =  Optional.of(new ComicDto(new Comic()));
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);
        Map<String, String> uriVariables = new HashMap<>();
        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, String.class, uriVariables);
        try{
            Bookmodel comicInfo = objectMapper.readValue(response.getBody(), Bookmodel.class);
            if(comicInfo.getTotalItems() > 0){
                Item respItem = Arrays.stream(comicInfo.getItems()).toList().get(0);

                Comic responseComicData = new Comic();
                responseComicData.setTitle(respItem.getVolumeInfo().getTitle() == null ? "" : respItem.getVolumeInfo().getTitle());
                String authors = "";
                for(String author: respItem.getVolumeInfo().getAuthors()){
                    if(authors.isBlank()){
                        authors = author;
                    }else{
                        authors+=";" + author;
                    }
                }
                responseComicData.setAuthor(authors);
                responseComicData.setPublisher(respItem.getVolumeInfo().getPublisher() == null ? "" : respItem.getVolumeInfo().getPublisher());
                if(respItem.getVolumeInfo().getImageLinks()!=null){
                    responseComicData.setCoverImgUrl(respItem.getVolumeInfo().getImageLinks().getThumbnail() == null ? "" : respItem.getVolumeInfo().getImageLinks().getThumbnail());
                }
                if(respItem.getVolumeInfo().getPublishedDate() != null && parseFlexibleDate(respItem.getVolumeInfo().getPublishedDate())!=null) {
                    responseComicData.setReleaseDate(parseFlexibleDate(respItem.getVolumeInfo().getPublishedDate()));
                }
                newComic =  Optional.of(new ComicDto(responseComicData));
            }
        } catch(JsonProcessingException jsonExp){
            System.out.println(jsonExp.getMessage());
        } catch(IOException ioException){
            System.out.println(ioException.getMessage());
        }
        return newComic;
    }

    public Optional<ComicDto> getComicsByObj(Comic comic) {
        String apiUrl = getURLStringFromObject(comic);

        Optional<ComicDto> newComic =  Optional.of(new ComicDto(new Comic()));
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);
        Map<String, String> uriVariables = new HashMap<>();
        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, String.class, uriVariables);
        try{
            Bookmodel comicInfo = objectMapper.readValue(response.getBody(), Bookmodel.class);
            if(comicInfo.getTotalItems() > 0){
                Item respItem = Arrays.stream(comicInfo.getItems()).toList().get(0);

                Comic responseComicData = new Comic();
                responseComicData.setTitle(respItem.getVolumeInfo().getTitle() == null ? "" : respItem.getVolumeInfo().getTitle());
                String authors = "";
                for(String author: respItem.getVolumeInfo().getAuthors()){
                    if(authors.isBlank()){
                        authors = author;
                    }else{
                        authors+=";" + author;
                    }
                }
                responseComicData.setAuthor(authors);
                responseComicData.setPublisher(respItem.getVolumeInfo().getPublisher() == null ? "" : respItem.getVolumeInfo().getPublisher());
                if(respItem.getVolumeInfo().getImageLinks()!=null){
                    responseComicData.setCoverImgUrl(respItem.getVolumeInfo().getImageLinks().getThumbnail() == null ? "" : respItem.getVolumeInfo().getImageLinks().getThumbnail());
                }
                if(respItem.getVolumeInfo().getPublishedDate() != null && parseFlexibleDate(respItem.getVolumeInfo().getPublishedDate())!=null) {
                    responseComicData.setReleaseDate(parseFlexibleDate(respItem.getVolumeInfo().getPublishedDate()));
                }
                newComic =  Optional.of(new ComicDto(responseComicData));
            }
        } catch(JsonProcessingException jsonExp){
            logger.info(jsonExp.getMessage());
        }
        return newComic;
    }

    private String getURLStringFromObject(Comic comic) {
        StringBuilder searchString = new StringBuilder("volumes?q=intitle:");

        if(comic.getTitle()!=null){
            searchString.append(comic.getTitle().replace(' ', '+'));
        }

        if(comic.getAuthor()!=null){
            if(comic.getTitle()!=null){
                searchString.append("+");
            }

            String[] authors = comic.getAuthor().split(",");
            searchString.append(authors[0].replace(' ', '+'));
        }
        if(comic.getFormat()!=null){
            if(comic.getTitle()!=null || comic.getAuthor()!=null){
                searchString.append("+");
            }
            searchString.append(comic.getFormat());
        }
        String apiUrl = apiPrefix + searchString + "&key="+ apiKey;
        return apiUrl;
    }

    private LocalDate parseFlexibleDate(String input) {
        if (input == null || input.isBlank()) {
            return null;
        }

        String[] patterns = new String[] {
                "yyyy",
                "yyyy-MM",
                "yyyy-MM-dd",
                "yyyy-MM-dd'T'HH",
                "yyyy-MM-dd'T'HH:mm",
                "yyyy-MM-dd'T'HH:mm:ss",
                "yyyy-MM-dd'T'HH:mm:ss.SSS",
                "yyyy-MM-dd'T'HH:mmX",
                "yyyy-MM-dd'T'HH:mm:ssX",
                "yyyy-MM-dd'T'HH:mm:ss.SSSX",
                "yyyy-MM-dd'T'HH:mmXXX",
                "yyyy-MM-dd'T'HH:mm:ssXXX",
                "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"
        };

        for (String pattern : patterns) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
                if (pattern.equals("yyyy")) {
                    Year y = Year.parse(input, formatter);
                    return y.atDay(1);
                }
                if (pattern.equals("yyyy-MM")) {
                    YearMonth ym = YearMonth.parse(input, formatter);
                    return ym.atDay(1);
                }
                if (pattern.contains("H")) {
                    return LocalDateTime.parse(input, formatter).toLocalDate();
                }
                return LocalDate.parse(input, formatter);

            } catch (DateTimeParseException ignored) {
            }
        }
        return null;
    }

}