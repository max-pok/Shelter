pipeline {
    agent {
        docker {
            image 'windsekirun/jenkins-android-docker:1.1.1'
        }
    }
    options {
        skipStagesAfterUnstable()
    }
    stages {
        stage('Checkout') {
            steps {
                //Checking out the repo
                checkout changelog: true, poll: true, scm: [$class: 'GitSCM', branches: [[name: '*/develop']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: '${gitProdCredential}', url: 'https://github.com/max-pok/Shelter.git']]]
            }
        }
        stage('Prepare') {
            steps {
                // Prepare for next stages
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
                    //run a gradle test
                    sh './gradlew clean test --no-daemon'
                    junit '**/build/test-results/testDebugUnitTest/*.xml' //make the junit test results available in any case (success & failure)
                }
            }
        }
//        stage('Frontend Unit Tests') {
//            steps {
//                script {
//                    //sh './gradlew cleanFrontendTest --no-daemon'
//                    sh './gradlew frontendUnitTest --no-daemon'
//                }
//            }
//        }
        stage('Build APK') {
            steps {
                // Finish building and packaging the APK
                sh './gradlew assembleDebug'
            }
        }
    }
    post {
        failure {
            emailext(subject: '$JOB_NAME - Build# $BUILD_NUMBER - $BUILD_STATUS',
                    body: '$DEFAULT_CONTENT',
                    replyTo: 'maxim.p9@gmail.com',
                    to: 'maxim.p9@gmail.com'
            )

            emailext(subject: '$JOB_NAME - Build# $BUILD_NUMBER - $BUILD_STATUS',
                    body: '$DEFAULT_CONTENT',
                    replyTo: 'adirat@ac.sce.ac.il',
                    to: 'adirat@ac.sce.ac.il')

            emailext(subject: '$JOB_NAME - Build# $BUILD_NUMBER - $BUILD_STATUS',
                    body: '$DEFAULT_CONTENT',
                    replyTo: 'saritdi@ac.sce.ac.il',
                    to: 'saritdi@ac.sce.ac.il')

            emailext(subject: '$JOB_NAME - Build# $BUILD_NUMBER - $BUILD_STATUS',
                    body: '$DEFAULT_CONTENT',
                    replyTo: 'hadarba1@ac.sce.ac.il',
                    to: 'hadarba1@ac.sce.ac.il')


        }
    }
}