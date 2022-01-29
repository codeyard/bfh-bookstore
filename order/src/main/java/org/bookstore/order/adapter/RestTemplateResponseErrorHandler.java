package org.bookstore.order.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.bookstore.order.controller.ErrorInfo;
import org.bookstore.order.exception.BookNotFoundException;
import org.bookstore.order.exception.PaymentFailedException;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

import static org.springframework.http.HttpStatus.Series.CLIENT_ERROR;

@Component
public class RestTemplateResponseErrorHandler implements ResponseErrorHandler {


    @Override
    public boolean hasError(ClientHttpResponse httpResponse) throws IOException {
        return httpResponse.getStatusCode().series() == CLIENT_ERROR;
    }

    @Override
    public void handleError(ClientHttpResponse httpResponse) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        ErrorInfo errorInfo = objectMapper.readValue(httpResponse.getBody(), ErrorInfo.class);
        switch (httpResponse.getStatusCode()) {
            case NOT_FOUND -> throw new BookNotFoundException(errorInfo);
            case BAD_REQUEST, UNPROCESSABLE_ENTITY -> throw new PaymentFailedException(errorInfo);
        }

    }
}
