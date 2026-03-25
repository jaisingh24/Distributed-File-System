# 🚀 Distributed File System (DFS) - Java (Socket-Based)

## 📌 Overview

This project is a **Distributed File System (DFS)** implemented using **Java sockets**, simulating a simplified version of systems like HDFS and GFS. It enables files to be split into chunks and distributed across multiple storage nodes, with a master node managing metadata and coordination.

The system demonstrates core concepts of:
- Distributed Systems
- Networking (Sockets)
- Concurrency (Multithreading)
- Data Management

---

## 🧠 Architecture
Client → Master Node → Storage Nodes

### 🔹 Master Node
- Registers storage nodes
- Maintains metadata (file → chunks → nodes)
- Provides node info to client
- Handles file lookup for download

### 🔹 Storage Nodes
- Store file chunks locally
- Serve chunk data on request
- Communicate with master for registration

### 🔹 Client
- Splits files into chunks
- Uploads chunks to storage nodes
- Sends metadata to master
- (Download feature in progress)

---

## ⚙️ Features

### ✅ Implemented
- Socket-based communication (no frameworks)
- File chunking (configurable size)
- Distributed storage across multiple nodes
- Round-robin load balancing
- Metadata tracking in master node
- Binary-safe file transfer using Data Streams

### 🚧 Upcoming Features
- File download & reconstruction
- Fault tolerance (node failure handling)
- Replication (multiple copies of chunks)
- Retry mechanism for failed transfers

---

## 🧱 Tech Stack

- **Language:** Java  
- **Networking:** Java Sockets (TCP)  
- **Concurrency:** Multithreading  
- **Storage:** Local File System  

---

## 📂 Project Structure
dfs-system/
├── master/
│ └── MasterServer.java
├── storage/
│ └── StorageNode.java
├── client/
│ └── Client.java
└── test.txt


---

## 🔄 Workflow

### 📤 Upload Flow

1. Client reads file
2. Splits file into chunks
3. Requests storage nodes from master
4. Sends chunks to storage nodes
5. Sends metadata to master

---

### 📥 Download Flow (Planned)

1. Client requests file metadata from master
2. Master returns chunk locations
3. Client fetches chunks from storage nodes
4. Client merges chunks to recreate file

---

## 🧪 How to Run

### 1️⃣ Start Master Server

---

### 2️⃣ Start Storage Nodes

---

### 3️⃣ Run Client

---

## 📁 Output

After running the system, chunk files will be created:

Each chunk is stored on different nodes.

---

## 🧠 Key Learnings

- Handling binary data over sockets
- Designing custom communication protocols
- Avoiding stream corruption issues
- Managing distributed metadata
- Implementing chunk-based storage systems

---

## 🏆 Resume Description

Built a distributed file system using Java sockets with chunk-based storage, metadata management, and multi-node communication, demonstrating concepts of distributed systems and scalable architecture.

---

## 🔥 Future Improvements

- Add replication for fault tolerance
- Implement consistent hashing
- Add health checks (heartbeat system)
- Build web UI dashboard

---

## 👨‍💻 Author

Jai Singh Katiyar
