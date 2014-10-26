package se.mah.ab7271.wolf;


/**
 *  This Interface class is used for one way communication from the APIs to the Activity-class
 **/
public interface Callback {
    public void updateDisplays(String question, String answer);
    public void makeQuery(String question);
}
