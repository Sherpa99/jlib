def checkoutSRC() {
    echo 'Checking out source code!' 
    echo "Building applicaiton version : ${params.BRANCH_NAME}"
    git "${GIT_URL}"
}
def buildApp() {
    echo 'Building the application!' 
    echo "Building applicaiton version : ${params.BRANCH_NAME}"
    sh " mvn package -DskipTests=true "
}
def pushToNexus() {
    echo 'Publish to nexus!'
    // Read POM xml file using 'readMavenPom' step , this step 'readMavenPom' is included in: https://plugins.jenkins.io/pipeline-utility-steps
    pom = readMavenPom file: "pom.xml";
    // Find built artifact under target folder
    filesByGlob = findFiles(glob: "target/*.${pom.packaging}");
    // Print some info from the artifact found
    echo "${filesByGlob[0].name} ${filesByGlob[0].path} ${filesByGlob[0].directory} ${filesByGlob[0].length} ${filesByGlob[0].lastModified}"
    // Extract the path from the File found
    artifactPath = filesByGlob[0].path;
    // Assign to a boolean response verifying If the artifact name exists
    artifactExists = fileExists artifactPath;
    if(artifactExists) {
        echo "*** File: ${artifactPath}, group: ${pom.groupId}, packaging: ${pom.packaging}, version ${pom.version}";
        nexusArtifactUploader(
            nexusVersion: NEXUS_VERSION,
            protocol: NEXUS_PROTOCOL,
            nexusUrl: NEXUS_URL,
            groupId: pom.groupId,
            version: pom.version,
            repository: NEXUS_REPOSITORY,
            credentialsId: NEXUS_CREDENTIAL_ID,
            artifacts: [
                // Artifact generated such as .jar, .ear and .war files.
                [artifactId: pom.artifactId,
                classifier: '',
                file: artifactPath,
                type: pom.packaging],
                // Lets upload the pom.xml file for additional information for Transitive dependencies
                [artifactId: pom.artifactId,
                classifier: '',
                file: "pom.xml",
                type: "pom"]
            ]
        );
    } else {
        error "*** File: ${artifactPath}, could not be found";
    }
}
def deployApp() {
    echo 'Deployment deploying the application' 
    sh 'oc create deployment infordata-poc-app --image=us.icr.io/gs-cda-dev-ns/infordata-dev:1.1'
}
def createService() {
    echo 'Deployment deploying the application' 
    sh 'oc new-app infordata-poc-app/infordata-poc-app'
}
def exposeService() {
    echo 'Deployment deploying the application' 
    sh 'oc expose svc/infordata-poc-app'
}
return this
