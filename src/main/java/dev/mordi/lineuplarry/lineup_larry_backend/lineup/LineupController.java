package dev.mordi.lineuplarry.lineup_larry_backend.lineup;

import org.jooq.DSLContext;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;


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
    public Lineup getById(@PathVariable Long id) {
        return repository.getLineupById(id);
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public Lineup createLineup(@RequestBody Lineup lineup) {
        System.out.println("Received lineup (controller):" + lineup);
        return repository.createLineup(lineup);
    }

    @PutMapping("/{id}")
    public Lineup updateLineup(@PathVariable Long id, @RequestBody Lineup lineup) {
        return repository.updateLineup(id, lineup);
    }

    @DeleteMapping("/{id}")
    public Lineup deleteLineup(@PathVariable Long id) {
        return repository.deleteLineup(id);
    }

    @GetMapping("/user/{id}")
    public List<Lineup> getAllLineupsFromUser(@PathVariable Long id) {
        return repository.getLineupsByUserId(id);
    }
}
