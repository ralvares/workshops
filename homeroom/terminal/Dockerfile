FROM quay.io/centos/centos:stream8

USER root

RUN HOME=/root && \
INSTALL_PKGS="npm unzip nano python3-devel python3-setuptools python3-pip bash-completion \
        cadaver jq tmux sudo buildah podman atlas-devel gcc-gfortran \
        libffi-devel libtool-ltdl httpd httpd-devel mod_ssl mod_ldap mod_session python3-mod_wsgi supervisor mercurial \
        ansible python3-openshift python3-wheel python3-virtualenv python3-jmespath bc java-1.8.0-openjdk java-1.8.0-openjdk-devel" && \
yum update -y && yum install -y epel-release && yum -y --setopt=tsflags=nodocs install $INSTALL_PKGS && \
yum -y clean all --enablerepo='*'

RUN HOME=/root && \
    (curl -s -0 https://downloads.apache.org/maven/maven-3/3.6.3/binaries/apache-maven-3.6.3-bin.tar.gz | \
    tar -zx -C /usr/local) && \
    mv /usr/local/apache-maven-3.6.3 /usr/local/maven && \
    ln -sf /usr/local/maven/bin/mvn /usr/local/bin/mvn

RUN HOME=/root && \
    curl -sL -0 https://services.gradle.org/distributions/gradle-6.6.1-bin.zip -o /tmp/gradle-6.6.1-bin.zip && \
    unzip /tmp/gradle-6.6.1-bin.zip -d /usr/local/ && \
    rm /tmp/gradle-6.6.1-bin.zip && \
    mv /usr/local/gradle-6.6.1 /usr/local/gradle && \
    ln -sf /usr/local/gradle/bin/gradle /usr/local/bin/gradle

# Install OpenShift clients.

RUN curl -s -o /tmp/oc.tar.gz https://mirror.openshift.com/pub/openshift-v4/clients/oc/latest/linux/oc.tar.gz && \
    tar -C /usr/local/bin -zxf /tmp/oc.tar.gz oc && \
    chmod +x /usr/local/bin/oc && \
    rm /tmp/oc.tar.gz

# Install Odo clients

RUN curl -sL https://mirror.openshift.com/pub/openshift-v4/clients/odo/latest/odo-linux-amd64 -o /usr/local/bin/odo && \
    chmod +x /usr/local/bin/odo

# Install Kubernetes client.

RUN curl -sL -o /usr/local/bin/kubectl https://storage.googleapis.com/kubernetes-release/release/v1.20.0/bin/linux/amd64/kubectl && \
    chmod +x /usr/local/bin/kubectl

# install roxctl - RHACS

RUN curl -sL -o /usr/local/bin/roxctl https://mirror.openshift.com/pub/rhacs/assets/latest/bin/Linux/roxctl && \
    chmod +x /usr/local/bin/roxctl
    
RUN curl -sL -o /tmp/helm.tar.gz https://get.helm.sh/helm-v3.6.1-linux-amd64.tar.gz && \
    tar -C /tmp -zxf /tmp/helm.tar.gz linux-amd64/helm && \
    mv /tmp/linux-amd64/helm /usr/local/bin/helm && chmod +x /usr/local/bin/helm

COPY bin/. /usr/local/bin/

# Common environment variables.

ENV PYTHONUNBUFFERED=1 \
    PYTHONIOENCODING=UTF-8 \
    LC_ALL=en_US.UTF-8 \
    LANG=en_US.UTF-8 \
    PIP_NO_CACHE_DIR=off

# Install Butterfly

COPY butterfly /opt/workshop/butterfly

RUN HOME=/opt/workshop/butterfly && \
    cd /opt/workshop/butterfly && \
    virtualenv /opt/workshop/butterfly && \
    source /opt/workshop/butterfly/bin/activate && \
    pip3 install --no-cache-dir -r requirements.txt && \
    /opt/workshop/butterfly/install-fonts.sh && \
    /opt/workshop/butterfly/fixup-styles.sh

# Install gateway application using SCL Node.js 10.

COPY gateway /opt/workshop/gateway

RUN HOME=/opt/workshop/gateway && \
    cd /opt/workshop/gateway && \
    npm install --production && \
    npm audit fix && \
    chown -R 1001:0 /opt/workshop/gateway/node_modules && \
    fix-permissions /opt/workshop/gateway/node_modules

# Finish environment setup.

ENV BASH_ENV=/opt/workshop/etc/profile \
    ENV=/opt/workshop/etc/profile \
    PROMPT_COMMAND=". /opt/workshop/etc/profile"

COPY s2i/. /usr/libexec/s2i/

COPY etc/. /opt/workshop/etc/

RUN echo "auth requisite pam_deny.so" >> /etc/pam.d/su && \
    sed -i.bak -e 's/^%wheel/# %wheel/' /etc/sudoers && \
    chmod g+w /etc/passwd

RUN sed -i.bak -e 's/driver = "overlay"/driver = "vfs"/' \
      /etc/containers/storage.conf

RUN sed -i.bak \
      -e "/\[registries.search\]/{N;s/registries = \[.*\]/registries = ['docker.io', 'registry.fedoraproject.org', 'quay.io', 'registry.centos.org']/}" \
      -e "/\[registries.insecure\]/{N;s/registries = \[.*\]/registries = ['docker-registry.default.svc:5000','image-registry.openshift-image-registry.svc:5000']/}" \
      /etc/containers/registries.conf

COPY containers/libpod.conf /etc/containers/

# COPY containers/sudoers.d/ /etc/sudoers.d/

ENV BUILDAH_ISOLATION=chroot

RUN mkdir -p /opt/app-root/etc/init.d && \
    mkdir -p /opt/app-root/etc/profile.d && \
    mkdir -p /opt/app-root/src && \
    mkdir -p /opt/app-root/etc/supervisor && \
    mkdir -p /opt/app-root/gateway/routes && \
    chown -R 1001:0 /opt/app-root && \
    fix-permissions /opt/app-root

COPY .bash_profile /opt/app-root/src/.bash_profile

RUN chown -R 1001:0 /opt/app-root && \
    fix-permissions /opt/app-root -P

RUN cp -rf /opt/workshop/etc/supervisord.conf /etc/supervisord.conf

LABEL io.k8s.display-name="Terminal" \
      io.openshift.expose-services="10080:http" \
      io.openshift.tags="builder,butterfly" \
      io.openshift.s2i.scripts-url=image:///usr/libexec/s2i

EXPOSE 10080

USER 1001

CMD [ "/usr/libexec/s2i/run" ]
