package dev.mordi.lineuplarry.lineup_larry_backend.lineup;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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

    @GetMapping()
    public ResponseEntity<List<Lineup>> getLineups(
            @RequestParam(required = false)
            @Size(min = 3, max = 40, message = "Title must be between {min} and {max} characters")
            String title,
            // maybe consider custom validations
            @RequestParam(required = false)
            String agent,
            @RequestParam(required = false)
            String map,
            @RequestParam(required = false, defaultValue = "20")
            Long pageSize,
            @RequestParam(required = false)
            Optional<Long> lastValue
    ) {
        List<Lineup> lineups = lineupService.getLineup(title, agent, map, pageSize, lastValue.orElse(null));
        return new ResponseEntity<>(lineups, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Lineup> getById(@PathVariable Long id) {
        Optional<Lineup> lineup = lineupService.getById(id);
        return lineup.map(ResponseEntity::ok)
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public Lineup createLineup(@Valid @RequestBody Lineup lineup) {
        return lineupService.createLineup(lineup);
    }

    @PutMapping("/{id}")
    public void updateLineup(@PathVariable Long id, @Valid @RequestBody Lineup lineup) {
        lineupService.updateLineup(id, lineup);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLineup(@PathVariable Long id) {
        lineupService.deleteLineup(id);
    }


    @GetMapping("/user/{id}")
    public ResponseEntity<List<Lineup>> getAllLineupsFromUser(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "20")
            Long pageSize,
            @RequestParam(required = false)
            Optional<Long> lastValue) {
        Optional<List<Lineup>> lineups = lineupService.getAllLineupsFromUserId(id, pageSize, lastValue.orElse(null));

        return lineups.map(ResponseEntity::ok)
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
