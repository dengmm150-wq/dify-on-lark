package top.duhongming.provider.dify.service;

import io.github.imfangs.dify.client.DifyClient;
import io.github.imfangs.dify.client.callback.ChatflowStreamCallback;
import io.github.imfangs.dify.client.event.*;
import io.github.imfangs.dify.client.exception.DifyApiException;
import io.github.imfangs.dify.client.model.chat.AppInfoResponse;
import io.github.imfangs.dify.client.model.chat.SuggestedQuestionsResponse;
import io.github.imfangs.dify.client.model.common.Metadata;
import io.github.imfangs.dify.client.model.common.RetrieverResource;
import io.github.imfangs.dify.client.model.common.SimpleResponse;
import io.github.imfangs.dify.client.model.common.Usage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import top.duhongming.platform.lark.service.SendMessageCardService;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;

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
    void testSuggestedWithDifyApiException() throws Exception {
        String messageId = "test_message_id";
        String user = "test_user";

        when(difyClient.getSuggestedQuestions(messageId, user))
                .thenThrow(new DifyApiException(500, "Dify API error", "Internal Server Error"));

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

        // 测试onMessage事件
        MessageEvent messageEvent = new MessageEvent();
        messageEvent.setAnswer("Test answer part 1");
        callback.onMessage(messageEvent);

        // 测试onAgentMessage事件
        AgentMessageEvent agentMessageEvent = new AgentMessageEvent();
        agentMessageEvent.setAnswer("Test answer part 2");
        callback.onAgentMessage(agentMessageEvent);

        // 测试onWorkflowStarted事件
        callback.onWorkflowStarted(new WorkflowStartedEvent());

        // 测试onNodeStarted事件
        callback.onNodeStarted(new NodeStartedEvent());

        // 测试onNodeFinished事件
        callback.onNodeFinished(new NodeFinishedEvent());

        // 测试onWorkflowFinished事件
        WorkflowFinishedEvent workflowFinishedEvent = new WorkflowFinishedEvent();
        WorkflowFinishedEvent.WorkflowFinishedData data = new WorkflowFinishedEvent.WorkflowFinishedData();
        Map<String, Object> outputs = new HashMap<>();
        outputs.put("answer", "Test workflow answer");
        data.setOutputs(outputs);
        workflowFinishedEvent.setData(data);
        callback.onWorkflowFinished(workflowFinishedEvent);

        // 测试onError事件
        ErrorEvent errorEvent = new ErrorEvent();
        errorEvent.setMessage("Test error");
        callback.onError(errorEvent);

        // 测试onException事件
        callback.onException(new RuntimeException("Test exception"));
    }

    @Test
    void testChatMessagesOnMessageEnd() throws Exception {
        String query = "test query";
        String cardId = "test_card_id";
        String user = "test_user";
        String messageId = "test_message_id";

        // Mock suggested方法的返回值
        SuggestedQuestionsResponse suggestedResponse = new SuggestedQuestionsResponse();
        suggestedResponse.setData(Arrays.asList("Suggested question 1", "Suggested question 2"));
        when(difyClient.getSuggestedQuestions(anyString(), anyString())).thenReturn(suggestedResponse);

        // 使用ArgumentCaptor捕获ChatflowStreamCallback
        ArgumentCaptor<ChatflowStreamCallback> callbackCaptor =
                ArgumentCaptor.forClass(ChatflowStreamCallback.class);

        // 执行方法
        difyService.chatMessages(query, cardId, user);

        // 验证sendChatMessageStream被调用
        verify(difyClient, times(1)).sendChatMessageStream(any(), callbackCaptor.capture());

        // 获取捕获的callback
        ChatflowStreamCallback callback = callbackCaptor.getValue();

        // 创建MessageEndEvent并设置相关属性
        MessageEndEvent messageEndEvent = new MessageEndEvent();
        messageEndEvent.setMessageId(messageId);

        // 设置metadata和retrieverResources
        Metadata metadata = new Metadata();
        RetrieverResource resource1 = new RetrieverResource();
        resource1.setDocumentName("Document 1");
        RetrieverResource resource2 = new RetrieverResource();
        resource2.setDocumentName("Document 2");
        metadata.setRetrieverResources(Arrays.asList(resource1, resource2));

        // 设置usage
        Usage usage = new Usage();
        usage.setPromptTokens(100);
        usage.setCompletionTokens(200);
        usage.setTotalTokens(300);
        usage.setLatency(1.5);
        metadata.setUsage(usage);

        messageEndEvent.setMetadata(metadata);

        // 触发onMessageEnd事件
        callback.onMessageEnd(messageEndEvent);

        // 验证各种方法被调用
        verify(sendMessageCardService, atLeastOnce()).updateCardMsg(anyString(), anyString(), anyString(), anyInt());
        verify(sendMessageCardService, times(1)).like(anyString(), anyString(), anyString(), anyBoolean(), anyInt());
        verify(sendMessageCardService, times(1)).dislike(anyString(), anyString(), anyString(), anyBoolean(), anyInt());
    }

    @Test
    void testUpdateRetrieverResource() throws Exception {
        // 使用反射测试私有方法
        Method method = DifyService.class.getDeclaredMethod("updateRetrieverResource",
                MessageEndEvent.class, String.class, Integer.class);
        method.setAccessible(true);

        // 创建测试数据
        MessageEndEvent event = new MessageEndEvent();
        Metadata metadata = new Metadata();
        RetrieverResource resource = new RetrieverResource();
        resource.setDocumentName("Test Document");
        metadata.setRetrieverResources(Collections.singletonList(resource));
        event.setMetadata(metadata);

        String cardId = "test_card_id";
        Integer sequence = 1;

        // 执行私有方法
        method.invoke(difyService, event, cardId, sequence);

        // 验证方法被调用
        verify(sendMessageCardService, times(1)).updateCardMsg(cardId,
                "streaming_document", "<number_tag>1</number_tag>Test Document\n", sequence);
    }

    @Test
    void testUpdateRetrieverResourceWithEmptyList() throws Exception {
        // 使用反射测试私有方法
        Method method = DifyService.class.getDeclaredMethod("updateRetrieverResource",
                MessageEndEvent.class, String.class, Integer.class);
        method.setAccessible(true);

        // 创建测试数据（空retrieverResources列表）
        MessageEndEvent event = new MessageEndEvent();
        Metadata metadata = new Metadata();
        metadata.setRetrieverResources(Collections.emptyList());
        event.setMetadata(metadata);

        String cardId = "test_card_id";
        Integer sequence = 1;

        // 执行私有方法
        method.invoke(difyService, event, cardId, sequence);

        // 验证updateCardMsg没有被调用
        verify(sendMessageCardService, never()).updateCardMsg(eq(cardId),
                eq("streaming_document"), anyString(), eq(sequence));
    }

    @Test
    void testUpdateSuggested() throws Exception {
        // Mock suggested方法的返回值
        String messageId = "test_message_id";
        String user = "test_user";
        String cardId = "test_card_id";
        Integer sequence = 1;

        SuggestedQuestionsResponse response = new SuggestedQuestionsResponse();
        response.setData(Arrays.asList("Question 1", "Question 2"));
        when(difyClient.getSuggestedQuestions(messageId, user)).thenReturn(response);

        // 使用反射测试私有方法
        Method method = DifyService.class.getDeclaredMethod("updateSuggested",
                String.class, String.class, String.class, Integer.class);
        method.setAccessible(true);

        // 执行私有方法
        method.invoke(difyService, messageId, user, cardId, sequence);

        // 验证方法被调用
        String expectedContent = "<number_tag>1</number_tag>Question 1\n<number_tag>2</number_tag>Question 2\n";
        verify(sendMessageCardService, times(1)).updateCardMsg(cardId,
                "streaming_question", expectedContent, sequence);
    }

    @Test
    void testUpdateUsage() throws Exception {
        // 使用反射测试私有方法
        Method method = DifyService.class.getDeclaredMethod("updateUsage",
                MessageEndEvent.class, String.class, Integer.class);
        method.setAccessible(true);

        // 创建测试数据
        MessageEndEvent event = new MessageEndEvent();
        Metadata metadata = new Metadata();
        Usage usage = new Usage();
        usage.setPromptTokens(100);
        usage.setCompletionTokens(200);
        usage.setTotalTokens(300);
        usage.setLatency(1.5);
        metadata.setUsage(usage);
        event.setMetadata(metadata);

        String cardId = "test_card_id";
        Integer sequence = 1;

        // 执行私有方法
        method.invoke(difyService, event, cardId, sequence);

        // 验证方法被调用
        String expectedContent = "<font color=\"grey-600\">提示词token:100, 回答token:200, 总token:300, 耗时:1.50s</font>";
        verify(sendMessageCardService, times(1)).updateCardMsg(cardId,
                "streaming_usage", expectedContent, sequence);
    }
}