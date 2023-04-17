package com.genesis.worker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.genesis.common.domain.CommonModel;
import com.genesis.common.domain.ProcessingResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;

@Component
@Slf4j
public class Processor {

    private final ObjectMapper jsonMapper = new ObjectMapper();

    @Bean
    public Function<Message<byte[]>, ProcessingResult> process() {
        return (message) -> {
            CommonModel value;
            try {
                value = jsonMapper.readValue(new String(message.getPayload(), StandardCharsets.UTF_8), CommonModel.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            log.info("Received {}", value);
            ProcessingResult result = ProcessingResult.builder()
                    .success(value.getAge() >= 18)
                    .build();
            log.info("User {} is {} to drink alcohol", value.getName(), result.isSuccess() ? "allowed" : "not allowed");
            return result;
        };
    }
}
