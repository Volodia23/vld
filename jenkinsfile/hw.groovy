
pipeline {
    agent{node('master')}
    stages {
        stage('Cleaning WS and get file from repo') {
            steps {
                script {
                    cleanWs()
                    withCredentials([
                        usernamePassword(credentialsId: 'srv_sudo',
                        usernameVariable: 'username',
                        passwordVariable: 'password')
                    ]) {
                        try {
                            sh "echo '${password}' | sudo -S docker stop es"
                            sh "echo '${password}' | sudo -S docker container rm es"
                        } catch (Exception e) {
                            print 'container not exist, skip clean'
                        }
                    }
                }
                script {
                    echo 'UPD REPO'
                    checkout([$class                           : 'GitSCM',
                              branches                         : [[name: '*/master']],
                              doGenerateSubmoduleConfigurations: false,
                              extensions                       : [[$class           : 'RelativeTargetDirectory',
                                                                   relativeTargetDir: 'auto']],
                              submoduleCfg                     : [],
                              userRemoteConfigs                : [[credentialsId: '	EgorShangin', url: 'https://github.com/EgorShangin/hw.git']]])
                }
            }
        }
        stage ('Build & run docker image'){
            steps{
                script{
                     withCredentials([
                        usernamePassword(credentialsId: 'srv_sudo',
                        usernameVariable: 'username',
                        passwordVariable: 'password')
                    ]) {

                        sh "echo '${password}' | sudo -S docker build ${WORKSPACE}/auto -t es_nginx"
                        sh "echo '${password}' | sudo -S docker run -d -p 8144:80 --name es -v /home/adminci/study_ansible/Shangin:/result es_nginx"
                    }
                }
            }
        }
        stage ('write info into the file'){
            steps{
                script{
                    withCredentials([
                        usernamePassword(credentialsId: 'srv_sudo',
                        usernameVariable: 'username',
                        passwordVariable: 'password')
                    ]) {
                        
                        sh "echo '${password}' | sudo -S docker exec -t es bash -c 'df -h > /result/info.txt'"
                        sh "echo '${password}' | sudo -S docker exec -t es bash -c 'top -n 1 -b >> /result/info.txt'"
                    }
                }
            }
        }
        
    }

    
}
