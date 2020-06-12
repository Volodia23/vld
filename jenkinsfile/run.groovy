
pipeline {
    agent{node('master')}
    stages {
        stage('Dowload project') {
            steps {
                script {
                    cleanWs()
                    //git(branch: 'master', credentialsId: 'IvanSitnikovGit', url: 'https://github.com/sitozzz/jenkins_education.git')
                }
                script {
                    echo 'Start download project'
                    checkout([$class                           : 'GitSCM',
                              branches                         : [[name: '*/master']],
                              doGenerateSubmoduleConfigurations: false,
                              extensions                       : [[$class           : 'RelativeTargetDirectory',
                                                                   relativeTargetDir: 'auto']],
                              submoduleCfg                     : [],
                              userRemoteConfigs                : [[credentialsId: 'IvanSitnikovGit', url: 'https://github.com/sitozzz/jenkins_education.git']]])
                }
            }
        }
        stage ('Create docker image'){
            steps{
                script{
                    sh "docker build ${WORKSPACE}/auto -t webapp"
                    sh "docker run -d webapp"
                }
            }
        }
        
    }

    
}
