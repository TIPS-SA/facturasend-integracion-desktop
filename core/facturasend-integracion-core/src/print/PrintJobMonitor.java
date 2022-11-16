package print;
import javax.print.event.PrintJobEvent;
import javax.print.event.PrintJobListener;

public class PrintJobMonitor implements PrintJobListener {
	 
    public void printDataTransferCompleted(PrintJobEvent pje) {
        // Called to notify the client that data has been successfully
        // transferred to the print service, and the client may free
        // local resources allocated for that data.
         
        System.out.println("Data transfer Completed : "+pje.hashCode()
                +"\n"+pje.getPrintEventType());
    }

    public void printJobCanceled(PrintJobEvent pje) {
        // Called to notify the client that the job was canceled
        // by a user or a program.
         
        System.out.println("Cancelled : "+pje.hashCode()
            +"\n Event Type "+pje.getPrintEventType());
    }

    public void printJobCompleted(PrintJobEvent pje) {
        // Called to notify the client that the job completed successfully.
         
        System.out.println("Completed : "+pje.hashCode()
                +"\n Event Type "+pje.getPrintEventType());
    }

    public void printJobFailed(PrintJobEvent pje) {
        // Called to notify the client that the job failed to complete
        // successfully and will have to be resubmitted.
         
        System.out.println("Failed : "+pje.hashCode()
                +"\n Event Type "+pje.getPrintEventType());
    }

    public void printJobNoMoreEvents(PrintJobEvent pje) {
        // Called to notify the client that no more events will be delivered.
         
        System.out.println("No More Events : "+pje.hashCode()
                +"\n Event Type "+pje.getPrintEventType());
    }

    public void printJobRequiresAttention(PrintJobEvent pje) {
        // Called to notify the client that an error has occurred that the
        // user might be able to fix.\
         
        System.out.println("Requires Attention  : "+pje.hashCode()
                +"\n Event Type "+pje.getPrintEventType());
    }
}