package src;

import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class DataPreprocessingAgent extends Agent {
    protected void setup() {
        System.out.println("[Preprocessing Agent] Started: " + getAID().getName());

        // Register in DF
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("data-preprocessing");
        sd.setName(getLocalName());
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Behavior: Send a sample row of network traffic to ModelAgent
        addBehaviour(new OneShotBehaviour() {
            public void action() {
                // Sample NSL-KDD record (41 features, comma-separated)
                String sampleData = "0,tcp,ftp_data,SF,491,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,2,0,0,0,0,1,0,0,150,25,0.17,0.03,0.17,0,0,0,0,0";
                
                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                msg.addReceiver(new AID("ModelAgent", AID.ISLOCALNAME));
                msg.setContent(sampleData);
                send(msg);
                System.out.println("[Preprocessing Agent] Cleaned and sent network packet data to ModelAgent.");
            }
        });
    }

    protected void takeDown() {
        try { DFService.deregister(this); } catch (Exception e) {}
    }
}