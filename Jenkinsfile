#!/usr/bin/env groovy
@Library('github.com/stakater/stakater-pipeline-library@v2.16.15') _

releaseApplication {
    appName = "gateway"
    appType = "maven"
    builderImage = "stakater/builder-maven:3.5.4-jdk1.8-apline8-v0.0.3"
    goal = "clean package vertx:package"
    notifySlack = true
    runIntegrationTest = false
    gitUser = "stakater-user"
    gitEmail = "stakater@gmail.com"
    usePersonalAccessToken = true
    tokenCredentialID = 'GithubToken'
    serviceAccount = "jenkins"
    dockerRepositoryURL = 'docker.delivery.stakater.com:443'
    // configuration parameter for e2e tess
    e2eTestJob = "true"
    e2eJobName = "../stakater-nordmart-e2e-tests/master"
}
