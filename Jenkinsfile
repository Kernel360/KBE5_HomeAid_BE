pipeline {
    agent any

    environment {
        DISCORD_WEBHOOK = credentials('discord-webhook')
        DB_DRIVER = 'mysql'
        DB_HOST = 'mysql-ci'     // Docker Compose 서비스명 (같은 네트워크)
        DB_PORT = '3306'
        DB_NAME = 'homeaid_db'
        DB_USERNAME = 'homeaid_user'
        DB_PASSWORD = 'root'
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
    }

    post {
        success {
            script {
                def message = """{
                    "embeds": [{
                        "title": "✅ CI 성공",
                        "description": "**📦 Repository:** `${env.JOB_NAME}`\\n**🌿 Branch:** `${env.BRANCH_NAME}`\\n**👤 Triggered by:** `${env.BUILD_USER}`\\n[🔗 Jenkins 로그 확인하기](${env.BUILD_URL})",
                        "color": 5763719
                    }],
                    "content": "✅ CI 통과: `${env.BRANCH_NAME}` 브랜치입니다!"
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
                        "title": "❌ CI 실패",
                        "description": "**📦 Repository:** `${env.JOB_NAME}`\\n**🌿 Branch:** `${env.BRANCH_NAME}`\\n**👤 Triggered by:** `${env.BUILD_USER}`\\n[🔗 Jenkins 로그 확인하기](${env.BUILD_URL})",
                        "color": 16711680
                    }],
                    "content": "❗ CI 실패 발생: `${env.BRANCH_NAME}` 브랜치 확인해주세요!"
                }"""
                sh """
                curl -H "Content-Type: application/json" \
                     -X POST \
                     -d '${message}' \
                     ${DISCORD_WEBHOOK}
                """
            }
        }
        always {

        }
    }
}
