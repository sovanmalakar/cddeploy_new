FROM openjdk:8-jdk-stretch
# Set proxy setting
#ENV HTTP_PROXY=http://proxy-wsa.esl.cisco.com:80
#ENV https_proxy=http://proxy-wsa.esl.cisco.com:80

RUN apt-get update && apt-get install -y git curl && apt-get install sshpass 
#&& apt-get install -y python3.5 
USER ${root}
RUN apt-get install -y python3-pip

ARG user=jenkins
ARG group=jenkins
ARG uid=1000
ARG gid=1000
ARG http_port=8080
ARG agent_port=50000
ARG JENKINS_HOME=/var/jenkins_home

ENV JENKINS_HOME $JENKINS_HOME
ENV JENKINS_SLAVE_AGENT_PORT ${agent_port}

# Jenkins is run with user `jenkins`, uid = 1000
# If you bind mount a volume from the host or a data container,
# ensure you use the same uid
RUN mkdir -p $JENKINS_HOME \
  && chown ${uid}:${gid} $JENKINS_HOME \
  && groupadd -g ${gid} ${group} \
  && useradd -d "$JENKINS_HOME" -u ${uid} -g ${gid} -m -s /bin/bash ${user}

# Jenkins home directory is a volume, so configuration and build history
# can be persisted and survive image upgrades
VOLUME $JENKINS_HOME

# `/usr/share/jenkins/ref/` contains all reference configuration we want
# to set on a fresh new installation. Use it to bundle additional plugins
# or config file with your custom jenkins Docker image.
RUN mkdir -p /usr/share/jenkins/ref/init.groovy.d

# Use tini as subreaper in Docker container to adopt zombie processes
ARG TINI_VERSION=v0.18.0
COPY tini_pub.gpg ${JENKINS_HOME}/tini_pub.gpg
RUN curl -fsSL https://github.com/krallin/tini/releases/download/${TINI_VERSION}/tini-static-$(dpkg --print-architecture) -o /sbin/tini \
  && curl -fsSL https://github.com/krallin/tini/releases/download/${TINI_VERSION}/tini-static-$(dpkg --print-architecture).asc -o /sbin/tini.asc \
  && gpg --no-tty --import ${JENKINS_HOME}/tini_pub.gpg \
  && gpg --verify /sbin/tini.asc \
  && rm -rf /sbin/tini.asc /root/.gnupg \
  && chmod +x /sbin/tini

# jenkins version being bundled in this docker image
ARG JENKINS_VERSION
ENV JENKINS_VERSION ${JENKINS_VERSION:-2.164.3}

# jenkins.war checksum, download will be validated using it
ARG JENKINS_SHA=c851b603e3d320295eed671fde7c661209645c818da9b7564caee8371e52bede

# Can be used to customize where jenkins.war get downloaded from
ARG JENKINS_URL=https://repo.jenkins-ci.org/public/org/jenkins-ci/main/jenkins-war/${JENKINS_VERSION}/jenkins-war-${JENKINS_VERSION}.war

# could use ADD but this one does not check Last-Modified header neither does it allow to control checksum
# see https://github.com/docker/docker/issues/8331
RUN curl -fsSL ${JENKINS_URL} -o /usr/share/jenkins/jenkins.war 
  #&& echo "${JENKINS_SHA}  /usr/share/jenkins/jenkins.war" | sha256sum -c -

ENV JENKINS_UC https://updates.jenkins.io
ENV JENKINS_UC_EXPERIMENTAL=https://updates.jenkins.io/experimental
ENV JENKINS_INCREMENTALS_REPO_MIRROR=https://repo.jenkins-ci.org/incrementals
RUN chown -R ${user} "$JENKINS_HOME" /usr/share/jenkins/ref

# for main web interface:
EXPOSE ${http_port}

# will be used by attached slave agents:
EXPOSE ${agent_port}

ENV COPY_REFERENCE_FILE_LOG $JENKINS_HOME/copy_reference_file.log

USER ${user}
COPY jenkins-support /usr/local/bin/jenkins-support
# from a derived Dockerfile, can use `RUN plugins.sh active.txt` to setup /usr/share/jenkins/ref/plugins from a support bundle
COPY install-plugins.sh /usr/local/bin/install-plugins.sh

# Add/Remove  the plugins you want  
RUN /usr/local/bin/install-plugins.sh dashboard-view:latest 
RUN /usr/local/bin/install-plugins.sh pipeline-stage-view:latest
RUN /usr/local/bin/install-plugins.sh parameterized-trigger:latest
RUN /usr/local/bin/install-plugins.sh blueocean:latest blueocean-dashboard:latest
RUN /usr/local/bin/install-plugins.sh antisamy-markup-formatter:latest
RUN /usr/local/bin/install-plugins.sh filesystem_scm:latest
RUN /usr/local/bin/install-plugins.sh scm-api:latest
RUN /usr/local/bin/install-plugins.sh jdk-tool:latest
RUN /usr/local/bin/install-plugins.sh prometheus:latest
RUN /usr/local/bin/install-plugins.sh docker-plugin:latest
RUN /usr/local/bin/install-plugins.sh docker-plugin:latest

COPY executors.groovy /usr/share/jenkins/ref/init.groovy.d/executors.groovy
COPY job_initialize.sh /var/job_initialize.sh
#COPY /configs/users "$JENKINS_HOME"/users/

USER ${root}
ARG CURRENT_PIPELINE=/var/local/current_pipeline
RUN mkdir -p "$CURRENT_PIPELINE"
COPY /configs/jenkins_home_config.xml "$CURRENT_PIPELINE"/config.xml
COPY /configs/users "$CURRENT_PIPELINE"/users/
RUN mkdir -p "$CURRENT_PIPELINE"/secrets
COPY secrets "$CURRENT_PIPELINE"/secrets

RUN mkdir -p "$CURRENT_PIPELINE"/jobs/cd_jobs
RUN mkdir -p "$CURRENT_PIPELINE"/workspace
RUN mkdir -p "$CURRENT_PIPELINE"/nodes

COPY cd_jobs "$CURRENT_PIPELINE"/jobs/cd_jobs
COPY workspace "$CURRENT_PIPELINE"/workspace
COPY nodes "$CURRENT_PIPELINE"/nodes
COPY jobs_scripts /var/scripts

COPY DownloadLatestBuild /usr/sbin


USER ${user}

COPY jenkins.sh /usr/local/bin/jenkins.sh
COPY tini-shim.sh /bin/tini
ENTRYPOINT ["/sbin/tini", "--", "/usr/local/bin/jenkins.sh" ]
COPY requirements.txt /var/requirements.txt
RUN pip3 install -r /var/requirements.txt

USER ${root}
RUN apt-get install -y libltdl-dev
RUN rm -rf /var/lib/apt/lists/*

