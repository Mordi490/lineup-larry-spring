package dev.mordi.lineuplarry.lineup_larry_backend.lineup;

import dev.mordi.lineuplarry.lineup_larry_backend.enums.Agent;
import dev.mordi.lineuplarry.lineup_larry_backend.enums.Map;
import dev.mordi.lineuplarry.lineup_larry_backend.lineup.exceptions.InvalidLineupException;
import dev.mordi.lineuplarry.lineup_larry_backend.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LineupServiceTest {

    @InjectMocks
    private LineupService lineupService;

    @Mock
    private LineupRepository lineupRepository;

    private User userWithLineups;
    private User userWithoutLineups;
    private Lineup lineupOne;
    private Lineup lineupTwo;
    private Lineup lineupThree;
    private Lineup lineupFour;
    private Lineup lineupFive;

    @BeforeEach
    void setUp() {
        User userWithLineups = new User(1L, "userOne");
        User userWithoutLineups = new User(2L, "userTwo");


        lineupOne = new Lineup(1L, Agent.SOVA, Map.ASCENT, "titleOne", "bodyOne", 1L);
        lineupTwo = new Lineup(2L, Agent.SOVA, Map.ASCENT, "titleTwo", "bodyTwo", 1L);
        lineupThree = new Lineup(3L, Agent.BRIMSTONE, Map.BIND, "titleThree", "bodyThree", 1L);
        lineupFour = new Lineup(4L, Agent.CYPHER, Map.SUNSET, "same name", "bodyThree", 1L);
        lineupFive = new Lineup(5L, Agent.KILLJOY, Map.ICEBOX, "same name", "bodyThree", 1L);
    }

    @Test
    void getAll() {
        List<Lineup> allLineups = Arrays.asList(lineupOne, lineupTwo, lineupThree, lineupFour, lineupFive);
        when(lineupRepository.findAllLineups()).thenReturn(allLineups);

        List<Lineup> result = lineupService.getAll();

        assertThat(result).isEqualTo(allLineups);
        verify(lineupRepository).findAllLineups();
    }

    @Test
    void getAllAfterRemovalOfLineup() {
        List<Lineup> allLineupsMinusOne = Arrays.asList(lineupOne, lineupTwo);
        when(lineupRepository.findAllLineups()).thenReturn(allLineupsMinusOne);

        List<Lineup> result = lineupService.getAll();
        List<Lineup> resultMinusOne = result.subList(0, result.size());

        assertThat(resultMinusOne).isEqualTo(allLineupsMinusOne);
        verify(lineupRepository).findAllLineups();
    }

    // getLineupById
    @Test
    void successfulGetById() {
        when(lineupRepository.getLineupById(1L)).thenReturn(Optional.of(lineupOne));

        Optional<Lineup> fetchedLineup = lineupService.getById(1L);

        assertThat(fetchedLineup).isPresent();
        assertThat(fetchedLineup).isNotNull();
        assertThat(fetchedLineup.get().title()).isEqualTo(lineupOne.title());
        assertThat(fetchedLineup.get().body()).isEqualTo(lineupOne.body());
        assertThat(fetchedLineup.get()).isEqualTo(lineupOne);
        verify(lineupRepository).getLineupById(1L);
    }

    @Test
    void getByIdToNonexistentLineup() {
        Long nonexistentLineupId = 999L;
        when(lineupRepository.getLineupById(nonexistentLineupId)).thenReturn(Optional.empty());

        Optional<Lineup> fetchedLineup = lineupService.getById(nonexistentLineupId);

        assertThat(fetchedLineup).isNotPresent();
        verify(lineupRepository).getLineupById(nonexistentLineupId);
    }

    // get all lineups from user
    @Test
    void successfulGetAllLineupsFromUserWithLineups() {
        Optional<List<Lineup>> allLineupsFromUserOne = Optional.of(Arrays.asList(lineupOne, lineupTwo, lineupThree));
        when(lineupRepository.getLineupsByUserId(1L)).thenReturn(allLineupsFromUserOne);

        Optional<List<Lineup>> fetchedLineupsFromUser = lineupService.getAllLineupsFromUserId(1L);

        assertThat(fetchedLineupsFromUser).isPresent();
        assertThat(fetchedLineupsFromUser.get().size()).isEqualTo(3);
        assertThat(fetchedLineupsFromUser.get()).isEqualTo(allLineupsFromUserOne.get());
        verify(lineupRepository).getLineupsByUserId(1L);
    }

    @Test
    void successfulGetAllLineupsFromUserWithNoLineups() {
        Optional<List<Lineup>> emptyList = Optional.empty();
        Long userId = 2L;
        when(lineupRepository.getLineupsByUserId(userId)).thenReturn(emptyList);

        Optional<List<Lineup>> fetchedList = lineupService.getAllLineupsFromUserId(userId);

        assertThat(fetchedList).isEmpty();
        verify(lineupRepository).getLineupsByUserId(userId);
    }

    @Test
    void getAllLineupsFromNonexistentUser() {
        Long nonexistentUserId = 999L;
        InvalidLineupException.NoUserException exception = new InvalidLineupException.NoUserException(nonexistentUserId);
        when(lineupRepository.getLineupsByUserId(nonexistentUserId)).thenThrow(exception);

        assertThatThrownBy(() -> lineupService.getAllLineupsFromUserId(nonexistentUserId))
                .isInstanceOf(InvalidLineupException.NoUserException.class)
                .hasMessage("No user with id: '" + nonexistentUserId + "' exists");

        verify(lineupRepository).getLineupsByUserId(nonexistentUserId);
    }

    // create lineup
    @Test
    void successfulLineup() {
        Lineup lineupToCreate = new Lineup(null, Agent.SOVA, Map.ASCENT, "created lineup title", "created body content", 1L);
        Lineup lineupCreatedResponse = new Lineup(5L, Agent.SOVA, Map.ASCENT, lineupToCreate.title(), lineupToCreate.body(), lineupToCreate.userId());
        when(lineupRepository.createLineup(lineupToCreate)).thenReturn(lineupCreatedResponse);

        Lineup actualResponse = lineupService.createLineup(lineupToCreate);

        assertThat(actualResponse).isNotNull();
        assertThat(actualResponse).isEqualTo(lineupCreatedResponse);
        verify(lineupRepository).createLineup(lineupToCreate);
    }

    // fail on given id (lineup id)
    @Test
    void declineLineupCreationDueToHavingId() {
        Long lineupId = 44L;
        Lineup lineupToReject = new Lineup(lineupId, Agent.SOVA, Map.ASCENT, "muh title", "muh body", 1L);

        assertThatThrownBy(() -> lineupService.createLineup(lineupToReject))
                .isInstanceOf(InvalidLineupException.IncludedLineupIdException.class)
                .hasMessage("Do not supply an id when creating a lineup\nCannot create lineup with id: '" + lineupId + "'");
        verify(lineupRepository, never()).createLineup(lineupToReject);
    }

    // fail on not using your own userId, ONCE AUTH HAS BEEN IMPL

    // fail on blanks and empty title/content
    @Test
    void failCreateOnBlankTitle() {
        Lineup lineupWithBlankTitle = new Lineup(null, Agent.SOVA, Map.ASCENT, "  ", "valid body", 1L);

        assertThatThrownBy(() -> lineupService.createLineup(lineupWithBlankTitle))
                .isInstanceOf(InvalidLineupException.BlankTitleException.class)
                .hasMessage("Lineup title cannot be blank");
        verify(lineupRepository, never()).createLineup(lineupWithBlankTitle);
    }

    @Test
    void failCreateOnEmptyTitle() {
        Lineup lineupWithEmptyTitle = new Lineup(null, Agent.SOVA, Map.ASCENT, "", "valid body", 1L);

        assertThatThrownBy(() -> lineupService.createLineup(lineupWithEmptyTitle))
                .isInstanceOf(InvalidLineupException.EmptyTitleException.class)
                .hasMessage("Lineup title cannot be empty");
        verify(lineupRepository, never()).createLineup(lineupWithEmptyTitle);
    }

    @Test
    void failCreateOnBlankBody() {
        Lineup lineupWithBlankBody = new Lineup(null, Agent.SOVA, Map.ASCENT, "valid title", "  ", 1L);

        assertThatThrownBy(() -> lineupService.createLineup(lineupWithBlankBody))
                .isInstanceOf(InvalidLineupException.BlankBodyException.class)
                .hasMessage("Lineup body cannot be blank");
        verify(lineupRepository, never()).createLineup(lineupWithBlankBody);
    }

    @Test
    void failCreateOnEmptyBody() {
        Lineup lineupWithEmptyBody = new Lineup(null, Agent.SOVA, Map.ASCENT, "valid title", "", 1L);

        assertThatThrownBy(() -> lineupService.createLineup(lineupWithEmptyBody))
                .isInstanceOf(InvalidLineupException.EmptyBodyException.class)
                .hasMessage("Lineup body cannot be empty");
        verify(lineupRepository, never()).createLineup(lineupWithEmptyBody);
    }

    // update lineup
    @Test
    void successfulUpdateLineup() {
        Lineup updatedLineup = lineupOne.withTitle("updated title");
        doNothing().when(lineupRepository).updateLineup(updatedLineup);

        assertThatCode(() -> lineupService.updateLineup(updatedLineup.id(), updatedLineup))
                .doesNotThrowAnyException();
        verify(lineupRepository).updateLineup(updatedLineup);
    }

    // fail updates on removal of id
    @Test
    void failUpdateOnChangedLineupIdChange() {
        Lineup randomLineup = new Lineup(33L, Agent.SOVA, Map.ASCENT, "some title", "some body", 2L);

        assertThatThrownBy(() -> lineupService.updateLineup(2L, randomLineup))
                .isInstanceOf(InvalidLineupException.ChangedLineupIdException.class)
                .hasMessage("Cannot change lineup id from '2' to '33'");
    }

    // fail updates on same scenarios as creation, blank, empty...
    @Test
    void failUpdateOnBlankTitleUpdate() {
        Lineup updatedLineupWithBlankTitle = lineupOne.withTitle("  ");

        assertThatThrownBy(() -> lineupService.updateLineup(updatedLineupWithBlankTitle.id(), updatedLineupWithBlankTitle))
                .isInstanceOf(InvalidLineupException.BlankTitleException.class)
                .hasMessage("Lineup title cannot be blank");
        verify(lineupRepository, never()).updateLineup(updatedLineupWithBlankTitle);
    }

    @Test
    void failUpdateOnEmptyTitleUpdate() {
        Lineup updatedLineupWithEmptyTitle = lineupOne.withTitle("");

        assertThatThrownBy(() -> lineupService.updateLineup(updatedLineupWithEmptyTitle.id(), updatedLineupWithEmptyTitle))
                .isInstanceOf(InvalidLineupException.EmptyTitleException.class)
                .hasMessage("Lineup title cannot be empty");
        verify(lineupRepository, never()).updateLineup(updatedLineupWithEmptyTitle);
    }

    @Test
    void failUpdateOnBlankBodyUpdate() {
        Lineup updatedLineupWithBlankBody = lineupOne.withBody("  ");

        assertThatThrownBy(() -> lineupService.updateLineup(updatedLineupWithBlankBody.id(), updatedLineupWithBlankBody))
                .isInstanceOf(InvalidLineupException.BlankBodyException.class)
                .hasMessage("Lineup body cannot be blank");
        verify(lineupRepository, never()).updateLineup(updatedLineupWithBlankBody);
    }

    @Test
    void failUpdateOnEmptyBodyUpdate() {
        Lineup updatedLineupWithEmptyBody = lineupOne.withBody("");

        assertThatThrownBy(() -> lineupService.updateLineup(updatedLineupWithEmptyBody.id(), updatedLineupWithEmptyBody))
                .isInstanceOf(InvalidLineupException.EmptyBodyException.class)
                .hasMessage("Lineup body cannot be empty");
        verify(lineupRepository, never()).updateLineup(updatedLineupWithEmptyBody);
    }

    @Test
    void failUpdateOnNullTitle() {
        Lineup lineupWithNullTitle = lineupOne.withTitle(null);

        assertThatThrownBy(() -> lineupService.updateLineup(lineupWithNullTitle.id(), lineupWithNullTitle))
                .isInstanceOf(InvalidLineupException.NullTitleException.class)
                .hasMessage("Lineup title cannot be null");
        verify(lineupRepository, never()).updateLineup(lineupWithNullTitle);
    }


    @Test
    void failUpdateOnNullBody() {
        Lineup lineupWithNullBody = lineupOne.withBody(null);

        assertThatThrownBy(() -> lineupService.updateLineup(lineupWithNullBody.id(), lineupWithNullBody))
                .isInstanceOf(InvalidLineupException.NullBodyException.class)
                .hasMessage("Lineup body cannot be null");
        verify(lineupRepository, never()).updateLineup(lineupWithNullBody);
    }
    // fail updates when userId does not match with the user's principal, TODO: AFTER AUTH HAS BEEN IMPL

    // delete lineup
    @Test
    void successfulDelete() {
        Long lineupIdToDelete = lineupOne.id();

        doNothing().when(lineupRepository).deleteLineup(lineupIdToDelete);

        assertThatCode(() -> lineupService.deleteLineup(lineupIdToDelete)).doesNotThrowAnyException();

        verify(lineupRepository).deleteLineup(lineupIdToDelete);
    }

    @Test
    void failDeleteDueToBadCredentials() {
        // TODO: get back to this after auth has been impl
    }

    /**
     * lineupThree = new Lineup(4L, "same name", "bodyThree", 1L);
     * lineupThree = new Lineup(5L, "same name", "bodyThree", 1L);
     */

    @Test
    void successfulGetByTitle() {
        List<Lineup> expectedList = Arrays.asList(lineupFour, lineupFive);
        when(lineupRepository.getByTitle("same name")).thenReturn(Optional.of(expectedList));

        Optional<List<Lineup>> lineups = lineupRepository.getByTitle("same name");

        assertThat(lineups).isPresent();
        assertThat(lineups.get()).size().isEqualTo(2);
        verify(lineupRepository).getByTitle("same name");
    }

    @Test
    void successfulGetByTitleNoMatches() {
        Optional<List<Lineup>> expectedEmptyOptional = Optional.empty();
        String searchString = "some title that will not receive any matches";
        when(lineupRepository.getByTitle(searchString)).thenReturn(expectedEmptyOptional);

        Optional<List<Lineup>> lineups = lineupRepository.getByTitle(searchString);

        assertThat(lineups).isEmpty();
        verify(lineupRepository).getByTitle(searchString);
    }

    // expect to get errs on blank and empty strings
    @Test
    void failGetByTitleOnEmptyString() {
        assertThatThrownBy(() -> lineupService.getByTitle(""))
                .isInstanceOf(InvalidLineupException.EmptySearchTitleException.class)
                .hasMessage("Cannot search for string: '', since it's empty");

        verify(lineupRepository, never()).getByTitle("");
    }

    @Test
    void failGetByTitleOnBlankString() {
        assertThatThrownBy(() -> lineupService.getByTitle("  "))
                .isInstanceOf(InvalidLineupException.BlankSearchTitleException.class)
                .hasMessage("Cannot search for string '  ', since it's blank");

        verify(lineupRepository, never()).getByTitle("  ");
    }
}
