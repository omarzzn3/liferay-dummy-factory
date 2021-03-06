package com.liferay.support.tools.utils;

import com.google.common.collect.*;
import com.google.common.io.*;
import edu.uci.ics.crawler4j.crawler.*;
import edu.uci.ics.crawler4j.fetcher.*;
import edu.uci.ics.crawler4j.robotstxt.*;
import org.osgi.service.component.annotations.*;

import java.io.*;
import java.util.*;

/**
 * Image Crawl Controller
 * <p/>
 * This class manage crawlers for fetching dummy links from Internet
 *
 * @author Yasuyuki Takeo
 */
@Component(immediate = true, service = ImageCrawlController.class)
public class ImageCrawlController {

    public void exec(
        int numberOfCrawlers, int maxDepthOfCrawling, int maxPagesToFetch,
        String domain) throws Exception {

        CrawlConfig config = new CrawlConfig();

        File tempDir = Files.createTempDir();
        config.setCrawlStorageFolder(tempDir.getAbsolutePath());
        config.setMaxDepthOfCrawling(maxDepthOfCrawling);
        config.setMaxPagesToFetch(maxPagesToFetch);

		/*
         * Since images are binary content, we need to set this parameter to
		 * true to make sure they are included in the crawl.
		 */
        config.setIncludeBinaryContentInCrawling(true);

        PageFetcher     pageFetcher     = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller      = new CrawlController(config, pageFetcher, robotstxtServer);

        controller.addSeed(domain);

        ImageCrawler.configure(domain);

        //Start crawling
        controller.startNonBlocking(ImageCrawler.class, numberOfCrawlers);

        controller.waitUntilFinish();

        // Correcting URLs from each crawlers' results.
        List<Object> crawlersLocalData = controller.getCrawlersLocalData();
        for (Object localData : crawlersLocalData) {
            @SuppressWarnings("unchecked")
            List<String> urlLists = (List<String>) (localData);
            gatheredURLs.addAll(urlLists);
        }

    }

    public List<String> getURL() {
        return Lists.newArrayList(gatheredURLs);
    }

    private List<String> gatheredURLs = Collections.synchronizedList(new ArrayList<>());
}