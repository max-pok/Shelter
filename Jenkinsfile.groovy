pipeline {
    agent {
        docker {
            image 'windsekirun/jenkins-android-docker:1.1.1'
        }
    }
    options {
        // Stop the build early in case of compile or test failures
        skipStagesAfterUnstable()
    }
    stages {
        stage('Checkout') {
            steps { //Checking out the repo
                checkout changelog: true, poll: true, scm: [$class: 'GitSCM', branches: [[name: '*/hackathon']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: 'git', url: 'https://github.com/max-pok/Shelter.git']]]
            }
        }
        stage('Prepare') {
            steps {
                sh 'chmod +x ./gradlew'
            }
        }
        stage('Compile') {
            steps {
                // Compile the app and its dependencies
                sh './gradlew compileDebugSources'
            }
        }
        stage('Unit & Integration Tests') {
            steps {
                script {
                    sh './gradlew test' //run a gradle test
                }
            }
        }
        stage('Build APK') {
            steps {
                // Finish building and packaging the APK
                sh './gradlew assembleDebug'
            }
        }
    }
    post {
        always {
            emailext subject: '$DEFAULT_SUBJECT',
                    body: '$DEFAULT_CONTENT',
                    recipientProviders: [
                            [$class: 'CulpritsRecipientProvider'],
                            [$class: 'DevelopersRecipientProvider'],
                            [$class: 'RequesterRecipientProvider']
                    ],
                    replyTo: 'maxim.p9@gmail.com',
                    to: 'maxim.p9@gmail.com'
        }
    }
}