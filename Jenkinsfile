pipeline {
    agent any

    options {
        timestamps()
    }

    environment {
        IMAGE  = 'sivapraksh7/job-tracker'   // CHANGE ME
        APP_IP = '10.0.8.95'                               // CHANGE ME: app-server PRIVATE IP
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build and Test') {
            steps {
                sh 'chmod +x mvnw'
                sh './mvnw -B clean verify'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                    jacoco execPattern: 'target/jacoco.exec'
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('sonar-server') {
                    sh './mvnw -B org.sonarsource.scanner.maven:sonar-maven-plugin:5.7.0.6970:sonar -Dsonar.projectKey=job-tracker-cicd -Dsonar.projectName=job-tracker-cicd'
                }
            }
        }

        stage('Docker Build and Push') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub-creds', usernameVariable: 'DH_USER', passwordVariable: 'DH_PASS')]) {
                    sh '''
                        docker build -t $IMAGE:$BUILD_NUMBER -t $IMAGE:latest .
                        echo "$DH_PASS" | docker login -u "$DH_USER" --password-stdin
                        docker push $IMAGE:$BUILD_NUMBER
                        docker push $IMAGE:latest
                        docker logout
                    '''
                }
            }
        }

        stage('Deploy') {
            steps {
                sshagent(credentials: ['app-server-ssh']) {
                    sh '''
                        ssh -o StrictHostKeyChecking=no ubuntu@$APP_IP "docker pull $IMAGE:$BUILD_NUMBER && (docker rm -f job-tracker 2>/dev/null; docker run -d --name job-tracker --restart unless-stopped -p 8081:8081 $IMAGE:$BUILD_NUMBER)"
                    '''
                }
            }
        }

        stage('Health Check') {
            steps {
                sh 'curl -f --retry 12 --retry-delay 5 --retry-connrefused http://$APP_IP:8081/actuator/health'
            }
        }
    }
}