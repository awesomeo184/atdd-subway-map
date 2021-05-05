package wooteco.subway.station;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import wooteco.subway.exception.DuplicationException;

public class StationDao {
    private static Long seq = 0L;
    private static List<Station> stations = new ArrayList<>();

    public static Station save(Station station) {
        validateDuplicatedName(station);
        Station persistStation = createNewObject(station);
        stations.add(persistStation);
        return persistStation;
    }

    private static void validateDuplicatedName(Station station) {
        if (isDuplicate(station)) {
            throw new DuplicationException("이미 존재하는 역 이름입니다.");
        }
    }

    private static boolean isDuplicate(Station newStation) {
        return stations.stream()
            .anyMatch(station -> station.isSameName(newStation));
    }

    public static List<Station> findAll() {
        return stations;
    }

    private static Station createNewObject(Station station) {
        Field field = ReflectionUtils.findField(Station.class, "id");
        field.setAccessible(true);
        ReflectionUtils.setField(field, station, ++seq);
        return station;
    }

    public static void deleteAll() {
        stations.clear();
    }
}
