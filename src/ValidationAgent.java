package src;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class ValidationAgent extends Agent {
    protected void setup() {
        System.out.println("[Validation Agent] Started: " + getAID().getName());

        // Register in DF
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("validation-metrics");
        sd.setName(getLocalName());
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Behavior: Listen for prediction result and output validation status
        addBehaviour(new CyclicBehaviour() {
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    String status = msg.getContent();
                    System.out.println("================ SYSTEM VALIDATION REPORT ================");
                    System.out.println("Incoming packet status: " + status.toUpperCase());
                    if (status.equalsIgnoreCase("attack")) {
                        System.out.println("ALERT: Intrusion detected by SMA monitoring pipeline!");
                    } else {
                        System.out.println("STATUS: Safe network connection.");
                    }
                    System.out.println("==========================================================");
                } else {
                    block();
                }
            }
        });
    }

    protected void takeDown() {
        try { DFService.deregister(this); } catch (Exception e) {}
    }
}