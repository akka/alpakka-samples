package alpakka.sample.sqssample;

class ActorResponseMsg {
    final String data;

    ActorResponseMsg(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ActorResponseMsg(" + data + ")";
    }
}
