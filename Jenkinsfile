pipeline {
    agent any
    options {
        buildDiscarder(logRotator(numToKeepStr: '5', artifactNumToKeepStr: '5'))
    }
    // Uses this repo's own ./gradlew wrapper rather than a Jenkins global Gradle
    // tool - pins the exact Gradle version (9.5.1) this repo needs.
    tools {
        jdk 'jdk-25.0.2'
    }

    stages {
        stage('Build') {
            steps {
                sh 'chmod +x gradlew'
                sh './gradlew assemble'
            }
        }
        stage('Test') {
            // JavaFX UI tests need a display - matches what the old GitHub Actions
            // workflow did (Xvfb + xvfb-run), just running on this agent instead.
            steps {
                sh 'which xvfb-run || apk add --no-cache xvfb-run'
                sh 'xvfb-run --auto-servernum ./gradlew test jacocoTestReport jacocoTestCoverageVerification'
            }
            post {
                always {
                    junit(testResults: 'build/test-results/test/*.xml', skipPublishingChecks: true)
                }
            }
        }
        // No SonarQube Analysis stage yet - this repo has no Sonar project
        // configured, and that's being added separately later, not here.
        stage('Release - Linux package') {
            // Plain expression, not the built-in tag() when-condition: that crashes
            // on this Jenkins instance (a github-integration plugin incompatibility,
            // found on SpinningHeads) for any GitHubSCMSource-backed multibranch job.
            when { expression { return env.TAG_NAME?.startsWith('v') } }
            steps {
                sh './gradlew jpackage'
                archiveArtifacts artifacts: 'build/jpackage/*.deb', fingerprint: true
            }
        }

        stage('Publish to artifact share') {
            when { expression { return env.BRANCH_NAME == 'main' } }
            steps {
                sh '''
                    set -e
                    umask 000
                    dest=/mnt/artifacts/MyDocument
                    mkdir -p "$dest"
                    n=$(find build/libs -maxdepth 1 -name '*.jar' | wc -l)
                    [ "$n" -gt 0 ] || { echo "no jar in build/libs to publish"; exit 1; }
                    cp -f build/libs/*.jar "$dest"/
                '''
            }
        }

        stage('Publish installer to artifact share') {
            when { expression { return env.TAG_NAME?.startsWith('v') } }
            steps {
                sh '''
                    set -e
                    umask 000
                    dest=/mnt/artifacts/MyDocument
                    mkdir -p "$dest"
                    n=$(find build/jpackage -name '*.deb' | wc -l)
                    [ "$n" -gt 0 ] || { echo "no .deb under build/jpackage to publish"; exit 1; }
                    find build/jpackage -name '*.deb' -print0 | xargs -0 -I{} cp -f {} "$dest"/
                '''
            }
        }
    }
}
