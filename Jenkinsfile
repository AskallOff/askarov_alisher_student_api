// Jenkins pipeline of Alisher Askarov (IT2-2304, ID 38226)
// Checkout -> Build -> Test -> Docker Build -> Verify
pipeline {
    // built-in node with the Docker CLI (label set in jenkins/casc.yaml)
    agent { label 'docker' }

    tools {
        jdk 'jdk21'
    }

    parameters {
        string(name: 'BRANCH', defaultValue: 'main', description: 'Branch of askarov_alisher_student_api to build')
    }

    // no webhook from GitHub to localhost, so Jenkins asks GitHub for new commits every 2 minutes
    triggers {
        pollSCM('H/2 * * * *')
    }

    options {
        skipDefaultCheckout(true)
        disableConcurrentBuilds()
        timestamps()
        buildDiscarder(logRotator(numToKeepStr: '20'))
        timeout(time: 20, unit: 'MINUTES')
    }

    environment {
        STUDENT_NAME = 'Alisher Askarov'
        GIT_REPO     = 'git@github.com:AskallOff/askarov_alisher_student_api.git'
        GIT_CREDS    = 'askarov-github-deploy-key'
        IMAGE        = 'askarov-alisher-student-api'
        TAG          = 'v1.0-askarov-alisher'
    }

    stages {
        stage('Checkout') {
            steps {
                echo "Student: ${env.STUDENT_NAME} | job ${env.JOB_NAME} #${env.BUILD_NUMBER} | branch ${params.BRANCH}"
                // SSH clone with the deploy key stored in Jenkins Credentials
                git url: env.GIT_REPO, branch: params.BRANCH, credentialsId: env.GIT_CREDS
                sh 'git log -1 --pretty="Commit %h by %an: %s"'
            }
        }

        stage('Build') {
            steps {
                sh 'chmod +x gradlew'
                sh './gradlew --no-daemon clean bootJar'
            }
        }

        stage('Test') {
            steps {
                // a failed test makes gradlew return non-zero: the stage fails and the pipeline stops
                sh './gradlew --no-daemon test'
            }
            post {
                always {
                    junit testResults: 'build/test-results/test/*.xml', allowEmptyResults: true
                }
            }
        }

        stage('Docker Build') {
            steps {
                // second try only for network problems with Docker Hub
                retry(2) {
                    sh 'docker build -t ${IMAGE}:${TAG} -t ${IMAGE}:jenkins-${BUILD_NUMBER} .'
                }
            }
        }

        stage('Verify') {
            steps {
                sh '''
                    docker image inspect ${IMAGE}:${TAG} > /dev/null
                    echo "Image ${IMAGE}:${TAG} found, id $(docker image inspect ${IMAGE}:${TAG} --format '{{.Id}}' | cut -c 8-19)"
                    docker image ls ${IMAGE} --format 'table {{.Repository}}:{{.Tag}}\t{{.Size}}\t{{.CreatedSince}}'
                '''
                echo "VERIFY PASSED: image of ${env.STUDENT_NAME} is ready"
            }
        }
    }

    post {
        success {
            echo "RESULT: SUCCESS - ${env.STUDENT_NAME}, ${env.IMAGE}:${env.TAG}"
        }
        failure {
            echo "RESULT: FAILURE - pipeline of ${env.STUDENT_NAME} stopped, see the red stage"
        }
    }
}
