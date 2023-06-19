#!/usr/bin/env groovy
void call(Map demoBuild, String demoVersion) {
    // Credentials
    String orgRegistry = 'demo'
    String dockerHubCredential = 'dockerhub'
    String buildRegistry = 'gitlab.simplemdg.com:5050'
    String flowName = "${demoBuild.build.flow.name}"
    String baseImage     = "mcr.microsoft.com/dotnet/sdk"
    String baseTag       = "6.0"
    // Variables branch
    String checkBranches = "$env.BRANCH_NAME"
    String[] deployBranches = ['main', 'jenkins']

//========================================================================
//========================================================================

//========================================================================
//========================================================================

    stage ('Prepare Package') {
        script {
            writeFile file: '.ci/Dockerfile.SDK', text: libraryResource('dev/demo/flows/dotnet/docker/Dockerfile.SDK')
            writeFile file: '.ci/Dockerfile.Runtime.API', text: libraryResource('dev/demo/flows/dotnet/docker/Dockerfile.Runtime.API')
            writeFile file: '.ci/docker_entrypoint.sh', text: libraryResource('dev/demo/flows/dotnet/script/docker_entrypoint.sh')
        }
    }

    switch (flowName) {
        case 'dotnet':
            stage("Build Solution") {
                docker.build("demo/${demoBuild.name}-sdk:${demoVersion}", "--force-rm --no-cache -f ./.ci/Dockerfile.SDK \
                --build-arg BASEIMG=${baseImage} --build-arg IMG_VERSION=${baseTag} ${WORKSPACE}") 
            }
            stage("Publish Package") {
                docker.build("demo/${demoBuild.name}:${demoVersion}", "--force-rm --no-cache -f ./.ci/Dockerfile.Runtime.API \
                --build-arg BASEIMG=demo/${demoBuild.name}-sdk --build-arg IMG_VERSION=${demoVersion} \
                --build-arg ENTRYPOINT=${demoBuild.build.runtime.name} --build-arg RUNIMG=${baseImage} --build-arg RUNVER=${baseTag} .")
            }
        }
}
// }
//========================================================================
// Demo CI
// Version: v1.0
// Updated:
//========================================================================
//========================================================================
// Notes:
//
//
//========================================================================