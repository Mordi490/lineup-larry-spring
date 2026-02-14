package dev.mordi.lineuplarry.lineup_larry_backend.like;

import dev.mordi.lineuplarry.lineup_larry_backend.like.exceptions.InvalidLikeException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/likes")
public class LikeController {

    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    @GetMapping
    public List<Like> getAllLikes() {
        return likeService.getAllLikes();
    }

    // NB! will change once auth has been impl
    @GetMapping("/user/{userId}/lineup/{lineupId}")
    public ResponseEntity<Like> getById(
        @PathVariable Long userId,
        @PathVariable Long lineupId
    ) {
        Like like = likeService
            .getById(userId, lineupId)
            .orElseThrow(() ->
                new InvalidLikeException.LikeNotFound(userId, lineupId)
            );
        return ResponseEntity.ok(like);
    }

    // NB! these will change once auth is added
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Like likeLineup(@RequestBody Like like) {
        return likeService.likeLineup(like);
    }

    @DeleteMapping("/{lineupId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeLike(@RequestBody Like like) {
        likeService.removeLike(like);
    }

    @GetMapping("/user/{userId}")
    public List<Like> getLikesByUser(@PathVariable Long userId) {
        return likeService.getLikesByUser(userId);
    }

    @GetMapping("/lineup/{lineupId}")
    public List<Like> getLikesByLineup(@PathVariable Long lineupId) {
        return likeService.getLikesByLineup(lineupId);
    }

    // TODO: reconsider if this is even needed
    @GetMapping("/lineup/{lineupId}/count")
    public ResponseEntity<Long> getLikeCountByLineup(
        @PathVariable Long lineupId
    ) {
        long likeCount = likeService.getLikeCountByLineup(lineupId);
        return ResponseEntity.ok(likeCount);
    }
}
