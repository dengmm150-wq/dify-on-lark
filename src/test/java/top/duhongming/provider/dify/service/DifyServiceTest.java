package top.duhongming.provider.dify.service;

import io.github.imfangs.dify.client.DifyClient;
import io.github.imfangs.dify.client.callback.ChatflowStreamCallback;
import io.github.imfangs.dify.client.model.chat.AppInfoResponse;
import io.github.imfangs.dify.client.model.chat.SuggestedQuestionsResponse;
import io.github.imfangs.dify.client.model.common.SimpleResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import top.duhongming.platform.lark.service.SendMessageCardService;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DifyServiceTest {

    @Mock
    private DifyClient difyClient;

    @Mock
    private SendMessageCardService sendMessageCardService;

    @InjectMocks
    private DifyService difyService;

    @BeforeEach
    void setUp() {
        // Mockito自动初始化mock对象
    }

    @Test
    void testInfo() throws Exception {
        AppInfoResponse expectedResponse = new AppInfoResponse();
        when(difyClient.getAppInfo()).thenReturn(expectedResponse);
        
        AppInfoResponse result = difyService.info();
        
        assertNotNull(result);
        assertEquals(expectedResponse, result);
        verify(difyClient, times(1)).getAppInfo();
    }

    @Test
    void testSuggestedSuccess() throws Exception {
        String messageId = "test_message_id";
        String user = "test_user";
        SuggestedQuestionsResponse expectedResponse = new SuggestedQuestionsResponse();
        List<String> questions = Arrays.asList("Question 1", "Question 2", "Question 3");
        expectedResponse.setData(questions);
        
        when(difyClient.getSuggestedQuestions(messageId, user)).thenReturn(expectedResponse);
        
        SuggestedQuestionsResponse result = difyService.suggested(messageId, user);
        
        assertNotNull(result);
        assertEquals(expectedResponse, result);
        verify(difyClient, times(1)).getSuggestedQuestions(messageId, user);
    }

    @Test
    void testSuggestedWithIOException() throws Exception {
        String messageId = "test_message_id";
        String user = "test_user";
        
        when(difyClient.getSuggestedQuestions(messageId, user))
                .thenThrow(new IOException("Network error"));
        
        SuggestedQuestionsResponse result = difyService.suggested(messageId, user);
        
        assertNull(result);
        verify(difyClient, times(1)).getSuggestedQuestions(messageId, user);
    }

    @Test
    void testFeedbackMessage() throws Exception {
        String messageId = "test_message_id";
        String rating = "like";
        String user = "test_user";
        SimpleResponse expectedResponse = new SimpleResponse();
        
        when(difyClient.feedbackMessage(messageId, rating, user, "")).thenReturn(expectedResponse);
        
        SimpleResponse result = difyService.feedbackMessage(messageId, rating, user);
        
        assertNotNull(result);
        assertEquals(expectedResponse, result);
        verify(difyClient, times(1)).feedbackMessage(messageId, rating, user, "");
    }

    @Test
    void testChatMessages() throws Exception {
        String query = "test query";
        String cardId = "test_card_id";
        String user = "test_user";
        
        // 使用ArgumentCaptor捕获ChatflowStreamCallback
        ArgumentCaptor<ChatflowStreamCallback> callbackCaptor = 
                ArgumentCaptor.forClass(ChatflowStreamCallback.class);
        
        // 执行方法
        difyService.chatMessages(query, cardId, user);
        
        // 验证sendChatMessageStream被调用
        verify(difyClient, times(1)).sendChatMessageStream(any(), callbackCaptor.capture());
        
        // 获取捕获的callback
        ChatflowStreamCallback callback = callbackCaptor.getValue();
        assertNotNull(callback);
    }
}

