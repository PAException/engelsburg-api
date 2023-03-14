/*
 * Copyright (c) 2022 Paul Huerkamp. All rights reserved.
 */

package io.github.paexception.engelsburg.api.util.maskjson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import java.util.Optional;

public class MaskJsonSerializer extends StdSerializer<Object> implements ContextualSerializer {

	private String maskWith;

	public MaskJsonSerializer() {
		super(Object.class);
	}

	public MaskJsonSerializer(String maskWith) {
		super(Object.class);
		this.maskWith = maskWith;
	}

	@Override
	public JsonSerializer<?> createContextual(SerializerProvider serializerProvider,
			BeanProperty beanProperty) throws JsonMappingException {
		Optional<MaskJson> annotation = Optional.ofNullable(beanProperty).map(
				property -> property.getAnnotation(MaskJson.class));
		return new MaskJsonSerializer(annotation.map(MaskJson::value).orElse(null));
	}

	@Override
	public void serialize(Object o, JsonGenerator jsonGenerator,
			SerializerProvider serializerProvider) throws IOException {
		jsonGenerator.writeString(this.maskWith);
	}
}
