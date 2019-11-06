#!/usr/bin/env groovy
@Library('github.com/stakater/stakater-pipeline-library@v2.16.11') _

releaseApplication {
    appName = "gateway"
    appType = "maven"
    builderImage = "stakater/builder-maven:3.5.4-jdk1.8-apline8-v0.0.3"
    goal = "clean package vertx:package"
    notifySlack = false
    runIntegrationTest = false
    gitUser = "stakater-user"
    gitEmail = "stakater@gmail.com"
    usePersonalAccessToken = true
    tokenCredentialID = 'GithubToken'
    serviceAccount = "jenkins"
    dockerRepositoryURL = 'docker-delivery.workshop.stakater.com:443'
}
