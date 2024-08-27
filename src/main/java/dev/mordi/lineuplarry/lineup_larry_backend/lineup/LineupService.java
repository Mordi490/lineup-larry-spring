package dev.mordi.lineuplarry.lineup_larry_backend.lineup;

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

    public List<Lineup> getAll() {
        return lineupRepository.findAllLineups();
    }

    public Optional<Lineup> getById(Long id) {
        return lineupRepository.getLineupById(id);
    }

    public Optional<List<Lineup>> getAllLineupsFromUserId(Long id) {
        return lineupRepository.getLineupsByUserId(id);
    }

    public Optional<List<Lineup>> getByTitle(String name) {
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
        validateCreateData(lineup.title(), lineup.body(), lineup.userId());
        return lineupRepository.createLineup(lineup);
    }

    public void updateLineup(Long id, Lineup lineup) {
        validateUpdateLineupData(id, lineup.id(), lineup.title(), lineup.body(), lineup.userId());
        lineupRepository.updateLineup(lineup);
    }

    public void deleteLineup(Long id) {
        lineupRepository.deleteLineup(id);
    }

    // remember to revisit these once auth has been impl
    private void validateCreateData(String title, String body, Long userId) {
        if (title == null) {
            throw new InvalidLineupException.NullTitleException();
        }
        if (body == null) {
            throw new InvalidLineupException.NullBodyException();
        }
        if (userId == null) {
            throw new InvalidLineupException.UserIdNullException();
        }
        if (title.isEmpty()) {
            throw new InvalidLineupException.EmptyTitleException();
        }
        if (body.isEmpty()) {
            throw new InvalidLineupException.EmptyBodyException();
        }
        if (title.isBlank()) {
            throw new InvalidLineupException.BlankTitleException();
        }
        if (body.isBlank()) {
            throw new InvalidLineupException.BlankBodyException();
        }
    }

    private void validateUpdateLineupData(Long id, Long lineupId, String title, String body, Long userId) {
        if (!id.equals(lineupId)) {
            throw new InvalidLineupException.ChangedLineupIdException(id, lineupId);
        }
        if (title == null) {
            throw new InvalidLineupException.NullTitleException();
        }
        if (title.isEmpty()) {
            throw new InvalidLineupException.EmptyTitleException();
        }
        if (title.isBlank()) {
            throw new InvalidLineupException.BlankTitleException();
        }
        if (body == null) {
            throw new InvalidLineupException.NullBodyException();
        }
        if (body.isEmpty()) {
            throw new InvalidLineupException.EmptyBodyException();
        }
        if (body.isBlank()) {
            throw new InvalidLineupException.BlankBodyException();
        }
        // check if a lineup exists
        if (!lineupId.equals(userId)) {
            throw new InvalidLineupException.ChangedLineupIdException(lineupId, userId);
        }
    }
}
