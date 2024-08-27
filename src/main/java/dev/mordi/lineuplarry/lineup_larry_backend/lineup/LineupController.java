package dev.mordi.lineuplarry.lineup_larry_backend.lineup;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/lineups")
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
    public List<Lineup> getAllLineups() {
        return lineupService.getAll();
    }

    // TODO: consider using @RequestParam, might wait till we start with the SPA
    @GetMapping("/{id}")
    public ResponseEntity<Lineup> getById(@PathVariable Long id) {
        Optional<Lineup> lineup = lineupService.getById(id);
        return lineup.map(ResponseEntity::ok)
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // TODO: improve endpoint naming, this shit is ass atm
    @GetMapping("/search")
    public ResponseEntity<List<Lineup>> getByTitle(@RequestParam String title) {
        Optional<List<Lineup>> lineups = lineupService.getByTitle(title);
        return lineups.map(ResponseEntity::ok)
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
    public ResponseEntity<List<Lineup>> getAllLineupsFromUser(@PathVariable Long id) {
        Optional<List<Lineup>> lineups = lineupService.getAllLineupsFromUserId(id);

        return lineups.map(ResponseEntity::ok)
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
