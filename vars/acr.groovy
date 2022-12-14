def checkExist(String image, String registry) {

    try {
        sh (returnStdout: true, script: "az acr repository show --image $image --name $registry")

        echo "########## [INFO] Image ${image} is existing on $registry ACR"

        return true  

    } catch(Exception e) {
        echo "########## [WARNING] Image ${image} doesn't exist on $registry ACR"

        return false
    }
}

def importImage(String src_registry, String dst_registry, String image) {

    withCredentials([usernamePassword(credentialsId: 'hfmt_acr_hfmtdev', passwordVariable: 'pass', usernameVariable: 'user')]) {
        echo "########## [INFO] Import image ${image} to $dst_registry ACR"

        sh ("az acr import --source " + src_registry + ".azurecr.io/" + image + " --username " + user + " --password " + pass + " --name " + dst_registry + " --image " + image + " --force")
    }
}

def copyImage(String registry, String srcImage, String dstImage) {
    echo "########## [INFO] Copy image $srcImage to $dstImage"

    sh ("az acr import --source " + registry + ".azurecr.io/" + srcImage + " --name " + registry + " --image " + dstImage + " --force")
}