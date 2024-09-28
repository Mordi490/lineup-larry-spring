package dev.mordi.lineuplarry.lineup_larry_backend.lineup;

import dev.mordi.lineuplarry.lineup_larry_backend.enums.Agent;
import dev.mordi.lineuplarry.lineup_larry_backend.enums.Map;
import dev.mordi.lineuplarry.lineup_larry_backend.lineup.exceptions.InvalidLineupException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LineupService {

    private final LineupRepository lineupRepository;

    public LineupService(LineupRepository lineupRepository) {
        this.lineupRepository = lineupRepository;
    }

    // This should not be this ugly
    public List<Lineup> getAll(String title, String agent, String map) {
        validateTitle(title);
        Agent validatedAgent = validateAgent(agent);
        Map validatedMap = validateMap(map);

        if (title != null && validatedAgent == null && validatedMap == null) {
            return lineupRepository.getByTitle(title);
        } else if (title == null && validatedAgent != null && validatedMap == null) {
            return lineupRepository.findByAgent(validatedAgent);
        } else if (title == null && validatedAgent == null && validatedMap != null) {
            return lineupRepository.findByMap(validatedMap);
        } else if (title == null && validatedAgent != null && validatedMap != null) {
            return lineupRepository.findByAgentAndMap(validatedAgent, validatedMap);
        } else if (title != null && validatedAgent == null && validatedMap != null) {
            return lineupRepository.findByMapAndTitle(validatedMap, title);
        } else if (title != null && validatedAgent != null && validatedMap == null) {
            return lineupRepository.findByAgentAndTitle(validatedAgent, title);
        } else if (title != null && validatedAgent != null && validatedMap != null) {
            return lineupRepository.findByAgentAndMapAndTitle(validatedAgent, validatedMap, title);
        } else {
            return lineupRepository.findAllLineups();
        }
    }

    public Optional<Lineup> getById(Long id) {
        return lineupRepository.getLineupById(id);
    }

    public Optional<List<Lineup>> getAllLineupsFromUserId(Long id) {
        return lineupRepository.getLineupsByUserId(id);
    }

    public List<Lineup> getByTitle(String name) {
        validateGetByTitleString(name);
        return lineupRepository.getByTitle(name);
    }

    private void validateGetByTitleString(String name) {
        if (name.isEmpty()) {
            throw new InvalidLineupException.EmptySearchTitleException(name);
        }
        if (name.isBlank()) {
            throw new InvalidLineupException.BlankSearchTitleException(name);
        }
    }

    public Lineup createLineup(Lineup lineup) {
        if (lineup.id() != null) {
            throw new InvalidLineupException.IncludedLineupIdException(lineup.id());
        }
        validateCreateData(lineup);
        return lineupRepository.createLineup(lineup);
    }

    public void updateLineup(Long id, Lineup lineup) {
        validateUpdateLineupData(id, lineup);
        lineupRepository.updateLineup(lineup);
    }

    public void deleteLineup(Long id) {
        lineupRepository.deleteLineup(id);
    }

    // remember to revisit these once auth has been impl
    private void validateCreateData(Lineup lineupToValidate) {
        if (!lineupToValidate.agent().isValidAgent(lineupToValidate.agent().toString())) {
            throw new InvalidLineupException.InvalidAgentException(lineupToValidate.agent().toString());
        }
        if (!lineupToValidate.map().isValidMap(lineupToValidate.map().toString())) {
            throw new InvalidLineupException.InvalidMapException(lineupToValidate.map().toString());
        }
        if (lineupToValidate.title() == null) {
            throw new InvalidLineupException.NullTitleException();
        }
        if (lineupToValidate.body() == null) {
            throw new InvalidLineupException.NullBodyException();
        }
        if (lineupToValidate.userId() == null) {
            throw new InvalidLineupException.UserIdNullException();
        }
        if (lineupToValidate.title().isEmpty()) {
            throw new InvalidLineupException.EmptyTitleException();
        }
        if (lineupToValidate.body().isEmpty()) {
            throw new InvalidLineupException.EmptyBodyException();
        }
        if (lineupToValidate.title().isBlank()) {
            throw new InvalidLineupException.BlankTitleException();
        }
        if (lineupToValidate.body().isBlank()) {
            throw new InvalidLineupException.BlankBodyException();
        }
    }

    // UPDATE THIS TO USE take a lineup as param instead of the other thingies
    private void validateUpdateLineupData(Long id, Lineup lineupToUpdate) {
        if (lineupToUpdate.id() == null) {
            throw new InvalidLineupException.ChangedLineupIdException(id, null);
        }
        if (!lineupToUpdate.id().equals(id)) {
            throw new InvalidLineupException.ChangedLineupIdException(id, lineupToUpdate.id());
        }
        if (!lineupToUpdate.agent().isValidAgent(lineupToUpdate.agent().toString())) {
            throw new InvalidLineupException.InvalidAgentException(lineupToUpdate.agent().toString());
        }
        if (!lineupToUpdate.map().isValidMap(lineupToUpdate.map().toString())) {
            throw new InvalidLineupException.InvalidMapException(lineupToUpdate.map().toString());
        }
        if (lineupToUpdate.title() == null) {
            throw new InvalidLineupException.NullTitleException();
        }
        if (lineupToUpdate.title().isEmpty()) {
            throw new InvalidLineupException.EmptyTitleException();
        }
        if (lineupToUpdate.title().isBlank()) {
            throw new InvalidLineupException.BlankTitleException();
        }
        if (lineupToUpdate.body() == null) {
            throw new InvalidLineupException.NullBodyException();
        }
        if (lineupToUpdate.body().isEmpty()) {
            throw new InvalidLineupException.EmptyBodyException();
        }
        if (lineupToUpdate.body().isBlank()) {
            throw new InvalidLineupException.BlankBodyException();
        }
    }

    private void validateTitle(String title) {
        if (title != null && title.isBlank()) {
            throw new InvalidLineupException.BlankTitleException();
        }
    }

    private Agent validateAgent(String agent) {
        if (agent == null) {
            return null;
        }
        try {
            return Agent.valueOf(agent.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidLineupException.InvalidAgentException(agent);
        }
    }

    private Map validateMap(String map) {
        if (map == null) {
            return null;
        }
        try {
            return Map.valueOf(map.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidLineupException.InvalidMapException(map);
        }
    }
}
