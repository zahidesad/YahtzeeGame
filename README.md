# YahtzeeGame – Networked Two-Player Dice Game  
> Computer Networks Laboratory Project – Spring 2025  

![Build](https://img.shields.io/badge/Java-21-blue?logo=openjdk)
![License](https://img.shields.io/badge/License-Apache%202.0-green)

---

## ✨ Features
* **Online multiplayer** – two remote players matched automatically over TCP  
* **AWS-hosted server** – lightweight, headless, thread-pooled  
* **Modern Swing GUI** – FlatLaf skin, custom dice widgets & animated lobby  
* **Rule-complete Yahtzee logic** – upper-section bonus, Yahtzee bonuses, time-outs, concede  
* **Replay without restart** – return to lobby and start a new match instantly  

---

## 📸 Screenshot

<p align="center">
  <img src="src\main\resources\yahtzee\images\lobby-panel.png" alt="Lobby Panel" width="500"/>
</p>

---

<p align="center">
  <img src="src\main\resources\yahtzee\images\game-screen.png" alt="Game Screen" width="500"/>
</p>

---

## 🖼 High-Level Architecture

<p align="center">
  <img src="src\main\resources\yahtzee\images\high-level-architecture.png" alt="Architecture diagram" width="650"/>
</p>

*Both clients open a persistent TCP socket to the public AWS server.  
Each pair of sockets is handled by one `GameSession` running inside the server’s thread pool.*

## Low-Level Architecture

<p align="center">
  <img src="src\main\resources\yahtzee\images\diagram.png" alt="Architecture diagram" width="650"/>
</p>

---

## 📂 Project Structure

```
└── zahidesad-yahtzeegame/
    ├── README.md
    ├── LICENSE
    ├── pom.xml
    └── src/
        └── main/
            ├── java/
            │   ├── server/
            │   │   ├── GameSession.java
            │   │   ├── PlayerHandler.java
            │   │   └── YahtzeeServer.java
            │   └── yahtzee/
            │       ├── YahtzeeFrame.java
            │       ├── controller/
            │       │   └── GameController.java
            │       ├── model/
            │       │   ├── Dice.java
            │       │   ├── Game.java
            │       │   ├── Player.java
            │       │   ├── ScoreCard.java
            │       │   ├── ScoreEntry.java
            │       │   └── Categories/
            │       │       ├── Bonus.java
            │       │       ├── Category.java
            │       │       ├── Chance.java
            │       │       ├── FourOfAKind.java
            │       │       ├── FullHouse.java
            │       │       ├── LargeStraight.java
            │       │       ├── SmallStraight.java
            │       │       ├── ThreeOfAKind.java
            │       │       ├── Total.java
            │       │       ├── UpperCategory.java
            │       │       └── Yahtzee.java
            │       ├── network/
            │       │   ├── Message.java
            │       │   ├── MessageType.java
            │       │   └── NetworkClient.java
            │       └── view/
            │           ├── GamePanel.java
            │           ├── LobbyPanel.java
            │           ├── Resettable.java
            │           ├── ScoreBoard.java
            │           ├── ScoreGroup.java
            │           ├── StaticScoreGroup.java
            │           └── YahtzeeDice.java
            └── resources/
                └── yahtzee/
                    └── images/
```


---

## ⚙️ Requirements
* **JDK 21** (tested with Amazon Corretto 21)  
* **Maven 3.9+**  
* (Optional) an AWS EC2 instance for server deployment  

---

## 🛠️ Build & Run Locally
```bash
# clone
git clone https://github.com/<username>/zahidesad-yahtzeegame.git
cd zahidesad-yahtzeegame

# package fat-jar
mvn clean package

# start server (local test)
java -cp target/YahtzeeGame-*-jar-with-dependencies.jar server.YahtzeeServer

# start client (each player runs one instance)
java -cp target/YahtzeeGame-*-jar-with-dependencies.jar yahtzee.YahtzeeFrame
```

The default client connects to `localhost`; edit
`yahtzee/view/LobbyPanel.java → SERVER_IP` or type the public IP at runtime.

---

## ☁️ Deploying Server on AWS

1. Launch an Ubuntu 22.04 t3.micro instance.

2. Open TCP port 12345 in the security group.

3. Copy the fat-jar to /home/ubuntu/yahtzee/.

4. Add a simple systemd unit (sample below) and enable it.

```
# /etc/systemd/system/yahtzee.service
[Unit]
Description=Yahtzee Game Server
After=network.target

[Service]
WorkingDirectory=/home/ubuntu/yahtzee
ExecStart=/usr/bin/java -jar YahtzeeGame-*-jar-with-dependencies.jar
Restart=always
User=ubuntu

[Install]
WantedBy=multi-user.target
```

```
sudo systemctl daemon-reload
sudo systemctl enable --now yahtzee
journalctl -u yahtzee -f   # follow logs
```

Clients now connect to the instance’s public IPv4.

---

## 📝 License

This project is licensed under the Apache License 2.0

---

## 🙏 Acknowledgements

FlatLaf – modern open-source Look-and-Feel for Swing

Gson – JSON parsing and serialisation

Enjoy playing Yahtzee online!
