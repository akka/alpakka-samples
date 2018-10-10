package alpakka.sample.sqssample;

import akka.actor.AbstractActor;

class EnrichActor extends AbstractActor {

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Integer.class, message -> {
                    Main.log.debug("actor received '{}'", message);
                    sender().tell(new ActorResponseMsg(message.toString()), self());
                })
                .build();
    }
}
