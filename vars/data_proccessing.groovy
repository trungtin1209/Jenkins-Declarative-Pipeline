def build() {
    String buildNumber = currentBuild.getNumber()
    String data_processing = pwd() + "/$DATA_PROCESSING_DIR"
    String consPath = pwd() + "/$DATA_PROCESSING_DIR/hfmt-streaming-processing/src/main/resources/"
    String appJarPath = pwd() + "/$DATA_PROCESSING_DIR/hfmt-streaming-processing/target/"
    String appJarName = "hfmt-streaming-processing-jar-with-dependencies.jar"
    String packageVersion = "$BUSSINESS_BUILD_NUMBER"
    String packegeName = TAG_VERSION + "_" + buildNumber + "_" + BUSSINESS_BUILD_NUMBER

    sh '''
        #!/bin/bash
        
        export JAVA_HOME="$JAVA_HOME_DATA_PROCESSING"
        echo $JAVA_HOME
        java -version

        echo '########## [INFO] Starting to build data-processing'
        cd ''' + data_processing + ''' ; $MVN_CMD clean install -Dmaven.test.skip=true ; cd ..
    '''

    sh '''
        #!/bin/bash

        echo '########## [INFO] Compress package for azure artifact uploading'
        mkdir streaming-processing
        cp -rf ./$DEVOPS_DIR/hfmt-data-processing/* streaming-processing
        cp ''' + appJarPath + '''/''' + appJarName + ''' streaming-processing
    '''

        //cp ''' + consPath + '''/* tmp

        //rm -rf tmp/*.properties ; #will keep bi once app need to release bi property files
        //rm -rf tmp/reference_devops.conf tmp/detokenizer.sh tmp/reference.conf # teporary now release the original conf. MUST must update it by manually
        //cd tmp;zip -r ''' + packegeName + '''.zip *
}

def submit(String sparkMaster, String jarFile, String referenceFile) {
    sh '''
        $SPARK_CMD/bin/spark-submit \
        --class com.hvn.hfmt.processing.HFMTDataStreaming \
        --master ''' + sparkMaster + ''' \
        --deploy-mode cluster --supervise --executor-memory 1G \
        --total-executor-cores 1 ''' + jarFile + ''' ''' + referenceFile + ''' > output 2>&1
    '''
}

def getSparkMaster(String env)
{
    String spark = ''

    if(env == DEV){
        spark = DEV_SPARK_MASTER
    }
    else if(env == QC){
        spark = QC_SPARK_MASTER
    }
    else if(env == UAT){
        spark = UAT_SPARK_MASTER
    }
    else if(env == LOCAL_UAT){
        spark = LOCAL_UAT_SPARK_MASTER
    }

    return spark
}