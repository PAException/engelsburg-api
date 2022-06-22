/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.spring;

import io.github.paexception.engelsburg.api.service.scheduled.ArticleUpdateService;
import io.github.paexception.engelsburg.api.service.scheduled.CafeteriaUpdateService;
import io.github.paexception.engelsburg.api.service.scheduled.EventUpdateService;
import io.github.paexception.engelsburg.api.service.scheduled.SolarSystemUpdateService;
import io.github.paexception.engelsburg.api.service.scheduled.SubstituteUpdateService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@AllArgsConstructor
public class GeneralEventListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(GeneralEventListener.class);
	private static final ExecutorService THREAD_POOL = Executors.newCachedThreadPool();

	private final ArticleUpdateService articleUpdateService;
	private final CafeteriaUpdateService cafeteriaUpdateService;
	private final EventUpdateService eventUpdateService;
	private final SolarSystemUpdateService solarSystemUpdateService;
	private final SubstituteUpdateService substituteUpdateService;

	private static void async(Runnable asyncTask) {
		THREAD_POOL.submit(asyncTask);
	}

	@ConditionalOnProperty(value = "app.scheduling.enable", havingValue = "true", matchIfMissing = true)
	@EventListener(ApplicationReadyEvent.class)
	public void firstFetchOfSchedulingServices() {
		LOGGER.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> [ APPLICATION READY ] <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");

		async(this.articleUpdateService::loadPastArticles);
		async(this.articleUpdateService::fetchNewArticles);
		async(this.cafeteriaUpdateService::updateCafeteriaInformation);
		async(this.eventUpdateService::updateEvents);
		async(this.solarSystemUpdateService::updateSolarSystemInfo);
		async(this.substituteUpdateService::updateSubstitutes);
	}
}
