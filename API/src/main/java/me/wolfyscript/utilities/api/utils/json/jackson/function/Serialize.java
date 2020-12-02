package me.wolfyscript.utilities.api.utils.json.jackson.function;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public interface Serialize<T> {

    void serialize(T t, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException;
}
