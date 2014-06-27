package Interfaces;

/**
 * Created by Unknown on 21/06/2014.
 */
public interface ISubject {

    public void RegisterListener(IListener listener);

    public void NotifyUpdates(String what);
}
