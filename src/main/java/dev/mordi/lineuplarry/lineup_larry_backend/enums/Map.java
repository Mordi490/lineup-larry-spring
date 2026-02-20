package dev.mordi.lineuplarry.lineup_larry_backend.enums;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum Map {
    ASCENT, BIND, BREEZE, FRACTURE, HAVEN, ICEBOX, LOTUS, PEARL, SPLIT, SUNSET, ABYSS;

    private static final Set<String> MAPS = Stream.of(Map.values()).map(Enum::name)
            .collect(Collectors.toSet());

    public boolean isValidMap(String input) {
        return MAPS.contains(input.toUpperCase());
    }
}
