# Textly - Real-Time Multi-Client Chat Application

![Java](https://img.shields.io/badge/Java-17%2B-blue) ![JavaFX](https://img.shields.io/badge/JavaFX-19-purple) ![Sockets](https://img.shields.io/badge/TCP%2FIP-Sockets-orange) ![Concurrency](https://img.shields.io/badge/Thread--per--client-Model-green)

## 📌 Overview
A chat application demonstrating core networking principles with:
- **Java Sockets** for low-level communication  
- **JavaFX** for modern GUI  
- **Thread-per-client** architecture for scalability  

<!--
![Textly Demo](https://via.placeholder.com/800x500?text=Textly+Chat+Application+Screenshot)  
*(Replace with actual screenshot)*
-->

## ✨ Features
| Category        | Implementation Details |
|-----------------|-----------------------|
| **Messaging**   | Real-time broadcast |
| **Concurrency** | Supports 10+ concurrent users via thread isolation |
| **GUI**         | Color-coded messages (User/Server/System), Dynamic user list |
| **Stability**   | Graceful disconnection handling, Input validation |

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

### 🚀 Installation
- JDK 17+
- JavaFX 19 SDK

🔗 Connect: [GitHub](https://github.com/aslams2020) | [LinkedIn](https://www.linkedin.com/in/aslamsayyad02/) <br>
📧 Contact: sayyadaslam2020@gmail.com
