package ru.practicum.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.common.HitRequestDTO;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.nonNull;

@Component
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

    public ResponseEntity<Object> saveHit(HitRequestDTO hitRequestDTO) {
        return post("/hit", hitRequestDTO);
    }

    public ResponseEntity<Object> getStatistics(LocalDateTime start,
                                                LocalDateTime end,
                                                @Nullable List<String> uris,
                                                @Nullable Boolean unique) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("start", start.format(formatter));
        parameters.put("end", end.format(formatter));
        if (nonNull(uris)) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < uris.size(); i++) {
                if (i == uris.size() - 1) {
                    builder.append(uris.get(i));
                } else {
                    builder.append(uris.get(i) + ",");
                }
            }
            parameters.put("uris", builder.toString());
            return get("/stats?start={start}&end={end}&uris={uris}&unique=true", parameters);
        }
        if (nonNull(unique)) {
            parameters.put("unique", unique);
            return get("/stats?start={start}&end={end}&uris={uris}&unique={unique}", parameters);
        }
        return get("/stats?start={start}&end", parameters);
    }

}
