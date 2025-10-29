def sdkVersionAnalysis(lang,defsdk){
    def sdkVersion
    def result = [:]
    def ymlFile = new File("/Users/jinang/.jenkins/workspace/demo/.matrix.yml")
    def jsonFile = new File(".package.json")
    def existsYml = ymlFile.exists()
    try{
        buildStatusCheck = existsYml ? ymlFile.text : jsonFile.text
    }catch(e){
        if(defsdk=='java17'){
            result.readFlag  = "success"
            result.buildType  = "mvn"
            result.msg  = "JDK17 SDK版本且非matrix类应用，使用默认构建工具 mvn"
            return result
        }else{
            result.msg = "自定义构建模式，但没有 package.json 文件,构建中止 (JDK多版本构建模式请参考:<a href='https://wiki.itcjf.com/pages/viewpage.action?pageId=65871902'>模式定义</a> )"
            result.readFlag  = "fail"
            return result
        }
    }
    println((existsYml ? ".matrix.yml:\n" : "package.json\n:") + buildStatusCheck)
    if(existsYml){
        buildStatusCheck = sh(returnStdout: true, script: '''
            python3 -c "import yaml; print(yaml.safe_load(open('.matrix.yml')))"
        ''')
    }
    def responseMap =  new groovy.json.JsonSlurper().parseText(buildStatusCheck) as Map
    try{
        sdkVersion = responseMap.'language'.'version' 
        if(lang == "java"){
            if(sdkVersion in ['java17', 'java1.8', 'java1.7']){
                echo "java sdkVersion: $sdkVersion"
                // echo sdkVersion
                // echo "sdkVersion的类型是: ${sdkVersion.getClass()}"
                result.sdkVersion = sdkVersion
            }else{
                result.readFlag = "fail"
                result.msg = "项目 package.json 文件 sdkVersion 非标准版本($sdkVersion,标准版列表: java17、java1.8、java1.7),构建中止"
                return result
            }
        }else if(lang == "python"){
            echo "python sdkVersion: $sdkVersion"
        }else if(lang == "nodejs"){
            echo "nodejs sdkVersion: $sdkVersion"
        }else if(lang == "c"){
            echo "c sdkVersion: $sdkVersion" 
        }
    }catch(e){
        result.readFlag = "fail"
        result.msg = "项目 package.json 文件获取 sdkversion 异常,构建中止,exception: $e"
        return result
    }
    try{
        echo "java baseImage : $baseImage"
        result.readFlag = "success"
        result.msg = "获取 sdkVersion,baseImage 成功"
        result.baseImage= responseMap.'language'.'base_image' ?: ''
    }catch(e){
        result.readFlag = "success"
        result.msg = "项目 package.json 文件获取 sdkVersion 成功,baseImage 未定义,以CMDB中定义为准"
        result.baseImage= ''
    }
    if(defsdk=='java17'){
        try{
            result.buildType = responseMap.'buildType' ?: 'mvn'
            echo "buildType : $result"
            result.readFlag = "success"
            result.msg = "获取 buildType 成功"
        }catch(e){
            result.buildType = 'mvn'
            result.readFlag = "success"
            result.msg = "项目 package.json 文件获取 buildType 失败,默认使用 mvn 构建"
        } 
    }else{
        result.buildType = 'mvn'
    }
    return result
}