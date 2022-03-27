FROM icr.io/appcafe/open-liberty:full-java11-openj9-ubi

ARG VERSION=1.0
ARG REVISION=SNAPSHOT

LABEL \
  org.opencontainers.image.authors="My Name" \
  org.opencontainers.image.vendor="Open Liberty" \
  org.opencontainers.image.url="local" \
  org.opencontainers.image.source="https://github.com/OpenLiberty/guide-microprofile-reactive-messaging" \
  org.opencontainers.image.version="$VERSION" \
  org.opencontainers.image.revision="$REVISION" \
  vendor="Open Liberty" \
  name="inventory" \
  version="$VERSION-$REVISION" \
  summary="" \
  description="This image contains the inventory microservice running with the Open Liberty runtime."

COPY --chown=1001:0 src/main/liberty/config /config/
COPY --chown=1001:0 target/kafka-web.war /config/apps
COPY --chown=1001:0 src/main/liberty/config/resources/security/key.p12 /opt/ol/wlp/output/defaultServer/resources/security/key.p12


EXPOSE 9080 9443 8787

# It is recommended to run the configure.sh when build image for production.
# RUN configure.sh
