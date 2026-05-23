package com.comicvaultraiders.comicvaultraiders.jobs.frequentjobs;

import com.comicvaultraiders.comicvaultraiders.dto.ComicDto;
import com.comicvaultraiders.comicvaultraiders.model.Comic;
import com.comicvaultraiders.comicvaultraiders.model.RateLimit;
import com.comicvaultraiders.comicvaultraiders.service.ComicService;
import com.comicvaultraiders.comicvaultraiders.service.GoogleAPIService;
import com.comicvaultraiders.comicvaultraiders.service.RateLimitService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.log4j.Logger;

@Service
public class comicDataRepairJob {

    private final ComicService comicService;
    private final GoogleAPIService googleAPIService;

    private final RateLimitService rateLimitService;
    private final Logger logger = Logger.getLogger(this.getClass());

    public comicDataRepairJob(ComicService comicService, GoogleAPIService googleAPIService, RateLimitService rateLimitService) {
        this.comicService = comicService;
        this.googleAPIService = googleAPIService;
        this.rateLimitService = rateLimitService;
    }

    @Scheduled(cron = "0 */15 * * * *")
    public void fixCorruptedComicData() {
        ////second  minute  hour  day-of-month  month  day-of-week
        //quarter-hour period job:
        //@Scheduled(cron = "0 */15 * * * *")

        //run at specified time:
        //@Scheduled(cron = "0 30 15 * * *")
        RateLimit rateLimitl = rateLimitService.findByApiName("GOOGLE_BOOKS_REPAIR");
        Long availAbleAPICalls = rateLimitl.getDailyLimit()-rateLimitl.getTraffic();

        List<Comic> corruptedComics = new ArrayList<>();
        if(availAbleAPICalls > 0){
                corruptedComics = comicService.getAllComicsWithCorruptedData(ZonedDateTime.now().minusDays(7L), ZonedDateTime.now());
        }
        logger.info("fixCorruptedComicData job start: " + LocalDateTime.now());
        logger.info("num of corrupted comics: " + corruptedComics.size());

        for(Comic comic:corruptedComics){
            if(availAbleAPICalls > 0){
                Optional<ComicDto> tmpComic = googleAPIService.getComicsByObj(comic);
                availAbleAPICalls--;
                rateLimitl.setTraffic(rateLimitl.getTraffic()+1);
                rateLimitService.updateRateLimit(rateLimitl);
                if(tmpComic.isPresent()){
                    if(tmpComic.get().getCoverImgUrl()!=null){
                        String imgUrl = tmpComic.get().getCoverImgUrl();

                        String tinierImgUrl="";
                        if(!imgUrl.trim().isBlank() && imgUrl.contains("._SL")){
                            for(int i=3; i<imgUrl.length(); i++){
                                if("._SL".equals(imgUrl.substring(i-3,i+1))){
                                    tinierImgUrl = imgUrl.substring(0,i+1) + "300_.jpg";
                                    break;
                                }
                            }
                        }
                        comic.setCoverImgUrl("".equals(tinierImgUrl) ? imgUrl : tinierImgUrl);
                    }
                    if(tmpComic.get().getReleaseDate()!=null){
                        comic.setReleaseDate(tmpComic.get().getReleaseDate());
                    }
                }
                comic.setIsCheckedByRepairJob(true);
                comicService.updateComic(comic);
                logger.info("Comic Updated: " + comic.getId() + "th. id:  " + comic.getTitle());
            }else{
                break;
            }
        };
        logger.info("fixCorruptedComicData job completed at: " + LocalDateTime.now());
    }
}
