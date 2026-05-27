package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.stream.LongStream;

@Service
public class InfoService {
    private static final Logger logger = LoggerFactory.getLogger(InfoService.class);

    public long getOptimizedSum() {
        logger.info("Was invoked method for calculate optimized sum");
        return LongStream.rangeClosed(1, 1_000_000)
                .parallel()
                .sum();
    }
}
