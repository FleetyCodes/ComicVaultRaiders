package com.comicvaultraiders.comicvaultraiders.jobs.frequentjobs;

import com.comicvaultraiders.comicvaultraiders.dto.ComicDto;
import com.comicvaultraiders.comicvaultraiders.model.ComicBulkCreateQueue;
import com.comicvaultraiders.comicvaultraiders.model.RateLimit;
import com.comicvaultraiders.comicvaultraiders.service.ComicBulkCreateQueueService;
import com.comicvaultraiders.comicvaultraiders.service.ComicService;
import com.comicvaultraiders.comicvaultraiders.service.GoogleAPIService;
import com.comicvaultraiders.comicvaultraiders.service.RateLimitService;
import org.apache.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ComicBulkUploadJob {

    private final Logger logger = Logger.getLogger(this.getClass());

    private final ComicBulkCreateQueueService comicBulkCreateQueueService;

    private final GoogleAPIService googleAPIService;

    private final ComicService comicService;

    private final RateLimitService rateLimitService;

    public ComicBulkUploadJob(ComicBulkCreateQueueService comicBulkCreateQueueService, GoogleAPIService googleAPIService, ComicService comicService, RateLimitService rateLimitService) {
        this.comicBulkCreateQueueService = comicBulkCreateQueueService;
        this.googleAPIService = googleAPIService;
        this.comicService = comicService;
        this.rateLimitService = rateLimitService;
    }

    @Scheduled(cron = "0 */5 * * * *")
    public void comicBulkUploadProcess(){
        RateLimit googleApiRateLimit = rateLimitService.findByApiName("GOOGLE_BOOKS");

        if(googleApiRateLimit.getDailyLimit()-googleApiRateLimit.getTraffic() > 0){
            List<ComicBulkCreateQueue> bulkUploadQueue = comicBulkCreateQueueService.findAll();
            int numOfAPICall = 0;

            for(ComicBulkCreateQueue queueRow:bulkUploadQueue){
                List<ComicDto> newComics;
                Long startindex = queueRow.getStartIndex();

                do{
                    newComics = null;
                    try{
                        newComics = googleAPIService.comicBulkUpload(queueRow.getKeyword(), startindex);

                    }catch(Exception e){
                        logger.error("error during google books api call: " + e.getMessage());

                        rateLimitService.updateRateLimit(googleApiRateLimit);
                        queueRow.setStartIndex(startindex);
                        comicBulkCreateQueueService.updateRowInQueue(queueRow);
                        break;
                    }finally{
                        numOfAPICall++;
                        startindex++;
                    }
                    if(newComics!=null && !newComics.isEmpty()){
                        comicService.createBulkComics(newComics);
                    }
                }while(newComics!=null && !newComics.isEmpty() && numOfAPICall<50);

                googleApiRateLimit.setTraffic(googleApiRateLimit.getTraffic()+numOfAPICall);
                rateLimitService.updateRateLimit(googleApiRateLimit);
                if(newComics!=null && !newComics.isEmpty()){
                    queueRow.setStartIndex(startindex);
                    comicBulkCreateQueueService.updateRowInQueue(queueRow);
                    break; //stop the bulk upload before it exceeds the limit
                }
                comicBulkCreateQueueService.removeFromQueue(queueRow.getId());
            }
        }
    }


}
