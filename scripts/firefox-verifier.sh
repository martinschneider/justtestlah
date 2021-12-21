#/bin/bash
wget -O - http://deb.opera.com/archive.key | sudo apt-key add -
sudo sh -c 'echo "deb http://deb.opera.com/opera-stable/ stable non-free" >> /etc/apt/sources.list.d/opera.list'
sudo apt-get update 
sudo apt install firefox -y
sudo apt-get install -y xvfb libxkbcommon-x11-0 libxcb-icccm4 libxcb-image0 libxcb-keysyms1 libxcb-randr0 libxcb-render-util0 libxcb-xinerama0 libxcb-xinput0 libxcb-xfixes0
sudo /usr/bin/Xvfb "$DISPLAY" -screen 0 1280x1024x24 &
mvn install -DskipTests && cd justtestlah-core
mvn test -Djava.version=17 -Dselenide.browserBinary=$(which firefox) -Dselenide.browser=firefox -Dselenide.headless=false -Dtest=qa.justtestlah.integration.RunnerTest