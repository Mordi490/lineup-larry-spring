package dev.mordi.lineuplarry.lineup_larry_backend.lineup;

import org.jooq.DSLContext;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/lineups")
public class LineupController {

    DSLContext dsl;
    LineupRepository repository;

    LineupController(DSLContext dsl, LineupRepository repository) {
        this.dsl = dsl;
        this.repository = repository;
    }

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }

    @GetMapping()
    public List<Lineup> getAllLineups() {
        return repository.findAllLineups();
    }

    @GetMapping("/{id}")
    public Optional<Lineup> getById(@PathVariable Long id) {
        return repository.getLineupById(id);
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public Lineup createLineup(@RequestBody Lineup lineup) {
        return repository.createLineup(lineup);
    }

    @PutMapping("/{id}")
    public void updateLineup(@RequestBody Lineup lineup) {
        repository.updateLineup(lineup);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLineup(@PathVariable Long id) {
        repository.deleteLineup(id);
    }

    @GetMapping("/user/{id}")
    public List<Lineup> getAllLineupsFromUser(@PathVariable Long id) {
        return repository.getLineupsByUserId(id);
    }
}
