package dev.mordi.lineuplarry.lineup_larry_backend.user;

import static dev.mordi.lineuplarry.lineup_larry_backend.test.jooq.database.Tables.*;
import static org.jooq.Records.mapping;
import static org.jooq.impl.DSL.count;
import static org.jooq.impl.DSL.selectFrom;

import dev.mordi.lineuplarry.lineup_larry_backend.lineup.LineupIdTitleDTO;
import dev.mordi.lineuplarry.lineup_larry_backend.user.exceptions.InvalidUserException;
import java.util.List;
import java.util.Optional;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {

  @Autowired DSLContext dsl;

  UserRepository(DSLContext dsl) {
    this.dsl = dsl;
  }

  public List<User> getAllUsers() {
    return dsl.selectFrom(USERS).fetch(r -> r.into(User.class));
  }

  public Optional<User> getUserById(Long id) {
    return dsl.select(USERS.ID, USERS.USERNAME)
        .from(USERS)
        .where(USERS.ID.eq(id))
        .fetchOptional()
        .map(mapping(User::new));
  }

  // TODO: create tests
  protected boolean doesUserExist(Long id) {
    boolean exists = dsl.fetchExists(selectFrom(USERS).where(USERS.ID.eq(id)));

    if (!exists) {
      throw new InvalidUserException.UserNotFoundException(id);
    }
    return true;
  }

  // consider using "UserRecord" instead
  public User createUser(User user) {
    return dsl.insertInto(USERS)
        .set(USERS.USERNAME, user.username())
        .returning()
        .fetchOne(mapping(User::new));
  }

  public void updateUser(Long id, User user) {
    dsl.fetchOptional(USERS, USERS.ID.eq(user.id()))
        .ifPresent(
            r -> {
              r.setUsername(user.username());
              r.store();
            });
  }

  public void deleteUser(Long id) {
    int rowsAffected = dsl.deleteFrom(USERS).where(USERS.ID.eq(id)).execute();

    if (rowsAffected == 0) {
      throw new InvalidUserException.UserNotFoundException(id);
    }
  }

  // TODO: look into if fewer queries can be made to produces the same result
  public UserSummaryDTO getUserSummary(Long userId) {
    boolean exists = doesUserExist(userId);

    if (!exists) {
      throw new InvalidUserException.UserNotFoundException(userId);
    }

    var userInfo =
        dsl.select(USERS.ID, USERS.USERNAME)
            .from(USERS)
            .where(USERS.ID.eq(userId))
            .fetchOneInto(
                User
                    .class); // confirm that fetchOneInto works or revert back to
                             // fetch(mapping((etc...

    // perform CTEs for each of the lists we want to fetch
    List<LineupIdTitleDTO> recentlyCreatedLineups =
        dsl.select(LINEUP.ID, LINEUP.TITLE)
            .from(LINEUP)
            .where(LINEUP.USER_ID.eq(userId))
            .orderBy(LINEUP.CREATED_AT)
            .limit(5)
            .fetchInto(LineupIdTitleDTO.class);

    List<LineupIdTitleDTO> mostLikedLineup =
        dsl.select(LINEUP.ID, LINEUP.TITLE)
            .from(LINEUP)
            .leftJoin(LIKES)
            .on(LIKES.LINEUP_ID.eq(LINEUP.ID))
            .where(LINEUP.USER_ID.eq(userId))
            .groupBy(LINEUP.ID)
            .orderBy(count(LIKES.USER_ID).desc(), LINEUP.CREATED_AT.desc())
            .limit(5)
            .fetchInto(LineupIdTitleDTO.class);

    List<LineupIdTitleDTO> recentlyLikedLineups =
        dsl.select(LINEUP.ID, LINEUP.TITLE)
            .from(LIKES)
            .join(LINEUP)
            .on(LIKES.LINEUP_ID.eq(LINEUP.ID))
            .where(LIKES.USER_ID.eq(userId))
            .orderBy(LIKES.CREATED_AT.desc())
            .limit(5)
            .fetchInto(LineupIdTitleDTO.class);

    return new UserSummaryDTO(
        userInfo.id(),
        userInfo.username(),
        recentlyCreatedLineups,
        mostLikedLineup,
        recentlyLikedLineups);
  }
}
