package dev.mordi.lineuplarry.lineup_larry_backend.enums;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum Agent {
    BRIMSTONE,
    VIPER,
    Omen,
    KILLJOY,
    CYPHER,
    SOVA,
    SAGE,
    PHOENIX,
    JETT,
    REYNA,
    RAZE,
    BREACH,
    SKYE,
    YORU,
    ASTRA,
    KAYO,
    CHAMBER,
    NEON,
    FADE,
    HARBOR,
    GEKKO,
    DEADLOCK,
    ISO,
    CLOVE,
    VYSE;

    private static final Set<String> AGENTS =
            Stream.of(Agent.values()).map(Enum::name).collect(Collectors.toSet());

    public boolean isValidAgent(String input) {
        return AGENTS.contains(input.toUpperCase());
    }
}
