ARG BASE
FROM ${BASE}
WORKDIR /databus
EXPOSE 5284
ADD databus /databus
RUN chmod +x /databus/databus && \
    printf './databus > log.txt 2>&1' >> /databus/start_databus.sh && \
    chmod +x /databus/start_databus.sh

ENTRYPOINT ["/databus/start_databus.sh"]