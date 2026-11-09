package src;

import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ModelAgent extends Agent {
    protected void setup() {
        System.out.println("[Model Agent] Started: " + getAID().getName());

        // Register in DF
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("machine-learning-model");
        sd.setName(getLocalName());
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Behavior: Listen for preprocessed data and run prediction
        addBehaviour(new CyclicBehaviour() {
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    String inputData = msg.getContent();
                    System.out.println("[Model Agent] Received data for classification.");

                    String prediction = runPythonPrediction(inputData);
                    System.out.println("[Model Agent] Classification output: " + prediction);

                    // Forward result to ValidationAgent
                    ACLMessage outMsg = new ACLMessage(ACLMessage.INFORM);
                    outMsg.addReceiver(new AID("ValidationAgent", AID.ISLOCALNAME));
                    outMsg.setContent(prediction);
                    send(outMsg);
                } else {
                    block();
                }
            }
        });
    }

    private String runPythonPrediction(String data) {
        try {
            ProcessBuilder pb = new ProcessBuilder("python", "training/predict.py", data);
            pb.redirectErrorStream(true); // Directs Python error streams to Java output
            Process p = pb.start();

            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                output.append(line);
            }
            p.waitFor();
            return output.toString().trim();
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (Exception e) {
        }
    }
}