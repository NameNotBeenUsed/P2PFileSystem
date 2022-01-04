# P2PFileSystem
CNT5106C - Computer Network

# Group Project - Computer Networks

Group 15: Jiayu Li, Junge Xu, Mingjun Yu

##Video Link
https://uflorida-my.sharepoint.com/:f:/g/personal/jungexu_ufl_edu/Eim8TLHEg1JJuWlrSs-AYPgBLSjLrMB1M3bhwezR47lz0Q?e=cSQNkH

## Project Overview
In this project, we implemented a P2P file sharing software similar to BitTorrent in Java.

There are three peers in our project. The first peer with peerId 1001 has the complete file. The other two peers, who have peerId 1002 and 1003 do not have the file. The two remaining have peerId 1002 and 1003 respectively. They do not have the complete file. 

## Division of Work
Each of us has completed a part of the functional modules in the project.
Mingjun take charge of input and output of the files for send as well as the event logger.
Jiayu finished functions related to the message create and read as well as the linux part.
Junge Xu is responsible for the functions related to peers choicing.
For the message process, Jiayu takes charge of the first 4 types. And Junge takes the other types.
As for the remaining and debuging section, we coorperate with each other and finished it together.

Authors of Files:
PeerProcess.java: Jiayu Li, Junge Xu Mingjun Yu
For others files:
Jiayu Li: Message.java StarRemotePeers.java Utility.java
Junge Xu: DownloadRates.java
Mingjun Yu: Common.java FilePieces.java Logger.java PeerInfo.java
