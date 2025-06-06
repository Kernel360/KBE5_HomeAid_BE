pipeline {
    agent any

    environment {
        DISCORD_WEBHOOK = credentials('discord-webhook')
        DB_DRIVER = 'mysql'
        DB_HOST = 'mysql-ci'
        DB_PORT = '3306'
        DB_NAME = 'homeaid_db'
        DB_USERNAME = 'homeaid_user'
        DB_PASSWORD = 'root'
        DOCKER_IMAGE = 'sangwjdev/homeaid-backend'
        DOCKERHUB_CREDENTIALS = credentials('dockerhub-credentials')
        DOCKERHUB_USERNAME = "${DOCKERHUB_CREDENTIALS_USR}"
        DOCKERHUB_PASSWORD = "${DOCKERHUB_CREDENTIALS_PSW}"
    }

    tools {
        jdk 'OpenJDK17'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Set Variables') {
            steps {
                wrap([$class: 'BuildUser']) {
                    script {
                        env.BUILD_USER = "${env.BUILD_USER}"
                        echo "Triggered by: ${env.BUILD_USER}"
                    }
                }
            }
        }

        stage('Build and Test') {
            steps {
                sh 'chmod +x ./gradlew'
                withEnv([
                    "DB_DRIVER=${DB_DRIVER}",
                    "DB_HOST=${DB_HOST}",
                    "DB_PORT=${DB_PORT}",
                    "DB_NAME=${DB_NAME}",
                    "DB_USERNAME=${DB_USERNAME}",
                    "DB_PASSWORD=${DB_PASSWORD}"
                ]) {
                    sh './gradlew clean build'
                }
            }
        }

        stage('Docker Build & Push') {
            steps {
                script {
                    sh """
                        # Docker Build
                        docker build -t ${DOCKER_IMAGE}:${BUILD_NUMBER} .
                        docker tag ${DOCKER_IMAGE}:${BUILD_NUMBER} ${DOCKER_IMAGE}:latest
            
                        # DockerHub 로그인
                        echo $DOCKERHUB_PASSWORD | docker login -u $DOCKERHUB_USERNAME --password-stdin
            
                        # Docker Push
                        docker push ${DOCKER_IMAGE}:${BUILD_NUMBER}
                        docker push ${DOCKER_IMAGE}:latest
                        """
                    }
                }
            }

        stage('Deploy') {
            steps {
                sshagent(['ssh-secret-key']) {
                    sh '''
                        ssh ubuntu@3.35.183.135 "
                        cd /home/ubuntu/deployment/dir/KBE5_HomeAid_BE &&
                        docker-compose down &&
                        docker-compose pull &&
                        docker-compose up -d
                        "
                    '''
                }
            }
        }
    }

    post {
        success {
            wrap([$class: 'BuildUser']) {
                script {
                    def message = """{
                        "embeds": [{
                            "title": "✅ CI/CD 성공",
                            "description": "**📦 Repository:** `${env.JOB_NAME}`\\n**🌿 Branch:** `${env.BRANCH_NAME}`\\n**👤 Triggered by:** `${env.BUILD_USER}`\\n[🔗 Jenkins 로그 확인하기](${env.BUILD_URL})",
                            "color": 5763719
                        }],
                        "content": "✅ CI/CD 통과: `${env.BRANCH_NAME}` 브랜치 배포 완료!"
                    }"""
                    sh """
                    curl -H "Content-Type: application/json" \
                         -X POST \
                         -d '${message}' \
                         ${DISCORD_WEBHOOK}
                    """
                }
            }
        }
        failure {
            wrap([$class: 'BuildUser']) {
                script {
                    def message = """{
                        "embeds": [{
                            "title": "❌ CI/CD 실패",
                            "description": "**📦 Repository:** `${env.JOB_NAME}`\\n**🌿 Branch:** `${env.BRANCH_NAME}`\\n**👤 Triggered by:** `${env.BUILD_USER}`\\n[🔗 Jenkins 로그 확인하기](${env.BUILD_URL})",
                            "color": 16711680
                        }],
                        "content": "❗ CI/CD 실패 발생: `${env.BRANCH_NAME}` 브랜치 확인해주세요!"
                    }"""
                    sh """
                    curl -H "Content-Type: application/json" \
                         -X POST \
                         -d '${message}' \
                         ${DISCORD_WEBHOOK}
                    """
                }
            }
        }
    }
}
