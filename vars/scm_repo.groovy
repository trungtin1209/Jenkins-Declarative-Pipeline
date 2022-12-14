def checkoutHfmtDevOps(String env, String subDir = ".") {

    String branch = ''

    if(env == DEV){
        branch = DEV_BRANCH
    }
    else if(env == QC){
        branch = QC_BRANCH
    }
    else if(env == UAT){
        branch = UAT_BRANCH
    }
    else if(env == LOCAL_UAT){
        branch = LOCAL_UAT_BRANCH
    }

    echo "Branch: " + branch

    checkoutWithBranch( GITLAB_HFMT_DEVOPS, branch,subDir)
}

def checkoutWithBranch(String remoteURL ,String branch, String subDir = ".") {

    checkout([
        $class: 'GitSCM', 
        branches: [[name: branch]], 
        extensions: [
            [
                $class: 'CleanBeforeCheckout', 
                deleteUntrackedNestedRepositories: true
            ],
            [
                $class: 'RelativeTargetDirectory', 
                relativeTargetDir: subDir
            ]
        ], 
        gitTool: 'GIT_HOME', 
        userRemoteConfigs: [[
            credentialsId: "$GITLAB_CREDENTIAL_ID", 
            url: remoteURL]]
        ]
    )
}

def tagExist(String repo, String tag) {

    def status = sh (returnStdout: true, script: "cd " + pwd() + "/$repo" +" && git tag | grep $tag || true")

    status = status.replaceAll(" ","").replaceAll("\n","")
    
    if(status == tag)
         return true
    else 
         return false
}

def releaseHVNAzure(String repo, String tag) {
    withCredentials([string(credentialsId: 'hvn_az_repo_token', variable: 'az_repo_token')]) {
        sh 
        '''
            cd $repo
            git remote add origin2 https://${az_repo_token}@dev.azure.com/HFMT/Phoenix/_git/'''+ repo +'''
            git push origin2 '''+ tag +''' 
            cd ..
        '''
    }
}