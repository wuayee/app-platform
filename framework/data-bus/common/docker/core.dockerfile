FROM ubuntu:jammy
WORKDIR /home
EXPOSE 5284
ADD databus /home/databus
RUN chmod +x /home/databus && \
    printf './databus > log.txt 2>&1' >> /home/start.sh && \
    chmod +x /home/start.sh
CMD ["/bin/bash", "/home/start.sh"]