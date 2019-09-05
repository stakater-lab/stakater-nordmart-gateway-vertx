#!/usr/bin/env groovy
@Library('github.com/stakater/stakater-pipeline-library@v2.16.0') _

releaseApplication {
    appName = "gateway"
    appType = "maven"
    builderImage = "stakater/builder-maven:3.5.4-jdk1.8-v2.0.1-v0.0.6"
    goal = "clean package vertx:package"
    notifySlack = false
    runIntegrationTest = false
    gitUser = "stakater-user"
    gitEmail = "stakater@gmail.com"
    usePersonalAccessToken = true
    tokenCredentialID = 'GithubToken'
    serviceAccount = "jenkins"
    dockerRepositoryURL = 'docker.delivery.stakater.com:443'
    javaRepositoryURL = 'http://nexus.delivery.stakater.com/repository/maven'
}
