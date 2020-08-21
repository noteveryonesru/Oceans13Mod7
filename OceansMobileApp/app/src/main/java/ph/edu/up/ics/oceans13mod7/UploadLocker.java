package ph.edu.up.ics.oceans13mod7;

public interface UploadLocker {
    public void lock();
    public void unlock();
    public void toastError(String s);
}
