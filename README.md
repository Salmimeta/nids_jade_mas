# Network Intrusion Detection System (NIDS) using JADE & Machine Learning

An end-to-end Multi-Agent System (MAS) for network anomaly detection, developed for the **M2 SMA (Systèmes Multi-Agents)** coursework. This project integrates a Java-based **JADE** agent framework with a Python **Random Forest** classification model trained on the **NSL-KDD dataset**.

---

## 🏗️ System Architecture

The project follows a 3-agent cooperative pipeline:

1. **`DataPreprocessingAgent` (Java):** Simulates network traffic ingestion, pre-processes incoming packet features, and forwards them via ACL messages.
2. **`ModelAgent` (Java / Python Bridge):** Receives packet data, invokes the Python runtime (`predict.py`) using `ProcessBuilder`, and queries the pre-trained Machine Learning model.
3. **`ValidationAgent` (Java):** Evaluates the classification result (`NORMAL` vs `ATTACK`) and logs system alerts in real-time.


---

## 📁 Project Structure

```text
TP2_SMA/
├── dataset/
│   ├── KDDTrain+.txt              # NSL-KDD Training Dataset
│   └── KDDTest+.txt               # NSL-KDD Test Dataset
├── lib/
│   └── jade.jar                   # JADE Framework Library (v4.6.1)
├── src/
│   ├── DataPreprocessingAgent.java # Agent 1: Data Provider
│   ├── ModelAgent.java            # Agent 2: ML Execution Bridge
│   └── ValidationAgent.java       # Agent 3: Alert & Report Handler
├── training/
│   ├── training_model.py          # Python model training script
│   ├── predict.py                 # CLI prediction bridge script
│   ├── rf_model.pkl               # Serialized Random Forest Model
│   └── encoders.pkl               # Serialized Categorical Encoders
└── README.md


📊 Machine Learning Model Metrics
The classifier is built using scikit-learn's RandomForestClassifier trained on KDDTrain+.txt and evaluated against the standard KDDTest+.txt benchmark:

Overall Accuracy: ~77.27% (Standard benchmark performance on unseen test attacks)

Attack Precision: 97% (Low false-positive rate on safe traffic)

Normal Traffic Recall: 97% (Reliable identification of legitimate connections)

🚀 Execution & Usage
1. Prerequisites
Java Development Kit (JDK 8+ / 17+)

Python 3.8+ with the required libraries:

Bash
pip install pandas scikit-learn joblib
2. Train the Model (Optional)
The pre-trained .pkl files are included in training/. To retrain from scratch:

Bash
cd training
python training_model.py
cd ..
3. Compile Java Agents
From the root TP2_SMA/ directory:

Bash
javac -cp "lib/jade.jar" -d bin src/*.java
4. Run the JADE Pipeline
Launch the JADE platform with the interactive GUI and boot all three agents:

Windows (PowerShell):

PowerShell
java -cp "bin;lib/jade.jar" jade.Boot -gui -agents "ValidationAgent:src.ValidationAgent;ModelAgent:src.ModelAgent;DataPreprocessingAgent:src.DataPreprocessingAgent"
Linux / macOS:

Bash
java -cp "bin:lib/jade.jar" jade.Boot -gui -agents "ValidationAgent:src.ValidationAgent;ModelAgent:src.ModelAgent;DataPreprocessingAgent:src.DataPreprocessingAgent"
🛠️ Tech Stack
Agent Middleware: JADE (Java Agent Development Framework v4.6.1)

Machine Learning: Python (scikit-learn, pandas, joblib)

Dataset: NSL-KDD Network Security Benchmark