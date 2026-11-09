import pandas as pd
import numpy as np
from sklearn.ensemble import RandomForestClassifier
from sklearn.metrics import accuracy_score, classification_report
from sklearn.preprocessing import LabelEncoder
import joblib

# 1. Define Official NSL-KDD Column Names
columns = [
    'duration', 'protocol_type', 'service', 'flag', 'src_bytes', 'dst_bytes',
    'land', 'wrong_fragment', 'urgent', 'hot', 'num_failed_logins', 'logged_in',
    'num_compromised', 'root_shell', 'su_attempted', 'num_root', 'num_file_creations',
    'num_shells', 'num_access_files', 'num_outbound_cmds', 'is_host_login',
    'is_guest_login', 'count', 'srv_count', 'serror_rate', 'srv_serror_rate',
    'rerror_rate', 'srv_rerror_rate', 'same_srv_rate', 'diff_srv_rate',
    'srv_diff_host_rate', 'dst_host_count', 'dst_host_srv_count',
    'dst_host_same_srv_rate', 'dst_host_diff_srv_rate', 'dst_host_same_src_port_rate',
    'dst_host_srv_diff_host_rate', 'dst_host_serror_rate', 'dst_host_srv_serror_rate',
    'dst_host_rerror_rate', 'dst_host_srv_rerror_rate', 'label', 'difficulty_level'
]

print("Loading datasets...")
train_df = pd.read_csv('../dataset/KDDTrain+.txt', header=None, names=columns)
test_df = pd.read_csv('../dataset/KDDTest+.txt', header=None, names=columns)

# Drop difficulty_level (not a feature)
train_df = train_df.drop(columns=['difficulty_level'])
test_df = test_df.drop(columns=['difficulty_level'])

# 2. Convert Target to Binary Label: 'normal' vs 'attack'
train_df['label'] = train_df['label'].apply(lambda x: 'normal' if x == 'normal' else 'attack')
test_df['label'] = test_df['label'].apply(lambda x: 'normal' if x == 'normal' else 'attack')

# Separate Features (X) and Target (y)
X_train = train_df.drop(columns=['label'])
y_train = train_df['label']
X_test = test_df.drop(columns=['label'])
y_test = test_df['label']

# 3. Categorical Encoding
categorical_cols = ['protocol_type', 'service', 'flag']
encoders = {}

for col in categorical_cols:
    le = LabelEncoder()
    # Combine unique values from both sets to prevent unseen label errors
    all_categories = pd.concat([X_train[col], X_test[col]]).astype(str)
    le.fit(all_categories)
    
    X_train[col] = le.transform(X_train[col].astype(str))
    X_test[col] = le.transform(X_test[col].astype(str))
    encoders[col] = le

# 4. Train the Random Forest Classifier
print("Training Random Forest Classifier...")
rf_model = RandomForestClassifier(n_estimators=100, random_state=42)
rf_model.fit(X_train, y_train)

# 5. Evaluate on Test Data (Evaluation Metrics Requirement - Task 4)
y_pred = rf_model.predict(X_test)

print("\n================ EVALUATION METRICS ================")
print(f"Accuracy: {accuracy_score(y_test, y_pred) * 100:.2f}%\n")
print(classification_report(y_test, y_pred))

# 6. Save Trained Model and Encoders for JADE Agents
joblib.dump(rf_model, 'rf_model.pkl')
joblib.dump(encoders, 'encoders.pkl')
print("\nModel saved successfully as 'rf_model.pkl'!")