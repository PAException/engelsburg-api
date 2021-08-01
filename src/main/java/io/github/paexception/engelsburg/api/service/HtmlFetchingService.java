package io.github.paexception.engelsburg.api.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;

public abstract class HtmlFetchingService extends FetchingService {

	@Override
	protected Document request(String url) throws IOException {
		return Jsoup.connect(url).get();
	}

}
