package at.technikumwien.dmsbackend.service.impl;

import at.technikumwien.dmsbackend.service.dto.OCRResultDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQListenerImpl {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQListenerImpl.class);

    public void receiveResult(OCRResultDTO resultDTO) {
        logger.info("Received result from RabbitMQ: " + resultDTO.getDocumentId());
        logger.info(resultDTO.getRecognizedText());
    }
}
