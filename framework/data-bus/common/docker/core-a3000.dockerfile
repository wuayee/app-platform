ARG BASE
FROM ${BASE}
WORKDIR /databus
EXPOSE 5284
ADD databus /databus
RUN chmod +x /databus/databus

ENTRYPOINT ["/databus/databus"]