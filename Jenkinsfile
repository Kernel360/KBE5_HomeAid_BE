pipeline {
    agent any

    environment {
        DISCORD_WEBHOOK = credentials('discord-webhook')
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

        stage('Build MySQL Service') {
            steps {
                script {
                    // Docker로 MySQL 컨테이너 띄우기 (jenkins 서버에 Docker가 설치되어 있어야 함)
                    sh '''
                    docker run -d \
                        --name mysql-ci \
                        -e MYSQL_DATABASE=${DB_NAME} \
                        -e MYSQL_ROOT_PASSWORD=root \
                        -e MYSQL_USER=homeaid_user \
                        -e MYSQL_PASSWORD=${DB_PASSWORD} \
                        -p 3306:3306 \
                        --health-cmd="mysqladmin ping -h localhost --silent" \
                        --health-interval=10s \
                        --health-timeout=5s \
                        --health-retries=3 \
                        mysql:latest

                    # DB 준비 대기 (최대 60초)
                    for i in {1..12}; do
                        if docker exec mysql-ci mysqladmin ping -h localhost --silent; then
                            echo "MySQL is ready!"
                            break
                        fi
                        echo "Waiting for MySQL..."
                        sleep 5
                    done
                    '''
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
            // Clean up MySQL container
            sh '''
            docker stop mysql-ci || true
            docker rm mysql-ci || true
            '''
        }
    }
}
