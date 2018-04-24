package io.split.client;

import io.split.client.api.Key;
import io.split.grammar.Treatments;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * An implementation of SplitClient that considers all partitions
 * passed in the constructor to be 100% on for all users, and
 * any other split to be 100% off for all users. This implementation
 * is useful for using Codigo in localhost environment.
 *
 * @author adil
 */
public final class LocalhostSplitClient implements SplitClient {
    private static final Logger _log = LoggerFactory.getLogger(LocalhostSplitClient.class);

    private Map<SplitAndKey, String> _splitKeyTreatmentMap;

    public LocalhostSplitClient(Map<SplitAndKey, String> map) {
        checkNotNull(map, "splitKeyTreatmentMap must not be null");
        _splitKeyTreatmentMap = map;
    }

    @Override
    public String getTreatment(String key, String split) {
        if (key == null || split == null) {
            return Treatments.CONTROL;
        }

        SplitAndKey override = SplitAndKey.of(split, key);
        if (_splitKeyTreatmentMap.containsKey(override)) {
            return _splitKeyTreatmentMap.get(override);
        }

        SplitAndKey splitDefaultTreatment = SplitAndKey.of(split);

        String treatment = _splitKeyTreatmentMap.get(splitDefaultTreatment);

        if (treatment == null) {
            return Treatments.CONTROL;
        }

        return treatment;
    }

    @Override
    public String getTreatment(String key, String split, Map<String, Object> attributes) {
        return getTreatment(key, split);
    }

    @Override
    public String getTreatment(Key key, String split, Map<String, Object> attributes) {
        return getTreatment(key.matchingKey(), split, attributes);
    }

    public void updateFeatureToTreatmentMap(Map<SplitAndKey, String> map) {
        if (map  == null) {
            _log.warn("A null map was passed as an update. Ignoring this update.");
            return;
        }
        _splitKeyTreatmentMap = map;
    }

    @Override
    public void destroy() {
        _splitKeyTreatmentMap.clear();
    }

    @Override
    public boolean track(String key, String trafficType, String eventType) {
        return false;
    }

    @Override
    public boolean track(String key, String trafficType, String eventType, double value) {
        return false;
    }

}
