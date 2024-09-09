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
        Optional<User> res = dsl.select(USERS.ID, USERS.USERNAME)
                .from(USERS)
                .where(USERS.ID.eq(id))
                .fetchOptional().map(mapping(User::new));

        /*
        if (res.isEmpty()) {
            throw new InvalidLineupException.NoUserException(id);
        }
         */
        return res;
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
                .ifPresent(r -> {
                    r.setUsername(user.username());
                    r.store();
                });
    }

    public void deleteUser(Long id) {
        int rowsAffected = dsl.deleteFrom(USERS)
                .where(USERS.ID.eq(id))
                .execute();

        if (rowsAffected == 0) {
            throw new InvalidUserException.UserNotFoundException(id);
        }
    }
}
