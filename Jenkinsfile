#!/usr/bin/env groovy
@Library('github.com/stakater/stakater-pipeline-library@revamp') _

releaseMavenApp {
    gitUser = "stakater-user"
    gitEmail = "stakater@gmail.com"
    usePersonalAccessToken = true
    tokenCredentialID = 'GithubToken'
    appName = "gateway"
    mavenGoal = "clean vertx:package"
    notifySlack = false
    runIntegrationTest = false
    deployManifest = true
    namespace = "coolstore"
    dockerRepositoryURL = 'docker.release.stakater.com:443'
    javaRepositoryURL = 'http://nexus.release/repository/maven'
    podVolumes = [
        additionalSecretVolumes: [[secretName: 'k8s-current-cluster-kubeconfig', mountPath: '/home/jenkins/.kube']]
    ]
}