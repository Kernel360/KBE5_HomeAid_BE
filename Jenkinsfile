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

        IMAGE_NAME = 'homeaid-backend'
        IMAGE_TAG = 'latest'
    }

    tools {
        jdk 'OpenJDK17'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
                sh 'git fetch --unshallow || true'
            }
        }

        stage('Set Variables') {
            steps {
                script {
                    def author = sh(script: "git log -1 --pretty=format:'%an'", returnStdout: true).trim()
                    def email = sh(script: "git log -1 --pretty=format:'%ae'", returnStdout: true).trim()
                    def message = sh(script: "git log -1 --pretty=format:'%s'", returnStdout: true).trim()
                    def hash = sh(script: "git rev-parse --short HEAD", returnStdout: true).trim()
                    env.BUILD_USER = "${author} (${email})"
                    env.GIT_COMMIT_MSG = message
                    env.GIT_COMMIT_HASH = hash

                    echo "📌 Commit by: ${env.BUILD_USER}"
                    echo "📄 Message: ${env.GIT_COMMIT_MSG}"
                    echo "🔗 Hash: ${env.GIT_COMMIT_HASH}"
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

        stage('Stop and Remove Backend Container & Image') {
            // when {
            //     expression { env.BRANCH_NAME == 'dev' }
            // }
            steps {
                sh """
                docker stop backend-app || true
                docker rm backend-app || true

                IMAGE_ID=\$(docker images -q ${IMAGE_NAME}:${IMAGE_TAG})
                if [ ! -z "\$IMAGE_ID" ]; then
                    docker rmi \$IMAGE_ID
                else
                    echo "이미지 없음 → 삭제 생략"
                fi
                echo "🧽 Dangling 이미지 정리"
                DANGLING_IDS=\$(docker images -f "dangling=true" -q)
                if [ ! -z "\$DANGLING_IDS" ]; then
                    docker rmi \$DANGLING_IDS || true
                else
                    echo "Dangling 이미지 없음"
                fi
                """
            }
        }

        stage('Build & Run via Docker Compose') {
            // when {
            //         expression { env.BRANCH_NAME == 'dev' }
            //     }
            steps {
                sh """
                docker-compose build backend
                docker-compose up -d backend
                """
            }
        }
    }

    post {
        success {
            script {
                def message = """{
                    "embeds": [{
                        "title": "✅ CI/CD 성공",
                        "description": "**📦 Repository:** `${env.JOB_NAME}`\\n**🌿 Branch:** `${env.BRANCH_NAME}`\\n**🧑 Commit by:** `${env.BUILD_USER}`\\n**📝 Message:** ${env.GIT_COMMIT_MSG}\\n[🔗 Jenkins 로그 확인하기](${env.BUILD_URL})",
                        "color": 5763719
                    }],
                    "content": "✅ CI/CD 완료: `${env.BRANCH_NAME}` 브랜치입니다!"
                }"""
                sh """
                curl -H "Content-Type: application/json" \
                     -X POST \
                     -d '${message}' \
                     ${DISCORD_WEBHOOK}
                """
            }
        }

        failure {
            script {
                def message = """{
                    "embeds": [{
                        "title": "❌ CI/CD 실패",
                        "description": "**📦 Repository:** `${env.JOB_NAME}`\\n**🌿 Branch:** `${env.BRANCH_NAME}`\\n**🧑 Commit by:** `${env.BUILD_USER}`\\n**📝 Message:** ${env.GIT_COMMIT_MSG}\\n[🔗 Jenkins 로그 확인하기](${env.BUILD_URL})",
                        "color": 16711680
                    }],
                    "content": "❗ 오류 발생: `${env.BRANCH_NAME}` 브랜치 확인 요망!"
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
