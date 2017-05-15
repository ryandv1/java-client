package io.split.engine.matchers;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class HierarchicalMatcher implements Matcher {

    private final String _test;
    private final Set<String> _treatments = new HashSet<>();

    public HierarchicalMatcher(String test, Collection<String> treatments) {
        if (test == null) {
            throw new IllegalArgumentException("Null test parameter");
        }
        if (treatments == null) {
            throw new IllegalArgumentException("Null treatments parameter");
        }
        _test = test;
        _treatments.addAll(treatments);
    }


    @Override
    public boolean match(Object key) {
        return _treatments.contains(key);
    }

    @Override
    public String toString() {
        StringBuilder bldr = new StringBuilder();
        bldr.append("is in Split ");
        bldr.append(_test);
        bldr.append(" with treatment [");
        boolean first = true;

        for (String item : _treatments) {
            if (!first) {
                bldr.append(',');
            }
            bldr.append('"');
            bldr.append(item);
            bldr.append('"');
            first = false;
        }

        bldr.append("]");
        return bldr.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HierarchicalMatcher that = (HierarchicalMatcher) o;

        if (_test != null ? !_test.equals(that._test) : that._test != null) return false;
        return _treatments != null ? _treatments.equals(that._treatments) : that._treatments == null;
    }

    @Override
    public int hashCode() {
        int result = _test != null ? _test.hashCode() : 0;
        result = 31 * result + (_treatments != null ? _treatments.hashCode() : 0);
        return result;
    }
}