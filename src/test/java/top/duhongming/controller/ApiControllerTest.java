package top.duhongming.controller;

import com.lark.oapi.service.im.v1.model.ListChat;
import com.lark.oapi.service.im.v1.model.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import top.duhongming.platform.lark.service.SendMessageTextService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApiControllerTest {

    @Mock
    private SendMessageTextService sendMessageTextService;

    @InjectMocks
    private ApiController apiController;

    @BeforeEach
    void setUp() {
        // Mockito自动初始化mock对象
    }

    @ParameterizedTest
    @MethodSource("timeTypeProvider")
    void testChatHistoryWithDifferentTimeTypes(String timeType) throws Exception {
        String chatId = "test_chat_id";
        String sortType = "ByCreateTimeDesc";
        List<Message> expectedMessages = new ArrayList<>();
        
        // 简化：使用更一致的参数匹配器
        when(sendMessageTextService.chatHistory(eq(chatId), eq(sortType), anyString(), anyString()))
                .thenReturn(expectedMessages);
        
        List<Message> result = apiController.chatHistory(chatId, sortType, null, null, timeType);
        
        assertNotNull(result);
        assertEquals(expectedMessages, result);
        verify(sendMessageTextService, times(1)).chatHistory(eq(chatId), eq(sortType), anyString(), anyString());
    }

    private static Stream<String> timeTypeProvider() {
        return Stream.of("day", "week", "month", "year");
    }

    @Test
    void testChatHistoryWithProvidedTimeRange() throws Exception {
        String chatId = "test_chat_id";
        String sortType = "ByCreateTimeDesc";
        String startTime = "1000000000";
        String endTime = "2000000000";
        List<Message> expectedMessages = new ArrayList<>();
        
        // 简化：移除不必要的eq()匹配器
        when(sendMessageTextService.chatHistory(chatId, sortType, startTime, endTime))
                .thenReturn(expectedMessages);
        
        List<Message> result = apiController.chatHistory(chatId, sortType, startTime, endTime, null);
        
        assertNotNull(result);
        assertEquals(expectedMessages, result);
        verify(sendMessageTextService, times(1)).chatHistory(chatId, sortType, startTime, endTime);
    }

    @Test
    void testChats() throws Exception {
        String userIdType = "open_id";
        String sortType = "ByCreateTimeDesc";
        List<ListChat> expectedChats = new ArrayList<>();
        
        // 已经是简化形式
        when(sendMessageTextService.chats(userIdType, sortType)).thenReturn(expectedChats);
        
        List<ListChat> result = apiController.chats(userIdType, sortType);
        
        assertNotNull(result);
        assertEquals(expectedChats, result);
        verify(sendMessageTextService, times(1)).chats(userIdType, sortType);
    }
}