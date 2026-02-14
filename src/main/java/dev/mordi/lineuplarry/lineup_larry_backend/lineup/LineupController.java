package dev.mordi.lineuplarry.lineup_larry_backend.lineup;

import dev.mordi.lineuplarry.lineup_larry_backend.lineup.exceptions.InvalidLineupException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lineups")
@Validated
public class LineupController {

    LineupService lineupService;

    LineupController(LineupService lineupService) {
        this.lineupService = lineupService;
    }

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }

    @GetMapping
    public ResponseEntity<List<LineupWithAuthorDTO>> getLineups(
        @RequestParam(required = false) @Size(
            min = 3,
            max = 40,
            message = "Title must be between {min} and {max} characters"
        ) String title,
        // maybe consider custom validations
        @RequestParam(required = false) String agent,
        @RequestParam(required = false) String map,
        @RequestParam(required = false, defaultValue = "20") Long pageSize,
        @RequestParam(required = false) Optional<Long> lastValue
    ) {
        List<LineupWithAuthorDTO> lineups = lineupService.getLineup(
            title,
            agent,
            map,
            pageSize,
            lastValue.orElse(null)
        );
        return new ResponseEntity<>(lineups, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineupWithAuthorDTO> getById(@PathVariable Long id) {
        LineupWithAuthorDTO lineup = lineupService
            .getById(id)
            .orElseThrow(() ->
                new InvalidLineupException.NoSuchLineupException(id)
            );
        return ResponseEntity.ok(lineup);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Lineup createLineup(@Valid @RequestBody Lineup lineup) {
        return lineupService.createLineup(lineup);
    }

    @PutMapping("/{id}")
    public void updateLineup(
        @PathVariable Long id,
        @Valid @RequestBody Lineup lineup
    ) {
        lineupService.updateLineup(id, lineup);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLineup(@PathVariable Long id) {
        lineupService.deleteLineup(id);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<List<LineupWithAuthorDTO>> getAllLineupsFromUser(
        @PathVariable Long id,
        @RequestParam(required = false, defaultValue = "20") Long pageSize,
        @RequestParam(required = false) Optional<Long> lastValue
    ) {
        List<LineupWithAuthorDTO> lineups = lineupService
            .getAllLineupsFromUserId(id, pageSize, lastValue.orElse(null))
            .orElseThrow(() -> new InvalidLineupException.NoUserException(id));
        return ResponseEntity.ok(lineups);
    }
}
