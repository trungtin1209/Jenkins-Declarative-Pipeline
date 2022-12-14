def getAzureSP(String env)
{
    String az_sp = ''

    if(env == DEV){
        az_sp = DEV_AZ_SP_ACCOUNT
    }
    else if(env == QC){
        az_sp = QC_AZ_SP_ACCOUNT
    }
    else if(env == UAT){
        az_sp = UAT_AZ_SP_ACCOUNT
    }
    else if(env == LOCAL_UAT){
        az_sp = LOCAL_UAT_AZ_SP_ACCOUNT
    }

    return az_sp
}


def login(String env) {
    String az_sp = getAzureSP(env)

    loginWithServicePrincipal(az_sp)
}

def loginWithServicePrincipal(String az_sp) {

    withCredentials([azureServicePrincipal(az_sp)]) {
        try {
            def output = sh (returnStdout: true, script: 'az account show | grep $AZURE_CLIENT_ID')

            if(output == '') {
                sh('az login --service-principal -u $AZURE_CLIENT_ID -p $AZURE_CLIENT_SECRET --tenant $AZURE_TENANT_ID')
            }

        } catch(Exception e) {

            sh('az login --service-principal -u $AZURE_CLIENT_ID -p $AZURE_CLIENT_SECRET --tenant $AZURE_TENANT_ID')
        }

        sh('az account set --subscription $AZURE_SUBSCRIPTION_ID')
    }
}

def logout() {
    //sh('az logout')
}

def getKeyVault(String env)
{
    String kv = ''

    if(env == DEV){
        kv = DEV_KEYVAULT
    }
    else if(env == QC){
        kv = QC_KEYVAULT
    }
    else if(env == UAT){
        kv = UAT_KEYVAULT
    }
    else if(env == LOCAL_UAT){
        kv = LOCAL_UAT_KEYVAULT
    }

    return kv
}