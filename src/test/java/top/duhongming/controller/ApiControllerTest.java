package top.duhongming.controller;

import com.lark.oapi.service.im.v1.model.ListChat;
import com.lark.oapi.service.im.v1.model.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import top.duhongming.platform.lark.service.SendMessageTextService;

import java.util.ArrayList;
import java.util.List;

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

    @Test
    void testChatHistoryWithDayTimeType() throws Exception {
        String chatId = "test_chat_id";
        String sortType = "ByCreateTimeDesc";
        List<Message> expectedMessages = new ArrayList<>();
        
        when(sendMessageTextService.chatHistory(anyString(), eq(sortType), anyString(), anyString()))
                .thenReturn(expectedMessages);
        
        List<Message> result = apiController.chatHistory(chatId, sortType, null, null, "day");
        
        assertNotNull(result);
        assertEquals(expectedMessages, result);
        verify(sendMessageTextService, times(1)).chatHistory(eq(chatId), eq(sortType), anyString(), anyString());
    }

    @Test
    void testChatHistoryWithWeekTimeType() throws Exception {
        String chatId = "test_chat_id";
        String sortType = "ByCreateTimeDesc";
        List<Message> expectedMessages = new ArrayList<>();
        
        when(sendMessageTextService.chatHistory(anyString(), eq(sortType), anyString(), anyString()))
                .thenReturn(expectedMessages);
        
        List<Message> result = apiController.chatHistory(chatId, sortType, null, null, "week");
        
        assertNotNull(result);
        assertEquals(expectedMessages, result);
        verify(sendMessageTextService, times(1)).chatHistory(eq(chatId), eq(sortType), anyString(), anyString());
    }

    @Test
    void testChatHistoryWithMonthTimeType() throws Exception {
        String chatId = "test_chat_id";
        String sortType = "ByCreateTimeDesc";
        List<Message> expectedMessages = new ArrayList<>();
        
        when(sendMessageTextService.chatHistory(anyString(), eq(sortType), anyString(), anyString()))
                .thenReturn(expectedMessages);
        
        List<Message> result = apiController.chatHistory(chatId, sortType, null, null, "month");
        
        assertNotNull(result);
        assertEquals(expectedMessages, result);
        verify(sendMessageTextService, times(1)).chatHistory(eq(chatId), eq(sortType), anyString(), anyString());
    }

    @Test
    void testChatHistoryWithYearTimeType() throws Exception {
        String chatId = "test_chat_id";
        String sortType = "ByCreateTimeDesc";
        List<Message> expectedMessages = new ArrayList<>();
        
        when(sendMessageTextService.chatHistory(anyString(), eq(sortType), anyString(), anyString()))
                .thenReturn(expectedMessages);
        
        List<Message> result = apiController.chatHistory(chatId, sortType, null, null, "year");
        
        assertNotNull(result);
        assertEquals(expectedMessages, result);
        verify(sendMessageTextService, times(1)).chatHistory(eq(chatId), eq(sortType), anyString(), anyString());
    }

    @Test
    void testChatHistoryWithProvidedTimeRange() throws Exception {
        String chatId = "test_chat_id";
        String sortType = "ByCreateTimeDesc";
        String startTime = "1000000000";
        String endTime = "2000000000";
        List<Message> expectedMessages = new ArrayList<>();
        
        when(sendMessageTextService.chatHistory(eq(chatId), eq(sortType), eq(startTime), eq(endTime)))
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
        
        when(sendMessageTextService.chats(userIdType, sortType)).thenReturn(expectedChats);
        
        List<ListChat> result = apiController.chats(userIdType, sortType);
        
        assertNotNull(result);
        assertEquals(expectedChats, result);
        verify(sendMessageTextService, times(1)).chats(userIdType, sortType);
    }
}

