package io.split.engine.experiments;

import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * a value class representing an io.codigo.dtos.Experiment. Why are we not using
 * that class? Because it does not have the logic of matching. ParsedExperiment
 * has the matchers that also encapsulate the logic of matching. We
 * can easily cache this object.
 *
 * @author adil
 */
public class ParsedSplit {

    private final String _split;
    private final int _seed;
    private final boolean _killed;
    private final String _defaultTreatment;
    private final ImmutableList<ParsedCondition> _parsedCondition;
    private final String _trafficTypeName;
    private final long _changeNumber;

    public static Builder builder() {
        return new Builder();
    }

    private ParsedSplit(String feature, int seed, boolean killed, String defaultTreatment, List<ParsedCondition> parsedConditions, String trafficTypeName, long changeNumber) {
        _split = feature;
        _seed = seed;
        _killed = killed;
        _defaultTreatment = defaultTreatment;
        _parsedCondition = ImmutableList.copyOf(parsedConditions);
        _trafficTypeName = trafficTypeName;
        _changeNumber = changeNumber;
        if (_defaultTreatment == null) {
            throw new IllegalArgumentException("DefaultTreatment is null");
        }
    }

    public String feature() {
        return _split;
    }

    public int seed() {
        return _seed;
    }

    public boolean killed() {
        return _killed;
    }

    public String defaultTreatment() {
        return _defaultTreatment;
    }

    public List<ParsedCondition> parsedConditions() {
        return _parsedCondition;
    }

    public String trafficTypeName() {return _trafficTypeName;}

    public long changeNumber() {return _changeNumber;}

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + _split.hashCode();
        result = 31 * result + (int)(_seed ^ (_seed >>> 32));
        result = 31 * result + (_killed ? 1 : 0);
        result = 31 * result + _defaultTreatment.hashCode();
        result = 31 * result + _parsedCondition.hashCode();
        result = 31 * result + (_trafficTypeName == null ? 0 : _trafficTypeName.hashCode());
        result = 31 * result + (int)(_changeNumber ^ (_changeNumber >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (this == obj) return true;
        if (!(obj instanceof ParsedSplit)) return false;

        ParsedSplit other = (ParsedSplit) obj;

        return _split.equals(other._split)
                && _seed == other._seed
                && _killed == other._killed
                && _defaultTreatment.equals(other._defaultTreatment)
                && _parsedCondition.equals(other._parsedCondition)
                && _trafficTypeName == null ? other._trafficTypeName == null : _trafficTypeName.equals(other._trafficTypeName)
                && _changeNumber == other._changeNumber;
    }

    @Override
    public String toString() {
        StringBuilder bldr = new StringBuilder();
        bldr.append("name:");
        bldr.append(_split);
        bldr.append(", seed:");
        bldr.append(_seed);
        bldr.append(", killed:");
        bldr.append(_killed);
        bldr.append(", default treatment:");
        bldr.append(_defaultTreatment);
        bldr.append(", parsedConditions:");
        bldr.append(_parsedCondition);
        bldr.append(", trafficTypeName:");
        bldr.append(_trafficTypeName);
        bldr.append(", changeNumber:");
        bldr.append(_changeNumber);
        return bldr.toString();

    }

    public static final class Builder {
        private String _split;
        private Integer _seed;
        private Boolean _killed;
        private String _defaultTreatment;
        private List<ParsedCondition> _parsedConditions;
        private String _trafficTypeName;
        private Long _changeNumber;

        private Builder() {
        }

        public Builder split(String split) {
            this._split = split;
            return this;
        }

        public Builder seed(Integer seed) {
            this._seed = seed;
            return this;
        }

        public Builder killed(Boolean killed) {
            this._killed = killed;
            return this;
        }

        public Builder defaultTreatment(String defaultTreatment) {
            this._defaultTreatment = defaultTreatment;
            return this;
        }

        public Builder parsedConditions(List<ParsedCondition> parsedConditions) {
            this._parsedConditions = parsedConditions;
            return this;
        }

        public Builder trafficTypeName(String trafficTypeName) {
            this._trafficTypeName = trafficTypeName;
            return this;
        }

        public Builder changeNumber(Long changeNumber) {
            this._changeNumber = changeNumber;
            return this;
        }

        public ParsedSplit build() {
            String missing = "";
            if (_split == null) {
                missing += " split name";
            }
            if (_seed == null) {
                missing += " seed";
            }
            if (_killed == null) {
                missing += " killed";
            }
            if (_defaultTreatment == null) {
                missing += " defaultTreatment";
            }
            if (_parsedConditions == null) {
                missing += " parsedConditions";
            }
            if (_trafficTypeName == null) {
                missing += " trafficTypeName";
            }
            if (_changeNumber == null) {
                missing += " changeNumber";
            }
            if (!missing.isEmpty()) {
                throw new IllegalStateException("Missing required properties:" + missing);
            }
            return new ParsedSplit(
                    this._split,
                    this._seed.intValue(),
                    this._killed.booleanValue(),
                    this._defaultTreatment,
                    this._parsedConditions,
                    this._trafficTypeName,
                    this._changeNumber);
        }
    }

}
