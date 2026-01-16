package com.comicvaultraiders.comicvaultraiders.jobs.quarterHour;

import com.comicvaultraiders.comicvaultraiders.dto.ComicDto;
import com.comicvaultraiders.comicvaultraiders.model.Comic;
import com.comicvaultraiders.comicvaultraiders.service.ComicService;
import com.comicvaultraiders.comicvaultraiders.service.GoogleAPIService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import org.apache.log4j.Logger;

@Service
public class comicDataRepairJob {

    private final ComicService comicService;
    private final GoogleAPIService googleAPIService;
    private final Logger logger = Logger.getLogger(this.getClass());

    public comicDataRepairJob(ComicService comicService, GoogleAPIService googleAPIService) {
        this.comicService = comicService;
        this.googleAPIService = googleAPIService;
    }

    @Scheduled(cron = "0 */15 * * * *")
    public void fixCorruptedComicData() {
        //quarter-hour period job:
        //@Scheduled(cron = "0 */15 * * * *")

        //run at specified time:
        //@Scheduled(cron = "0 30 15 * * *")
        logger.info("fixCorruptedComicData start: " + LocalDateTime.now());
        List<Comic> corruptedComics = comicService.getAllComicsWithCorruptedData(ZonedDateTime.now().minusDays(1L), ZonedDateTime.now().plusHours(2L));
        logger.info("num of corrupted comics: " + corruptedComics.size());

        logger.info("check data with Google API");
        corruptedComics.stream().forEach(comic -> {
            Optional<ComicDto> tmpComic = googleAPIService.getComicsByObj(comic);
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
                comic.setIsCheckedByRepairJob(true);
                comicService.updateComic(comic.getId() ,comic);
                logger.info("Comic Updated: " + comic.getId() + "th. id:  " + comic.getTitle());
            }
        });
        logger.info("update completed at: " + LocalDateTime.now());
    }
}
