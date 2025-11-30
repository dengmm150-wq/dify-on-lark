pipeline {
    agent { node { label 'ren9000k' } }
    tools {
        maven 'Maven 3.9.11'
    }
    environment {
        GIT_URL = 'http://gitea.duhongming.top/gitops/dify-on-lark.git'
        GIT_BRANCH = 'main'
        VERSION = sh(script: 'mvn help:evaluate -Dexpression=project.version -q -DforceStdout', returnStdout: true).trim()

        DOCKERHUB_USERNAME = "duhongming"
        DOCKERHUB_REPO = "dify-on-lark"
        IMAGE_TAG = sh(script: 'mvn help:evaluate -Dexpression=project.version -q -DforceStdout', returnStdout: true).trim()

        ALI_REGISTRY = "registry.cn-hangzhou.aliyuncs.com"
        ALI_NAMESPACE = "dockerdance"
        ALI_REPO = "dify-on-lark"
    }

    stages {
        stage('拉取代码') {
            steps {
                script {
                    sh 'printenv'
                    checkout scmGit(
                        branches: [[name: "refs/heads/main"]],
                        userRemoteConfigs: [[
                            url: "${GIT_URL}",
                            credentialsId: 'gitea-creds'
                        ]]
                    )
                    sh "git log -1 --pretty=format:'%h - %an, %ad: %s'"
                }
            }
        }
        stage('编译打包') {
            steps {
                script {
                    sh 'printenv'
                    sh """
                        mvn clean package -DskipTests
                    """
                }
            }
        }
        stage('登录镜像仓库') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'docker-hub-creds',
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PWD'
                )]) {
                    sh 'printenv'
                    sh '''
                        docker login -u ${DOCKER_USER} -p ${DOCKER_PWD}
                    '''
                }

                withCredentials([usernamePassword(
                    credentialsId: 'ali-docker-hub-creds',
                    usernameVariable: 'ALI_USER',
                    passwordVariable: 'ALI_PWD'
                )]) {
                    sh 'printenv'
                    sh '''
                        docker login ${ALI_REGISTRY} -u ${ALI_USER} -p ${ALI_PWD}
                    '''
                }
            }
        }

        stage('构建Docker镜像') {
            steps {
                sh 'printenv'
                sh '''
                    docker build --build-arg LATEST_TAG=${IMAGE_TAG}  -t ${DOCKERHUB_USERNAME}/${DOCKERHUB_REPO}:${IMAGE_TAG} \
                                -t ${DOCKERHUB_USERNAME}/${DOCKERHUB_REPO}:latest \
                                -t ${ALI_REGISTRY}/${ALI_NAMESPACE}/${ALI_REPO}:${IMAGE_TAG} \
                                -t ${ALI_REGISTRY}/${ALI_NAMESPACE}/${ALI_REPO}:latest .
                '''
            }
        }
         stage('推送镜像到仓库') {
            steps {
                sh 'printenv'
                sh '''
                    docker push ${DOCKERHUB_USERNAME}/${DOCKERHUB_REPO}:${IMAGE_TAG}
                    docker push ${DOCKERHUB_USERNAME}/${DOCKERHUB_REPO}:latest
                '''
                sh '''
                    docker push ${ALI_REGISTRY}/${ALI_NAMESPACE}/${ALI_REPO}:${IMAGE_TAG}
                    docker push ${ALI_REGISTRY}/${ALI_NAMESPACE}/${ALI_REPO}:latest
                '''
            }
        }
    }
}