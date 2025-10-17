package top.duhongming.platform.lark.service;

import com.lark.oapi.Client;
import com.lark.oapi.core.utils.Jsons;
import com.lark.oapi.service.im.v1.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.duhongming.utils.BeanUtils;

import java.util.HashMap;
import java.util.Map;

import static top.duhongming.common.Constants.ID_TEMPLATE;
import static top.duhongming.platform.lark.config.LarkConfig.consoleLog;

/**
 * 发送文本消息服务
 *
 * @author duhongming
 * @see
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SendMessageTextService {

    /**
     * 发送消息: https://open.feishu.cn/document/server-docs/im-v1/message/create
     *
     * @param eventMessage
     * @param eventSender
     * @return
     * @throws Exception
     */
    public CreateMessageRespBody sendMsg(EventMessage eventMessage, EventSender eventSender) throws Exception {

        Map<String, String> map = new HashMap<>();
        UserId senderId = eventSender.getSenderId();
        // 方式2：使用 String.format()
        String respMsg = String.format(ID_TEMPLATE, senderId.getUserId(),
                eventMessage.getChatId(),
                senderId.getUserId(),
                senderId.getOpenId(),
                senderId.getUnionId()
        );
        map.put("text", respMsg);
        CreateMessageReq req = CreateMessageReq.newBuilder()
                .receiveIdType("chat_id")
                .createMessageReqBody(CreateMessageReqBody.newBuilder()
                        .receiveId(eventMessage.getChatId())
                        .msgType("text")
                        .content(Jsons.DEFAULT.toJson(map))
                        .build())
                .build();
        // 发起请求
        CreateMessageResp resp = BeanUtils.getBean(Client.class).im().v1().message().create(req);
        consoleLog(resp);
        return resp.getData();
    }

}
