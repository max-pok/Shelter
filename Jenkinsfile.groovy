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
                    try {
                        sh './gradlew clean test --no-daemon' //run a gradle task
                    } finally {
                        junit '**/build/test-results/test/*.xml' //make the junit test results available in any case (success & failure)
                    }
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
            emailext body: 'Build Failed', recipientProviders: [[$class: 'DevelopersRecipientProvider'], [$class: 'RequesterRecipientProvider']], subject: 'Test'
        }
    }
}