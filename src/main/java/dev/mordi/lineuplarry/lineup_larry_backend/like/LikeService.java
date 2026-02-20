package dev.mordi.lineuplarry.lineup_larry_backend.like;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class LikeService {

    private final LikeRepository likeRepository;

    public LikeService(LikeRepository likeRepository) {
        this.likeRepository = likeRepository;
    }

    public List<Like> getAllLikes() {
        return likeRepository.getAllLikes();
    }

    // TODO: consider any form of validations after applying some @BeanValidation
    public Optional<Like> getById(Long userId, Long lineupId) {
        Like like = new Like(userId, lineupId, null);
        return likeRepository.getLikeById(like.userId(), like.lineupId());
    }

    public Like likeLineup(Like like) {
        return likeRepository.likeLineup(like);
    }

    public void removeLike(Like like) {
        likeRepository.removeLike(like);
    }

    public List<Like> getLikesByUser(Long userId) {
        return likeRepository.getLikesByUser(userId);
    }

    public List<Like> getLikesByLineup(Long lineupId) {
        return likeRepository.getLikesByLineup(lineupId);
    }

    public long getLikeCountByLineup(Long lineupId) {
        return likeRepository.getLikeCountByLineup(lineupId);
    }
}
