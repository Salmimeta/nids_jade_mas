import sys
import os
import pandas as pd
import joblib

# Dynamically find the folder where predict.py is stored
SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))

# Load trained model and encoders using paths relative to this script
rf_model = joblib.load(os.path.join(SCRIPT_DIR, 'rf_model.pkl'))
encoders = joblib.load(os.path.join(SCRIPT_DIR, 'encoders.pkl'))

if len(sys.argv) > 1:
    raw_data = sys.argv[1].split(',')
    
    cols = [
        'duration', 'protocol_type', 'service', 'flag', 'src_bytes', 'dst_bytes',
        'land', 'wrong_fragment', 'urgent', 'hot', 'num_failed_logins', 'logged_in',
        'num_compromised', 'root_shell', 'su_attempted', 'num_root', 'num_file_creations',
        'num_shells', 'num_access_files', 'num_outbound_cmds', 'is_host_login',
        'is_guest_login', 'count', 'srv_count', 'serror_rate', 'srv_serror_rate',
        'rerror_rate', 'srv_rerror_rate', 'same_srv_rate', 'diff_srv_rate',
        'srv_diff_host_rate', 'dst_host_count', 'dst_host_srv_count',
        'dst_host_same_srv_rate', 'dst_host_diff_srv_rate', 'dst_host_same_src_port_rate',
        'dst_host_srv_diff_host_rate', 'dst_host_serror_rate', 'dst_host_srv_serror_rate',
        'dst_host_rerror_rate', 'dst_host_srv_rerror_rate'
    ]
    
    df = pd.DataFrame([raw_data], columns=cols)
    
    for col in ['protocol_type', 'service', 'flag']:
        if col in df.columns:
            df[col] = encoders[col].transform(df[col].astype(str))
            
    df = df.apply(pd.to_numeric)
    
    prediction = rf_model.predict(df)[0]
    print(prediction)