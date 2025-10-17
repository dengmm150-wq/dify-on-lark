package top.duhongming.platform.lark.service;

import com.lark.oapi.Client;
import com.lark.oapi.service.im.v1.model.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.duhongming.utils.BeanUtils;

import static top.duhongming.platform.lark.config.LarkConfig.consoleLog;

/**
 * 图片服务
 *
 * @author duhongming
 * @see
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService {

    /**
     * 下载图片：https://open.feishu.cn/document/server-docs/im-v1/image/get
     *
     * @param imageKey
     */
    @SneakyThrows
    public void downloadImage(String imageKey, String path) {
        // 创建请求对象
        GetImageReq req = GetImageReq.newBuilder()
                .imageKey(imageKey)
                .build();

        // 发起请求
        GetImageResp resp = BeanUtils.getBean(Client.class).im().v1().image().get(req);

        // 业务数据处理
        resp.writeFile(path);
    }

    /**
     * 上传图片：https://open.feishu.cn/document/server-docs/im-v1/image/create
     *
     * @param file
     */
    @SneakyThrows
    public void uploadImage(java.io.File file) {
        // 创建请求对象
        CreateImageReq req = CreateImageReq.newBuilder()
                .createImageReqBody(CreateImageReqBody.newBuilder()
                        .imageType("message")
                        .image(file)
                        .build())
                .build();

        // 发起请求
        CreateImageResp resp = BeanUtils.getBean(Client.class).im().v1().image().create(req);

        consoleLog(resp);
    }

}
