package top.duhongming.platform.lark.config;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lark.oapi.Client;
import com.lark.oapi.core.request.EventReq;
import com.lark.oapi.core.response.BaseResponse;
import com.lark.oapi.core.response.EmptyData;
import com.lark.oapi.core.utils.Jsons;
import com.lark.oapi.event.CustomEventHandler;
import com.lark.oapi.event.EventDispatcher;
import com.lark.oapi.event.cardcallback.P2CardActionTriggerHandler;
import com.lark.oapi.event.cardcallback.model.P2CardActionTrigger;
import com.lark.oapi.event.cardcallback.model.P2CardActionTriggerResponse;
import com.lark.oapi.service.im.ImService;
import com.lark.oapi.service.im.v1.model.*;
import io.github.imfangs.dify.client.model.chat.AppInfoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.duhongming.platform.lark.service.ImageService;
import top.duhongming.platform.lark.service.SendMessageCardService;
import top.duhongming.platform.lark.service.SendMessageTextService;
import top.duhongming.provider.dify.service.DifyService;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

/**
 * 飞书配置
 *
 * @author duhongming
 * @see
 * @since 1.0.0
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class LarkConfig {
    private final SendMessageCardService sendMessageCardService;
    private final SendMessageTextService sendMessageTextService;
    private final DifyService difyService;
    private final ImageService imageService;

    /**
     * 构建 API Client
     *
     * @return
     */
    @Bean("larkClient")
    public Client larkClient(LarkProperties larkProperties) {
        return Client.newBuilder(larkProperties.getAppId(), larkProperties.getAppSecret()) // 默认配置为自建应用
                .logReqAtDebug(true) // 在 debug 模式下会打印 http 请求和响应的 headers、body 等信息。
                .build();
    }

    /**
     * https://open.feishu.cn/document/server-side-sdk/java-sdk-guide/handle-events
     * <p>
     *
     * @return
     */
    @Bean
    public EventDispatcher eventDispatcher(LarkProperties larkProperties) {
        return EventDispatcher.newBuilder(larkProperties.getVerificationToken(), larkProperties.getEncryptKey())
                // https://open.feishu.cn/document/server-docs/im-v1/message/events/receive
                .onP2MessageReceiveV1(new ImService.P2MessageReceiveV1Handler() {
                    @Override
                    public void handle(P2MessageReceiveV1 p2MessageReceiveV1) throws Exception {
                        P2MessageReceiveV1Data event = p2MessageReceiveV1.getEvent();
                        EventMessage eventMessage = event.getMessage();
                        EventSender eventSender = event.getSender();
                        String messageType = eventMessage.getMessageType();

                        MentionEvent[] mentions = eventMessage.getMentions();
                        if (StringUtils.equals(eventMessage.getChatType(), "group") || StringUtils.equals(eventMessage.getChatType(), "topic_group")) {
                            //接收群聊中@机器人消息事件-没有@该机器人时为空
                            if (Objects.isNull(mentions)) {
                                return;
                            }
                            Boolean isAt = false;
                            for (MentionEvent mention : mentions) {
                                if (StringUtils.equals(larkProperties.getAppName(), mention.getName())) {
                                    isAt = true;
                                }
                            }
                            if (!isAt) {
                                return;
                            }
                        }
                        log.info("[ onP2MessageReceiveV1 access ], data: {}\n", Jsons.DEFAULT.toJson(p2MessageReceiveV1));
                        String text = "";
                        //https://open.feishu.cn/document/server-docs/im-v1/message-content-description/message_content#3c92befd
                        if (messageType.equals("text")) {
                            String content = eventMessage.getContent();
                            JsonObject jsonObject = Jsons.DEFAULT.fromJson(content, JsonObject.class);
                            text = jsonObject.get("text").getAsString().replaceAll("@_user_[\\d]+", "").trim();
                        } else if (messageType.equals("post")) {
                            text = eventMessage.getContent();
                        } else if (messageType.equals("image")) {
                            String imageContent = eventMessage.getContent();
                            JsonObject jsonObject = Jsons.DEFAULT.fromJson(imageContent, JsonObject.class);
                            String imageKey = jsonObject.get("image_key").getAsString();
                            imageService.downloadImage(imageKey, "./" + imageKey + ".jpg");
                        }

                        if (StringUtils.equals(eventMessage.getChatType(), "group") && StringUtils.equals(text, "id")) {
                            //指令id
                            sendMessageTextService.sendMsg(eventMessage, eventSender);
                        } else {
                            AppInfoResponse appInfoResponse = difyService.info();
                            String cardId = sendMessageCardService.createCardByAutoAndReply(event, appInfoResponse);
                            difyService.chatMessages(text, cardId, eventSender.getSenderId().getUserId());
                        }
                    }
                })
                .onP2CardActionTrigger(new P2CardActionTriggerHandler() {
                                           @Override
                                           public P2CardActionTriggerResponse handle(P2CardActionTrigger event) {
                                               log.info("[ onP2CardActionTrigger access ], data: {}\n", Jsons.DEFAULT.toJson(event));
                                               Map<String, Object> value = event.getEvent().getAction().getValue();
                                               String messageId = value.get("messageId").toString();
                                               String user = value.get("user").toString();
                                               String cardId = value.get("cardId").toString();

                                               long now = System.currentTimeMillis() / 1000;
                                               if (value.get("action").equals("like")) {
                                                   Boolean thumbsup = !Boolean.parseBoolean(value.get("thumbsup").toString());
                                                   difyService.feedbackMessage(messageId, thumbsup ? "like" : null, user);
                                                   sendMessageCardService.like(cardId, user, messageId, thumbsup, (int) now++);
                                                   if (thumbsup) {
                                                       sendMessageCardService.dislike(cardId, user, messageId, false, (int) now++);
                                                   }
                                               }
                                               if (value.get("action").equals("dislike")) {
                                                   Boolean thumbdown = !Boolean.parseBoolean(value.get("thumbdown").toString());
                                                   difyService.feedbackMessage(messageId, thumbdown ? "dislike" : null, user);
                                                   sendMessageCardService.dislike(cardId, user, messageId, thumbdown, (int) now++);
                                                   if (thumbdown) {
                                                       sendMessageCardService.like(cardId, user, messageId, false, (int) now);
                                                   }
                                               }
                                               P2CardActionTriggerResponse resp = new P2CardActionTriggerResponse();
                                               return resp;
                                           }
                                       }
                )
                .onP2ChatAccessEventBotP2pChatEnteredV1(new ImService.P2ChatAccessEventBotP2pChatEnteredV1Handler() {
                    @Override
                    public void handle(P2ChatAccessEventBotP2pChatEnteredV1 event) throws Exception {
                        log.info("[ onP2ChatAccessEventBotP2pChatEnteredV1 access ], data: {}\n", Jsons.DEFAULT.toJson(event.getEvent()));
                    }
                })
                .onCustomizedEvent("这里填入你要自定义订阅的 event 的 key，例如 out_approval", new CustomEventHandler() {
                    @Override
                    public void handle(EventReq event) throws Exception {
                        log.info("[ onCustomizedEvent access ], type: message, data: {}\n", new String(event.getBody(), StandardCharsets.UTF_8));
                    }
                })
                .build();
    }

    @Bean("wsClient")
    public com.lark.oapi.ws.Client wsClient(EventDispatcher eventDispatcher, LarkProperties larkProperties) {
        return new com.lark.oapi.ws.Client.Builder(larkProperties.getAppId(), larkProperties.getAppSecret())
                .eventHandler(eventDispatcher).build();
    }

    public static void consoleLog(BaseResponse resp) {
        // 处理服务端错误
        if (!resp.success()) {
            log.error(String.format("code:%s,msg:%s,reqId:%s, resp:%s",
                    resp.getCode(), resp.getMsg(), resp.getRequestId(), Jsons.createGSON(true, false).toJson(JsonParser.parseString(new String(resp.getRawResponse().getBody(), StandardCharsets.UTF_8)))));
            return;
        }

        // 业务数据处理
        if (resp.getData() instanceof EmptyData) {
            return;
        }
        log.info(Jsons.DEFAULT.toJson(resp.getData()));
    }
}
