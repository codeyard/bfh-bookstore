package org.bookstore.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(JUnit4.class)
class JsonMapperTest {


    @Before
    public void init() {
        JavaTimeModule module = new JavaTimeModule();
        LocalDateTimeDeserializer localDateTimeDeserializer = new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
        module.addDeserializer(LocalDateTime.class, localDateTimeDeserializer);


    }

    @Test
    public void test() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        final String json = "{\n" +
            "    \"timestamp\": \"2020-06-01T08:20:00\",\n" +
            "    \"status\": 422,\n" +
            "    \"error\": \"Internal Server Error\",\n" +
            "    \"path\": \"/orders/\"\n" +
            "} }";
        ErrorInfo instance = objectMapper.readValue(json, ErrorInfo.class);

        assertEquals(LocalDateTime.parse("2020-06-01T08:20:00", DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")), instance.getTimestamp());
    }

}



