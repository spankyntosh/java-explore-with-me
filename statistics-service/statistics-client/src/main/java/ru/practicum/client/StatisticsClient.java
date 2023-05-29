package ru.practicum.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.common.HitRequestDTO;

import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.Objects.nonNull;

@Service
public class StatisticsClient extends BaseClient {

    @Autowired
    public StatisticsClient(@Value("${statistics-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> saveHit(String path, HitRequestDTO hitRequestDTO) {
        return post("/hit", hitRequestDTO);
    }

    public ResponseEntity<Object> getStatistics(LocalDateTime start,
                                                LocalDateTime end,
                                                @Nullable List<String> uris,
                                                @Nullable Boolean unique) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("start", start);
        parameters.put("end", end);
        if (nonNull(uris)) {
            parameters.put("uris", uris);
        }
        if (nonNull(unique)) {
            parameters.put("unique", unique);
        }
        return get("/stats", parameters);
    }

}
