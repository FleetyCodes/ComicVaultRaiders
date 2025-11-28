package com.comicvaultraiders.comicvaultraiders.service;

import com.comicvaultraiders.comicvaultraiders.modell.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class GoogleAPIService {

    @Value("${google.books.api.prefix:}")
    private String apiPrefix;

    private final ObjectMapper objectMapper;

    public GoogleAPIService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Optional<ComicDto> getComicInfo(String barcode) {
        String apiUrl = apiPrefix + "volumes?q=isbn:" + barcode;
        Optional<ComicDto> newComic =  Optional.of(new ComicDto(new Comic()));
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        //headers.setBearerAuth(accessToken);
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

    public static ZonedDateTime parseFlexibleDate(String input) {
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

                if (!pattern.contains("X")) {
                    if (pattern.equals("yyyy")) {
                        int year = Integer.parseInt(input);
                        return LocalDate.of(year, 1, 1)
                                .atStartOfDay(ZoneOffset.UTC);
                    }

                    if (pattern.equals("yyyy-MM")) {
                        String[] parts = input.split("-");
                        int year = Integer.parseInt(parts[0]);
                        int month = Integer.parseInt(parts[1]);
                        return LocalDate.of(year, month, 1)
                                .atStartOfDay(ZoneOffset.UTC);
                    }

                    if (pattern.equals("yyyy-MM-dd")) {
                        LocalDate d = LocalDate.parse(input, formatter);
                        return d.atStartOfDay(ZoneOffset.UTC);
                    }

                    LocalDateTime dt = LocalDateTime.parse(input, formatter);
                    return dt.atZone(ZoneOffset.UTC);
                }

                TemporalAccessor ta = formatter.parse(input);
                return ZonedDateTime.from(ta);

            } catch (Exception ignore) {
            }
        }
        return null;
    }

}
