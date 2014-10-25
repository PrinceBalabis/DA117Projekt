package se.mah.ab7271.wolf;

public interface Callback {
    public void updateDisplays(String question, String answer);
    public void makeQuery(String question);
    public void enableSpeechToText();
}
