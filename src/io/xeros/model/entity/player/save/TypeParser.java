package io.xeros.model.entity.player.save;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class TypeParser<T> {

    private final String[] array;

    public TypeParser(String[] array) {
        this.array = array;
    }

    public List<String> parse() {
        List<String> result = new ArrayList<>();
        for (String token : array) {
            if (token == null || token.length() <= 2) continue;
            String[] packed = token.substring(1, token.length() - 1).split(",");
            for (String pack : packed) {
                if (pack == null) break;
                result.add(pack.trim());
            }
        }
        return result;
    }

    public List<T> mapTo(Function<String, T> mapper) {
        List<String> parsed = this.parse();
        List<T> mapped = new ArrayList<>();
        for (String t : parsed) {
            mapped.add(mapper.apply(t));
        }
        return mapped;
    }
}
