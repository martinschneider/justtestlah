#!/bin/bash
sudo apt-get update
sudo apt-get install -y libopencv-core4.5
sudo apt-get install -y software-properties-common 
sudo add-apt-repository -y ppa:alex-p/tesseract-ocr
sudo apt-get update 
sudo apt-get install -y tesseract-ocr 
sudo pip install pyocr
sudo apt-get install -y xvfb libxkbcommon-x11-0 libxcb-icccm4 libxcb-image0 libxcb-keysyms1 libxcb-randr0 libxcb-render-util0 libxcb-xinerama0 libxcb-xinput0 libxcb-xfixes0
sudo /usr/bin/Xvfb "$DISPLAY" -screen 0 1280x1024x24 &
sudo wget https://github.com/tesseract-ocr/tessdata_best/raw/main/eng.traineddata -P /usr/share/tesseract-ocr/4.00/tessdata
mvn install -DskipTests && cd justtestlah-demos
mvn test -DjtlProps=$(pwd)/target/test-classes/visual-verifier.properties -Djava.version=17