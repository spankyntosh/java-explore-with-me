package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.common.StatsResponseDTO;
import ru.practicum.model.Statistics;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface StatisticsRepository extends JpaRepository<Statistics, Integer> {


    @Query("SELECT new ru.practicum.common.StatsResponseDTO(s.appName, s.uri, COUNT(s.ip)) " +
            "FROM Statistics AS s " +
            "WHERE s.timestamp BETWEEN ?1 AND ?2 " +
            "GROUP BY s.appName, s.uri " +
            "ORDER BY COUNT(s.ip) DESC")
    Collection<StatsResponseDTO> getAllStatistics(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.practicum.common.StatsResponseDTO(s.appName, s.uri, COUNT(s.ip)) " +
            "FROM Statistics AS s " +
            "WHERE s.timestamp BETWEEN ?1 AND ?2 " +
            "GROUP BY s.appName, s.uri " +
            "ORDER BY COUNT(DISTINCT s.ip) DESC")
    Collection<StatsResponseDTO> getAllStatisticsWithDistinctIp(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.practicum.common.StatsResponseDTO(s.appName, s.uri, COUNT(s.ip)) " +
            "FROM Statistics AS s " +
            "WHERE s.timestamp BETWEEN ?1 AND ?2 " +
            "AND s.uri IN (?3) " +
            "GROUP BY s.appName, s.uri " +
            "ORDER BY COUNT(s.ip) DESC")
    Collection<StatsResponseDTO> getStatisticsByUri(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("SELECT new ru.practicum.common.StatsResponseDTO(s.appName, s.uri, COUNT(s.ip)) " +
            "FROM Statistics AS s " +
            "WHERE s.timestamp BETWEEN ?1 AND ?2 " +
            "AND s.uri IN (?3) " +
            "GROUP BY s.appName, s.uri " +
            "ORDER BY COUNT(DISTINCT s.ip) DESC")
    Collection<StatsResponseDTO> getStatisticsByUriWithDistinctIp(LocalDateTime start, LocalDateTime end, List<String> uris);
}
