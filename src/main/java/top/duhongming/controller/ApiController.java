package top.duhongming.controller;

import com.lark.oapi.service.im.v1.model.ListChat;
import com.lark.oapi.service.im.v1.model.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import top.duhongming.platform.lark.service.SendMessageTextService;
import top.duhongming.utils.TimeRangeSecondUtils;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ApiController {
    private final SendMessageTextService sendMessageTextService;

    @GetMapping("/api/v1/chat/history")
    public List<Message> chatHistory(String chatId, String sortType, String startTime, String endTime, String timeType) throws Exception {
        if (StringUtils.equals(timeType, "day")) {
            startTime = String.valueOf(TimeRangeSecondUtils.getYesterdayStartTime());
            endTime = String.valueOf(TimeRangeSecondUtils.getYesterdayEndTime());
        } else if (StringUtils.equals(timeType, "week")) {
            startTime = String.valueOf(TimeRangeSecondUtils.getLastWeekStartTime());
            endTime = String.valueOf(TimeRangeSecondUtils.getLastWeekEndTime());
        } else if (StringUtils.equals(timeType, "month")) {
            startTime = String.valueOf(TimeRangeSecondUtils.getLastMonthStartTime());
            endTime = String.valueOf(TimeRangeSecondUtils.getLastMonthEndTime());
        } else if (StringUtils.equals(timeType, "year")) {
            startTime = String.valueOf(TimeRangeSecondUtils.getLastYearStartTime());
            endTime = String.valueOf(TimeRangeSecondUtils.getLastYearEndTime());
        }
        return sendMessageTextService.chatHistory(chatId, sortType, startTime, endTime);
    }

    @GetMapping("/api/v1/chats")
    public List<ListChat> chats(String userIdType, String sortType) throws Exception {
        return sendMessageTextService.chats(userIdType, sortType);
    }
}
