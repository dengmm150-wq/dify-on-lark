package top.duhongming.platform.lark.service;

import com.lark.oapi.Client;
import com.lark.oapi.core.utils.Jsons;
import com.lark.oapi.service.cardkit.v1.model.*;
import com.lark.oapi.service.im.v1.model.*;
import io.github.imfangs.dify.client.model.chat.AppInfoResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import top.duhongming.CardDTO;
import top.duhongming.platform.lark.config.LarkProperties;
import top.duhongming.utils.BeanUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static top.duhongming.common.Constants.*;
import static top.duhongming.platform.lark.config.LarkConfig.consoleLog;

/**
 * 发送卡片消息服务
 *
 * @author duhongming
 * @see
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SendMessageCardService {
    private final LarkProperties larkProperties;


    /**
     * 自动创建卡片并回复
     * 1.创建卡片实体 2.回复卡片消息 3.发送卡片消息
     *
     * @param event
     * @param appInfoResponse
     * @return
     * @throws Exception
     */
    public String createCardByAutoAndReply(P2MessageReceiveV1Data event, AppInfoResponse appInfoResponse) throws Exception {
        EventMessage eventMessage = event.getMessage();
        String messageId = eventMessage.getMessageId();
        String cardId;
        //如果配置中cardTemplateId不存在，则使用内置Card
        if (StringUtils.isBlank(larkProperties.getCardTemplateId())) {
            cardId = createCardByCardJson(appInfoResponse);
        } else {
            cardId = createCardByTemplate(appInfoResponse);
        }
        this.replyCardMsg(messageId, cardId);
        return cardId;
    }

    /**
     * 1.调用创建卡片实体接口
     * 流式更新卡片: https://open.feishu.cn/document/cardkit-v1/streaming-updates-openapi-overview#-9c98aa9
     * 创建卡片实体: https://open.feishu.cn/document/cardkit-v1/card/create
     *
     * @return
     * @throws Exception
     */
    public String createCardByCardJson(AppInfoResponse appInfoResponse) throws Exception {
        String cardJson = CARD_JSON.replace("${name}", appInfoResponse.getName())
                .replace("${description}", appInfoResponse.getDescription())
                .replace("${answer}", "正在回答中......")
                .replace("${question}", "暂无相关问题")
                .replace("${document}", "暂无相关文档");

        // 创建请求对象
        CreateCardReq req = CreateCardReq.newBuilder()
                .createCardReqBody(CreateCardReqBody.newBuilder()
                        .type("card_json")
                        .data(cardJson)
                        .build())
                .build();

        // 发起请求
        CreateCardResp resp = BeanUtils.getBean(Client.class).cardkit().v1().card().create(req);

        consoleLog(resp);
        return resp.getData().getCardId();
    }

    /**
     * 1.调用创建卡片实体接口
     * 流式更新卡片: https://open.feishu.cn/document/cardkit-v1/streaming-updates-openapi-overview#-9c98aa9
     * 创建卡片实体: https://open.feishu.cn/document/cardkit-v1/card/create
     *
     * @return
     * @throws Exception
     */
    public String createCardByTemplate(AppInfoResponse appInfoResponse) throws Exception {
        CardDTO cardDTO = new CardDTO();
        cardDTO.setTemplate_id(larkProperties.getCardTemplateId());
        Map<String, Object> variable = new HashMap<>();
        variable.put("name", appInfoResponse.getName());
        variable.put("description", appInfoResponse.getDescription());
        variable.put("answer", "正在回答中......");
        variable.put("question", "暂无相关问题");
        variable.put("document", "暂无相关文档");
        cardDTO.setTemplate_variable(variable);
        // 创建请求对象
        CreateCardReq req = CreateCardReq.newBuilder()
                .createCardReqBody(CreateCardReqBody.newBuilder()
                        .type("template")
                        .data(Jsons.DEFAULT.toJson(cardDTO))
                        .build())
                .build();

        // 发起请求
        CreateCardResp resp = BeanUtils.getBean(Client.class).cardkit().v1().card().create(req);

        consoleLog(resp);
        return resp.getData().getCardId();
    }

    /**
     * 2.调用回复消息接口
     * 回复消息: https://open.feishu.cn/document/server-docs/im-v1/message/reply
     *
     * @param messageId
     * @param cardId
     * @throws Exception
     */
    public void replyCardMsg(String messageId, String cardId) throws Exception {
        // 创建请求对象
        ReplyMessageReq req = ReplyMessageReq.newBuilder()
                .messageId(messageId)
                .replyMessageReqBody(ReplyMessageReqBody.newBuilder()
                        .content("{\"type\":\"card\",\"data\":{\"card_id\":\"" + cardId + "\"}}")
                        .msgType("interactive")
                        .replyInThread(false)
                        .uuid(UUID.randomUUID().toString())
                        .build())
                .build();

        // 发起请求
        ReplyMessageResp resp = BeanUtils.getBean(Client.class).im().v1().message().reply(req);

        consoleLog(resp);
    }

    /**
     * 3.调用发送消息接口
     * 流式更新卡片: https://open.feishu.cn/document/cardkit-v1/streaming-updates-openapi-overview#-9c98aa9
     * 发送消息: https://open.feishu.cn/document/server-docs/im-v1/message/create
     *
     * @param cardId
     * @return
     * @throws Exception
     */
    public void sendCardMsg(String cardId, String receiveIdType, String receiveId) throws Exception {
        // 创建请求对象
        CreateMessageReq req = CreateMessageReq.newBuilder()
                .receiveIdType(receiveIdType)
                .createMessageReqBody(CreateMessageReqBody.newBuilder()
                        .receiveId(receiveId)
                        .msgType("interactive")
                        .content("{\"type\":\"card\",\"data\":{\"card_id\":\"" + cardId + "\"}}")
                        .uuid(UUID.randomUUID().toString())
                        .build())
                .build();

        // 发起请求
        CreateMessageResp resp = BeanUtils.getBean(Client.class).im().v1().message().create(req);

        consoleLog(resp);
    }


    /**
     * 4.调用流式更新文本接口
     * 流式更新卡片: https://open.feishu.cn/document/cardkit-v1/streaming-updates-openapi-overview#90f1d897
     * 流式更新文本: https://open.feishu.cn/document/cardkit-v1/card-element/content
     *
     * @param cardId
     * @param content
     * @throws Exception
     */
    @SneakyThrows
    public void updateCardMsg(String cardId, String elementId, String content, Integer sequence) {
        // 创建请求对象
        ContentCardElementReq req = ContentCardElementReq.newBuilder()
                .cardId(cardId)
                .elementId(elementId)
                .contentCardElementReqBody(ContentCardElementReqBody.newBuilder()
                        .uuid(UUID.randomUUID().toString())
                        .content(content)
                        .sequence(sequence)
                        .build())
                .build();

        // 发起请求
        ContentCardElementResp resp = BeanUtils.getBean(Client.class).cardkit().v1().cardElement().content(req);

        consoleLog(resp);
    }

    /**
     * 5.调用更新组件接口
     * 更新组件: https://open.feishu.cn/document/cardkit-v1/card-element/update
     */
    @SneakyThrows
    public void updateCardElement(String cardId, String elementId, String element, Integer sequence) {
        // 创建请求对象
        UpdateCardElementReq req = UpdateCardElementReq.newBuilder()
                .cardId(cardId)
                .elementId(elementId)
                .updateCardElementReqBody(UpdateCardElementReqBody.newBuilder()
                        .uuid(UUID.randomUUID().toString())
                        .element(element)
                        .sequence(sequence)
                        .build())
                .build();

        // 发起请求
        UpdateCardElementResp resp = BeanUtils.getBean(Client.class).cardkit().v1().cardElement().update(req);

        consoleLog(resp);
    }

    public void like(String cardId, String user, String messageId, Boolean thumbsup, Integer sequence) {
        this.updateCardElement(cardId, "like", ENABLE_LIKE_BUTTON
                .replace("${user}", user)
                .replace("${messageId}", messageId)
                .replace("${cardId}", cardId)
                .replace("${thumbsup}", String.valueOf(thumbsup))
                .replace("${icon}", thumbsup ? "filled" : "outlined"), sequence);
    }

    public void dislike(String cardId, String user, String messageId, Boolean thumbdown, Integer sequence) {
        this.updateCardElement(cardId, "dislike", ENABLE_DISLIKE_BUTTON
                .replace("${user}", user)
                .replace("${messageId}", messageId)
                .replace("${cardId}", cardId)
                .replace("${thumbdown}", String.valueOf(thumbdown))
                .replace("${icon}", thumbdown ? "filled" : "outlined"), sequence);
    }

}
