package top.duhongming.provider.dify.service;

import io.github.imfangs.dify.client.DifyClient;
import io.github.imfangs.dify.client.callback.ChatflowStreamCallback;
import io.github.imfangs.dify.client.enums.ResponseMode;
import io.github.imfangs.dify.client.event.*;
import io.github.imfangs.dify.client.exception.DifyApiException;
import io.github.imfangs.dify.client.model.chat.AppInfoResponse;
import io.github.imfangs.dify.client.model.chat.ChatMessage;
import io.github.imfangs.dify.client.model.chat.SuggestedQuestionsResponse;
import io.github.imfangs.dify.client.model.common.Metadata;
import io.github.imfangs.dify.client.model.common.RetrieverResource;
import io.github.imfangs.dify.client.model.common.SimpleResponse;
import io.github.imfangs.dify.client.model.common.Usage;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import top.duhongming.platform.lark.service.SendMessageCardService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static top.duhongming.common.Constants.*;

/**
 * Dify服务
 *
 * @author duhongming
 * @see
 * @since 1.0.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DifyService {

    private final DifyClient difyClient;

    private final SendMessageCardService sendMessageCardService;

    /**
     * 获取应用基本信息
     *
     * @return
     */
    @SneakyThrows
    public AppInfoResponse info() {
        return difyClient.getAppInfo();
    }

    /**
     * 获取下一轮建议问题列表
     *
     * @param messageId
     * @param user
     * @return
     */
    public SuggestedQuestionsResponse suggested(String messageId, String user) {
        try {
            return difyClient.getSuggestedQuestions(messageId, user);
        } catch (IOException | DifyApiException e) {
            return null;
        }
    }

    /**
     * 消息反馈（点赞）
     *
     * @param messageId
     * @param rating
     * @param user
     * @return
     */
    @SneakyThrows
    public SimpleResponse feedbackMessage(String messageId, String rating, String user) {
        return difyClient.feedbackMessage(messageId, rating, user, "");
    }

    @SneakyThrows
    public void chatMessages(String query, String cardId, String user) {
        HashMap<String, Object> inputs = new HashMap<>();
        inputs.put("project", "电检维修助手");
        // 创建聊天消息
        ChatMessage message = ChatMessage.builder()
                .query(query)
                .user(user)
                .inputs(inputs)
                .responseMode(ResponseMode.STREAMING)
                .build();


        // 发送流式消息
        difyClient.sendChatMessageStream(message, new ChatflowStreamCallback() {
            int sequence = 1;
            final StringBuffer content = new StringBuffer();
            long lastTime = System.currentTimeMillis();

            @Override
            public void onMessage(MessageEvent event) {
                log.info("onMessage: {}", event);
                handleAnswer(event.getAnswer());
            }

            @Override
            public void onAgentMessage(AgentMessageEvent event) {
                log.info("onAgentMessage: {}", event);
                handleAnswer(event.getAnswer());
            }

            @Override
            public void onMessageEnd(MessageEndEvent event) {
                log.info("onMessageEnd: {}", event);
                //更新模型回答
                String messageId = event.getMessageId();
                content.append("\n");
                content.append("<at id=").append(user).append("></at>");
                sendMessageCardService.updateCardMsg(cardId, CARD_ELEMENT_ID_STREAMING_ANSWER, content.toString(), sequence++);

                //更新相关问题
                SuggestedQuestionsResponse suggestedQuestionsResponse = suggested(messageId, user);
                List<String> suggestedQuestionsList = suggestedQuestionsResponse.getData();
                StringBuilder suggestedContent = new StringBuilder();
                for (int i = 0; i < suggestedQuestionsList.size(); i++) {
                    suggestedContent.append("<number_tag>").append(i + 1).append("</number_tag>").append(suggestedQuestionsList.get(i)).append("\n");
                }
                if (StringUtils.isNotBlank(suggestedContent.toString())) {
                    sendMessageCardService.updateCardMsg(cardId, CARD_ELEMENT_ID_STREAMING_QUESTION, suggestedContent.toString(), sequence++);
                }

                //更新相关知识库引用
                StringBuilder retrieverContent = new StringBuilder();
                Optional<List<RetrieverResource>> optional = Optional.ofNullable(event).map(MessageEndEvent::getMetadata).map(Metadata::getRetrieverResources);
                if (optional.isPresent()) {
                    List<RetrieverResource> retrieverResources = optional.get();
                    for (int i = 0; i < retrieverResources.size(); i++) {
                        RetrieverResource retrieverResource = retrieverResources.get(i);
                        retrieverContent.append("<number_tag>").append(i + 1).append("</number_tag>").append(retrieverResource.getDocumentName()).append("\n");
                    }
                    if (StringUtils.isNotBlank(retrieverContent.toString())) {
                        sendMessageCardService.updateCardMsg(cardId, CARD_ELEMENT_ID_STREAMING_DOCUMENT, retrieverContent.toString(), sequence++);
                    }
                }

                //更新使用token和耗时统计
                Metadata metadata = event.getMetadata();
                Usage usage = metadata.getUsage();
                String usageContent = "<font color=\"grey-600\">提示词token:" + usage.getPromptTokens() + ", " +
                        "回答token:" + usage.getCompletionTokens() + ", " +
                        "总token:" + usage.getTotalTokens() + ", " +
                        "耗时:" + String.format("%.2f", usage.getLatency()) + "s</font>";
                sendMessageCardService.updateCardMsg(cardId, CARD_ELEMENT_ID_STREAMING_USAGE, usageContent, sequence++);

                //更新消息反馈
                sendMessageCardService.like(cardId, user, messageId, false, sequence++);
                sendMessageCardService.dislike(cardId, user, messageId, false, sequence++);
            }

            @Override
            public void onWorkflowStarted(WorkflowStartedEvent event) {
                log.info("onWorkflowStarted: {}", event);
            }

            @Override
            public void onNodeStarted(NodeStartedEvent event) {
                log.info("onNodeStarted: {}", event);
            }

            @Override
            public void onNodeFinished(NodeFinishedEvent event) {
                log.info("onNodeFinished: {}", event);
            }

            @Override
            public void onWorkflowFinished(WorkflowFinishedEvent event) {
                log.info("onWorkflowFinished: {}", event);
            }

            @Override
            public void onIterationStarted(IterationStartedEvent event) {
                log.info("onIterationStarted: {}", event);
            }

            @Override
            public void onIterationNext(IterationNextEvent event) {
                log.info("onIterationNext: {}", event);
            }

            @Override
            public void onIterationCompleted(IterationCompletedEvent event) {
                log.info("onIterationCompleted: {}", event);
            }

            @Override
            public void onLoopStarted(LoopStartedEvent event) {
                log.info("onLoopStarted: {}", event);
            }

            @Override
            public void onLoopNext(LoopNextEvent event) {
                log.info("onLoopNext: {}", event);
            }

            @Override
            public void onLoopCompleted(LoopCompletedEvent event) {
                log.info("onLoopCompleted: {}", event);
            }

            @Override
            public void onPing(PingEvent event) {
                log.info("onPing: {}", event);
            }

            @Override
            public void onMessageFile(MessageFileEvent event) {
                log.info("onMessageFile: {}", event);
            }

            @Override
            public void onTTSMessage(TtsMessageEvent event) {
                log.info("onTTSMessage: {}", event);
            }

            @Override
            public void onTTSMessageEnd(TtsMessageEndEvent event) {
                log.info("onTTSMessageEnd: {}", event);
            }

            @Override
            public void onMessageReplace(MessageReplaceEvent event) {
                log.info("onMessageReplace: {}", event);
            }


            @Override
            public void onAgentThought(AgentThoughtEvent event) {
                log.info("onAgentThought: {}", event);
            }

            @Override
            public void onAgentLog(AgentLogEvent event) {
                log.info("onAgentLog: {}", event);
            }

            @Override
            public void onError(ErrorEvent event) {
                log.error("onError: {}", event.getMessage());
            }

            @Override
            public void onException(Throwable throwable) {
                log.error("onException: {}", throwable.getMessage());
            }

            private void handleAnswer(String answer) {
                content.append(answer);
                sequence++;

                long now = System.currentTimeMillis();

                if (now - lastTime > 500) {
                    lastTime = now;
                    sendMessageCardService.updateCardMsg(cardId, CARD_ELEMENT_ID_STREAMING_ANSWER, content.toString(), sequence++);
                }
            }
        });
    }
}
