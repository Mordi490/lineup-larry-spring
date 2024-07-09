package dev.mordi.lineuplarry.lineup_larry_backend.user;

import dev.mordi.lineuplarry.lineup_larry_backend.user.exceptions.InvalidUserException;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static dev.mordi.lineuplarry.lineup_larry_backend.test.jooq.database.Tables.USERS;
import static org.jooq.Records.mapping;

@Repository
public class UserRepository {

    @Autowired
    DSLContext dsl;

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
                .fetchOptional().map(mapping(User::create));
    }

    // TODO: move this to utils or something similar
    private void validateUsername(String username) {
        if (username == null) {
            throw new InvalidUserException.NullUsernameException();
        }
        if (username.isBlank()) {
            throw new InvalidUserException.BlankUsernameException();
        }
    }

    private void validateUpdateUser(Long id, User user) {
        if (!id.equals(user.id())) {
            throw new InvalidUserException.IdDoesNotMatchUserException(id, user);
        }
        if (user.username() == null) {
            throw new InvalidUserException.NullUsernameException();
        }
        if (user.username().isBlank()) {
            throw new InvalidUserException.BlankUsernameException();
        }
    }

    // consider using "UserRecord" instead
    public User createUser(User user) {
        validateUsername(user.username());

        return dsl.insertInto(USERS)
                .set(USERS.USERNAME, user.username())
                .returning()
                .fetchOne(r -> new User(
                        r.getId(),
                        r.getUsername()
                ));
    }

    public void updateUser(Long id, User user) {
        // validate that the id and username has not changed
        validateUpdateUser(id, user);

        dsl.fetchOptional(USERS, USERS.ID.eq(user.id()))
                .ifPresent(r -> {
                    r.setUsername(user.username());
                    r.store();
                });
    }

    public void deleteUser(Long id) {
        boolean exists = dsl.fetchExists(
                dsl.selectOne().from(USERS).where(USERS.ID.eq(id))
        );

        if (!exists) {
            throw new InvalidUserException.UserNotFoundException(id);
        }

        dsl.deleteFrom(USERS).where(USERS.ID.eq(id)).execute();
    }
}
