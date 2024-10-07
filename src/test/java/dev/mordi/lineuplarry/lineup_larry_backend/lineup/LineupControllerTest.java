package dev.mordi.lineuplarry.lineup_larry_backend.lineup;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.mordi.lineuplarry.lineup_larry_backend.enums.Agent;
import dev.mordi.lineuplarry.lineup_larry_backend.enums.Map;
import dev.mordi.lineuplarry.lineup_larry_backend.lineup.exceptions.InvalidLineupException;
import dev.mordi.lineuplarry.lineup_larry_backend.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// TODO: look at how useless every test is

@WebMvcTest(LineupController.class)
@ExtendWith(MockitoExtension.class)
public class LineupControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    LineupService lineupService;

    private final ObjectMapper om = new ObjectMapper();

    private User userWithLineups;
    private User userWithNoLineups;
    private Lineup lineupOne;
    private Lineup lineupTwo;
    private Lineup lineupWithSameTitleAsLineupOne;

    @BeforeEach
    void setUp() {
        this.userWithLineups = new User(1L, "John");
        this.userWithNoLineups = new User(2L, "Jane");
        this.lineupOne = new Lineup(1L, Agent.SOVA, Map.ASCENT, "lineup title", "lineup body", 1L);
        this.lineupTwo = new Lineup(2L, Agent.SOVA, Map.ASCENT, "lineup title two", "lineup body two", 1L);
        this.lineupWithSameTitleAsLineupOne = new Lineup(4L, Agent.SOVA, Map.ASCENT, "lineup title", "lineup body", 1L);
    }

    @Test
    void pingTest() throws Exception {
        mockMvc.perform(get("/api/lineups/ping"))
                .andExpect(status().isOk())
                .andExpect(content().string("pong"));
    }

    // getAll
    @Test
    void successfulGetAll() throws Exception {
        when(lineupService.getLineup(null, null, null, 20L, null)).thenReturn(Arrays.asList(lineupOne, lineupTwo));

        MvcResult result = mockMvc.perform(get("/api/lineups"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andReturn();

        assertThat(result.getResponse().getContentType()).isEqualToIgnoringCase(MediaType.APPLICATION_JSON.toString());
        assertThat(result.getResponse().getContentAsString()).contains(lineupOne.title(), lineupTwo.title());
        verify(lineupService).getLineup(null, null, null, 20L, null);
    }

    // getById
    @Test
    void successfulGetById() throws Exception {
        Long lineupId = lineupOne.id();
        when(lineupService.getById(lineupId)).thenReturn(Optional.of(lineupOne));

        MvcResult result = mockMvc.perform(get("/api/lineups/{id}", lineupId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("lineup title"))
                .andExpect(jsonPath("$.body").value("lineup body"))
                .andExpect(jsonPath("$.userId").value(1))
                .andReturn();

        assertThat(result.getResponse().getContentType()).isEqualToIgnoringCase("application/json");
        assertThat(result.getResponse().getContentAsString()).contains(lineupOne.title());
        verify(lineupService).getById(lineupId);
    }

    @Test
    void getByIdOnNonexistentId() throws Exception {
        Long nonexistentId = 999L;
        when(lineupService.getById(nonexistentId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/lineups/{id}", nonexistentId))
                .andExpect(status().isNotFound())
                .andReturn();

        verify(lineupService).getById(nonexistentId);
    }

    // getAllByUserId: "/api/lineups/user/{id}"
    @Test
    void getLineupsFromUserWithLineups() throws Exception {
        Long userId = lineupOne.userId();
        Optional<List<Lineup>> userOnesLineups = Optional.of(List.of(lineupOne, lineupTwo));
        when(lineupService.getAllLineupsFromUserId(userId, 20L, null)).thenReturn(userOnesLineups);

        mockMvc.perform(get("/api/lineups/user/{id}", userId))
                .andExpect(status().isOk())
                .andReturn();

        verify(lineupService).getAllLineupsFromUserId(userId, 20L, null);
    }

    @Test
    void getLineupsFromUserWithNoLineups() throws Exception {
        Long userId = userWithNoLineups.id();
        Optional<List<Lineup>> emptyArray = Optional.of(List.of());
        when(lineupService.getAllLineupsFromUserId(userId, 20L, null)).thenReturn(emptyArray);

        mockMvc.perform(get("/api/lineups/user/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());

        verify(lineupService).getAllLineupsFromUserId(userId, 20L, null);
    }

    @Test
    void getLineupsFromNonexistentUser() throws Exception {
        Long nonexistentUserId = 222L;
        InvalidLineupException.NoUserException exception = new InvalidLineupException.NoUserException(nonexistentUserId);
        when(lineupService.getAllLineupsFromUserId(nonexistentUserId, 20L, null)).thenThrow(exception);

        mockMvc.perform(get("/api/lineups/user/{id}", nonexistentUserId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("User not found"))
                .andExpect(jsonPath("$.detail").value("No user with id: '" + nonexistentUserId + "' exists"));

        verify(lineupService).getAllLineupsFromUserId(nonexistentUserId, 20L, null);
    }

    // create
    @Test
    void successfulCreateLineup() throws Exception {
        Lineup lineupToCreate = new Lineup(null, Agent.SOVA, Map.ASCENT, "created title", "created body", userWithLineups.id());
        Lineup expectedLineupCreatedResult = new Lineup(3L, Agent.SOVA, Map.ASCENT, "created title", "created body", userWithLineups.id());
        when(lineupService.createLineup(lineupToCreate)).thenReturn(expectedLineupCreatedResult);

        try {
            String lineupToCreateJson = om.writeValueAsString(lineupToCreate);
            mockMvc.perform(post("/api/lineups")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(lineupToCreateJson))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(3));

            verify(lineupService).createLineup(lineupToCreate);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void successfulCreateLineupWithNullId() throws Exception {
        Lineup lineupToCreate = new Lineup(null, Agent.SOVA, Map.ASCENT, "created title", "created body", userWithLineups.id());
        Lineup expectedLineupResult = new Lineup(3L, Agent.SOVA, Map.ASCENT, "created title", "created body", userWithLineups.id());
        when(lineupService.createLineup(lineupToCreate)).thenReturn(expectedLineupResult);

        try {
            String lineupToCreateJson = om.writeValueAsString(lineupToCreate);

            mockMvc.perform(post("/api/lineups")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(lineupToCreateJson))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(3));

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        verify(lineupService).createLineup(lineupToCreate);
    }

    // update
    @Test
    void successfulUpdate() throws Exception {
        Lineup newLineupData = new Lineup(lineupOne.id(), Agent.SOVA, Map.ASCENT, "updated title", "updated body", lineupOne.userId());
        doNothing().when(lineupService).updateLineup(newLineupData.id(), newLineupData);

        try {
            String newLineupDataJson = om.writeValueAsString(newLineupData);

            mockMvc.perform(put("/api/lineups/{id}", lineupOne.id())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(newLineupDataJson))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").doesNotExist());

            verify(lineupService).updateLineup(newLineupData.id(), newLineupData);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    // delete
    @Test
    void successfulDelete() throws Exception {
        doNothing().when(lineupService).deleteLineup(lineupOne.id());

        mockMvc.perform(delete("/api/lineups/{id}", lineupOne.id()))
                .andExpect(status().isNoContent());

        verify(lineupService).deleteLineup(lineupOne.id());
    }

    @Test
    void failUpdateOnBlankTitle() throws Exception {
        Lineup lineupWithBlankTitle = lineupOne.withTitle("  ");
        try {
            String lineupWithBlankTitleJson = om.writeValueAsString(lineupWithBlankTitle);
            var res = mockMvc.perform(put("/api/lineups/{id}", lineupWithBlankTitle.id())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(lineupWithBlankTitleJson))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title").value("Invalid data"))
                    .andReturn();

            assertThat(res.getResponse().getContentAsString()).contains("title: title cannot be blank");
            assertThat(res.getResponse().getContentType()).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON.toString());
            verify(lineupService, never()).updateLineup(lineupWithBlankTitle.id(), lineupWithBlankTitle);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void failUpdateOnEmptyTitle() throws Exception {
        Lineup lineupWithEmptyTitle = lineupOne.withTitle("");

        try {
            String lineupWithEmptyTitleJson = om.writeValueAsString(lineupWithEmptyTitle);
            var res = mockMvc.perform(put("/api/lineups/{id}", lineupOne.id())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(lineupWithEmptyTitleJson))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title").value("Invalid data"))
                    .andReturn();

            assertThat(res.getResponse().getContentAsString()).containsIgnoringCase("title: title cannot be empty");
            assertThat(res.getResponse().getContentType()).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON.toString());
            verify(lineupService, never()).updateLineup(lineupWithEmptyTitle.id(), lineupWithEmptyTitle);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void failUpdateOnBlankBody() throws Exception {
        Lineup lineupWithEmptyTitle = lineupOne.withBody("   ");

        try {
            String lineupWithEmptyTitleJson = om.writeValueAsString(lineupWithEmptyTitle);
            var res = mockMvc.perform(put("/api/lineups/{id}", lineupOne.id())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(lineupWithEmptyTitleJson))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title").value("Invalid data"))
                    .andReturn();

            assertThat(res.getResponse().getContentAsString()).containsIgnoringCase("body: body cannot be blank");
            assertThat(res.getResponse().getContentType()).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON.toString());
            verify(lineupService, never()).updateLineup(lineupWithEmptyTitle.id(), lineupWithEmptyTitle);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void failUpdateOnEmptyBody() throws Exception {
        Lineup lineupWithEmptyBody = lineupOne.withBody("");

        try {
            String lineupWithEmptyBodyJson = om.writeValueAsString(lineupWithEmptyBody);
            var res = mockMvc.perform(put("/api/lineups/{id}", lineupOne.id())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(lineupWithEmptyBodyJson))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title").value("Invalid data"))
                    .andReturn();

            assertThat(res.getResponse().getContentAsString()).contains("body: body cannot be empty");
            assertThat(res.getResponse().getContentType()).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON.toString());
            verify(lineupService, never()).updateLineup(lineupWithEmptyBody.id(), lineupWithEmptyBody);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void failUpdateOnNullTitle() throws Exception {
        Lineup lineupWithNullTitle = lineupOne.withTitle(null);

        try {
            String lineupWithNullTitleJson = om.writeValueAsString(lineupWithNullTitle);
            var res = mockMvc.perform(put("/api/lineups/{id}", lineupOne.id())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(lineupWithNullTitleJson))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title").value("Invalid data"))
                    .andReturn();

            assertThat(res.getResponse().getContentAsString()).containsIgnoringCase("title: title cannot be null");
            assertThat(res.getResponse().getContentType()).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON.toString());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void failUpdateOnNullBody() throws Exception {
        Lineup lineupWithNullBody = lineupOne.withBody(null);

        try {
            String lineupWithNullBodyJson = om.writeValueAsString(lineupWithNullBody);
            var res = mockMvc.perform(put("/api/lineups/{id}", lineupOne.id())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(lineupWithNullBodyJson))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.title").value("Invalid data"))
                    .andReturn();

            assertThat(res.getResponse().getContentAsString()).containsIgnoringCase("body: body cannot be null");
            assertThat(res.getResponse().getContentType()).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON.toString());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    // test: getByTitle; success, fail, blank & empty
    // lineupId's 4 and 5 have the tile: "same name"
    @Test
    void successfulGetByTitle() {
        List<Lineup> mockedResult = Arrays.asList(lineupOne, lineupWithSameTitleAsLineupOne);
        when(lineupService.getByTitle("lineup title", 20L)).thenReturn(mockedResult);

        List<Lineup> lineups = lineupService.getByTitle("lineup title", 20L);

        assertThat(lineups).isNotNull();
        assertThat(lineups.size()).isEqualTo(2);
        verify(lineupService).getByTitle("lineup title", 20L);
    }

    @Test
    void successfulGetByTitleNoMatches() {
        List<Lineup> mockedResult = List.of();
        when(lineupService.getByTitle("bad search title", 20L)).thenReturn(mockedResult);

        List<Lineup> lineups = lineupService.getByTitle("bad search title", 20L);

        assertThat(lineups.toArray()).isEmpty();
    }

    @Test
    void failCreateOnInvalidAgent() throws Exception {
        var res = mockMvc.perform(post("/api/lineups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"valid title","body":"valid body","agent":"INVALIDAGENT","map":"SUNSET","userId":1}"""))
                .andExpect(status().isBadRequest())
                .andReturn();

        try {
            assertThat(res.getResponse().getContentAsString()).isNotEmpty();
            assertThat(res.getResponse().getContentType()).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON.toString());
            assertThat(res.getResponse().getContentAsString()).containsIgnoringCase("'INVALIDAGENT' is not a valid agent");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void failCreateOnInvalidMap() throws Exception {
        var res = mockMvc.perform(post("/api/lineups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"valid title","body":"valid body","agent":"SOVA","map":"INVALIDMAP","userId":2}"""))
                .andExpect(status().isBadRequest())
                .andReturn();

        try {
            assertThat(res.getResponse().getContentAsString()).isNotEmpty();
            assertThat(res.getResponse().getContentType()).isEqualTo(MediaType.APPLICATION_PROBLEM_JSON.toString());
            assertThat(res.getResponse().getContentAsString()).containsIgnoringCase("'INVALIDMAP' is not a valid map");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
