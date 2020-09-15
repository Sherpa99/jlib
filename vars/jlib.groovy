def checkoutSRC() {
    echo 'Checking out source code!' 
    echo "Building applicaiton version : ${params.BRANCH_NAME}"
    git "${GIT_URL}"
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
