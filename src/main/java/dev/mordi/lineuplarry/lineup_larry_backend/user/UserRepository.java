package dev.mordi.lineuplarry.lineup_larry_backend.user;

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

    // consider using "UserRecord" instead
    public User createUser(User user) {
        System.out.println("Received user (userRepo): " + user);
        return dsl.insertInto(USERS)
                .set(USERS.USERNAME, user.username())
                .returning()
                .fetchOne(r -> new User(
                        r.getId(),
                        r.getUsername()
                ));
    }

    public void updateUser(User user) {
        dsl.fetchOptional(USERS, USERS.ID.eq(user.id()))
                .ifPresent(r -> {
                    r.setUsername(user.username());
                    r.store();
                });
    }


    public void deleteUser(Long id) {
        dsl.deleteFrom(USERS).where(USERS.ID.eq(id)).execute();
    }
}
