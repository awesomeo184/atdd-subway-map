package wooteco.subway.domain.station;

import java.sql.PreparedStatement;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import wooteco.subway.web.exception.SubwayException;

@Repository
public class StationDao {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Station> stationRowMapper = (rs, rowNum) -> new Station(
            rs.getLong("id"),
            rs.getString("name")
    );

    public StationDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long save(Station station) {
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        final String sql = "INSERT INTO station (name) VALUES (?)";

        try {
            jdbcTemplate.update(con -> {
                PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
                ps.setString(1, station.getName());
                return ps;
            }, keyHolder);
        } catch (DuplicateKeyException e) {
            throw new SubwayException("중복된 역 이름입니다");
        }

        return keyHolder.getKey().longValue();
    }

    public List<Station> findAll() {
        final String sql = "SELECT id, name FROM station";
        return jdbcTemplate.query(sql, stationRowMapper);
    }

    public Optional<Station> findById(Long id) {
        final String sql = "SELECT id, name FROM station WHERE id = ?";

        try {
            final Station station = jdbcTemplate.queryForObject(sql, stationRowMapper, id);
            return Optional.of(station);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void delete(Long id) {
        final String sql = "DELETE FROM station WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public List<Station> stationsFilteredById(List<Long> ids) {
        final String inSql = String.join(",", Collections.nCopies(ids.size(), "?"));
        final String sql = String.format("SELECT id, name FROM station WHERE id IN (%s)", inSql);

        return jdbcTemplate.query(
                sql,
                ids.toArray(),
                stationRowMapper);
    }
}
