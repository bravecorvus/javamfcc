FROM ascdc/jdk8
WORKDIR /
RUN apt update && apt install -y build-essential unzip cmake gcc g++ clang libatlas-base-dev net-tools mawk gawk csh ffmpeg sox
COPY ./src/ ./
RUN tar -xzf SPTK-3.11.tar.gz
WORKDIR /SPTK-3.11
RUN mkdir /output
RUN touch /tmp.mfc
RUN ./configure
RUN make
RUN make install
WORKDIR /app
COPY ./MFCC.java ./
RUN javac MFCC.java
ENTRYPOINT ["java", "MFCC"]
