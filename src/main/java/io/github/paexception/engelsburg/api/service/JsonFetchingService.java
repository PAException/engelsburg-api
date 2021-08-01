package io.github.paexception.engelsburg.api.service;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.URL;

public abstract class JsonFetchingService extends FetchingService {

	@Override
	protected JsonElement request(String url) throws IOException {
		DataInputStream input = new DataInputStream(new URL(url).openConnection().getInputStream());

		return JsonParser.parseString(new String(input.readAllBytes()));
	}

}
