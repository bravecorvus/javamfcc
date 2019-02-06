# MFCC Coefficient Extractor Program
> Andrew Lee

Simplified program originally hosted at the [marrytts repo](https://github.com/marytts/marytts/blob/4aada82d17abf01a7ea0d8bce755866ebaa6e235/marytts-runtime/src/main/java/marytts/htsengine/HTSEngineTest.java).

## Running the program `cd` into this directory, start Docker, and run the commands

1. `cd` into this directory
2. Replace [output/audio.wav](output/audio.wav) with a WAV audio file of the same name
3. Run the following commands
```
docker build -t javamfcc:latest . && docker run --name javamfcc --rm -it -v $PWD/output:/output javamfcc:latest /bin/sh
```
