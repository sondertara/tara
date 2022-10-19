package com.sondertara.common.bean;

import com.sondertara.common.function.JoinFunction;
import com.sondertara.common.function.KeyExtractor;
import com.sondertara.common.lang.Joins;
import com.sondertara.common.lang.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JoinsTest {
    List<String> people = Arrays.asList("1-Ann", "2-Ben", "3-Cecilia");
    List<Pair<Long, Integer>> ages = Arrays.asList(Pair.of(1L, 17), Pair.of(2L, 20), Pair.of(3L, 21));

    KeyExtractor<Long, String> keyExtractor1 = new KeyExtractor<Long, String>() {
        @Override
        public Long extract(String value) {
            return Long.valueOf(value.split("-")[0]);
        }
    };

    KeyExtractor<Long, Pair<Long, Integer>> keyExtractor2 = value -> value.getKey();

    JoinFunction<Long, String, Pair<Long, Integer>, String> joinFunction = new JoinFunction<Long, String, Pair<Long, Integer>, String>() {
        @Override
        public String compute(Long key, String person, Pair<Long, Integer> agePair) {
            return person.split("-")[1] + " is " + agePair.getValue() + " years old";
        }
    };

    private void checkResults(Map<Long, String> statements) {
        Assertions.assertEquals("Ann is 17 years old", statements.get(1L));
        Assertions.assertEquals("Ben is 20 years old", statements.get(2L));
        Assertions.assertEquals("Cecilia is 21 years old", statements.get(3L));
    }

    @Test
    public void testLoopJoin() {
        Map<Long, String> statements = Joins.loopJoin(people, ages, keyExtractor1, keyExtractor2, joinFunction);
        checkResults(statements);
    }

    @Test
    public void testHashJoin() {
        Map<Long, String> statements = Joins.hashJoin(people, ages, keyExtractor1, keyExtractor2, joinFunction);
        checkResults(statements);
    }

    @Test
    public void testMapsJoin() {
        Map<Long, String> peopleMap = new HashMap<>();
        for (int i = 0; i < people.size(); i++) {
            peopleMap.put((long) i + 1, people.get(i));
        }

        Map<Long, Pair<Long, Integer>> agesMap = new HashMap<>();
        for (int i = 0; i < ages.size(); i++) {
            agesMap.put((long) i + 1, ages.get(i));
        }

        Map<Long, String> statements = Joins.mapsJoin(peopleMap, agesMap, joinFunction);
        checkResults(statements);
    }
}
