FROM quay.io/ralvares/workshops:terminal

USER root

RUN HOME=/opt/workshop/reveal.js && \
    mkdir /opt/workshop/reveal.js && \
    cd /opt/workshop/reveal.js && \
    curl -sL -o src.tar.gz https://github.com/hakimel/reveal.js/archive/3.9.2.tar.gz && \
    tar --strip-components 1 -xf src.tar.gz && \
    rm src.tar.gz

COPY renderer /opt/workshop/renderer

RUN HOME=/opt/workshop/renderer && \
    cd /opt/workshop/renderer && \
    npm install --production

COPY gateway/. /opt/workshop/gateway/.

COPY bin/. /usr/local/bin/.
#COPY bin/. /opt/workshop/bin/.
COPY etc/. /opt/workshop/etc/.

USER 1001
