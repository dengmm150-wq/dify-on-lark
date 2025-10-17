package top.duhongming.common;

/**
 * 常量
 *
 * @author duhongming
 * @see
 * @since 1.0.0
 */
public class Constants {
    public static final String ID_TEMPLATE = """
            <at user_id="%s">用户名（可不填）</at>
            
            <b>chat_id:</b>     %s
            
            <b>user_id:</b>     %s
            
            <b>open_id:</b>     %s
            
            <b>union_id:</b>    %s
            """;
    public static final String CARD_ELEMENT_ID_STREAMING_ANSWER = "streaming_answer";
    public static final String CARD_ELEMENT_ID_STREAMING_QUESTION = "streaming_question";
    public static final String CARD_ELEMENT_ID_STREAMING_DOCUMENT = "streaming_document";
    public static final String CARD_ELEMENT_ID_STREAMING_USAGE = "streaming_usage";
    public static final String CARD_JSON = """
            {
                  "schema": "2.0",
                  "config": {
                      "update_multi": true,
                      "streaming_mode": true,
                      "streaming_config": {
                          "print_step": {
                              "default": 5
                          },
                          "print_frequency_ms": {
                              "default": 40
                          },
                          "print_strategy": "fast"
                      }
                  },
                  "body": {
                      "direction": "vertical",
                      "padding": "12px 12px 12px 12px",
                      "elements": [
                          {
                              "tag": "markdown",
                              "content": "${answer}",
                              "text_align": "left",
                              "text_size": "normal",
                              "margin": "0px 0px 0px 0px",
                              "element_id": "streaming_answer"
                          },
                          {
                              "tag": "column_set",
                              "horizontal_spacing": "8px",
                              "horizontal_align": "left",
                              "columns": [
                                  {
                                      "tag": "column",
                                      "width": "weighted",
                                      "elements": [
                                          {
                                              "tag": "markdown",
                                              "content": "",
                                              "text_align": "left",
                                              "text_size": "normal",
                                              "margin": "0px 0px 0px 0px"
                                          },
                                          {
                                              "tag": "markdown",
                                              "content": "",
                                              "text_align": "left",
                                              "text_size": "normal",
                                              "margin": "0px 0px 0px 0px"
                                          }
                                      ],
                                      "padding": "0px 0px 0px 0px",
                                      "direction": "vertical",
                                      "horizontal_spacing": "8px",
                                      "vertical_spacing": "2px",
                                      "horizontal_align": "left",
                                      "vertical_align": "top",
                                      "margin": "0px 0px 0px 0px",
                                      "weight": 1
                                  }
                              ],
                              "margin": "0px 0px 0px 0px"
                          },
                          {
                              "tag": "markdown",
                              "content": "**相关问题**",
                              "text_align": "left",
                              "text_size": "normal",
                              "margin": "0px 0px 0px 0px"
                          },
                          {
                              "tag": "markdown",
                              "content": "${question}",
                              "text_align": "left",
                              "text_size": "normal",
                              "margin": "0px 0px 0px 0px",
                              "element_id": "streaming_question"
                          },
                          {
                              "tag": "markdown",
                              "content": "**相关文档**",
                              "text_align": "left",
                              "text_size": "normal",
                              "margin": "0px 0px 0px 0px"
                          },
                          {
                              "tag": "markdown",
                              "content": "${document}",
                              "text_align": "left",
                              "text_size": "normal",
                              "margin": "0px 0px 0px 0px",
                              "element_id": "streaming_document"
                          },
                          {
                              "tag": "hr",
                              "margin": "0px 0px 0px 0px"
                          },
                          {
                              "tag": "column_set",
                              "horizontal_spacing": "12px",
                              "horizontal_align": "right",
                              "columns": [
                                  {
                                      "tag": "column",
                                      "width": "weighted",
                                      "elements": [
                                          {
                                              "tag": "markdown",
                                              "content": "<font color=\\"grey-600\\">提示词token:--, 回答token:--, 总token:--, 耗时:--s</font>",
                                              "text_align": "left",
                                              "text_size": "notation",
                                              "margin": "4px 0px 0px 0px",
                                              "icon": {
                                                  "tag": "standard_icon",
                                                  "token": "robot_outlined",
                                                  "color": "grey"
                                              },
                                              "element_id": "streaming_usage"
                                          }
                                      ],
                                      "padding": "0px 0px 0px 0px",
                                      "direction": "vertical",
                                      "horizontal_spacing": "8px",
                                      "vertical_spacing": "8px",
                                      "horizontal_align": "left",
                                      "vertical_align": "top",
                                      "margin": "0px 0px 0px 0px",
                                      "weight": 1
                                  },
                                  {
                                      "tag": "column",
                                      "width": "20px",
                                      "elements": [
                                          {
                                              "tag": "button",
                                              "text": {
                                                  "tag": "plain_text",
                                                  "content": ""
                                              },
                                              "type": "text",
                                              "width": "fill",
                                              "size": "medium",
                                              "icon": {
                                                  "tag": "standard_icon",
                                                  "token": "thumbsup_outlined"
                                              },
                                              "hover_tips": {
                                                  "tag": "plain_text",
                                                  "content": "有帮助"
                                              },
                                              "behaviors": [
                                                  {
                                                      "type": "callback",
                                                      "value": {
                                                          "": ""
                                                      }
                                                  }
                                              ],
                                              "margin": "0px 0px 0px 0px",
                                              "element_id": "like"
                                          }
                                      ],
                                      "padding": "0px 0px 0px 0px",
                                      "direction": "vertical",
                                      "horizontal_spacing": "8px",
                                      "vertical_spacing": "8px",
                                      "horizontal_align": "left",
                                      "vertical_align": "top",
                                      "margin": "0px 0px 0px 0px"
                                  },
                                  {
                                      "tag": "column",
                                      "width": "30px",
                                      "elements": [
                                          {
                                              "tag": "button",
                                              "text": {
                                                  "tag": "plain_text",
                                                  "content": ""
                                              },
                                              "type": "text",
                                              "width": "default",
                                              "size": "medium",
                                              "icon": {
                                                  "tag": "standard_icon",
                                                  "token": "thumbdown_outlined"
                                              },
                                              "hover_tips": {
                                                  "tag": "plain_text",
                                                  "content": "无帮助"
                                              },
                                              "behaviors": [
                                                  {
                                                      "type": "callback",
                                                      "value": "dislike"
                                                  }
                                              ],
                                              "margin": "0px 0px 0px 0px",
                                              "element_id": "dislike"
                                          }
                                      ],
                                      "padding": "0px 0px 0px 0px",
                                      "vertical_spacing": "8px",
                                      "horizontal_align": "left",
                                      "vertical_align": "top",
                                      "margin": "0px 0px 0px 0px"
                                  }
                              ],
                              "margin": "0px 0px 4px 0px"
                          }
                      ]
                  },
                  "header": {
                      "title": {
                          "tag": "plain_text",
                          "content": "${name}"
                      },
                      "subtitle": {
                          "tag": "plain_text",
                          "content": "${description}"
                      },
                      "template": "indigo",
                      "icon": {
                          "tag": "standard_icon",
                          "token": "myai_colorful"
                      },
                      "padding": "12px 12px 12px 12px"
                  }
              }
            """;
    public static final String ENABLE_LIKE_BUTTON = """
            {
                "tag": "button",
                "text": {
                    "tag": "plain_text",
                    "content": ""
                },
                "type": "text",
                "width": "fill",
                "size": "medium",
                "icon": {
                    "tag": "standard_icon",
                    "token": "thumbsup_${icon}"
                },
                "hover_tips": {
                    "tag": "plain_text",
                    "content": "有帮助"
                },
                "value": {
                    "action": "like",
                    "user": "${user}",
                    "cardId": "${cardId}",
                    "messageId": "${messageId}",
                    "thumbsup": "${thumbsup}"
                },
                "margin": "0px 0px 0px 0px",
                "element_id": "like"
            }
            """;
    public static final String ENABLE_DISLIKE_BUTTON = """
            {
                "tag": "button",
                "text": {
                    "tag": "plain_text",
                    "content": ""
                },
                "type": "text",
                "width": "default",
                "size": "medium",
                "icon": {
                    "tag": "standard_icon",
                    "token": "thumbdown_${icon}"
                },
                "hover_tips": {
                    "tag": "plain_text",
                    "content": "无帮助"
                },
                "value": {
                    "action": "dislike",
                    "user": "${user}",
                    "cardId": "${cardId}",
                    "messageId": "${messageId}",
                    "thumbdown": "${thumbdown}"
                },
                "margin": "0px 0px 0px 0px",
                "element_id": "dislike"
            }
            """;
}
