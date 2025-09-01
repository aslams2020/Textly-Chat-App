# Textly - Real-Time Multi-Client Chat Application

![Java](https://img.shields.io/badge/Java-17%2B-blue) ![JavaFX](https://img.shields.io/badge/JavaFX-19-purple) ![Sockets](https://img.shields.io/badge/TCP%2FIP-Sockets-orange) ![Concurrency](https://img.shields.io/badge/Thread--per--client-Model-green)

## 📌 Overview
A chat application demonstrating core networking principles with:
- **Java Sockets** for low-level communication  
- **JavaFX** for modern GUI  
- **Thread-per-client** architecture for scalability  

## ✨ Features
| Category        | Implementation Details |
|-----------------|-----------------------|
| **Messaging**   | Real-time broadcast |
| **Concurrency** | Supports 10+ concurrent users via thread isolation |
| **GUI**         | Color-coded messages (User/Server/System), Dynamic user list |
| **Stability**   | Graceful disconnection handling, Input validation |

## Installation & Running

1. Compile The Project  : 
```java
mvn clean compile
```


2. Run the Server : 
```java
mvn exec:java -D"exec.mainClass=server.Server"
```

3. Run Clients (in separate terminals)
```java
mvn exec:java -D"exec.mainClass=client.Client"
```

## How to Use
1. Start the server first

2. Launch multiple client instances

3. Enter a username (3-20 alphanumeric characters)

4. Start chatting in real-time!

5. Type "exit" to leave the chat

## 🛠 Tech Stack
```mermaid
pie
    title Technology Distribution
    "Java Sockets" : 35
    "JavaFX GUI" : 30
    "Threading" : 25
    "Error Handling" : 10
```
```mermaid
sequenceDiagram
    participant Client A
    participant Server
    participant Client B
    
    Client A->>Server: Connect (Port 1234)
    Server->>Client A: UserList
    Client A->>Server: Send Message
    Server->>Client B: Broadcast Message
    Client B->>Server: Typing Notification
```

**Screenshot :**
![image](https://github.com/user-attachments/assets/70a79f6e-d147-4c31-bd7d-d3e4c95952b8)


### 🚀 Installation
- JDK 17+
- JavaFX 19 SDK

🔗 Connect: [GitHub](https://github.com/aslams2020) | [LinkedIn](https://www.linkedin.com/in/aslamsayyad02/) <br>
📧 Contact: sayyadaslam2020@gmail.com

<!--
mvn exec:java -D"exec.mainClass=server.Server"
mvn exec:java "-Dexec.mainClass=client.Client"
-->