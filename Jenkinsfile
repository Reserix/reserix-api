pipeline {
    agent any

    tools {
        jdk 'JDK17'
    }

    environment {
        IMAGE_NAME = 'thedevfaiz/reserix-api'
        IMAGE_TAG = 'develop'
        DEV_HOST = '135.181.185.147'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Create .env') {
            when {
                branch 'develop'
            }
            stage('Create .env') {
                steps {
                    withCredentials([
                            usernamePassword(
                                    credentialsId: 'postgres-credential',
                                    usernameVariable: 'POSTGRES_USER',
                                    passwordVariable: 'POSTGRES_PASSWORD'
                            ),
                            string(
                                    credentialsId: 'jwt-secret',
                                    variable: 'JWT_SECRET'
                            )
                    ]) {
                        sh '''
                cat > .env <<EOF
POSTGRES_DB=reserix_db

POSTGRES_USER=$POSTGRES_USER
POSTGRES_PASSWORD=$POSTGRES_PASSWORD

SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/reserix_db
SPRING_DATASOURCE_USERNAME=$POSTGRES_USER
SPRING_DATASOURCE_PASSWORD=$POSTGRES_PASSWORD

SPRING_DATA_REDIS_HOST=redis
SPRING_DATA_REDIS_PORT=6379

JWT_SECRET=$JWT_SECRET
EOF

                chmod 600 .env
            '''
                    }
                }
            }

        stage('Start DB') {
            when {
                branch 'develop'
            }
            steps {
                sh 'docker compose up -d postgres redis'
                sh 'sleep 10'
            }
        }

        stage('Build') {
            steps {
                sh './gradlew clean build -x test'
            }
        }

        stage('Test') {
            steps {
                sh './gradlew test'
            }
            post {
                always {
                    junit '**/build/test-results/test/*.xml'
                }
            }
        }

//        stage('Docker Build') {
//            when {
//                branch 'develop'
//            }
//            steps {
//                sh 'docker build -t $IMAGE_NAME:$IMAGE_TAG .'
//            }
//        }
//
//        stage('Docker Push') {
//            when {
//                branch 'develop'
//            }
//            steps {
//                withCredentials([usernamePassword(
//                    credentialsId: 'dockerhub-credential',
//                    usernameVariable: 'DOCKER_USERNAME',
//                    passwordVariable: 'DOCKER_PASSWORD'
//                )]) {
//                    sh '''
//                        echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USERNAME" --password-stdin
//                        docker push $IMAGE_NAME:$IMAGE_TAG
//                    '''
//                }
//            }
//        }

        stage('Docker Build & Push') {
            when {
                branch 'develop'
            }
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub-credential',
                    usernameVariable: 'DOCKER_USERNAME',
                    passwordVariable: 'DOCKER_PASSWORD'
                )]) {
                    sh '''
                        echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USERNAME" --password-stdin

                        docker buildx create --use --name multiarch-builder || docker buildx use multiarch-builder

                        docker buildx build \
                          --platform linux/amd64,linux/arm64 \
                          -t $IMAGE_NAME:$IMAGE_TAG \
                          --push .
                    '''
                }
            }
        }

        stage('Deploy to Dev') {
            when {
                branch 'develop'
            }
            steps {
                withCredentials([sshUserPrivateKey(
                    credentialsId: 'dev-server-ssh',
                    keyFileVariable: 'SSH_KEY',
                    usernameVariable: 'SSH_USER'
                )]) {
                    sh '''
                        ssh -i "$SSH_KEY" \
                            -o StrictHostKeyChecking=no \
                            "$SSH_USER@$DEV_HOST" "

                            cd /opt/reserix-api &&

                            docker compose pull &&
                            docker compose up -d
                        "
                    '''
                }
            }
        }
    }

    post {
        success {
            echo 'Build and tests completed successfully.'
        }
        failure {
            echo 'Build or tests failed.'
        }
    }
}