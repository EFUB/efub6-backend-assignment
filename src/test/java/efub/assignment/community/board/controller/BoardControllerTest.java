package efub.assignment.community.board.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import efub.assignment.community.board.dto.request.CreateBoardRequest;
import efub.assignment.community.board.service.BoardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BoardController.class)
@AutoConfigureMockMvc(addFilters = false)
@MockitoBean(types = JpaMetamodelMappingContext.class)
public class BoardControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private BoardService boardService;

    @Test
    void create_board() throws Exception {
        // given
        Long memberId = 1L;
        Long createdBoardId = 1L;

        CreateBoardRequest requestDTO = CreateBoardRequest.builder()
                .name("자유게시판")
                .description("자유롭게 작성해주세요~")
                .notification("게시글 유출은 엄격히 금지합니다!")
                .build();

        given(boardService.createBoard(eq(memberId), any(CreateBoardRequest.class)))
                .willReturn(createdBoardId);

        // when & then
        mockMvc.perform(post("/members/{memberId}/boards", memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/boards/" + createdBoardId));
    }
}