git fetch
git pull origin master
git tag --sort=-creatordate | head -n 1

# 定义最新tag变量
LATEST_TAG=$(git tag --sort=-creatordate | head -n 1)

mvn clean package

# 构建镜像
docker build --build-arg LATEST_TAG=$LATEST_TAG -t registry.cn-hangzhou.aliyuncs.com/dockerdance/dify-on-lark:$LATEST_TAG .

# 推送至阿里云仓库
docker push registry.cn-hangzhou.aliyuncs.com/dockerdance/dify-on-lark:$LATEST_TAG

# 标记为第二个仓库标签
docker tag registry.cn-hangzhou.aliyuncs.com/dockerdance/dify-on-lark:$LATEST_TAG duhongming/dify-on-lark:$LATEST_TAG

# 推送至第二个仓库
docker push duhongming/dify-on-lark:$LATEST_TAG

git push gitee master